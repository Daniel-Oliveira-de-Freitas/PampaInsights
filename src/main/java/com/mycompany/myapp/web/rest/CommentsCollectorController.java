package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.CommentsCollectorService;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments-collector")
@CrossOrigin(origins = "*")
public class CommentsCollectorController {

    private final CommentsCollectorService commentsCollectorService;

    public CommentsCollectorController(CommentsCollectorService commentsCollectorService) {
        this.commentsCollectorService = commentsCollectorService;
    }

    @PostMapping("/collect")
    public ResponseEntity<Map<String, Object>> collectComments(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<String> urls = (List<String>) body.get("urls");
        String keyword = (String) body.get("keyword");
        Object searchObj = body.get("search");
        String search = searchObj != null ? searchObj.toString() : null;

        if (urls == null || urls.isEmpty() || keyword == null || keyword.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "urls e keyword são obrigatórios"));
        }

        try {
            List<Map<String, Object>> comments = commentsCollectorService.retrieveComments(urls, keyword, search);
            return ResponseEntity.ok(Map.of("comments", comments));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("message", e.getMessage()));
        }
    }
}
