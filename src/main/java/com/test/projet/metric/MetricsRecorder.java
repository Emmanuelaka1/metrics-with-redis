
package com.test.projet.metric;

import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Component
public class MetricsRecorder {

    private final MeterRegistry registry;
    private final MetricsAggregator aggregator;

    public MetricsRecorder(MeterRegistry registry, MetricsAggregator aggregator) {
        this.registry = registry;
        this.aggregator = aggregator;
    }

    public <T> T timeAndRecord(String entity, String op, Supplier<T> action) {
        Timer.Sample sample = Timer.start(registry);
        boolean error = false;
        try {
            return action.get();
        } catch (RuntimeException e) {
            error = true;
            throw e;
        } finally {
            long ms = (long) sample.stop(
                Timer.builder("db.operation.time")
                    .description("CRUD operation time")
                    .tag("entity", entity)
                    .tag("operation", op)
                    .register(registry)
            ) / 1_000_000;

            Counter.builder("db.operation.count")
                .tag("entity", entity)
                .tag("operation", op)
                .register(registry)
                .increment();

            if (error) {
                Counter.builder("db.operation.errors")
                    .tag("entity", entity)
                    .tag("operation", op)
                    .register(registry)
                    .increment();
            }
            aggregator.record(entity, op, ms, error);
        }
    }
}
