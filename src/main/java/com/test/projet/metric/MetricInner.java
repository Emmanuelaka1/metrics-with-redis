package com.test.projet.metric;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MetricInner {
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("value")
    private double value;
    
    @JsonProperty("type")
    private String type;

    @JsonCreator
    public MetricInner(@JsonProperty("name") String name, 
                       @JsonProperty("value") double value, 
                       @JsonProperty("type") String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    // Constructeur par défaut pour Jackson
    public MetricInner() {
    }

    public String getName() {
        return name;
    }

    public MetricInner setName(String name) {
        this.name = name;
        return this;
    }

    public double getValue() {
        return value;
    }

    public MetricInner setValue(double value) {
        this.value = value;
        return this;
    }

    // Méthode pour obtenir la valeur formatée selon le type (pas sérialisée)
    @JsonIgnore
    public String getFormattedValue() {
        return switch (type != null ? type.toLowerCase() : "default") {
            case "timer", "time" -> String.format("%.2f ms", value);
            case "counter", "number" -> String.format("%.0f", value);
            case "gauge" -> String.format("%.2f", value);
            case "histogram" -> String.format("%.2f", value);
            default -> String.valueOf(value);
        };
    }

    public String getType() {
        return type;
    }

    public MetricInner setType(String type) {
        this.type = type;
        return this;
    }
}