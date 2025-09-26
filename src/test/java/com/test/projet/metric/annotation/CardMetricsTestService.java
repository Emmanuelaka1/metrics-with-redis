package com.test.projet.metric.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service de test pour valider l'annotation @CardMetrics avec volumes de données
 * Utilisé par les tests unitaires pour tester Insert, Update, Delete
 * 
 * @author Generated
 * @since 1.0.0
 */
@Service
public class CardMetricsTestService {

    private static final Logger LOG = LoggerFactory.getLogger(CardMetricsTestService.class);

    /**
     * Opération d'insertion avec @CardMetrics
     * Type de carte: INSERT_SERVICE, Opération: Insert
     */
    @CardMetrics(value = "Insert", typeCarte = "INSERT_SERVICE")
    public String insertCardData(String cardData, String dataType) {
        LOG.debug("Insertion des données: {} de type: {}", cardData, dataType);

        // Simuler du traitement d'insertion
        try {
            // Temps variable entre 10-50ms pour simuler du traitement réel
            Thread.sleep(10 + (int) (Math.random() * 40));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simuler la validation des données
        if (cardData == null || cardData.isEmpty()) {
            throw new IllegalArgumentException("Les données de carte ne peuvent pas être vides");
        }

        String result = String.format("Données insérées: %s [Type: %s] à %s",
                        cardData, dataType, java.time.LocalDateTime.now());

        LOG.debug("Insertion réussie: {}", result);
        return result;
    }

    /**
     * Opération de mise à jour avec @CardMetrics
     * Type de carte: UPDATE_SERVICE, Opération: Update
     */
    @CardMetrics(value = "Update", typeCarte = "UPDATE_SERVICE")
    public String updateCardData(String cardId, String newData) {
        LOG.debug("Mise à jour de la carte: {} avec nouvelles données: {}", cardId, newData);

        // Simuler du traitement de mise à jour (généralement plus long qu'une insertion)
        try {
            // Temps variable entre 20-80ms
            Thread.sleep(20 + (int) (Math.random() * 60));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simuler la validation
        if (cardId == null || cardId.isEmpty()) {
            throw new IllegalArgumentException("L'ID de carte ne peut pas être vide");
        }

        if (newData == null) {
            throw new IllegalArgumentException("Les nouvelles données ne peuvent pas être nulles");
        }

        String result = String.format("Carte %s mise à jour avec: %s à %s",
                        cardId, newData, java.time.LocalDateTime.now());

        LOG.debug("Mise à jour réussie: {}", result);
        return result;
    }

    /**
     * Opération de suppression avec @CardMetrics et gestion d'exceptions
     * Type de carte: DELETE_SERVICE, Opération: Delete (ou Delete_ERROR)
     * collectOnException = true pour capturer les métriques même en cas d'erreur
     */
    @CardMetrics(value = "Delete", typeCarte = "DELETE_SERVICE", collectOnException = true)
    public void deleteCardData(String cardId, boolean forceFailure) throws Exception {
        LOG.debug("Suppression de la carte: {} (forceFailure: {})", cardId, forceFailure);

        // Simuler du traitement de suppression
        try {
            // Temps variable entre 15-60ms
            Thread.sleep(15 + (int) (Math.random() * 45));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Validation de base
        if (cardId == null || cardId.isEmpty()) {
            throw new IllegalArgumentException("L'ID de carte ne peut pas être vide");
        }

        // Forcer l'échec si demandé (pour tester les exceptions)
        if (forceFailure) {
            throw new Exception(String.format("Échec forcé de suppression pour la carte: %s à %s",
                            cardId, java.time.LocalDateTime.now()));
        }

        // Pas d'échec aléatoire pour les tests déterministes
        LOG.debug("Suppression réussie de la carte: {}", cardId);
    }

    /**
     * Opération rapide pour tester les performances
     * Type de carte: PERFORMANCE_SERVICE, Opération: FastOperation
     */
    @CardMetrics(value = "FastOperation", typeCarte = "PERFORMANCE_SERVICE")
    public String fastOperation(String data) {
        // Opération très rapide pour tester l'overhead de l'annotation
        // Pas de Thread.sleep() pour mesurer l'impact pur de l'annotation

        if (data == null) {
            return "NULL_DATA_PROCESSED";
        }

        return "PROCESSED_" + data.toUpperCase() + "_" + System.nanoTime();
    }

    /**
     * Opération avec métriques réelles (pas de mock) pour validation end-to-end
     * Type de carte: REAL_TEST_SERVICE, Opération: RealOperation
     */
    @CardMetrics(value = "RealOperation", typeCarte = "REAL_TEST_SERVICE")
    public String realMetricsOperation(String input) {
        LOG.debug("Opération réelle avec métriques: {}", input);

        // Simuler du traitement réel
        try {
            // Temps variable pour avoir des métriques intéressantes
            Thread.sleep(25 + (int) (Math.random() * 75)); // 25-100ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simuler du traitement CPU
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            result.append(input).append("_").append(i);
        }

        String finalResult = String.format("REAL_PROCESSED_%s_AT_%s_LENGTH_%d",
                        input,
                        System.currentTimeMillis(),
                        result.length());

        LOG.debug("Opération réelle terminée: {}", finalResult.substring(0, Math.min(50, finalResult.length())));
        return finalResult;
    }

    /**
     * Opération pour tester différents types de carte dans le même service
     * Type de carte: MULTI_CARD_TYPE, Opération: MultiCardOperation
     */
    @CardMetrics(value = "MultiCardOperation", typeCarte = "MULTI_CARD_TYPE")
    public String multiCardTypeOperation(String cardType, String data) {
        LOG.debug("Opération multi-carte pour type: {} avec données: {}", cardType, data);

        // Temps de traitement différent selon le type de carte
        try {
            switch (cardType.toUpperCase()) {
                case "VISA":
                    Thread.sleep(30 + (int) (Math.random() * 20)); // 30-50ms
                    break;
                case "MASTERCARD":
                    Thread.sleep(40 + (int) (Math.random() * 30)); // 40-70ms
                    break;
                case "AMEX":
                    Thread.sleep(50 + (int) (Math.random() * 50)); // 50-100ms
                    break;
                default:
                    Thread.sleep(20 + (int) (Math.random() * 10)); // 20-30ms
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String result = String.format("MULTI_CARD_PROCESSED: Type=%s, Data=%s, Time=%s",
                        cardType, data, java.time.LocalDateTime.now());

        LOG.debug("Opération multi-carte terminée: {}", result);
        return result;
    }

    /**
     * Opération batch pour tester des volumes importants
     * Type de carte: BATCH_SERVICE, Opération: BatchOperation
     */
    @CardMetrics(value = "BatchOperation", typeCarte = "BATCH_SERVICE")
    public int batchOperation(int batchSize) {
        LOG.debug("Opération batch avec taille: {}", batchSize);

        int processedCount = 0;

        // Simuler le traitement d'un batch
        try {
            // Temps proportionnel à la taille du batch
            Thread.sleep(Math.min(500, batchSize * 2)); // Max 500ms

            // Simuler le traitement de chaque élément
            for (int i = 0; i < batchSize; i++) {
                // Simuler un traitement très léger par élément
                if (i % 100 == 0) { // Petite pause tous les 100 éléments
                    Thread.sleep(1);
                }
                processedCount++;
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return processedCount; // Retourner le nombre traité avant interruption
        }

        LOG.debug("Batch traité avec succès: {} éléments", processedCount);
        return processedCount;
    }
}