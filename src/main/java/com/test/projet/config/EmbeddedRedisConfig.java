
package com.test.projet.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;

@Configuration
@ConditionalOnProperty(name = "redis.embedded.enabled", havingValue = "true", matchIfMissing = true)
public class EmbeddedRedisConfig {

    private RedisServer redisServer;
    
    @Value("${redis.embedded.heapdir:}")
    private String heapdir;
    
    @Value("${redis.embedded.maxheap:}")
    private String maxheap;

    @PostConstruct
    public void startRedis() {
        try {
            int port = Integer.parseInt(System.getProperty("spring.redis.port", "6379"));
            
            RedisServerBuilder builder = RedisServer.builder()
                .port(port);
                
            // Ajout des paramètres Windows Redis si configurés
            if (!heapdir.isEmpty()) {
                builder.setting("heapdir " + heapdir);
            }
            if (!maxheap.isEmpty()) {
                builder.setting("maxheap " + maxheap);
            }
            
            redisServer = builder.build();
            redisServer.start();
            System.out.println("[EmbeddedRedis] Started on port " + port + 
                ((!heapdir.isEmpty()) ? " with heapdir=" + heapdir : "") +
                ((!maxheap.isEmpty()) ? " maxheap=" + maxheap : ""));
        } catch (Exception e) {
            System.err.println("[EmbeddedRedis] Failed to start: " + e.getMessage());
        }
    }

    @PreDestroy
    public void stopRedis() {
        try {
            if (redisServer != null && redisServer.isActive()) {
                redisServer.stop();
                System.out.println("[EmbeddedRedis] Stopped");
            }
        } catch (Exception ignored) {}
    }
}
