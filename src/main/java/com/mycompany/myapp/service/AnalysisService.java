package com.mycompany.myapp.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    /**
     * Cache de sentimento por texto do comentário. Comentários idênticos (ex.: vários usuários
     * analisando o mesmo vídeo/URL) são classificados apenas uma vez; as demais buscas reaproveitam
     * o resultado, evitando sobrecarregar a API de análise sob uso concorrente.
     */
    private final Map<String, Integer> sentimentCache = new ConcurrentHashMap<>();

    /**
     * Garante que apenas UMA requisição por vez chegue à API de análise. Como o modelo de
     * embeddings é pesado, chamadas simultâneas estouram a memória do serviço (OOM). Serializando
     * as chamadas, o serviço permanece estável mesmo com vários usuários pesquisando ao mesmo tempo.
     */
    private final Semaphore apiGate = new Semaphore(1);

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

        // 1) Descobre quais textos ainda não têm sentimento em cache (sem repetir duplicados).
        LinkedHashSet<String> missing = new LinkedHashSet<>();
        for (String t : texts) {
            if (t != null && !sentimentCache.containsKey(t)) {
                missing.add(t);
            }
        }

        // 2) Classifica os faltantes (serializado, com re-checagem do cache após o semáforo).
        if (!missing.isEmpty()) {
            try {
                apiGate.acquire();
                try {
                    // Outra busca concorrente pode ter preenchido o cache enquanto esperávamos.
                    List<String> stillMissing = new ArrayList<>();
                    for (String t : missing) {
                        if (!sentimentCache.containsKey(t)) {
                            stillMissing.add(t);
                        }
                    }
                    if (!stillMissing.isEmpty()) {
                        List<Integer> results = callApi(stillMissing);
                        int n = Math.min(results.size(), stillMissing.size());
                        for (int i = 0; i < n; i++) {
                            if (results.get(i) != null) {
                                sentimentCache.put(stillMissing.get(i), results.get(i));
                            }
                        }
                    }
                } finally {
                    apiGate.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Análise de sentimento interrompida", e);
            }
        }

        // 3) Monta a resposta na mesma ordem da entrada (usa -1 quando não houver classificação).
        List<Integer> out = new ArrayList<>(texts.size());
        for (String t : texts) {
            Integer s = t != null ? sentimentCache.get(t) : null;
            out.add(s != null ? s : -1);
        }
        return out;
    }

    private List<Integer> callApi(List<String> texts) {
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
