package com.mycompany.myapp.service;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

import com.mycompany.myapp.domain.ChatUser;
import com.mycompany.myapp.domain.Conversation;
import com.mycompany.myapp.domain.Message;
import com.mycompany.myapp.security.SecurityUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final List<ChatUser> chatUserList = new ArrayList<>();

    public ChatService(ChatClient.Builder builder) {
        this.chatClient = builder
            .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
            .defaultSystem("You are a helpful AI Assistant answering questions about cities around the world.")
            .defaultFunctions("currentWeatherFunction")
            .build();
    }

    public Conversation chat(Message message, String conversationId) {
        Conversation conversation = findConversationById(conversationId);

        String content = chatClient
            .prompt()
            .user(message.getContent())
            .advisors(a ->
                a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversation.getConversationId()).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 5000)
            )
            .call()
            .content();

        Message chatMessage = new Message("agent", content);
        conversation.getMessages().add(message);
        conversation.getMessages().add(chatMessage);

        return conversation;
    }

    private Conversation createNewConversation(ChatUser chatUser) {
        Conversation conversation = new Conversation();
        conversation.setConversationId(UUID.randomUUID().toString());
        conversation.setName("Conversation " + LocalDateTime.now());
        chatUser.addConversation(conversation);
        return conversation;
    }

    public ChatUser findChatUserByLogin(String userLogin) {
        Optional<ChatUser> chatMessageOptional = chatUserList.stream().filter(item -> item.getUserLogin().equals(userLogin)).findFirst();
        if (chatMessageOptional.isEmpty()) {
            ChatUser chatUser = new ChatUser();
            chatUser.setUserLogin(userLogin);
            chatUserList.add(chatUser);
            return chatUser;
        }

        return chatMessageOptional.get();
    }

    public Conversation findConversationById(String conversationId) {
        ChatUser chatUser = findChatUserByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow());
        Optional<Conversation> conversation = chatUser
            .getConversations()
            .stream()
            .filter(conv -> conv.getConversationId().equals(conversationId))
            .findFirst();

        if (conversation.isEmpty()) {
            return createNewConversation(chatUser);
        }

        return conversation.orElseThrow();
    }
}
