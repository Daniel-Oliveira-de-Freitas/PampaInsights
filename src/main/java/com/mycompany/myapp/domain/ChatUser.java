package com.mycompany.myapp.domain;

import java.util.ArrayList;
import java.util.List;

public class ChatUser {

    private String userLogin;
    private List<Conversation> conversations = new ArrayList<>();

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    public void addConversation(Conversation conversation) {
        conversations.add(conversation);
    }
}
