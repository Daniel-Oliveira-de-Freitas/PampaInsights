package com.mycompany.myapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.domain.Comment;
import com.mycompany.myapp.domain.Search;
import com.mycompany.myapp.repository.CommentRepository;
import com.mycompany.myapp.repository.SearchRepository;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
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

    private static final String EXTRACT_URL = "https://mining-comments-api.vercel.app/comments/extract";

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

    public List<Map<String, Object>> retrieveComments(List<String> urls, String keyword, String searchIdStr, int maxPages) {
        List<Map<String, Object>> comments = new ArrayList<>();
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("urls", urls);
        requestPayload.put("keyword", keyword);
        requestPayload.put("search", searchIdStr);
        requestPayload.put("maxPages", maxPages);

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
                extractComments(response.getBody(), comments);
            }

            List<String> validBodies = comments
                .stream()
                .filter(c -> !c.containsKey("error"))
                .map(c -> String.valueOf(c.getOrDefault("body", "")))
                .collect(Collectors.toList());

            List<Integer> sentiments = analysisService.predict(validBodies);

            int sentimentIdx = 0;
            for (Map<String, Object> commentMap : comments) {
                if (!commentMap.containsKey("error") && sentimentIdx < sentiments.size()) {
                    commentMap.put("sentiment", sentiments.get(sentimentIdx++));
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
