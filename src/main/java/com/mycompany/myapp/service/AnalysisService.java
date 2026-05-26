package com.mycompany.myapp.service;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);
    private final RestTemplate restTemplate;
    private static final String PREDICT_URL = "https://pampainsights-sentiment-analysis-api-production.up.railway.app/predict";

    public AnalysisService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(120_000);
        this.restTemplate = new RestTemplate(factory);
    }

    public List<Integer> predict(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return List.of();
        }

        Map<String, Object> requestBody = Map.of("comments", texts);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        log.debug("Enviando {} comentários para análise de sentimento", texts.size());

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            PREDICT_URL,
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        if (response.getBody() == null || !response.getBody().containsKey("result")) {
            throw new RuntimeException("Resposta inválida da API de análise: campo 'result' ausente");
        }

        @SuppressWarnings("unchecked")
        List<Integer> result = (List<Integer>) response.getBody().get("result");
        log.debug("Análise concluída: {} resultados", result.size());
        return result;
    }
}
