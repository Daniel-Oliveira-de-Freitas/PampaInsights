package com.mycompany.myapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CommentsCollectorService {

    private final RestTemplate restTemplate;

    private static final String SCRAPING_URL = "https://mining-comments-api.vercel.app/comments/scraping";
    private static final String CRAWLING_URL = "https://mining-comments-api.vercel.app/comments/crawling";

    public CommentsCollectorService() {
        this.restTemplate = new RestTemplate();
    }

    public List<Map<String, Object>> retrieveComments(List<String> urls, String keyword) {
        List<Map<String, Object>> result = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " + "AppleWebKit/537.36 (KHTML, like Gecko) " + "Chrome/90.0.4430.212 Safari/537.36"
        );

        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("urls", urls);
        requestPayload.put("keyword", keyword);

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

            assert scrapingResponse.getBody() != null;
            extractComments(scrapingResponse.getBody(), result);
            assert crawlingResponse.getBody() != null;
            extractComments(crawlingResponse.getBody(), result);
        } catch (Exception e) {
            result.add(Map.of("error", "Falha ao buscar comentários: " + e.getMessage()));
        }

        return result;
    }

    private void extractComments(Map<String, Object> body, List<Map<String, Object>> result) {
        Object commentsObj = body.get("comments");

        if (commentsObj instanceof List<?>) {
            for (Object item : (List<?>) commentsObj) {
                if (item instanceof Map<?, ?>) {
                    result.add((Map<String, Object>) item);
                }
            }
        }
    }
}
