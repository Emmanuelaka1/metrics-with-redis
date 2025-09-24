package com.test.projet.metric;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MetricsDto {
    @JsonProperty("typeCarte")
    private String typeCarte;
    
    @JsonProperty("metrics")
    private List<MetricInner> metrics;

    @JsonCreator
    public MetricsDto(@JsonProperty("typeCarte") String typeCarte) {
        this.typeCarte = typeCarte;
        this.metrics = new ArrayList<>();
    }

    // Constructeur par défaut pour Jackson
    public MetricsDto() {
        this.metrics = new ArrayList<>();
    }

    public void addMetric(String name, double value, String type) {
        metrics.add(new MetricInner(name, value, type));
    }

    public void addMetric(MetricInner metricInner) {
        metrics.add(metricInner);
    }

    public String getTypeCarte() {
        return typeCarte;
    }

    public List<MetricInner> getMetrics() {
        return metrics;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MetricsDto {typeCarte:").append(typeCarte).append(", metrics:").append(metrics).append("}");
        return builder.toString();
    }
}