package com.mycompany.myapp.domain;

import java.util.ArrayList;
import java.util.List;

public class Conversation {

    private String conversationId;
    private String name;
    private List<Message> messages = new ArrayList<>();

    public Conversation() {}

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
