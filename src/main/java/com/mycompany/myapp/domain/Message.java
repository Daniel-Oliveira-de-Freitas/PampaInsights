package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {

    @JsonProperty("role")
    public String role;

    @JsonProperty("content")
    public String content;

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public Message() {}

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" + "role='" + role + '\'' + ", content='" + content + '\'' + '}';
    }
}
