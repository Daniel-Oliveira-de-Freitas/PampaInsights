package com.mycompany.myapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.domain.Comment;
import com.mycompany.myapp.domain.Search;
import com.mycompany.myapp.repository.CommentRepository;
import com.mycompany.myapp.repository.SearchRepository;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CommentsCollectorService {

    private static final Logger log = LoggerFactory.getLogger(CommentsCollectorService.class);

    private static final String EXTRACT_URL = "https://mining-comments-api.vercel.app/comments/extract";

    private final RestTemplate restTemplate;
    private final SearchRepository searchRepository;
    private final CommentRepository commentRepository;

    public CommentsCollectorService(SearchRepository searchRepository, CommentRepository commentRepository) {
        this.restTemplate = new RestTemplate();
        this.searchRepository = searchRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * Resultado da coleta: comentários encontrados + avisos por URL sem resultado.
     */
    public static class CollectionResult {

        private final List<Map<String, Object>> comments;
        private final List<String> warnings;

        public CollectionResult(List<Map<String, Object>> comments, List<String> warnings) {
            this.comments = comments;
            this.warnings = warnings;
        }

        public List<Map<String, Object>> getComments() {
            return comments;
        }

        public List<String> getWarnings() {
            return warnings;
        }
    }

    /**
     * Coleta comentários via API de mineração e persiste no banco.
     * Retorna CollectionResult com comentários e avisos amigáveis para URLs sem resultado.
     */
    public CollectionResult retrieveComments(List<String> urls, String keyword, String searchIdStr) {
        List<Map<String, Object>> comments = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("urls", urls);
        requestPayload.put("keyword", keyword);
        requestPayload.put("search", searchIdStr);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(requestPayload);
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                EXTRACT_URL,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                // Analisa stats e popula warnings para URLs sem resultado
                Object statsObj = body.get("stats");
                if (statsObj instanceof List<?>) {
                    analyzeStats((List<?>) statsObj, warnings);
                }

                extractComments(body, comments);
            }

            if (searchIdStr != null && !searchIdStr.isBlank()) {
                saveComments(comments, Long.parseLong(searchIdStr));
            }
        } catch (Exception e) {
            log.error("Erro ao recuperar comentários: {}", e.getMessage());
            warnings.add("Falha ao comunicar com a API de mineração: " + e.getMessage());
        }

        return new CollectionResult(comments, warnings);
    }

    /**
     * Analisa os stats retornados pela API e gera mensagens amigáveis
     * para URLs com source "none" (sem resultado) ou "error" (falha).
     */
    @SuppressWarnings("unchecked")
    private void analyzeStats(List<?> stats, List<String> warnings) {
        for (Object statObj : stats) {
            if (!(statObj instanceof Map<?, ?>)) continue;
            Map<String, Object> stat = (Map<String, Object>) statObj;

            String source = String.valueOf(stat.getOrDefault("source", ""));
            String domain = String.valueOf(stat.getOrDefault("domain", stat.getOrDefault("url", "")));
            Object errorObj = stat.get("error");

            log.info(
                "Stats [{}]: source={}, fetcher={}, comments={}, elapsed={}s",
                domain,
                source,
                stat.getOrDefault("fetcher", "-"),
                stat.getOrDefault("commentsCount", 0),
                stat.getOrDefault("elapsedSec", 0)
            );

            if ("error".equals(source)) {
                String detail = errorObj != null ? ": " + errorObj : "";
                warnings.add(
                    "Não foi possível processar \"" + domain + "\"" + detail + ". " + "Verifique se a URL está correta e acessível."
                );
            } else if ("none".equals(source)) {
                warnings.add(
                    "Nenhum comentário encontrado em \"" +
                    domain +
                    "\". " +
                    "O site pode bloquear coleta automática, não ter comentários públicos, " +
                    "ou não ser suportado pela ferramenta."
                );
            }
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

        comments.forEach(commentMap -> {
            if (commentMap.containsKey("error")) {
                log.warn("Comentário ignorado (erro da API): {}", commentMap.get("error"));
                return;
            }
            try {
                Comment comment = new Comment();
                comment.setKeyword(String.valueOf(commentMap.getOrDefault("keyword", "")));
                comment.setBody(String.valueOf(commentMap.getOrDefault("body", "")));
                comment.setCreateDate(parseDate(String.valueOf(commentMap.getOrDefault("createDate", ""))));
                comment.setSearch(search);
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
