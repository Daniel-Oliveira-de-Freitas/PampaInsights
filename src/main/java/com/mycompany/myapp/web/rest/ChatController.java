package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Conversation;
import com.mycompany.myapp.domain.Message;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.ChatService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("ai/chat/{conversationId}")
    public Conversation chat(@RequestBody Message message, @PathVariable String conversationId) {
        return chatService.chat(message, conversationId);
    }

    @PostMapping(value = "ai/chat/{conversationId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestBody Message message, @PathVariable String conversationId) {
        return chatService.chatStream(message, conversationId);
    }

    @PostMapping("ai/chat/create-conversation")
    public Conversation createConversation() {
        return chatService.createNewConversation();
    }

    @PostMapping("ai/chat/conversations")
    public List<Conversation> getConversations() {
        return chatService.findChatUserByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow()).getConversations();
    }

    @GetMapping("ai/chat/{conversationId}")
    public List<Message> getConversationMessages(@PathVariable String conversationId) {
        Conversation conversation = chatService.findConversationById(conversationId);

        return conversation.getMessages();
    }
}
