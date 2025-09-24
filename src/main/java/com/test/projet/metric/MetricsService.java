package com.test.projet.metric;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MetricsService {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void collectAndStoreMetrics(String typeCarte, String operationType, long executionTime) {
        // Récupérer les métriques existantes ou créer un nouvel objet
        MetricsDto metricsObject = getMetricsFromRedis(typeCarte);
        if (metricsObject == null) {
            metricsObject = new MetricsDto(typeCarte);
        }

        // Collecter et cumuler les métriques
        MetricsUtils.collectAndAccumulateMetrics(metricsObject, meterRegistry, operationType, typeCarte, redisTemplate, executionTime);

        // Convertir l'objet en JSON et le stocker
        try {
            String json = objectMapper.writeValueAsString(metricsObject);
            redisTemplate.opsForValue().set("metrics:" + typeCarte, json);
        } catch (Exception e) {
            e.printStackTrace(); // Gérer l'exception de manière appropriée
        }
    }

    public MetricsDto getMetricsFromRedis(String typeCarte) {
        try {
            String json = redisTemplate.opsForValue().get("metrics:" + typeCarte);
            if (json == null || json.trim().isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, MetricsDto.class);
        } catch (Exception e) {
            e.printStackTrace(); // Gérer l'exception de manière appropriée
            return null;
        }
    }

    public boolean deleteMetricsFromRedis(String typeCarte) {
        try {
            return redisTemplate.delete("metrics:" + typeCarte);
        } catch (Exception e) {
            e.printStackTrace(); // Gérer l'exception de manière appropriée
            return false;
        }
    }
}