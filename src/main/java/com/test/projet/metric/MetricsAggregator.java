
package com.test.projet.metric;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MetricsAggregator {

    private final StringRedisTemplate redis;

    public MetricsAggregator(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void record(String entity, String op, long durationMs, boolean error) {
        String key = MetricsKeys.redisKey(entity, op);
        BoundHashOperations<String, Object, Object> h = redis.boundHashOps(key);

        h.increment("count", 1);
        h.increment("sumMs", durationMs);
        h.put("lastMs", String.valueOf(durationMs));
        if (error) h.increment("errors", 1);

        updateMinMax(h, durationMs);
    }

    private void updateMinMax(BoundHashOperations<String, Object, Object> h, long v) {
        String max = (String) h.get("maxMs");
        if (max == null || v > Double.parseDouble(max)) h.put("maxMs", String.valueOf(v));
        String min = (String) h.get("minMs");
        if (min == null || v < Double.parseDouble(min)) h.put("minMs", String.valueOf(v));
    }

    public Map<String, Object> read(String entity, String op) {
        Map<Object, Object> raw = redis.opsForHash().entries(MetricsKeys.redisKey(entity, op));
        Map<String, Object> out = new LinkedHashMap<>();
        raw.forEach((k,v) -> out.put(k.toString(), v.toString()));
        long count = Long.parseLong((String) out.getOrDefault("count", "0"));
        double sum = Double.parseDouble((String) out.getOrDefault("sumMs", "0"));
        out.put("avgMs", count == 0 ? 0 : sum / count);
        return out;
    }
}
