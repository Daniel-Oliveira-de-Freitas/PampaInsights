package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Conversation;
import com.mycompany.myapp.domain.Message;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.ChatService;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService, ChatClient.Builder builder) {
        this.chatService = chatService;
    }

    @PostMapping("ai/chat/{conversationId}")
    public Conversation chat(@RequestBody Message message, @PathVariable String conversationId) {
        return chatService.chat(message, conversationId);
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
