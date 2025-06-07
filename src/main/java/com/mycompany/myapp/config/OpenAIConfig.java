package com.mycompany.myapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAIConfig {

    @Value("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")
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
