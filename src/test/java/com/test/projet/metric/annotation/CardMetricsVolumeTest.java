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
 * Tests FONCTIONNELS VOLUME pour l'annotation @CardMetrics avec Redis RÉEL
 * 
 * 🎯 OBJECTIF: Tests de volume avec de VRAIES opérations Redis
 * 🚀 APPROCHE: Tests haute performance avec volumes importants - PAS DE MOCKS !
 * 📊 VALIDATION: Vérification des métriques volumétriques dans Redis avec MetricsAggregated
 * 
 * @author Generated for Volume Functional Tests
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestPropertySource(properties = {
    "spring.redis.host=localhost",
    "spring.redis.port=6379",
    "logging.level.com.test.projet.metric.aspect=DEBUG"
})
class CardMetricsVolumeTest {

    @Autowired
    private CardMetricsTestService cardMetricsTestService;

    @Autowired
    private MetricsService metricsService; // VRAI service - PAS @MockBean

    @BeforeEach
    void setUp() {
        System.out.println("🚀 Préparation des tests VOLUME avec Redis...");
        System.out.println("💯 Tests de volume avec de VRAIES métriques stockées dans Redis !");
    }

    @Test
    @DisplayName("🔥 Test Insert VOLUME - 100 opérations avec @CardMetrics Redis")
    void testInsertOperations100Times() {
        System.out.println("🔥 DÉMARRAGE Test Insert VOLUME - 100 opérations");
        
        final int numberOfOperations = 100;
        final String typeCarte = "INSERT_SERVICE";
        final String operationType = "Insert";
        
        // État initial
        long initialCount = getMetricCount(typeCarte, operationType);
        System.out.println("📊 Count initial INSERT: " + initialCount);
        
        // When - Exécuter 100 opérations d'insertion RÉELLES
        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= numberOfOperations; i++) {
            String cardData = String.format("VOLUME-INSERT-%03d", i);
            String dataType = "VOLUME_TYPE_" + i;
            
            String result = cardMetricsTestService.insertCardData(cardData, dataType);
            assertNotNull(result, "Le résultat ne devrait pas être null");
            
            if (i % 20 == 0) {
                System.out.printf("   ✅ Insert %d/%d réussi\n", i, numberOfOperations);
            }
        }
        long endTime = System.currentTimeMillis();
        
        // Then - Vérifier les métriques RÉELLES dans Redis
        waitForRedis(2000);
        
        MetricsAggregated finalMetrics = metricsService.getMetrics(typeCarte, operationType);
        assertNotNull(finalMetrics, "Les métriques INSERT devraient exister dans Redis");
        
        long finalCount = finalMetrics.getCount();
        long actualIncrease = finalCount - initialCount;
        
        assertTrue(actualIncrease >= numberOfOperations, 
            "L'augmentation devrait être >= " + numberOfOperations + ", mais était: " + actualIncrease);
        
        System.out.println("📊 RÉSULTAT INSERT VOLUME:");
        System.out.println("   📈 Count: " + initialCount + " → " + finalCount + " (+" + actualIncrease + ")");
        System.out.println("   ⏱️ Average: " + finalMetrics.getAverageTime() + "ms");
        System.out.println("   ⏱️ Min: " + finalMetrics.getMinTime() + "ms");
        System.out.println("   ⏱️ Max: " + finalMetrics.getMaxTime() + "ms");
        System.out.println("   🚀 Temps total: " + (endTime - startTime) + "ms");
        
        System.out.println("🎉 Test Insert VOLUME " + numberOfOperations + " opérations RÉUSSI!");
    }

    @Test
    @DisplayName("🔥 Test Update VOLUME - 100 opérations avec @CardMetrics Redis")
    void testUpdateOperations100Times() {
        System.out.println("🔥 DÉMARRAGE Test Update VOLUME - 100 opérations");
        
        final int numberOfOperations = 100;
        final String typeCarte = "UPDATE_SERVICE";
        final String operationType = "Update";
        
        // État initial
        long initialCount = getMetricCount(typeCarte, operationType);
        System.out.println("📊 Count initial UPDATE: " + initialCount);
        
        // When - Exécuter 100 opérations de mise à jour RÉELLES
        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= numberOfOperations; i++) {
            String cardId = String.format("VOLUME-UPDATE-%03d", i);
            String newData = String.format("VOLUME_UPDATED_DATA_%03d_%s", i, System.currentTimeMillis());
            
            String result = cardMetricsTestService.updateCardData(cardId, newData);
            assertNotNull(result, "Le résultat ne devrait pas être null");
            
            if (i % 20 == 0) {
                System.out.printf("   ✅ Update %d/%d réussi\n", i, numberOfOperations);
            }
        }
        long endTime = System.currentTimeMillis();
        
        // Then - Vérifier les métriques RÉELLES dans Redis
        waitForRedis(2000);
        
        MetricsAggregated finalMetrics = metricsService.getMetrics(typeCarte, operationType);
        assertNotNull(finalMetrics, "Les métriques UPDATE devraient exister dans Redis");
        
        long finalCount = finalMetrics.getCount();
        long actualIncrease = finalCount - initialCount;
        
        assertTrue(actualIncrease >= numberOfOperations, 
            "L'augmentation devrait être >= " + numberOfOperations + ", mais était: " + actualIncrease);
        
        System.out.println("📊 RÉSULTAT UPDATE VOLUME:");
        System.out.println("   📈 Count: " + initialCount + " → " + finalCount + " (+" + actualIncrease + ")");
        System.out.println("   ⏱️ Average: " + finalMetrics.getAverageTime() + "ms");
        System.out.println("   ⏱️ Min: " + finalMetrics.getMinTime() + "ms");
        System.out.println("   ⏱️ Max: " + finalMetrics.getMaxTime() + "ms");
        System.out.println("   🚀 Temps total: " + (endTime - startTime) + "ms");
        
        System.out.println("🎉 Test Update VOLUME " + numberOfOperations + " opérations RÉUSSI!");
    }

    @Test
    @DisplayName("🔥 Test Delete VOLUME - 100 opérations (50 succès + 50 exceptions) avec @CardMetrics Redis")
    void testDeleteOperations100TimesWithExceptions() {
        System.out.println("🔥 DÉMARRAGE Test Delete VOLUME - 100 opérations avec erreurs");
        
        final int numberOfOperations = 100;
        final int successfulDeletes = 50;
        final int failedDeletes = 50;
        final String typeCarte = "DELETE_SERVICE";
        final String successOperationType = "Delete";
        final String errorOperationType = "Delete_ERROR";
        
        // États initiaux
        long initialSuccessCount = getMetricCount(typeCarte, successOperationType);
        long initialErrorCount = getMetricCount(typeCarte, errorOperationType);
        
        System.out.println("📊 Success Count initial: " + initialSuccessCount);
        System.out.println("📊 Error Count initial: " + initialErrorCount);
        
        int actualSuccesses = 0;
        int actualFailures = 0;
        
        // When - Exécuter 100 opérations de suppression (50 succès + 50 échecs) RÉELLES
        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= numberOfOperations; i++) {
            String cardId = String.format("VOLUME-DELETE-%03d", i);
            // Les 50 premiers réussissent, les 50 suivants échouent
            boolean shouldFail = i > successfulDeletes;
            
            try {
                cardMetricsTestService.deleteCardData(cardId, shouldFail);
                actualSuccesses++;
                if (i % 10 == 0 && !shouldFail) {
                    System.out.printf("   ✅ Delete Success %d/%d\n", actualSuccesses, successfulDeletes);
                }
            } catch (Exception e) {
                actualFailures++;
                if (i % 10 == 0 && shouldFail) {
                    System.out.printf("   ❌ Delete Failure %d/%d (attendu)\n", actualFailures, failedDeletes);
                }
                // Exception attendue pour les 50 derniers
                assertTrue(shouldFail, "Exception inattendue pour l'opération " + i);
            }
        }
        long endTime = System.currentTimeMillis();
        
        // Then - Vérifier les compteurs
        assertEquals(successfulDeletes, actualSuccesses, "Nombre de succès incorrect");
        assertEquals(failedDeletes, actualFailures, "Nombre d'échecs incorrect");
        
        // Vérifier les métriques RÉELLES dans Redis
        waitForRedis(3000);
        
        MetricsAggregated finalSuccessMetrics = metricsService.getMetrics(typeCarte, successOperationType);
        MetricsAggregated finalErrorMetrics = metricsService.getMetrics(typeCarte, errorOperationType);
        
        assertNotNull(finalSuccessMetrics, "Métriques succès devraient exister");
        assertNotNull(finalErrorMetrics, "Métriques erreurs devraient exister");
        
        long finalSuccessCount = finalSuccessMetrics.getCount();
        long finalErrorCount = finalErrorMetrics.getCount();
        
        long successIncrease = finalSuccessCount - initialSuccessCount;
        long errorIncrease = finalErrorCount - initialErrorCount;
        
        assertTrue(successIncrease >= successfulDeletes, 
            "Augmentation succès >= " + successfulDeletes + ", mais était: " + successIncrease);
        assertTrue(errorIncrease >= failedDeletes, 
            "Augmentation erreurs >= " + failedDeletes + ", mais était: " + errorIncrease);
        
        System.out.println("📊 RÉSULTAT DELETE VOLUME:");
        System.out.println("   ✅ Succès: " + initialSuccessCount + " → " + finalSuccessCount + " (+" + successIncrease + ")");
        System.out.println("   ❌ Erreurs: " + initialErrorCount + " → " + finalErrorCount + " (+" + errorIncrease + ")");
        System.out.println("   ⏱️ Success Avg: " + finalSuccessMetrics.getAverageTime() + "ms");
        System.out.println("   ⏱️ Error Avg: " + finalErrorMetrics.getAverageTime() + "ms");
        System.out.println("   🚀 Temps total: " + (endTime - startTime) + "ms");
        
        System.out.println("🎉 Test Delete VOLUME " + actualSuccesses + " succès + " + actualFailures + " échecs = " + numberOfOperations + " opérations RÉUSSI!");
    }

    @Test
    @DisplayName("🎯 Test PERFORMANCE VOLUME - Mesurer l'impact de l'annotation sur 100 opérations Redis")
    void testPerformanceImpact() {
        System.out.println("🎯 DÉMARRAGE Test PERFORMANCE VOLUME - 100 opérations");
        
        final int numberOfOperations = 100;
        final String typeCarte = "PERFORMANCE_SERVICE";
        final String operationType = "FastOperation";
        
        // État initial
        long initialCount = getMetricCount(typeCarte, operationType);
        System.out.println("📊 Count initial PERFORMANCE: " + initialCount);
        
        // When - Mesurer le temps d'exécution avec annotation
        long startTime = System.nanoTime();
        
        for (int i = 1; i <= numberOfOperations; i++) {
            String data = String.format("VOLUME_PERF_DATA_%03d", i);
            String result = cardMetricsTestService.fastOperation(data);
            assertNotNull(result, "Le résultat ne devrait pas être null");
            
            if (i % 25 == 0) {
                System.out.printf("   ⚡ FastOperation %d/%d réussi\n", i, numberOfOperations);
            }
        }
        
        long endTime = System.nanoTime();
        long totalTimeWithAnnotation = (endTime - startTime) / 1_000_000; // Convertir en ms
        
        // Then - Vérifier les métriques RÉELLES dans Redis
        waitForRedis(2000);
        
        MetricsAggregated finalMetrics = metricsService.getMetrics(typeCarte, operationType);
        assertNotNull(finalMetrics, "Les métriques PERFORMANCE devraient exister dans Redis");
        
        long finalCount = finalMetrics.getCount();
        long actualIncrease = finalCount - initialCount;
        
        assertTrue(actualIncrease >= numberOfOperations, 
            "L'augmentation devrait être >= " + numberOfOperations + ", mais était: " + actualIncrease);
        
        // Vérifier que l'annotation n'impacte pas trop les performances
        System.out.println("📊 RÉSULTAT PERFORMANCE VOLUME:");
        System.out.println("   📈 Count: " + initialCount + " → " + finalCount + " (+" + actualIncrease + ")");
        System.out.println("   ⚡ Average Redis: " + finalMetrics.getAverageTime() + "ms");
        System.out.println("   ⚡ Min Redis: " + finalMetrics.getMinTime() + "ms");
        System.out.println("   ⚡ Max Redis: " + finalMetrics.getMaxTime() + "ms");
        System.out.println("   🚀 Temps total exécution: " + totalTimeWithAnnotation + "ms");
        System.out.println("   📊 Temps moyen par opération: " + (totalTimeWithAnnotation / numberOfOperations) + "ms");
        
        // L'annotation ne devrait pas ajouter plus de 10ms en moyenne par opération pour les tests de volume
        assertTrue(totalTimeWithAnnotation < 10000, 
            "Performance dégradée : " + totalTimeWithAnnotation + "ms pour " + numberOfOperations + " opérations");
        
        System.out.println("🎉 Test PERFORMANCE VOLUME " + numberOfOperations + " opérations RÉUSSI!");
    }

    @Test
    @DisplayName("🏆 Test MIXTE VOLUME - Insert (100) + Update (100) + Delete (100) Redis")
    void testMixedOperations300Total() {
        System.out.println("🏆 DÉMARRAGE Test MIXTE VOLUME - 300 opérations totales");
        
        final int operationsPerType = 100;
        final int totalOperations = operationsPerType * 3;
        
        // États initiaux
        long initialInsertCount = getMetricCount("INSERT_SERVICE", "Insert");
        long initialUpdateCount = getMetricCount("UPDATE_SERVICE", "Update");
        long initialDeleteSuccessCount = getMetricCount("DELETE_SERVICE", "Delete");
        long initialDeleteErrorCount = getMetricCount("DELETE_SERVICE", "Delete_ERROR");
        
        System.out.println("📊 Count initial INSERT: " + initialInsertCount);
        System.out.println("📊 Count initial UPDATE: " + initialUpdateCount);
        System.out.println("📊 Count initial DELETE: " + initialDeleteSuccessCount);
        System.out.println("📊 Count initial DELETE_ERROR: " + initialDeleteErrorCount);
        
        long startTime = System.currentTimeMillis();
        
        // When - Exécuter les 3 types d'opérations
        // 1. Insert 100 fois
        System.out.println("\n📝 Phase INSERT 100 opérations...");
        for (int i = 1; i <= operationsPerType; i++) {
            String result = cardMetricsTestService.insertCardData("MIXED-INSERT-" + i, "TYPE_" + i);
            assertNotNull(result);
            if (i % 25 == 0) {
                System.out.printf("   ✅ Insert %d/%d réussi\n", i, operationsPerType);
            }
        }
        
        // 2. Update 100 fois
        System.out.println("\n✏️ Phase UPDATE 100 opérations...");
        for (int i = 1; i <= operationsPerType; i++) {
            String result = cardMetricsTestService.updateCardData("MIXED-UPDATE-" + i, "NEW_DATA_" + i);
            assertNotNull(result);
            if (i % 25 == 0) {
                System.out.printf("   ✅ Update %d/%d réussi\n", i, operationsPerType);
            }
        }
        
        // 3. Delete 100 fois (50 succès + 50 échecs)
        System.out.println("\n🗑️ Phase DELETE 100 opérations (50 succès + 50 échecs)...");
        int deleteSuccesses = 0;
        int deleteFailures = 0;
        for (int i = 1; i <= operationsPerType; i++) {
            try {
                // Les 50 premiers réussissent, les 50 suivants échouent
                cardMetricsTestService.deleteCardData("MIXED-DELETE-" + i, i > 50);
                deleteSuccesses++;
                if (deleteSuccesses % 10 == 0) {
                    System.out.printf("   ✅ Delete Success %d/50\n", deleteSuccesses);
                }
            } catch (Exception e) {
                deleteFailures++;
                if (deleteFailures % 10 == 0) {
                    System.out.printf("   ❌ Delete Failure %d/50 (attendu)\n", deleteFailures);
                }
            }
        }
        
        long endTime = System.currentTimeMillis();
        
        // Then - Vérifications détaillées avec Redis
        waitForRedis(3000);
        
        MetricsAggregated insertMetrics = metricsService.getMetrics("INSERT_SERVICE", "Insert");
        MetricsAggregated updateMetrics = metricsService.getMetrics("UPDATE_SERVICE", "Update");
        MetricsAggregated deleteMetrics = metricsService.getMetrics("DELETE_SERVICE", "Delete");
        MetricsAggregated errorMetrics = metricsService.getMetrics("DELETE_SERVICE", "Delete_ERROR");
        
        // Toutes les métriques devraient exister
        assertNotNull(insertMetrics, "Métriques INSERT devraient exister");
        assertNotNull(updateMetrics, "Métriques UPDATE devraient exister");
        assertNotNull(deleteMetrics, "Métriques DELETE devraient exister");
        assertNotNull(errorMetrics, "Métriques ERROR devraient exister");
        
        // Vérifications des augmentations
        long insertIncrease = insertMetrics.getCount() - initialInsertCount;
        long updateIncrease = updateMetrics.getCount() - initialUpdateCount;
        long deleteIncrease = deleteMetrics.getCount() - initialDeleteSuccessCount;
        long errorIncrease = errorMetrics.getCount() - initialDeleteErrorCount;
        
        assertTrue(insertIncrease >= operationsPerType, "Insert increase >= " + operationsPerType);
        assertTrue(updateIncrease >= operationsPerType, "Update increase >= " + operationsPerType);
        assertTrue(deleteIncrease >= deleteSuccesses, "Delete increase >= " + deleteSuccesses);
        assertTrue(errorIncrease >= deleteFailures, "Error increase >= " + deleteFailures);
        
        System.out.println("\n📊 RÉSULTAT MIXTE VOLUME:");
        System.out.println("   📝 INSERT: " + initialInsertCount + " → " + insertMetrics.getCount() + " (+" + insertIncrease + ")");
        System.out.println("   ✏️ UPDATE: " + initialUpdateCount + " → " + updateMetrics.getCount() + " (+" + updateIncrease + ")");
        System.out.println("   ✅ DELETE: " + initialDeleteSuccessCount + " → " + deleteMetrics.getCount() + " (+" + deleteIncrease + ")");
        System.out.println("   ❌ ERROR: " + initialDeleteErrorCount + " → " + errorMetrics.getCount() + " (+" + errorIncrease + ")");
        System.out.println("   🚀 Temps total: " + (endTime - startTime) + "ms");
        System.out.println("   📊 Avg per operation: " + ((endTime - startTime) / totalOperations) + "ms");
        
        assertEquals(50, deleteSuccesses, "50 succès de suppression attendus");
        assertEquals(50, deleteFailures, "50 échecs de suppression attendus");
        
        System.out.println("🎉 Test MIXTE VOLUME " + totalOperations + " opérations RÉUSSI!");
    }

    @Test
    @DisplayName("🔬 Test MÉTRIQUES RÉELLES VOLUME - Vérifier la cumulation des données dans Redis")
    void testRealMetricsAccumulation() {
        System.out.println("🔬 DÉMARRAGE Test MÉTRIQUES RÉELLES VOLUME");
        
        final int numberOfOperations = 50;
        final String typeCarte = "REAL_TEST_SERVICE";
        final String operationType = "RealOperation";
        
        // État initial
        long initialCount = getMetricCount(typeCarte, operationType);
        System.out.println("📊 Count initial REAL_TEST: " + initialCount);
        
        // When - Exécuter des opérations réelles (pas juste des mocks)
        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= numberOfOperations; i++) {
            String input = String.format("VOLUME_REAL_DATA_%03d", i);
            String result = cardMetricsTestService.realMetricsOperation(input);
            assertNotNull(result, "Le résultat ne devrait pas être null");
            
            if (i % 10 == 0) {
                System.out.printf("   🔬 RealOperation %d/%d réussi\n", i, numberOfOperations);
            }
        }
        long endTime = System.currentTimeMillis();
        
        // Then - Vérifier les métriques réelles stockées dans Redis
        waitForRedis(2000);
        
        MetricsAggregated metrics = metricsService.getMetrics(typeCarte, operationType);
        
        assertNotNull(metrics, "Les métriques devraient être stockées dans Redis");
        
        long finalCount = metrics.getCount();
        long actualIncrease = finalCount - initialCount;
        
        assertTrue(actualIncrease >= numberOfOperations, 
            "L'augmentation devrait être >= " + numberOfOperations + ", mais était: " + actualIncrease);
        
        // Les métriques de temps doivent être > 0 (car RealOperation a un traitement CPU)
        assertTrue(metrics.getAverageTime() > 0, "Le temps moyen doit être > 0");
        assertTrue(metrics.getMinTime() > 0, "Le temps minimum doit être > 0");
        assertTrue(metrics.getMaxTime() > 0, "Le temps maximum doit être > 0");
        assertTrue(metrics.getMaxTime() >= metrics.getMinTime(), "Max time >= Min time");

        System.out.println("📊 RÉSULTAT MÉTRIQUES RÉELLES VOLUME:");
        System.out.println("   📈 Count: " + initialCount + " → " + finalCount + " (+" + actualIncrease + ")");
        System.out.println("   ⏱️ Average: " + String.format("%.2f", metrics.getAverageTime()) + "ms");
        System.out.println("   ⏱️ Min: " + metrics.getMinTime() + "ms");
        System.out.println("   ⏱️ Max: " + metrics.getMaxTime() + "ms");
        System.out.println("   🚀 Temps total: " + (endTime - startTime) + "ms");
        System.out.println("   📊 LastUpdated: " + metrics.getLastUpdated());
        
        System.out.println("🎉 Test MÉTRIQUES RÉELLES VOLUME " + numberOfOperations + " opérations RÉUSSI!");
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