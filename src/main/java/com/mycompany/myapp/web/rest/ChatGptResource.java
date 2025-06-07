package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.ChatGptRequest;
import com.mycompany.myapp.domain.ChatGptResponse;
import com.mycompany.myapp.domain.Message;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
public class ChatGptResource {

    @Autowired
    private RestTemplate template;

    @Value("https://api.groq.com/openai/v1/chat/completions")
    private String chatGptUrl;

    @Value("llama-3.3-70b-versatile")
    private String chatGptModel;

    @GetMapping("/bot/{message}")
    public ResponseEntity<?> getChatGptResponse(@PathVariable("message") String message) {
        ChatGptRequest request = new ChatGptRequest();
        List<Message> messageList = new ArrayList<>();
        Message chatGptMessage = new Message();
        chatGptMessage.setRole("user");
        chatGptMessage.setContent(message);
        messageList.add(chatGptMessage);
        request.setModel(chatGptModel);
        request.setMessages(messageList);

        try {
            ChatGptResponse chatGptResponse = template.postForObject(chatGptUrl, request, ChatGptResponse.class);
            return ResponseEntity.ok(chatGptResponse);
        } catch (HttpClientErrorException e) {
            String errorMessage = e.getResponseBodyAsString();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao acessar o serviço: " + errorMessage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + e.getMessage());
        }
    }
}
