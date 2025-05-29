package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "prompt_tokens", "completion_tokens", "total_tokens" })
public class Usage {

    @JsonProperty("prompt_tokens")
    public Integer promptTokens;

    @JsonProperty("completion_tokens")
    public Integer completionTokens;

    @JsonProperty("total_tokens")
    public Integer totalTokens;

    public Usage(Integer promptTokens, Integer completionTokens, Integer totalTokens) {
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.totalTokens = totalTokens;
    }

    public Usage() {}

    public Integer getPromptTokens() {
        return promptTokens;
    }

    public void setPromptTokens(Integer promptTokens) {
        this.promptTokens = promptTokens;
    }

    public Integer getCompletionTokens() {
        return completionTokens;
    }

    public void setCompletionTokens(Integer completionTokens) {
        this.completionTokens = completionTokens;
    }

    public Integer getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(Integer totalTokens) {
        this.totalTokens = totalTokens;
    }

    @Override
    public String toString() {
        return "Usage{" + "promptTokens=" + promptTokens + ", completionTokens=" + completionTokens + ", totalTokens=" + totalTokens + '}';
    }
}
