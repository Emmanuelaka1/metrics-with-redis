package com.test.projet.metric;

import java.util.List;

public class BatchCollectionRequest {
    private List<MetricRequest> metrics;

    public BatchCollectionRequest() {}

    public BatchCollectionRequest(List<MetricRequest> metrics) {
        this.metrics = metrics;
    }

    public List<MetricRequest> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<MetricRequest> metrics) {
        this.metrics = metrics;
    }
}