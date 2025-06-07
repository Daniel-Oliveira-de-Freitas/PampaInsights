package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "object", "created", "model", "choices", "usage", "system_fingerprint" })
public class ChatGptResponse {

    @JsonProperty("id")
    public String id;

    @JsonProperty("object")
    public String object;

    @JsonProperty("created")
    public Integer created;

    @JsonProperty("model")
    public String model;

    @JsonProperty("choices")
    public List<Choice> choices;

    @JsonProperty("usage")
    public Usage usage;

    @JsonProperty("system_fingerprint")
    public Object systemFingerprint;

    public ChatGptResponse(
        String id,
        String object,
        Integer created,
        String model,
        List<Choice> choices,
        Usage usage,
        Object systemFingerprint
    ) {
        this.id = id;
        this.object = object;
        this.created = created;
        this.model = model;
        this.choices = choices;
        this.usage = usage;
        this.systemFingerprint = systemFingerprint;
    }

    public ChatGptResponse() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public Object getSystemFingerprint() {
        return systemFingerprint;
    }

    public void setSystemFingerprint(Object systemFingerprint) {
        this.systemFingerprint = systemFingerprint;
    }

    @Override
    public String toString() {
        return (
            "ChatGptResponse{" +
            "id='" +
            id +
            '\'' +
            ", object='" +
            object +
            '\'' +
            ", created=" +
            created +
            ", model='" +
            model +
            '\'' +
            ", choices=" +
            choices +
            ", usage=" +
            usage +
            ", systemFingerprint=" +
            systemFingerprint +
            '}'
        );
    }
}
