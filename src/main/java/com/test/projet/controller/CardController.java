package com.test.projet.controller;

import com.test.projet.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour tester l'annotation @CardMetrics
 * 
 * @author Generated
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/cards")
@Tag(name = "Card Management", description = "API pour tester la collecte automatique de métriques avec @CardMetrics")
public class CardController {

    @Autowired
    private CardService cardService;

    @PostMapping("/create")
    @Operation(summary = "Créer une carte", description = "Crée une nouvelle carte et collecte automatiquement les métriques")
    public ResponseEntity<String> createCard(
            @Parameter(description = "Numéro de carte") @RequestParam String cardNumber,
            @Parameter(description = "Type de carte") @RequestParam(defaultValue = "STANDARD") String cardType) {
        
        String result = cardService.createCard(cardNumber, cardType);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/update/{cardNumber}")
    @Operation(summary = "Mettre à jour une carte", description = "Met à jour une carte VISA et collecte les métriques avec typeCarte personnalisé")
    public ResponseEntity<String> updateCard(
            @Parameter(description = "Numéro de carte") @PathVariable String cardNumber,
            @Parameter(description = "Nouvelles données") @RequestBody String newData) {
        
        String result = cardService.updateCard(cardNumber, newData);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{cardNumber}")
    @Operation(summary = "Supprimer une carte", description = "Supprime une carte MasterCard et collecte les métriques même en cas d'exception")
    public ResponseEntity<String> deleteCard(
            @Parameter(description = "Numéro de carte") @PathVariable String cardNumber) {
        
        try {
            cardService.deleteCard(cardNumber);
            return ResponseEntity.ok("Carte supprimée avec succès: " + cardNumber);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur lors de la suppression: " + e.getMessage());
        }
    }

    @GetMapping("/validate/{cardNumber}")
    @Operation(summary = "Valider une carte", description = "Valide une carte et collecte les métriques")
    public ResponseEntity<String> validateCard(
            @Parameter(description = "Numéro de carte") @PathVariable String cardNumber) {
        
        boolean isValid = cardService.validateCard(cardNumber);
        return ResponseEntity.ok("Carte " + cardNumber + " est " + (isValid ? "valide" : "invalide"));
    }

    @PostMapping("/test-batch")
    @Operation(summary = "Test en lot", description = "Execute plusieurs opérations pour tester la collecte de métriques")
    public ResponseEntity<String> testBatch(@RequestParam(defaultValue = "5") int count) {
        StringBuilder result = new StringBuilder("Exécution de " + count + " opérations:\n");
        
        for (int i = 1; i <= count; i++) {
            String cardNumber = "CARD-" + String.format("%03d", i);
            
            // Créer
            cardService.createCard(cardNumber, "TEST");
            result.append("- Carte ").append(cardNumber).append(" créée\n");
            
            // Mettre à jour
            cardService.updateCard(cardNumber, "Updated data " + i);
            result.append("- Carte ").append(cardNumber).append(" mise à jour\n");
            
            // Valider
            boolean valid = cardService.validateCard(cardNumber);
            result.append("- Carte ").append(cardNumber).append(" validée: ").append(valid).append("\n");
            
            // Supprimer (peut échouer)
            try {
                cardService.deleteCard(cardNumber);
                result.append("- Carte ").append(cardNumber).append(" supprimée\n");
            } catch (Exception e) {
                result.append("- Échec suppression carte ").append(cardNumber).append(": ").append(e.getMessage()).append("\n");
            }
        }
        
        return ResponseEntity.ok(result.toString());
    }
}