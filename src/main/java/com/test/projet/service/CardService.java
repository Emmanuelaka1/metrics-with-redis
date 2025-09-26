package com.test.projet.service;

import com.test.projet.metric.annotation.CardMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service d'exemple pour démontrer l'utilisation de l'annotation @CardMetrics
 * 
 * @author Generated
 * @since 1.0.0
 */
@Service
public class CardService {

    private static final Logger logger = LoggerFactory.getLogger(CardService.class);

    /**
     * Méthode pour créer une carte - collecte automatiquement les métriques
     * Type de carte: "CardService", Opération: "Create"
     */
    @CardMetrics("Create")
    public String createCard(String cardNumber, String cardType) {
        logger.info("Création de la carte: {} de type: {}", cardNumber, cardType);
        
        // Simuler du traitement
        try {
            Thread.sleep(50 + (int)(Math.random() * 100)); // 50-150ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return "Carte créée: " + cardNumber;
    }

    /**
     * Méthode pour mettre à jour une carte - avec type de carte personnalisé
     * Type de carte: "VISA_CARD", Opération: "Update"
     */
    @CardMetrics(value = "Update", typeCarte = "VISA_CARD")
    public String updateCard(String cardNumber, String newData) {
        logger.info("Mise à jour de la carte: {} avec données: {}", cardNumber, newData);
        
        // Simuler du traitement
        try {
            Thread.sleep(100 + (int)(Math.random() * 200)); // 100-300ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return "Carte mise à jour: " + cardNumber;
    }

    /**
     * Méthode pour supprimer une carte - collecte même en cas d'exception
     * Type de carte: "MASTER_CARD", Opération: "Delete"
     */
    @CardMetrics(value = "Delete", typeCarte = "MASTER_CARD", collectOnException = true)
    public void deleteCard(String cardNumber) throws Exception {
        logger.info("Suppression de la carte: {}", cardNumber);
        
        // Simuler du traitement
        try {
            Thread.sleep(75 + (int)(Math.random() * 50)); // 75-125ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simuler une exception de temps en temps
        if (Math.random() < 0.2) { // 20% de chance d'exception
            throw new Exception("Erreur lors de la suppression de la carte: " + cardNumber);
        }
        
        logger.info("Carte supprimée avec succès: {}", cardNumber);
    }

    /**
     * Méthode pour valider une carte
     * Type de carte: "CardService", Opération: "Validate"
     */
    @CardMetrics("Validate")
    public boolean validateCard(String cardNumber) {
        logger.info("Validation de la carte: {}", cardNumber);
        
        // Simuler du traitement
        try {
            Thread.sleep(25 + (int)(Math.random() * 75)); // 25-100ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simuler une validation (vrai 80% du temps)
        boolean isValid = Math.random() > 0.2;
        logger.info("Résultat validation carte {}: {}", cardNumber, isValid);
        
        return isValid;
    }
}