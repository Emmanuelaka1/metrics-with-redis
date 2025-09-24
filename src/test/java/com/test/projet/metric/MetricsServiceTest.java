package com.test.projet.metric;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.core.instrument.MeterRegistry;

@ExtendWith(MockitoExtension.class)
class MetricsServiceTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private MetricsService metricsService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testCollectAndStoreMetrics_Success() {
        // Given
        String typeCarte = "VISA";
        String operationType = "PAYMENT";
        long executionTime = 1500L;

        // Créer une MetricsDto de test
        MetricsDto expectedMetrics = new MetricsDto(typeCarte);
        expectedMetrics.addMetric(new MetricInner("test.metric", 100.0, "Number"));

        try (MockedStatic<MetricsUtils> mockedUtils = mockStatic(MetricsUtils.class)) {
            mockedUtils.when(() -> MetricsUtils.collectAndAccumulateMetrics(
                any(MetricsDto.class), 
                eq(meterRegistry), 
                eq(operationType), 
                eq(typeCarte), 
                eq(redisTemplate), 
                eq(executionTime)
            )).thenAnswer(invocation -> {
                MetricsDto dto = invocation.getArgument(0);
                dto.addMetric(new MetricInner("test.metric", 100.0, "Number"));
                return null;
            });

            // When
            metricsService.collectAndStoreMetrics(typeCarte, operationType, executionTime);

            // Then
            verify(valueOperations).set(eq("metrics:" + typeCarte), anyString());
            mockedUtils.verify(() -> MetricsUtils.collectAndAccumulateMetrics(
                any(MetricsDto.class), 
                eq(meterRegistry), 
                eq(operationType), 
                eq(typeCarte), 
                eq(redisTemplate), 
                eq(executionTime)
            ));
        }
    }

    @Test
    void testCollectAndStoreMetrics_JsonSerializationError() {
        // Given
        String typeCarte = "VISA";
        String operationType = "PAYMENT";
        long executionTime = 1500L;

        try (MockedStatic<MetricsUtils> mockedUtils = mockStatic(MetricsUtils.class)) {
            mockedUtils.when(() -> MetricsUtils.collectAndAccumulateMetrics(
                any(MetricsDto.class), 
                any(MeterRegistry.class), 
                anyString(), 
                anyString(), 
                any(StringRedisTemplate.class), 
                anyLong()
            )).thenAnswer(invocation -> {
                MetricsDto dto = invocation.getArgument(0);
                // Créer un objet qui peut causer une erreur de sérialisation
                dto.addMetric(new MetricInner("error.metric", 0.0, "Error"));
                return null;
            });

            // When & Then - Should not throw exception, but handle it gracefully
            assertDoesNotThrow(() -> 
                metricsService.collectAndStoreMetrics(typeCarte, operationType, executionTime)
            );
        }
    }

    @Test
    void testGetMetricsFromRedis_Success() throws JsonProcessingException {
        // Given
        String typeCarte = "MASTERCARD";
        MetricsDto expectedMetrics = new MetricsDto(typeCarte);
        expectedMetrics.addMetric(new MetricInner("payment.count", 50.0, "Number"));
        expectedMetrics.addMetric(new MetricInner("payment.time", 2.5, "Timer"));

        String jsonData = objectMapper.writeValueAsString(expectedMetrics);
        when(valueOperations.get("metrics:" + typeCarte)).thenReturn(jsonData);

        // When
        MetricsDto result = metricsService.getMetricsFromRedis(typeCarte);

        // Then
        assertNotNull(result);
        assertEquals(typeCarte, result.getTypeCarte());
        assertEquals(2, result.getMetrics().size());
        
        // Vérifier les métriques
        assertTrue(result.getMetrics().stream()
            .anyMatch(m -> "payment.count".equals(m.getName())));
        assertTrue(result.getMetrics().stream()
            .anyMatch(m -> "payment.time".equals(m.getName())));
    }

    @Test
    void testGetMetricsFromRedis_NotFound() {
        // Given
        String typeCarte = "AMEX";
        when(valueOperations.get("metrics:" + typeCarte)).thenReturn(null);

        // When
        MetricsDto result = metricsService.getMetricsFromRedis(typeCarte);

        // Then
        assertNull(result);
    }

    @Test
    void testGetMetricsFromRedis_InvalidJson() {
        // Given
        String typeCarte = "VISA";
        String invalidJson = "{ invalid json }";
        when(valueOperations.get("metrics:" + typeCarte)).thenReturn(invalidJson);

        // When
        MetricsDto result = metricsService.getMetricsFromRedis(typeCarte);

        // Then
        assertNull(result);
    }

    @Test
    void testGetMetricsFromRedis_EmptyJson() {
        // Given
        String typeCarte = "VISA";
        when(valueOperations.get("metrics:" + typeCarte)).thenReturn("");

        // When
        MetricsDto result = metricsService.getMetricsFromRedis(typeCarte);

        // Then
        assertNull(result);
    }

    @Test
    void testCollectAndStoreMetrics_WithDifferentOperationTypes() {
        // Test avec différents types d'opération
        String[] operationTypes = {"PAYMENT", "REFUND", "AUTHORIZATION", "CAPTURE"};
        String[] carteTypes = {"VISA", "MASTERCARD", "AMEX", "DISCOVER"};

        try (MockedStatic<MetricsUtils> mockedUtils = mockStatic(MetricsUtils.class)) {
            for (String operationType : operationTypes) {
                for (String carteType : carteTypes) {
                    // When
                    metricsService.collectAndStoreMetrics(carteType, operationType, 1000L);

                    // Then
                    mockedUtils.verify(() -> MetricsUtils.collectAndAccumulateMetrics(
                        any(MetricsDto.class), 
                        eq(meterRegistry), 
                        eq(operationType), 
                        eq(carteType), 
                        eq(redisTemplate), 
                        eq(1000L)
                    ));
                }
            }
        }
    }

    @Test
    void testGetMetricsFromRedis_WithComplexMetrics() throws JsonProcessingException {
        // Given
        String typeCarte = "VISA";
        MetricsDto complexMetrics = new MetricsDto(typeCarte);
        
        // Ajouter différents types de métriques
        complexMetrics.addMetric(new MetricInner("counter.transactions", 1250.0, "Number"));
        complexMetrics.addMetric(new MetricInner("timer.average", 1.25, "Timer"));
        complexMetrics.addMetric(new MetricInner("gauge.memory", 512.0, "Gauge"));
        complexMetrics.addMetric(new MetricInner("histogram.response", 2.3, "Histogram"));

        String jsonData = objectMapper.writeValueAsString(complexMetrics);
        when(valueOperations.get("metrics:" + typeCarte)).thenReturn(jsonData);

        // When
        MetricsDto result = metricsService.getMetricsFromRedis(typeCarte);

        // Then
        assertNotNull(result);
        assertEquals(typeCarte, result.getTypeCarte());
        assertEquals(4, result.getMetrics().size());
        
        // Vérifier chaque type de métrique
        assertTrue(result.getMetrics().stream()
            .anyMatch(m -> "counter.transactions".equals(m.getName()) && "Number".equals(m.getType())));
        assertTrue(result.getMetrics().stream()
            .anyMatch(m -> "timer.average".equals(m.getName()) && "Timer".equals(m.getType())));
        assertTrue(result.getMetrics().stream()
            .anyMatch(m -> "gauge.memory".equals(m.getName()) && "Gauge".equals(m.getType())));
        assertTrue(result.getMetrics().stream()
            .anyMatch(m -> "histogram.response".equals(m.getName()) && "Histogram".equals(m.getType())));
    }
}