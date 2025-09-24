package com.test.projet.metric;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class MetricsAccumulationTest {

    private MetricsDto metricsDto;

    @BeforeEach
    void setUp() {
        metricsDto = new MetricsDto("VISA");
    }

    @Test
    void testFirstMetricCollection() {
        // Première collecte
        MetricsUtils.collectAndAccumulateMetrics(metricsDto, null, "PAYMENT", "VISA", null, 100);

        assertEquals(4, metricsDto.getMetrics().size());
        
        // Vérifier Number
        MetricInner numberMetric = findMetric("Number", "PAYMENT");
        assertEquals(1.0, numberMetric.getValue(), 0.001);
        
        // Vérifier Average
        MetricInner avgMetric = findMetric("Average", "PAYMENT");
        assertEquals(100.0, avgMetric.getValue(), 0.001);
        
        // Vérifier Max
        MetricInner maxMetric = findMetric("Max", "PAYMENT");
        assertEquals(100.0, maxMetric.getValue(), 0.001);
        
        // Vérifier Min
        MetricInner minMetric = findMetric("Min", "PAYMENT");
        assertEquals(100.0, minMetric.getValue(), 0.001);
    }

    @Test
    void testAccumulatedMetrics() {
        // Première collecte
        MetricsUtils.collectAndAccumulateMetrics(metricsDto, null, "REFUND", "MASTERCARD", null, 100);
        
        // Deuxième collecte
        MetricsUtils.collectAndAccumulateMetrics(metricsDto, null, "REFUND", "MASTERCARD", null, 200);
        
        // Troisième collecte  
        MetricsUtils.collectAndAccumulateMetrics(metricsDto, null, "REFUND", "MASTERCARD", null, 125);

        assertEquals(4, metricsDto.getMetrics().size());
        
        // Vérifier Number (count = 3)
        MetricInner numberMetric = findMetric("Number", "REFUND");
        assertEquals(3.0, numberMetric.getValue(), 0.001);
        
        // Vérifier Average ((100 + 200 + 125) / 3 = 141.67)
        MetricInner avgMetric = findMetric("Average", "REFUND");
        assertEquals(141.67, avgMetric.getValue(), 0.1);
        
        // Vérifier Max (200)
        MetricInner maxMetric = findMetric("Max", "REFUND");
        assertEquals(200.0, maxMetric.getValue(), 0.001);
        
        // Vérifier Min (100)
        MetricInner minMetric = findMetric("Min", "REFUND");
        assertEquals(100.0, minMetric.getValue(), 0.001);
    }

    @Test
    void testMultipleOperationTypes() {
        // Collecte pour PAYMENT
        MetricsUtils.collectAndAccumulateMetrics(metricsDto, null, "PAYMENT", "VISA", null, 50);
        MetricsUtils.collectAndAccumulateMetrics(metricsDto, null, "PAYMENT", "VISA", null, 150);
        
        // Collecte pour REFUND
        MetricsUtils.collectAndAccumulateMetrics(metricsDto, null, "REFUND", "VISA", null, 75);
        
        assertEquals(8, metricsDto.getMetrics().size()); // 4 métriques × 2 types d'opération
        
        // Vérifier PAYMENT metrics
        assertEquals(2.0, findMetric("Number", "PAYMENT").getValue(), 0.001);
        assertEquals(100.0, findMetric("Average", "PAYMENT").getValue(), 0.001); // (50 + 150) / 2
        assertEquals(150.0, findMetric("Max", "PAYMENT").getValue(), 0.001);
        assertEquals(50.0, findMetric("Min", "PAYMENT").getValue(), 0.001);
        
        // Vérifier REFUND metrics
        assertEquals(1.0, findMetric("Number", "REFUND").getValue(), 0.001);
        assertEquals(75.0, findMetric("Average", "REFUND").getValue(), 0.001);
        assertEquals(75.0, findMetric("Max", "REFUND").getValue(), 0.001);
        assertEquals(75.0, findMetric("Min", "REFUND").getValue(), 0.001);
    }

    private MetricInner findMetric(String name, String type) {
        return metricsDto.getMetrics().stream()
                .filter(metric -> name.equals(metric.getName()) && type.equals(metric.getType()))
                .findFirst()
                .orElse(null);
    }
}