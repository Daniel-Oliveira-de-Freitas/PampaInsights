package com.mycompany.myapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAIConfig {

    @Value(
        "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"
    )
    private String openAiKey;

    @Bean
    public RestTemplate template() {
        RestTemplate template = new RestTemplate();
        template
            .getInterceptors()
            .add(
                ((request, body, execution) -> {
                        request.getHeaders().add("Authorization", "Bearer " + openAiKey);
                        return execution.execute(request, body);
                    })
            );
        return template;
    }
}
