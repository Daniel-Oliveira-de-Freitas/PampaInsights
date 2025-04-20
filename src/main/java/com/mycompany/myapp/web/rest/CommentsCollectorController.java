package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.CommentsCollectorService;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/comments-collector")
@CrossOrigin(origins = "*") // Permite chamadas do front-end ou Postman
public class CommentsCollectorController {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String BASE_API_URL = "https://mining-comments-api.vercel.app";

    private final CommentsCollectorService commentsCollectorService;

    public CommentsCollectorController(CommentsCollectorService commentsCollectorService) {
        this.commentsCollectorService = commentsCollectorService;
    }

    //    @PostMapping("/analyze")
    //    public ResponseEntity<List<Map<String, Object>>> analyzeUrl(@RequestBody Map<String, String> body) {
    //        String url = body.get("url");
    //        if (url == null || url.isEmpty()) {
    //            return ResponseEntity.badRequest().build();
    //        }
    //
    //        List<Map<String, Object>> result = commentsCollectorService.retrieveComments(url);
    //        return ResponseEntity.ok(result);
    //    }

    @PostMapping("/collect")
    public ResponseEntity<List<Map<String, Object>>> collectComments(@RequestBody Map<String, Object> body) {
        List<String> urls = (List<String>) body.get("urls");
        String keyword = (String) body.get("keyword");

        if (urls == null || urls.isEmpty() || keyword == null || keyword.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonList(Map.of("error", "urls e keyword são obrigatórios")));
        }

        List<Map<String, Object>> result = commentsCollectorService.retrieveComments(urls, keyword);
        return ResponseEntity.ok(result);
    }

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
