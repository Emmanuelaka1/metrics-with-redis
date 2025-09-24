package com.test.projet.metric;

public class MetricRequest {
    private String typeCarte;
    private String operationType;
    private long executionTime;

    public MetricRequest() {}

    public MetricRequest(String typeCarte, String operationType, long executionTime) {
        this.typeCarte = typeCarte;
        this.operationType = operationType;
        this.executionTime = executionTime;
    }

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

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
}