package com.test.projet.metric;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.core.instrument.MeterRegistry;

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

        // === NOUVELLE FONCTIONNALITÉ: Stocker aussi les métriques agrégées ===
        // Pour la compatibilité avec les tests fonctionnels réels
        storeAggregatedMetrics(typeCarte, operationType, executionTime);
    }

    /**
     * Stocke les métriques agrégées pour les tests fonctionnels.
     * Cette méthode maintient des statistiques agrégées par type d'opération.
     * 
     * @param typeCarte le type de carte
     * @param operationType le type d'opération
     * @param executionTime le temps d'exécution en millisecondes
     */
    private void storeAggregatedMetrics(String typeCarte, String operationType, long executionTime) {
        try {
            String redisKey = "metrics:" + typeCarte + ":" + operationType;

            // Récupérer les métriques agrégées existantes
            MetricsAggregated aggregated = getMetrics(typeCarte, operationType);
            if (aggregated == null) {
                aggregated = new MetricsAggregated(typeCarte, operationType);
            }

            // Mettre à jour avec les nouvelles données
            aggregated.updateMetrics(executionTime);

            // Sauvegarder dans Redis
            String json = objectMapper.writeValueAsString(aggregated);
            redisTemplate.opsForValue().set(redisKey, json);

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

    /**
     * Récupère les métriques pour un type de carte et un type d'opération spécifiques.
     * Cette méthode est ajoutée pour la compatibilité avec les tests fonctionnels.
     * 
     * @param typeCarte le type de carte
     * @param operationType le type d'opération (Insert, Update, Delete, etc.)
     * @return un objet MetricsAggregated contenant les métriques agrégées, ou null si aucune métrique trouvée
     */
    public MetricsAggregated getMetrics(String typeCarte, String operationType) {
        try {
            // Générer la clé Redis basée sur le type de carte et le type d'opération
            String redisKey = "metrics:" + typeCarte + ":" + operationType;
            String json = redisTemplate.opsForValue().get(redisKey);

            if (json == null || json.trim().isEmpty()) {
                return null; // Aucune métrique trouvée
            }

            return objectMapper.readValue(json, MetricsAggregated.class);
        } catch (Exception e) {
            e.printStackTrace(); // Gérer l'exception de manière appropriée
            return null;
        }
    }

    /**
     * Récupère toutes les métriques pour un type de carte donné.
     * 
     * @param typeCarte le type de carte
     * @return un objet MetricsDto contenant toutes les métriques, ou null si aucune trouvée
     */
    public MetricsDto getAllMetrics(String typeCarte) {
        return getMetricsFromRedis(typeCarte);
    }

    // methode recupere toutes les cles redis
    public Map<String, MetricsDto> getAllRedisKeys() {
        try {
            Set<String> keys = redisTemplate.keys("metrics:*");
            if (keys == null || keys.isEmpty())
                return Collections.emptyMap();

            // filter not null values
            return keys.stream().filter(key -> getMetricsFromRedis(key.replace("metrics:", "")) != null)
                            .collect(Collectors.toMap(key -> key.replace("metrics:", ""), key -> getMetricsFromRedis(key.replace("metrics:", ""))));

        } catch (Exception e) {
            e.printStackTrace(); // Gérer l'exception de manière appropriée
            return null;
        }
    }
}