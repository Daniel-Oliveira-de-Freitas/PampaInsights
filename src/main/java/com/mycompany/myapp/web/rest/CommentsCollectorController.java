package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.CommentsCollectorService;
import com.mycompany.myapp.service.CommentsCollectorService.CollectionResult;
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

    /**
     * POST /api/comments-collector/collect
     *
     * Body: { urls: string[], keyword: string, search: string }
     *
     * Resposta:
     * {
     *   "comments": [...],   // comentários coletados
     *   "warnings": [...]    // avisos para URLs sem resultado (pode ser lista vazia)
     * }
     */
    @PostMapping("/collect")
    public ResponseEntity<Map<String, Object>> collectComments(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<String> urls = (List<String>) body.get("urls");
        String keyword = (String) body.get("keyword");
        Object searchObj = body.get("search");
        String search = searchObj != null ? searchObj.toString() : null;

        if (urls == null || urls.isEmpty() || keyword == null || keyword.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("comments", Collections.emptyList());
            error.put("warnings", Collections.singletonList("urls e keyword são obrigatórios"));
            return ResponseEntity.badRequest().body(error);
        }

        CollectionResult result = commentsCollectorService.retrieveComments(urls, keyword, search);

        Map<String, Object> response = new HashMap<>();
        response.put("comments", result.getComments());
        response.put("warnings", result.getWarnings());

        return ResponseEntity.ok(response);
    }
}
