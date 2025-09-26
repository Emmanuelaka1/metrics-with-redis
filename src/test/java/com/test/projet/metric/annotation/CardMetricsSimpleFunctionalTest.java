package com.test.projet.metric.annotation;

import com.test.projet.metric.MetricsService;
import com.test.projet.metric.MetricsAggregated;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests FONCTIONNELS RÉELS pour l'annotation @CardMetrics avec Redis
 * 
 * 🎯 OBJECTIF: Tests fonctionnels complets avec de VRAIES opérations Redis
 * 🚀 APPROCHE: Utilisation exclusive du VRAI MetricsService - PAS DE MOCKS !
 * 📊 VALIDATION: Vérification des métriques stockées dans Redis avec MetricsAggregated
 * 
 * @author Generated for Real Functional Tests  
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestPropertySource(properties = {
    "spring.redis.host=localhost",
    "spring.redis.port=6379",
    "logging.level.com.test.projet.metric.aspect=DEBUG"
})
class CardMetricsSimpleFunctionalTest {

    @Autowired
    private CardMetricsTestService cardMetricsTestService;

    @Autowired
    private MetricsService metricsService; // VRAI service - PAS @MockBean

    @BeforeEach
    void setUp() {
        System.out.println("🚀 Préparation test FONCTIONNEL RÉEL avec Redis...");
        System.out.println("💯 Ce test utilise de VRAIES métriques stockées dans Redis avec MetricsAggregated !");
    }

    @Test
    @DisplayName("🔥 Test FONCTIONNEL Simple - Insert avec Redis")
    void testFunctionalSimpleInsert() {
        System.out.println("🔥 DÉMARRAGE Test FONCTIONNEL Simple Insert");
        
        final String typeCarte = "INSERT_SERVICE"; // Doit correspondre à l'annotation dans CardMetricsTestService
        final String operation = "Insert";
        
        // Obtenir l'état initial (peut être null)
        long initialCount = 0;
        try {
            MetricsAggregated initialMetrics = metricsService.getMetrics(typeCarte, operation);
            if (initialMetrics != null) {
                initialCount = initialMetrics.getCount();
            }
        } catch (Exception e) {
            System.out.println("📊 Aucune métrique existante - première exécution");
        }
        
        System.out.println("📊 Count initial: " + initialCount);
        
        // When - Exécuter 5 opérations d'insertion RÉELLES
        for (int i = 1; i <= 5; i++) {
            String cardData = String.format("SIMPLE-INSERT-%03d", i);
            String dataType = "SIMPLE_TYPE";
            
            String result = cardMetricsTestService.insertCardData(cardData, dataType);
            assertNotNull(result, "Le résultat ne devrait pas être null");
            assertTrue(result.contains("insérées"), "Le résultat devrait contenir 'insérées'");
            
            System.out.printf("   ✅ Insert %d/5 réussi\n", i);
        }
        
        // Then - Vérifier les métriques RÉELLES dans Redis
        try {
            Thread.sleep(1000); // Attendre que Redis soit mis à jour
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        MetricsAggregated finalMetrics = metricsService.getMetrics(typeCarte, operation);
        assertNotNull(finalMetrics, "Les métriques finales devraient exister dans Redis");
        
        long finalCount = finalMetrics.getCount();
        long actualIncrease = finalCount - initialCount;
        
        // Vérifications avec MetricsAggregated
        assertTrue(actualIncrease >= 5, "L'augmentation devrait être >= 5, mais était: " + actualIncrease);
        assertTrue(finalMetrics.getAverageTime() > 0, "Le temps moyen devrait être > 0");
        assertTrue(finalMetrics.getMinTime() >= 0, "Le temps minimum devrait être >= 0");
        assertTrue(finalMetrics.getMaxTime() > 0, "Le temps maximum devrait être > 0");
        
        System.out.println("📊 RÉSULTAT MÉTRIQUES RÉELLES Redis:");
        System.out.println("   📈 Count initial: " + initialCount);
        System.out.println("   📈 Count final: " + finalCount);
        System.out.println("   📈 Augmentation: +" + actualIncrease);
        System.out.println("   ⏱️ Average: " + finalMetrics.getAverageTime() + "ms");
        System.out.println("   ⏱️ Min: " + finalMetrics.getMinTime() + "ms");
        System.out.println("   ⏱️ Max: " + finalMetrics.getMaxTime() + "ms");
        System.out.println("   📅 LastUpdated: " + finalMetrics.getLastUpdated());
        
        System.out.println("🎉 Test FONCTIONNEL Simple Insert RÉUSSI!");
    }

    @Test
    @DisplayName("🔥 Test FONCTIONNEL Simple - Update avec Redis")
    void testFunctionalSimpleUpdate() {
        System.out.println("🔥 DÉMARRAGE Test FONCTIONNEL Simple Update");
        
        final String typeCarte = "UPDATE_SERVICE"; // Doit correspondre à l'annotation dans CardMetricsTestService
        final String operation = "Update";
        
        // Obtenir l'état initial
        long initialCount = getMetricCount(typeCarte, operation);
        System.out.println("📊 Count initial: " + initialCount);
        
        // When - Exécuter 3 opérations d'update RÉELLES
        for (int i = 1; i <= 3; i++) {
            String cardId = String.format("SIMPLE-UPDATE-%03d", i);
            String newData = String.format("SIMPLE_UPDATED_%03d", i);
            
            String result = cardMetricsTestService.updateCardData(cardId, newData);
            assertNotNull(result, "Le résultat ne devrait pas être null");
            assertTrue(result.contains("mise à jour"), "Le résultat devrait contenir 'mise à jour'");
            
            System.out.printf("   ✅ Update %d/3 réussi\n", i);
        }
        
        // Then - Vérifier les métriques RÉELLES
        waitForRedis(1000);
        
        MetricsAggregated finalMetrics = metricsService.getMetrics(typeCarte, operation);
        assertNotNull(finalMetrics, "Les métriques finales devraient exister");
        
        long finalCount = finalMetrics.getCount();
        long actualIncrease = finalCount - initialCount;
        
        assertTrue(actualIncrease >= 3, "L'augmentation devrait être >= 3, mais était: " + actualIncrease);
        assertTrue(finalMetrics.getAverageTime() > 0, "Le temps moyen devrait être > 0");
        
        System.out.println("📊 RÉSULTAT MÉTRIQUES Update:");
        System.out.println("   📈 Count: " + initialCount + " → " + finalCount + " (+" + actualIncrease + ")");
        System.out.println("   ⏱️ Average: " + finalMetrics.getAverageTime() + "ms");
        
        System.out.println("🎉 Test FONCTIONNEL Simple Update RÉUSSI!");
    }

    @Test
    @DisplayName("🔥 Test FONCTIONNEL Simple - Delete avec Erreurs Redis")
    void testFunctionalSimpleDeleteWithErrors() {
        System.out.println("🔥 DÉMARRAGE Test FONCTIONNEL Simple Delete avec erreurs");
        
        final String typeCarte = "DELETE_SERVICE"; // Doit correspondre à l'annotation dans CardMetricsTestService
        final String successOperation = "Delete";
        final String errorOperation = "Delete_ERROR";
        
        // États initiaux
        long initialSuccessCount = getMetricCount(typeCarte, successOperation);
        long initialErrorCount = getMetricCount(typeCarte, errorOperation);
        
        System.out.println("📊 Success Count initial: " + initialSuccessCount);
        System.out.println("📊 Error Count initial: " + initialErrorCount);
        
        // When - Exécuter 2 succès + 2 échecs
        int actualSuccesses = 0;
        int actualFailures = 0;
        
        // 2 succès
        for (int i = 1; i <= 2; i++) {
            try {
                String cardId = String.format("SIMPLE-DELETE-SUCCESS-%03d", i);
                cardMetricsTestService.deleteCardData(cardId, false); // false = ne pas échouer
                actualSuccesses++;
                System.out.printf("   ✅ Delete Success %d/2\n", i);
            } catch (Exception e) {
                fail("Exception inattendue: " + e.getMessage());
            }
        }
        
        // 2 échecs
        for (int i = 1; i <= 2; i++) {
            try {
                String cardId = String.format("SIMPLE-DELETE-FAIL-%03d", i);
                cardMetricsTestService.deleteCardData(cardId, true); // true = échouer
                fail("Exception attendue non lancée");
            } catch (Exception e) {
                actualFailures++;
                System.out.printf("   ❌ Delete Failure %d/2 (attendu)\n", i);
                assertTrue(e.getMessage().contains("Échec forcé"), "Message d'erreur attendu");
            }
        }
        
        // Vérifications
        assertEquals(2, actualSuccesses, "2 succès attendus");
        assertEquals(2, actualFailures, "2 échecs attendus");
        
        // Then - Vérifier les métriques RÉELLES
        waitForRedis(1500);
        
        MetricsAggregated finalSuccessMetrics = metricsService.getMetrics(typeCarte, successOperation);
        MetricsAggregated finalErrorMetrics = metricsService.getMetrics(typeCarte, errorOperation);
        
        assertNotNull(finalSuccessMetrics, "Métriques succès devraient exister");
        assertNotNull(finalErrorMetrics, "Métriques erreurs devraient exister");
        
        long finalSuccessCount = finalSuccessMetrics.getCount();
        long finalErrorCount = finalErrorMetrics.getCount();
        
        long successIncrease = finalSuccessCount - initialSuccessCount;
        long errorIncrease = finalErrorCount - initialErrorCount;
        
        assertTrue(successIncrease >= 2, "Augmentation succès >= 2, mais était: " + successIncrease);
        assertTrue(errorIncrease >= 2, "Augmentation erreurs >= 2, mais était: " + errorIncrease);
        
        System.out.println("📊 RÉSULTAT MÉTRIQUES Delete:");
        System.out.println("   ✅ Succès: " + initialSuccessCount + " → " + finalSuccessCount + " (+" + successIncrease + ")");
        System.out.println("   ❌ Erreurs: " + initialErrorCount + " → " + finalErrorCount + " (+" + errorIncrease + ")");
        System.out.println("   ⏱️ Success Avg: " + finalSuccessMetrics.getAverageTime() + "ms");
        System.out.println("   ⏱️ Error Avg: " + finalErrorMetrics.getAverageTime() + "ms");
        
        System.out.println("🎉 Test FONCTIONNEL Simple Delete avec Erreurs RÉUSSI!");
    }

    @Test
    @DisplayName("🎯 Test FONCTIONNEL COMPLET - Toutes opérations")
    void testFunctionalComplete() {
        System.out.println("🎯 DÉMARRAGE Test FONCTIONNEL COMPLET");
        System.out.println("📋 Test: Insert + Update + Delete + Erreurs");
        
        // === INSERT ===
        System.out.println("\n📝 Phase INSERT...");
        for (int i = 1; i <= 2; i++) {
            String result = cardMetricsTestService.insertCardData("COMPLETE-INSERT-" + i, "COMPLETE_TYPE");
            assertNotNull(result);
        }
        
        // === UPDATE ===
        System.out.println("\n✏️ Phase UPDATE...");
        for (int i = 1; i <= 2; i++) {
            String result = cardMetricsTestService.updateCardData("COMPLETE-UPDATE-" + i, "COMPLETE_DATA");
            assertNotNull(result);
        }
        
        // === DELETE (succès) ===
        System.out.println("\n✅ Phase DELETE succès...");
        try {
            cardMetricsTestService.deleteCardData("COMPLETE-DELETE-SUCCESS", false);
        } catch (Exception e) {
            fail("Exception inattendue: " + e.getMessage());
        }
        
        // === DELETE (échec) ===
        System.out.println("\n❌ Phase DELETE échec...");
        try {
            cardMetricsTestService.deleteCardData("COMPLETE-DELETE-FAIL", true);
            fail("Exception attendue");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Échec forcé"));
        }
        
        // Vérification finale
        waitForRedis(2000);
        
        MetricsAggregated insertMetrics = metricsService.getMetrics("INSERT_SERVICE", "Insert");
        MetricsAggregated updateMetrics = metricsService.getMetrics("UPDATE_SERVICE", "Update");
        MetricsAggregated deleteMetrics = metricsService.getMetrics("DELETE_SERVICE", "Delete");
        MetricsAggregated errorMetrics = metricsService.getMetrics("DELETE_SERVICE", "Delete_ERROR");
        
        // Toutes les métriques devraient exister
        assertNotNull(insertMetrics, "Métriques Insert");
        assertNotNull(updateMetrics, "Métriques Update");
        assertNotNull(deleteMetrics, "Métriques Delete");
        assertNotNull(errorMetrics, "Métriques Error");
        
        System.out.println("\n📊 RÉSUMÉ FINAL:");
        System.out.println("   📝 INSERT: " + insertMetrics.getCount() + " ops, " + insertMetrics.getAverageTime() + "ms avg");
        System.out.println("   ✏️ UPDATE: " + updateMetrics.getCount() + " ops, " + updateMetrics.getAverageTime() + "ms avg");
        System.out.println("   ✅ DELETE: " + deleteMetrics.getCount() + " ops, " + deleteMetrics.getAverageTime() + "ms avg");
        System.out.println("   ❌ ERROR: " + errorMetrics.getCount() + " ops, " + errorMetrics.getAverageTime() + "ms avg");
        
        System.out.println("\n🏆 L'annotation @CardMetrics fonctionne parfaitement !");
        System.out.println("✅ Collecte automatique des métriques RÉELLE");
        System.out.println("✅ Gestion des exceptions avec collectOnException RÉELLE");
        System.out.println("✅ Stockage dans Redis avec MetricsAggregated VALIDÉ");
        
        System.out.println("🎯 Test FONCTIONNEL COMPLET RÉUSSI!");
    }

    // ========== MÉTHODES UTILITAIRES ==========
    
    private long getMetricCount(String typeCarte, String operation) {
        try {
            MetricsAggregated metrics = metricsService.getMetrics(typeCarte, operation);
            return metrics != null ? metrics.getCount() : 0;
        } catch (Exception e) {
            return 0; // Première fois - pas de métrique existante
        }
    }
    
    private void waitForRedis(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}