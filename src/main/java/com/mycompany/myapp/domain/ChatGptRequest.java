package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "model", "messages", "temperature", "max_tokens", "top_p", "frequency_penalty", "presence_penalty" })
public class ChatGptRequest {

    @JsonProperty("model")
    public String model;

    @JsonProperty("messages")
    public List<Message> messages;

    @JsonProperty("temperature")
    public Integer temperature;

    @JsonProperty("max_tokens")
    public Integer maxTokens;

    @JsonProperty("top_p")
    public Integer topP;

    @JsonProperty("frequency_penalty")
    public Integer frequencyPenalty;

    @JsonProperty("presence_penalty")
    public Integer presencePenalty;

    public ChatGptRequest(
        String model,
        List<Message> messages,
        Integer temperature,
        Integer maxTokens,
        Integer topP,
        Integer frequencyPenalty,
        Integer presencePenalty
    ) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
        this.maxTokens = maxTokens;
        this.topP = topP;
        this.frequencyPenalty = frequencyPenalty;
        this.presencePenalty = presencePenalty;
    }

    public ChatGptRequest() {}

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Integer getTopP() {
        return topP;
    }

    public void setTopP(Integer topP) {
        this.topP = topP;
    }

    public Integer getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public void setFrequencyPenalty(Integer frequencyPenalty) {
        this.frequencyPenalty = frequencyPenalty;
    }

    public Integer getPresencePenalty() {
        return presencePenalty;
    }

    public void setPresencePenalty(Integer presencePenalty) {
        this.presencePenalty = presencePenalty;
    }

    @Override
    public String toString() {
        return (
            "ChatGptRequest{" +
            "model='" +
            model +
            '\'' +
            ", messages=" +
            messages +
            ", temperature=" +
            temperature +
            ", maxTokens=" +
            maxTokens +
            ", topP=" +
            topP +
            ", frequencyPenalty=" +
            frequencyPenalty +
            ", presencePenalty=" +
            presencePenalty +
            '}'
        );
    }
}
