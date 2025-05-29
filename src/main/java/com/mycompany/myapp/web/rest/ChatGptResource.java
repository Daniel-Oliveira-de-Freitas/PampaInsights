package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.ChatGptRequest;
import com.mycompany.myapp.domain.ChatGptResponse;
import com.mycompany.myapp.domain.Message;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class ChatGptResource {

    @Autowired
    private RestTemplate template;

    @Value("${ai.base-url}")
    private String chatUrl;

    @Value("${ai.model}")
    private String chatModel;

    @PostMapping("/bot")
    public ResponseEntity<?> getChatGptResponse(@RequestBody String message) {
        ChatGptRequest request = new ChatGptRequest();
        Message chatMessage = new Message();
        chatMessage.setRole("user");
        chatMessage.setContent(message);
        request.setModel(chatModel);
        request.setMessages(Collections.singletonList(chatMessage));

        try {
            ChatGptResponse chatGptResponse = template.postForObject(chatUrl, request, ChatGptResponse.class);
            return ResponseEntity.ok(chatGptResponse);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao acessar o serviço: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + e.getMessage());
        }
    }
}
