package com.test.projet.metric;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Meter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/metrics")
public class MetricsExportController {

    @Autowired
    private MeterRegistry meterRegistry;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Export toutes les métriques au format JSON
     */
    @GetMapping("/export/json")
    public ResponseEntity<Map<String, Object>> exportMetricsAsJson() {
        Map<String, Object> exportData = new HashMap<>();
        exportData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        exportData.put("application", "metrics-with-redis-starter");
        
        // Métriques personnalisées depuis Redis
        Map<String, Object> customMetrics = new HashMap<>();
        try {
            Set<String> redisKeys = redisTemplate.keys("metrics:*");
            if (redisKeys != null) {
                for (String key : redisKeys) {
                    String value = redisTemplate.opsForValue().get(key);
                    if (value != null) {
                        try {
                            // Essayer de parser comme JSON
                            customMetrics.put(key, objectMapper.readTree(value));
                        } catch (Exception e) {
                            // Sinon, stocker comme string
                            customMetrics.put(key, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            customMetrics.put("error", "Failed to retrieve Redis metrics: " + e.getMessage());
        }
        exportData.put("customMetrics", customMetrics);
        
        // Métriques système
        Map<String, Object> systemMetrics = new HashMap<>();
        for (Meter meter : meterRegistry.getMeters()) {
            String name = meter.getId().getName();
            Map<String, String> tags = new HashMap<>();
            meter.getId().getTags().forEach(tag -> tags.put(tag.getKey(), tag.getValue()));
            
            Map<String, Object> meterData = new HashMap<>();
            meterData.put("type", meter.getId().getType().name());
            meterData.put("tags", tags);
            
            List<Map<String, Object>> measurements = new ArrayList<>();
            meter.measure().forEach(measurement -> {
                Map<String, Object> measurementData = new HashMap<>();
                measurementData.put("statistic", measurement.getStatistic().name());
                measurementData.put("value", measurement.getValue());
                measurements.add(measurementData);
            });
            meterData.put("measurements", measurements);
            
            systemMetrics.put(name, meterData);
        }
        exportData.put("systemMetrics", systemMetrics);
        
        return ResponseEntity.ok(exportData);
    }
    
    /**
     * Export les métriques au format CSV
     */
    @GetMapping("/export/csv")
    public ResponseEntity<String> exportMetricsAsCsv() {
        StringBuilder csv = new StringBuilder();
        csv.append("timestamp,metric_name,metric_type,tag_key,tag_value,statistic,value\n");
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        for (Meter meter : meterRegistry.getMeters()) {
            String name = meter.getId().getName();
            String type = meter.getId().getType().name();
            
            // Pour chaque tag
            if (meter.getId().getTags().isEmpty()) {
                // Pas de tags
                meter.measure().forEach(measurement -> {
                    csv.append(String.format("%s,%s,%s,,,%s,%.6f\n",
                        timestamp, name, type,
                        measurement.getStatistic().name(),
                        measurement.getValue()));
                });
            } else {
                meter.getId().getTags().forEach(tag -> {
                    meter.measure().forEach(measurement -> {
                        csv.append(String.format("%s,%s,%s,%s,%s,%s,%.6f\n",
                            timestamp, name, type,
                            tag.getKey(), tag.getValue(),
                            measurement.getStatistic().name(),
                            measurement.getValue()));
                    });
                });
            }
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/csv");
        headers.add("Content-Disposition", "attachment; filename=metrics_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
        
        return ResponseEntity.ok().headers(headers).body(csv.toString());
    }
    
    /**
     * Export les métriques depuis Redis
     */
    @GetMapping("/export/redis")
    public ResponseEntity<Map<String, Object>> exportRedisMetrics() {
        Map<String, Object> redisData = new HashMap<>();
        redisData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        try {
            Set<String> keys = redisTemplate.keys("metrics:*");
            Map<String, Object> metrics = new HashMap<>();
            
            if (keys != null) {
                for (String key : keys) {
                    String value = redisTemplate.opsForValue().get(key);
                    if (value != null) {
                        try {
                            // Essayer de parser comme JSON
                            metrics.put(key, objectMapper.readTree(value));
                        } catch (Exception e) {
                            // Sinon, stocker comme string
                            metrics.put(key, value);
                        }
                    }
                }
            }
            
            redisData.put("metrics", metrics);
            redisData.put("totalKeys", keys != null ? keys.size() : 0);
        } catch (Exception e) {
            redisData.put("error", "Failed to retrieve Redis data: " + e.getMessage());
        }
        
        return ResponseEntity.ok(redisData);
    }
    
    /**
     * Export des métriques CRUD personnalisées
     */
    @GetMapping("/export/crud")
    public ResponseEntity<Map<String, Object>> exportCrudMetrics(@RequestParam(defaultValue = "all") String entity) {
        Map<String, Object> crudData = new HashMap<>();
        crudData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        try {
            String pattern = entity.equals("all") ? "metrics:*" : "metrics:" + entity + "*";
            Set<String> keys = redisTemplate.keys(pattern);
            
            Map<String, Object> metrics = new HashMap<>();
            if (keys != null) {
                for (String key : keys) {
                    String value = redisTemplate.opsForValue().get(key);
                    if (value != null) {
                        try {
                            metrics.put(key, objectMapper.readTree(value));
                        } catch (Exception e) {
                            metrics.put(key, value);
                        }
                    }
                }
            }
            
            crudData.put("entity", entity);
            crudData.put("metrics", metrics);
            crudData.put("totalKeys", keys != null ? keys.size() : 0);
        } catch (Exception e) {
            crudData.put("error", "Failed to retrieve CRUD metrics: " + e.getMessage());
        }
        
        return ResponseEntity.ok(crudData);
    }
    
    /**
     * Endpoint pour sauvegarder les métriques dans un fichier
     */
    @PostMapping("/export/save")
    public ResponseEntity<Map<String, String>> saveMetricsToFile(@RequestParam String format, 
                                                                  @RequestParam(defaultValue = "") String filename) {
        Map<String, String> response = new HashMap<>();
        
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String actualFilename = filename.isEmpty() ? "metrics_export_" + timestamp : filename;
            
            if (!actualFilename.contains(".")) {
                actualFilename += "." + format.toLowerCase();
            }
            
            response.put("status", "success");
            response.put("message", "Export endpoint created. Use GET /api/metrics/export/" + format.toLowerCase() + " to download");
            response.put("suggestedFilename", actualFilename);
            response.put("timestamp", timestamp);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to prepare export: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}