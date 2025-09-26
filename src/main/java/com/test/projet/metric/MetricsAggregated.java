package com.test.projet.metric;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Classe représentant des métriques agrégées pour les tests fonctionnels réels avec Redis.
 * Cette classe contient les statistiques compilées d'exécution pour un type d'opération spécifique.
 * 
 * @author Generated for Real Functional Tests
 * @since 1.0.0
 */
public class MetricsAggregated {
    
    @JsonProperty("typeCarte")
    private String typeCarte;
    
    @JsonProperty("operationType")
    private String operationType;
    
    @JsonProperty("count")
    private long count;
    
    @JsonProperty("totalTime")
    private long totalTime;
    
    @JsonProperty("averageTime")
    private double averageTime;
    
    @JsonProperty("minTime")
    private long minTime;
    
    @JsonProperty("maxTime")
    private long maxTime;
    
    @JsonProperty("lastUpdated")
    private long lastUpdated;

    // Constructeur par défaut pour Jackson
    public MetricsAggregated() {
        this.count = 0;
        this.totalTime = 0;
        this.averageTime = 0.0;
        this.minTime = Long.MAX_VALUE;
        this.maxTime = 0;
        this.lastUpdated = System.currentTimeMillis();
    }

    @JsonCreator
    public MetricsAggregated(@JsonProperty("typeCarte") String typeCarte, 
                           @JsonProperty("operationType") String operationType) {
        this();
        this.typeCarte = typeCarte;
        this.operationType = operationType;
    }

    /**
     * Constructeur complet pour initialiser toutes les métriques
     */
    public MetricsAggregated(String typeCarte, String operationType, long count, 
                           long totalTime, double averageTime, long minTime, long maxTime) {
        this.typeCarte = typeCarte;
        this.operationType = operationType;
        this.count = count;
        this.totalTime = totalTime;
        this.averageTime = averageTime;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.lastUpdated = System.currentTimeMillis();
    }

    /**
     * Met à jour les métriques avec un nouveau temps d'exécution
     */
    public void updateMetrics(long executionTime) {
        this.count++;
        this.totalTime += executionTime;
        this.averageTime = (double) this.totalTime / this.count;
        
        if (executionTime < this.minTime) {
            this.minTime = executionTime;
        }
        
        if (executionTime > this.maxTime) {
            this.maxTime = executionTime;
        }
        
        this.lastUpdated = System.currentTimeMillis();
    }

    // Getters et Setters
    public String getTypeCarte() {
        return typeCarte;
    }

    public void setTypeCarte(String typeCarte) {
        this.typeCarte = typeCarte;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public double getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(double averageTime) {
        this.averageTime = averageTime;
    }

    public long getMinTime() {
        return minTime == Long.MAX_VALUE ? 0 : minTime;
    }

    public void setMinTime(long minTime) {
        this.minTime = minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return String.format("MetricsAggregated{typeCarte='%s', operationType='%s', count=%d, " +
                           "totalTime=%d, averageTime=%.2f, minTime=%d, maxTime=%d, lastUpdated=%d}",
                           typeCarte, operationType, count, totalTime, averageTime, 
                           getMinTime(), maxTime, lastUpdated);
    }
}