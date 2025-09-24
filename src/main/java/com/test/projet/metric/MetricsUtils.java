package com.test.projet.metric;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class MetricsUtils {
    
    private static final Logger LOG = LoggerFactory.getLogger(MetricsUtils.class);
    
    private MetricsUtils() {
        // Constructeur privé pour classe utilitaire
    }
    
    // Méthode pour créer une Function qui convertit une métrique en double
    private static java.util.function.Function<MetricInner, Double> toDoubleValue() {
        return MetricInner::getValue;
    }
    
    // Méthode pour créer un Predicate qui vérifie le type d'opération
    private static Predicate<MetricInner> isOperationType(String operationType) {
        return metric -> operationType.equalsIgnoreCase(metric.getType());
    }
    
    // Exemple d'utilisation dans calculateMinMax
    public static List<Double> calculateMinMax(MetricsDto metricsObject, String operationType, double avg) {
        Optional<Double> min = metricsObject.getMetrics().stream()
            .filter(isOperationType(operationType))
            .map(toDoubleValue())
            .reduce(Double::min);
        
        Optional<Double> max = metricsObject.getMetrics().stream()
            .filter(isOperationType(operationType))
            .map(toDoubleValue())
            .reduce(Double::max);
        
        return List.of(min.orElse(avg), max.orElse(avg));
    }
    
    // Méthode pour ajouter des métriques en fonction de l'opération
    public static void addMetrics(MetricsDto metricsObject, String operationType, double count, double average, double max, double min) {
        addMetric(metricsObject, "Number", count, operationType);
        addMetric(metricsObject, "Average", average, operationType);
        addMetric(metricsObject, "Max", max, operationType);
        addMetric(metricsObject, "Min", min, operationType);
    }
    
    private static void addMetric(MetricsDto metricsObject, String name, double value, String operationType) {
        metricsObject.addMetric(new MetricInner(name, value, operationType));
    }
    
    public static void collectMetrics(MetricsDto metricsObject, io.micrometer.core.instrument.MeterRegistry meterRegistry, String operationType, String typeCarte, org.springframework.data.redis.core.StringRedisTemplate redisTemplate, long executionTime) {
        // Compteurs et timers pour l'opération
        io.micrometer.core.instrument.Counter counter = meterRegistry.counter("metrics." + operationType.toLowerCase() + ".count");
        Timer timer = meterRegistry.timer("metrics." + operationType.toLowerCase() + ".time");
        
        // Incrémenter le compteur
        counter.increment();
        
        // Enregistrer le temps d'exécution
        timer.record(executionTime, java.util.concurrent.TimeUnit.MILLISECONDS);
        
        var avg = timer.mean(java.util.concurrent.TimeUnit.MILLISECONDS);
        
        // Vérifier si l'objet existe déjà dans Redis
        String existingMetricsJson = redisTemplate.opsForValue().get("metrics:" + typeCarte);
        if (existingMetricsJson != null) {
            try {
                MetricsDto existingMetricsObject = new com.fasterxml.jackson.databind.ObjectMapper().readValue(existingMetricsJson, MetricsDto.class);
                
                // Calculer min et max
                List<Double> minMax = calculateMinMax(existingMetricsObject, operationType, avg);
                double min = minMax.get(0);
                double max = minMax.get(1);
                
                // Calculer count
                double count = counter.count();
                
                // Ajouter les métriques mises à jour à l'objet
                addMetrics(metricsObject, operationType, count, avg, max, min);
                
            } catch (Exception e) {
                LOG.error("Error while collecting metrics: ", e);
            }
        } else {
            // Si l'objet n'existe pas, initialiser avec les premières métriques
            counter.increment();
            addMetrics(metricsObject, operationType, counter.count(), avg, executionTime, executionTime);
        }
    }
    
    /**
     * Nouvelle méthode pour collecter et accumuler les métriques existantes
     */
    public static void collectAndAccumulateMetrics(MetricsDto metricsObject, MeterRegistry meterRegistry, 
            String operationType, String typeCarte, StringRedisTemplate redisTemplate, long executionTime) {
        
        try {
            // Rechercher les métriques existantes pour ce type d'opération
            MetricInner existingNumber = findMetricByNameAndType(metricsObject, "Number", operationType);
            MetricInner existingAverage = findMetricByNameAndType(metricsObject, "Average", operationType);
            MetricInner existingMax = findMetricByNameAndType(metricsObject, "Max", operationType);
            MetricInner existingMin = findMetricByNameAndType(metricsObject, "Min", operationType);
            
            double newCount, newAverage, newMax, newMin;
            
            if (existingNumber != null) {
                // Cumuler avec les valeurs existantes
                double oldCount = existingNumber.getValue();
                double oldAverage = existingAverage != null ? existingAverage.getValue() : executionTime;
                double oldMax = existingMax != null ? existingMax.getValue() : executionTime;
                double oldMin = existingMin != null ? existingMin.getValue() : executionTime;
                
                // Calculs cumulatifs
                newCount = oldCount + 1;
                newAverage = ((oldAverage * oldCount) + executionTime) / newCount;
                newMax = Math.max(oldMax, executionTime);
                newMin = Math.min(oldMin, executionTime);
                
                // Mettre à jour les métriques existantes
                updateOrAddMetric(metricsObject, "Number", newCount, operationType);
                updateOrAddMetric(metricsObject, "Average", newAverage, operationType);
                updateOrAddMetric(metricsObject, "Max", newMax, operationType);
                updateOrAddMetric(metricsObject, "Min", newMin, operationType);
                
            } else {
                // Première métrique pour ce type d'opération
                updateOrAddMetric(metricsObject, "Number", 1.0, operationType);
                updateOrAddMetric(metricsObject, "Average", (double) executionTime, operationType);
                updateOrAddMetric(metricsObject, "Max", (double) executionTime, operationType);
                updateOrAddMetric(metricsObject, "Min", (double) executionTime, operationType);
            }
            
        } catch (Exception e) {
            LOG.error("Error while accumulating metrics: ", e);
        }
    }
    
    /**
     * Recherche une métrique par nom et type dans l'objet MetricsDto
     */
    private static MetricInner findMetricByNameAndType(MetricsDto metricsObject, String name, String type) {
        return metricsObject.getMetrics().stream()
                .filter(metric -> name.equals(metric.getName()) && type.equals(metric.getType()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Met à jour une métrique existante ou en ajoute une nouvelle
     */
    private static void updateOrAddMetric(MetricsDto metricsObject, String name, double value, String type) {
        MetricInner existingMetric = findMetricByNameAndType(metricsObject, name, type);
        
        if (existingMetric != null) {
            // Mettre à jour la métrique existante
            existingMetric.setValue(value);
        } else {
            // Ajouter une nouvelle métrique
            metricsObject.addMetric(new MetricInner(name, value, type));
        }
    }
}