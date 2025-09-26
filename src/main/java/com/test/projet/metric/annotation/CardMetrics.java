package com.test.projet.metric.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour collecter automatiquement les métriques sur les méthodes
 * 
 * @author Generated
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CardMetrics {
    
    /**
     * Type d'opération pour les métriques
     * @return le type d'opération (ex: "Update", "Create", "Delete", etc.)
     */
    String value();
    
    /**
     * Type de carte pour les métriques
     * Si non spécifié, utilise le nom de la classe
     * @return le type de carte
     */
    String typeCarte() default "";
    
    /**
     * Indique si les métriques doivent être collectées même en cas d'exception
     * @return true pour collecter même en cas d'exception, false sinon
     */
    boolean collectOnException() default false;
}