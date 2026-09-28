package com.mycompany.myapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.domain.Comment;
import com.mycompany.myapp.domain.Search;
import com.mycompany.myapp.repository.CommentRepository;
import com.mycompany.myapp.repository.SearchRepository;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CommentsCollectorService {

    private static final Logger log = LoggerFactory.getLogger(CommentsCollectorService.class);

    // production URL
    private static final String EXTRACT_URL = "https://mining-comments-api.vercel.app/comments/extract";

    //local testing
    // private static final String EXTRACT_URL = "http://localhost:5000/comments/extract";

    /** Validade da coleta em cache: comentários novos na fonte só aparecem após esse período. */
    private static final Duration COLLECTION_CACHE_TTL = Duration.ofHours(6);

    /** Cache de coleta por URL + palavra-chave (em memória; zera quando a aplicação reinicia). */
    private final Map<String, CollectionEntry> collectionCache = new ConcurrentHashMap<>();

    private final RestTemplate restTemplate;
    private final SearchRepository searchRepository;
    private final CommentRepository commentRepository;
    private final AnalysisService analysisService;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    public CommentsCollectorService(
        SearchRepository searchRepository,
        CommentRepository commentRepository,
        AnalysisService analysisService
    ) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(600_000);
        this.restTemplate = new RestTemplate(factory);
        this.searchRepository = searchRepository;
        this.commentRepository = commentRepository;
        this.analysisService = analysisService;
    }

    public List<Map<String, Object>> retrieveComments(List<String> urls, String keyword, String searchIdStr, int maxComments) {
        List<Map<String, Object>> comments = new ArrayList<>();

        try {
            for (String url : urls) {
                comments.addAll(collectUrl(url, keyword, searchIdStr, maxComments));
            }

            List<String> validBodies = comments
                .stream()
                .filter(c -> !c.containsKey("error"))
                .map(c -> String.valueOf(c.getOrDefault("body", "")))
                .collect(Collectors.toList());

            List<Integer> sentiments;
            try {
                sentiments = analysisService.predict(validBodies);
            } catch (Exception e) {
                // Se a análise de sentimento falhar (ex.: API indisponível), ainda assim salvamos
                // os comentários coletados — sem sentimento — em vez de descartar a busca inteira.
                log.error("Falha na análise de sentimento, salvando comentários sem sentimento: {}", e.getMessage());
                sentiments = List.of();
            }

            int sentimentIdx = 0;
            for (Map<String, Object> commentMap : comments) {
                if (!commentMap.containsKey("error") && sentimentIdx < sentiments.size()) {
                    Integer sentiment = sentiments.get(sentimentIdx++);
                    // -1 = não classificado; não gravamos como se fosse um sentimento válido.
                    if (sentiment != null && sentiment >= 0) {
                        commentMap.put("sentiment", sentiment);
                    }
                }
            }

            if (searchIdStr != null && !searchIdStr.isBlank()) {
                saveComments(comments, Long.parseLong(searchIdStr));
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao recuperar comentários: {}", e.getMessage());
            throw new RuntimeException("Falha ao comunicar com a API de mineração: " + e.getMessage());
        }

        return comments;
    }

    /**
     * Coleta os comentários de uma URL, reaproveitando o cache quando possível. Usuários que
     * pesquisam o mesmo link com a mesma palavra-chave compartilham uma única coleta, evitando
     * chamadas repetidas à API de mineração e aos providers pagos (ex.: Apify no Facebook).
     */
    private List<Map<String, Object>> collectUrl(String url, String keyword, String searchIdStr, int maxComments) throws Exception {
        String key = url.trim() + "|" + (keyword == null ? "" : keyword.trim());
        CollectionEntry fresh = new CollectionEntry(new CompletableFuture<>(), maxComments, Instant.now());
        CollectionEntry entry = collectionCache.compute(key, (k, existing) -> covers(existing, maxComments) ? existing : fresh);

        if (entry == fresh) {
            // Esta requisição é a responsável pela coleta; as concorrentes aguardam o mesmo resultado.
            try {
                List<Map<String, Object>> collected = fetchFromMiningApi(url, keyword, searchIdStr, maxComments);
                fresh.future().complete(collected);
                if (collected.stream().noneMatch(c -> !c.containsKey("error"))) {
                    // Não guarda coleta vazia/com erro, para que a próxima busca tente novamente.
                    collectionCache.remove(key, fresh);
                }
            } catch (Exception e) {
                collectionCache.remove(key, fresh);
                fresh.future().completeExceptionally(e);
                throw e;
            }
        } else {
            log.debug("Coleta reaproveitada do cache para {}", url);
        }

        List<Map<String, Object>> cached;
        try {
            cached = entry.future().join();
        } catch (CompletionException e) {
            throw e.getCause() instanceof Exception cause ? cause : e;
        }

        // Cópia por requisição: os mapas recebem o sentimento depois e não podem ser compartilhados.
        return cached
            .stream()
            .limit(maxComments)
            .map(c -> {
                Map<String, Object> copy = new HashMap<>(c);
                copy.put("search", searchIdStr);
                return copy;
            })
            .collect(Collectors.toList());
    }

    /** Indica se a entrada em cache atende a um pedido de {@code maxComments} comentários. */
    private boolean covers(CollectionEntry entry, int maxComments) {
        if (entry == null || entry.isExpired()) {
            return false;
        }
        if (entry.maxComments() >= maxComments) {
            return true;
        }
        // Coleta anterior pediu menos, mas a fonte já se esgotou (vieram menos que o pedido).
        CompletableFuture<List<Map<String, Object>>> future = entry.future();
        return future.isDone() && !future.isCompletedExceptionally() && future.join().size() < entry.maxComments();
    }

    private List<Map<String, Object>> fetchFromMiningApi(String url, String keyword, String searchIdStr, int maxComments) throws Exception {
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("urls", List.of(url));
        requestPayload.put("keyword", keyword);
        requestPayload.put("search", searchIdStr);
        requestPayload.put("maxComments", maxComments);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String jsonBody = new ObjectMapper().writeValueAsString(requestPayload);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            EXTRACT_URL,
            HttpMethod.POST,
            requestEntity,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        List<Map<String, Object>> collected = new ArrayList<>();
        if (response.getBody() != null) {
            extractComments(response.getBody(), collected);
        }
        return collected;
    }

    private record CollectionEntry(CompletableFuture<List<Map<String, Object>>> future, int maxComments, Instant createdAt) {
        boolean isExpired() {
            return createdAt.plus(COLLECTION_CACHE_TTL).isBefore(Instant.now());
        }
    }

    @SuppressWarnings("unchecked")
    private void extractComments(Map<String, Object> body, List<Map<String, Object>> result) {
        Object commentsObj = body.get("comments");
        if (commentsObj instanceof List<?>) {
            ((List<?>) commentsObj).forEach(item -> {
                    if (item instanceof Map<?, ?>) {
                        result.add((Map<String, Object>) item);
                    }
                });
        } else {
            log.warn("'comments' não é uma lista válida: {}", commentsObj);
        }
    }

    private void saveComments(List<Map<String, Object>> comments, Long searchId) {
        Search search = searchRepository.findById(searchId).orElseThrow(() -> new RuntimeException("Search não encontrada: " + searchId));

        commentRepository.deleteBySearchId(searchId);
        entityManagerFactory.getCache().evict(Search.class, searchId);

        comments.forEach(commentMap -> {
            if (commentMap.containsKey("error")) {
                log.warn("Comentário ignorado (erro da API): {}", commentMap.get("error"));
                return;
            }
            try {
                Comment comment = new Comment();
                comment.setKeyword(String.valueOf(commentMap.getOrDefault("keyword", "")));
                comment.setBody(String.valueOf(commentMap.getOrDefault("body", "")));
                Object authorVal = commentMap.get("author");
                comment.setAuthor(authorVal != null ? authorVal.toString() : null);
                comment.setCreateDate(parseDate(String.valueOf(commentMap.getOrDefault("createDate", ""))));
                comment.setSearch(search);
                Object sentimentVal = commentMap.get("sentiment");
                if (sentimentVal instanceof Integer) {
                    comment.setSentiment(((Integer) sentimentVal).longValue());
                }
                commentRepository.save(comment);
            } catch (Exception e) {
                log.error("Erro ao salvar comentário: {}", e.getMessage());
            }
        });
    }

    private Instant parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank() || dateStr.equals("null")) return Instant.now();
        try {
            return OffsetDateTime.parse(dateStr).toInstant();
        } catch (DateTimeParseException ignored) {}
        try {
            return java.time.LocalDate.parse(dateStr).atStartOfDay(java.time.ZoneOffset.UTC).toInstant();
        } catch (DateTimeParseException ignored) {}
        log.warn("Data não parseável '{}', usando Instant.now()", dateStr);
        return Instant.now();
    }
}
