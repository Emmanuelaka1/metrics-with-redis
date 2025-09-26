package com.test.projet.metric.annotation;

import com.test.projet.metric.MetricsDto;
import com.test.projet.metric.MetricInner;

/**
 * Classe utilitaire pour les tests de métriques
 * Facilite l'accès aux métriques dans MetricsDto
 * 
 * @author Generated
 * @since 1.0.0
 */
public class MetricsTestUtils {
    
    private MetricsTestUtils() {
        // Utilitaire - constructeur privé
    }
    
    /**
     * Récupère la valeur d'une métrique par nom et type
     */
    public static Double getMetricValue(MetricsDto metricsDto, String name, String type) {
        return metricsDto.getMetrics().stream()
                .filter(metric -> name.equals(metric.getName()) && type.equals(metric.getType()))
                .findFirst()
                .map(MetricInner::getValue)
                .orElse(null);
    }
    
    /**
     * Récupère la valeur formatée d'une métrique par nom et type
     */
    public static String getMetricFormattedValue(MetricsDto metricsDto, String name, String type) {
        return metricsDto.getMetrics().stream()
                .filter(metric -> name.equals(metric.getName()) && type.equals(metric.getType()))
                .findFirst()
                .map(MetricInner::getFormattedValue)
                .orElse(null);
    }
    
    /**
     * Obtient le nombre d'exécutions pour un type d'opération
     */
    public static Double getCount(MetricsDto metricsDto, String operationType) {
        return getMetricValue(metricsDto, "Number", operationType);
    }
    
    /**
     * Obtient le temps moyen pour un type d'opération
     */
    public static Double getAverage(MetricsDto metricsDto, String operationType) {
        return getMetricValue(metricsDto, "Average", operationType);
    }
    
    /**
     * Obtient le temps minimum pour un type d'opération
     */
    public static Double getMin(MetricsDto metricsDto, String operationType) {
        return getMetricValue(metricsDto, "Min", operationType);
    }
    
    /**
     * Obtient le temps maximum pour un type d'opération
     */
    public static Double getMax(MetricsDto metricsDto, String operationType) {
        return getMetricValue(metricsDto, "Max", operationType);
    }
    
    /**
     * Obtient le nombre d'exécutions formaté pour un type d'opération
     */
    public static String getCountFormatted(MetricsDto metricsDto, String operationType) {
        return getMetricFormattedValue(metricsDto, "Number", operationType);
    }
    
    /**
     * Obtient le temps moyen formaté pour un type d'opération
     */
    public static String getAverageFormatted(MetricsDto metricsDto, String operationType) {
        return getMetricFormattedValue(metricsDto, "Average", operationType);
    }
    
    /**
     * Obtient le temps minimum formaté pour un type d'opération
     */
    public static String getMinFormatted(MetricsDto metricsDto, String operationType) {
        return getMetricFormattedValue(metricsDto, "Min", operationType);
    }
    
    /**
     * Obtient le temps maximum formaté pour un type d'opération
     */
    public static String getMaxFormatted(MetricsDto metricsDto, String operationType) {
        return getMetricFormattedValue(metricsDto, "Max", operationType);
    }
    
    /**
     * Vérifie si une métrique existe pour un type d'opération
     */
    public static boolean hasMetrics(MetricsDto metricsDto, String operationType) {
        return getCount(metricsDto, operationType) != null;
    }
    
    /**
     * Affiche les métriques pour debug
     */
    public static void printMetrics(MetricsDto metricsDto, String operationType) {
        System.out.printf("Métriques pour %s (%s):\n", metricsDto.getTypeCarte(), operationType);
        System.out.printf("  - Count: %s\n", getCountFormatted(metricsDto, operationType));
        System.out.printf("  - Average: %s\n", getAverageFormatted(metricsDto, operationType));
        System.out.printf("  - Min: %s\n", getMinFormatted(metricsDto, operationType));
        System.out.printf("  - Max: %s\n", getMaxFormatted(metricsDto, operationType));
    }
}