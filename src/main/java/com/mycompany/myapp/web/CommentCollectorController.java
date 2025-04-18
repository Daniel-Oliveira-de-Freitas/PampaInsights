package com.mycompany.myapp.web;

import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/collect-comments")
@CrossOrigin(origins = "*") // Permite chamadas do front-end ou Postman
public class CommentCollectorController {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String BASE_API_URL = "https://mining-comments-api.vercel.app";

    @PostMapping("/scraping")
    public ResponseEntity<?> collectCommentsWithScraping(@RequestBody Map<String, String> body) {
        String url = body.get("url");

        if (url == null || url.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("URL é obrigatória");
        }

        Map<String, String> request = new HashMap<>();
        request.put("url", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "Mozilla/5.0");
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(request, headers);

            ResponseEntity<Object> response = restTemplate.postForEntity(BASE_API_URL + "/comments/scraping", requestEntity, Object.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                "Erro ao coletar comentários via scraping: " + e.getMessage()
            );
        }
    }

    @PostMapping("/crawling")
    public ResponseEntity<?> collectCommentsWithCrawling(@RequestBody Map<String, String> body) {
        String url = body.get("url");

        if (url == null || url.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("URL é obrigatória");
        }

        Map<String, String> request = new HashMap<>();
        request.put("url", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "Mozilla/5.0");
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(BASE_API_URL + "/comments/crawling", requestEntity, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                "Erro ao coletar comentários via crawling: " + e.getMessage()
            );
        }
    }
}
