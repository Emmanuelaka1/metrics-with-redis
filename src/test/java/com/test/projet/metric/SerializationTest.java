package com.test.projet.metric;

import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.*;

public class SerializationTest {

    @Test
    public void testMetricInnerSerialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        
        // Créer un objet MetricInner
        MetricInner metric = new MetricInner("test.metric", 123.45, "Timer");
        
        // Sérialiser en JSON
        String json = mapper.writeValueAsString(metric);
        System.out.println("JSON sérialisé: " + json);
        
        // Désérialiser depuis JSON
        MetricInner deserializedMetric = mapper.readValue(json, MetricInner.class);
        
        // Vérifier que la désérialisation fonctionne
        assertEquals("test.metric", deserializedMetric.getName());
        assertEquals(123.45, deserializedMetric.getValue(), 0.001);
        assertEquals("Timer", deserializedMetric.getType());
    }
    
    @Test
    public void testMetricsDtoSerialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        
        // Créer un objet MetricsDto
        MetricsDto dto = new MetricsDto("VISA");
        dto.addMetric(new MetricInner("counter.transactions", 1250.0, "Number"));
        dto.addMetric(new MetricInner("timer.average", 1.25, "Timer"));
        
        // Sérialiser en JSON
        String json = mapper.writeValueAsString(dto);
        System.out.println("JSON MetricsDto sérialisé: " + json);
        
        // Désérialiser depuis JSON
        MetricsDto deserializedDto = mapper.readValue(json, MetricsDto.class);
        
        // Vérifier que la désérialisation fonctionne
        assertEquals("VISA", deserializedDto.getTypeCarte());
        assertEquals(2, deserializedDto.getMetrics().size());
    }
}