package com.test.projet.metric.aspect;

import com.test.projet.metric.MetricsService;
import com.test.projet.metric.annotation.CardMetrics;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Aspect pour intercepter les méthodes annotées avec @CardMetrics
 * et collecter automatiquement les métriques d'exécution
 * 
 * @author Generated
 * @since 1.0.0
 */
@Aspect
@Component
public class CardMetricsAspect {

    private static final Logger logger = LoggerFactory.getLogger(CardMetricsAspect.class);

    @Autowired
    private MetricsService metricsService;

    /**
     * Intercepte toutes les méthodes annotées avec @CardMetrics
     * et collecte automatiquement les métriques d'exécution
     * 
     * @param joinPoint le point de jointure AspectJ
     * @param cardMetrics l'annotation CardMetrics
     * @return le résultat de l'exécution de la méthode
     * @throws Throwable si une erreur survient durant l'exécution
     */
    @Around("@annotation(cardMetrics)")
    public Object collectMetrics(ProceedingJoinPoint joinPoint, CardMetrics cardMetrics) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        // Déterminer le type de carte
        String typeCarte = cardMetrics.typeCarte().isEmpty() ? 
            className : cardMetrics.typeCarte();
        
        // Type d'opération depuis l'annotation
        String operationType = cardMetrics.value();
        
        logger.debug("Début de la collecte de métriques pour {}#{} - Type: {}, Opération: {}", 
                    className, methodName, typeCarte, operationType);
        
        try {
            // Exécuter la méthode originale
            Object result = joinPoint.proceed();
            
            // Calculer le temps d'exécution
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Collecter les métriques en cas de succès
            metricsService.collectAndStoreMetrics(typeCarte, operationType, executionTime);
            
            logger.debug("Métriques collectées avec succès pour {}#{} - Temps: {}ms", 
                        className, methodName, executionTime);
            
            return result;
            
        } catch (Throwable throwable) {
            // Calculer le temps d'exécution même en cas d'exception
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Collecter les métriques en cas d'exception si configuré
            if (cardMetrics.collectOnException()) {
                String exceptionOperationType = operationType + "_ERROR";
                metricsService.collectAndStoreMetrics(typeCarte, exceptionOperationType, executionTime);
                
                logger.debug("Métriques collectées pour exception dans {}#{} - Temps: {}ms, Type: {}", 
                            className, methodName, executionTime, exceptionOperationType);
            } else {
                logger.debug("Exception dans {}#{} - Temps: {}ms, métriques non collectées", 
                            className, methodName, executionTime);
            }
            
            // Re-lancer l'exception
            throw throwable;
        }
    }
}