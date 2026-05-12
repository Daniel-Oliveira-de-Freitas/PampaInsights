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

    // ── Endpoint único da nova API (v3) ──────────────────────────────────────
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
     * Coleta comentários das URLs fornecidas via API de mineração e persiste no banco.
     *
     * @param urls        lista de URLs a minerar
     * @param keyword     palavra-chave da pesquisa
     * @param searchIdStr ID da pesquisa (String — será convertido para Long ao salvar)
     * @return lista de mapas com os comentários retornados pela API
     */
    public List<Map<String, Object>> retrieveComments(List<String> urls, String keyword, String searchIdStr) {
        List<Map<String, Object>> result = new ArrayList<>();

        // ── Monta o payload ──────────────────────────────────────────────────
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("urls", urls);
        requestPayload.put("keyword", keyword);
        requestPayload.put("search", searchIdStr);
        // maxCommentsPerUrl pode ser configurado aqui se necessário
        // requestPayload.put("maxCommentsPerUrl", 100);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(requestPayload);
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

            // ── Chamada única ao endpoint /comments/extract ──────────────────
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                EXTRACT_URL,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                // Log de stats para rastreabilidade
                Object stats = body.get("stats");
                if (stats != null) {
                    log.info("Stats da coleta: {}", stats);
                }

                extractComments(body, result);
            }

            saveComments(result, Long.parseLong(searchIdStr));
        } catch (Exception e) {
            log.error("Erro ao recuperar comentários: {}", e.getMessage());
            result.add(Map.of("error", "Falha ao buscar comentários: " + e.getMessage()));
        }

        return result;
    }

    // ── Extrai a lista "comments" do corpo da resposta ───────────────────────
    private void extractComments(Map<String, Object> body, List<Map<String, Object>> result) {
        Object commentsObj = body.get("comments");
        if (commentsObj instanceof List<?>) {
            ((List<?>) commentsObj).forEach(item -> {
                    if (item instanceof Map<?, ?>) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> commentMap = (Map<String, Object>) item;
                        result.add(commentMap);
                    }
                });
        } else {
            log.warn("'comments' não é uma lista válida: {}", commentsObj);
        }
    }

    // ── Persiste cada comentário na entidade Comment ─────────────────────────
    private void saveComments(List<Map<String, Object>> comments, Long searchId) {
        Search search = searchRepository
            .findById(searchId)
            .orElseThrow(() -> new RuntimeException("Search não encontrada com ID: " + searchId));

        comments.forEach(commentMap -> {
            // Ignora entradas de erro retornadas pela API
            if (commentMap.containsKey("error")) {
                log.warn("Comentário ignorado (erro da API): {}", commentMap.get("error"));
                return;
            }

            try {
                Comment comment = new Comment();

                comment.setKeyword(String.valueOf(commentMap.getOrDefault("keyword", "")));
                comment.setBody(String.valueOf(commentMap.getOrDefault("body", "")));

                // ── Parse de data robusto ────────────────────────────────────
                // A API v3 retorna ISO 8601 com timezone, ex: "2024-03-15T14:22:00Z"
                // ou "2026-05-12T16:53:07.489543+00:00".
                // OffsetDateTime suporta ambos os formatos.
                String createDateStr = String.valueOf(commentMap.getOrDefault("createDate", ""));
                Instant instant = parseDate(createDateStr);
                comment.setCreateDate(instant);

                comment.setSearch(search);
                commentRepository.save(comment);
            } catch (Exception e) {
                log.error("Erro ao salvar comentário '{}': {}", commentMap.get("body"), e.getMessage());
            }
        });
    }

    /**
     * Converte uma string de data ISO 8601 em Instant.
     * Suporta os formatos retornados pela API v3:
     *   - "2024-03-15T14:22:00Z"
     *   - "2026-05-12T16:53:07.489543+00:00"
     *   - "2024-03-15" (data simples, assume meia-noite UTC)
     * Retorna Instant.now() como fallback se não for possível parsear.
     */
    private Instant parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank() || dateStr.equals("null")) {
            return Instant.now();
        }

        // Tenta OffsetDateTime (cobre Z e +00:00)
        try {
            return OffsetDateTime.parse(dateStr).toInstant();
        } catch (DateTimeParseException ignored) {}

        // Tenta data simples "yyyy-MM-dd"
        try {
            return java.time.LocalDate.parse(dateStr).atStartOfDay(java.time.ZoneOffset.UTC).toInstant();
        } catch (DateTimeParseException ignored) {}

        log.warn("Não foi possível parsear a data '{}', usando Instant.now()", dateStr);
        return Instant.now();
    }
}
