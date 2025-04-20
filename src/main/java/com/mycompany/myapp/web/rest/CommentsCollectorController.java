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

    @PostMapping("/collect")
    public ResponseEntity<List<Map<String, Object>>> collectComments(@RequestBody Map<String, Object> body) {
        List<String> urls = (List<String>) body.get("urls");
        String keyword = (String) body.get("keyword");
        Object searchObj = body.get("search");
        String search = searchObj != null ? searchObj.toString() : null;

        if (urls == null || urls.isEmpty() || keyword == null || keyword.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonList(Map.of("error", "urls e keyword são obrigatórios")));
        }

        List<Map<String, Object>> result = commentsCollectorService.retrieveComments(urls, keyword, search);
        return ResponseEntity.ok(result);
    }
}
