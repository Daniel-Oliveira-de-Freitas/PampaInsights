package com.mycompany.myapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.domain.Comment;
import com.mycompany.myapp.domain.Search;
import com.mycompany.myapp.repository.CommentRepository;
import com.mycompany.myapp.repository.SearchRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    private final RestTemplate restTemplate;

    private static final String SCRAPING_URL = "https://mining-comments-api.vercel.app/comments/scraping";
    private static final String CRAWLING_URL = "https://mining-comments-api.vercel.app/comments/crawling";
    private final SearchRepository searchRepository;
    private final CommentRepository commentRepository;

    public CommentsCollectorService(SearchRepository searchRepository, CommentRepository commentRepository) {
        this.restTemplate = new RestTemplate();
        this.searchRepository = searchRepository;
        this.commentRepository = commentRepository;
    }

    public List<Map<String, Object>> retrieveComments(List<String> urls, String keyword, String searchIdStr) {
        List<Map<String, Object>> result = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36"
        );

        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("urls", urls);
        requestPayload.put("keyword", keyword);
        requestPayload.put("search", searchIdStr);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(requestPayload);
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<Map<String, Object>> scrapingResponse = restTemplate.exchange(
                SCRAPING_URL,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            ResponseEntity<Map<String, Object>> crawlingResponse = restTemplate.exchange(
                CRAWLING_URL,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (scrapingResponse.getBody() != null) {
                extractComments(scrapingResponse.getBody(), result);
            }
            if (crawlingResponse.getBody() != null) {
                extractComments(crawlingResponse.getBody(), result);
            }

            // Salva os comentários após coletar todos
            saveComments(result, Long.parseLong(searchIdStr));
        } catch (Exception e) {
            log.error("Erro ao recuperar comentários: {}", e.getMessage());
            result.add(Map.of("error", "Falha ao buscar comentários: " + e.getMessage()));
        }

        return result;
    }

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
        Search search = searchRepository.findById(searchId).get();

        comments.forEach(commentMap -> {
            try {
                Comment comment = new Comment();
                comment.setKeyword(String.valueOf(commentMap.get("keyword")));
                comment.setBody(String.valueOf(commentMap.get("body")));
                String createDateStr = String.valueOf(commentMap.get("createDate"));
                LocalDateTime localDateTime = LocalDateTime.parse(createDateStr);
                Instant instant = localDateTime.atZone(ZoneId.of("America/Sao_Paulo")).toInstant();
                comment.setCreateDate(instant);
                comment.setSearch(search);

                commentRepository.save(comment);
            } catch (Exception e) {
                log.error("Erro ao salvar comentário: {}", e.getMessage());
            }
        });
    }
}
