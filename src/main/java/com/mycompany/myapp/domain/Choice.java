package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "index", "message", "logprobs", "finish_reason" })
public class Choice {

    @JsonProperty("index")
    public Integer index;

    @JsonProperty("message")
    public Message message;

    @JsonProperty("logprobs")
    public Object logprobs;

    @JsonProperty("finish_reason")
    public String finishReason;

    public Choice(Integer index, Message message, Object logprobs, String finishReason) {
        this.index = index;
        this.message = message;
        this.logprobs = logprobs;
        this.finishReason = finishReason;
    }

    public Choice() {}

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Object getLogprobs() {
        return logprobs;
    }

    public void setLogprobs(Object logprobs) {
        this.logprobs = logprobs;
    }

    public String getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason;
    }

    @Override
    public String toString() {
        return (
            "Choice{" +
            "index=" +
            index +
            ", message=" +
            message +
            ", logprobs=" +
            logprobs +
            ", finishReason='" +
            finishReason +
            '\'' +
            '}'
        );
    }
}
