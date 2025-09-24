package com.test.projet.metric;

import java.util.ArrayList;
import java.util.List;

public class BatchCollectionResponse {
    private List<String> successes;
    private List<BatchFailure> failures;

    public BatchCollectionResponse() {
        this.successes = new ArrayList<>();
        this.failures = new ArrayList<>();
    }

    public void addSuccess(String typeCarte) {
        successes.add(typeCarte);
    }

    public void addFailure(String typeCarte, String errorMessage) {
        failures.add(new BatchFailure(typeCarte, errorMessage));
    }

    public List<String> getSuccesses() {
        return successes;
    }

    public void setSuccesses(List<String> successes) {
        this.successes = successes;
    }

    public List<BatchFailure> getFailures() {
        return failures;
    }

    public void setFailures(List<BatchFailure> failures) {
        this.failures = failures;
    }

    public static class BatchFailure {
        private String typeCarte;
        private String errorMessage;

        public BatchFailure() {}

        public BatchFailure(String typeCarte, String errorMessage) {
            this.typeCarte = typeCarte;
            this.errorMessage = errorMessage;
        }

        public String getTypeCarte() {
            return typeCarte;
        }

        public void setTypeCarte(String typeCarte) {
            this.typeCarte = typeCarte;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}