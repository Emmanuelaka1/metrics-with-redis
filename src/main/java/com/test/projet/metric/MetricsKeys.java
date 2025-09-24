
package com.test.projet.metric;

public final class MetricsKeys {
    private MetricsKeys() {}
    public static String redisKey(String entity, String op) {
        return "metrics:" + entity + ":" + op;
    }
}
