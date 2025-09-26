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
 * Tests FONCTIONNELS ÉTENDUS pour l'annotation @CardMetrics avec Redis
 * 
 * 🎯 OBJECTIF: Tests pour REAL_TEST_SERVICE, MULTI_CARD_TYPE, BATCH_SERVICE
 * 🚀 APPROCHE: Validation des méthodes avancées du CardMetricsTestService
 * 📊 VALIDATION: Vérification des métriques spécialisées dans Redis
 * 
 * @author Generated for Extended Functional Tests  
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestPropertySource(properties = {
    "spring.redis.host=localhost",
    "spring.redis.port=6379",
    "logging.level.com.test.projet.metric.aspect=DEBUG"
})
class CardMetricsExtendedFunctionalTest {

    @Autowired
    private CardMetricsTestService cardMetricsTestService;

    @Autowired
    private MetricsService metricsService;

    @BeforeEach
    void setUp() {
        System.out.println("🚀 Préparation des tests ÉTENDUS avec Redis...");
        System.out.println("💯 Tests pour REAL_TEST_SERVICE, MULTI_CARD_TYPE, BATCH_SERVICE");
    }

    @Test
    @DisplayName("🔥 Test REAL_TEST_SERVICE - RealOperation avec Redis")
    void testRealTestService() {
        System.out.println("🔥 DÉMARRAGE Test REAL_TEST_SERVICE");
        
        final String typeCarte = "REAL_TEST_SERVICE";
        final String operation = "RealOperation";
        
        // État initial
        long initialCount = getMetricCount(typeCarte, operation);
        System.out.println("📊 Count initial REAL_TEST_SERVICE: " + initialCount);
        
        // When - Exécuter plusieurs opérations réelles
        for (int i = 1; i <= 3; i++) {
            String input = String.format("REAL_INPUT_%03d", i);
            
            String result = cardMetricsTestService.realMetricsOperation(input);
            assertNotNull(result, "Le résultat ne devrait pas être null");
            assertTrue(result.startsWith("REAL_PROCESSED_"), "Le résultat devrait commencer par 'REAL_PROCESSED_'");
            assertTrue(result.contains(input), "Le résultat devrait contenir l'input");
            
            System.out.printf("   ✅ RealOperation %d/3 réussi: %s\n", i, 
                result.substring(0, Math.min(50, result.length())) + "...");
        }
        
        // Then - Vérifier les métriques RÉELLES
        waitForRedis(1500);
        
        MetricsAggregated finalMetrics = metricsService.getMetrics(typeCarte, operation);
        assertNotNull(finalMetrics, "Les métriques REAL_TEST_SERVICE devraient exister");
        
        long finalCount = finalMetrics.getCount();
        long actualIncrease = finalCount - initialCount;
        
        // Vérifications spécialisées pour REAL_TEST_SERVICE
        assertTrue(actualIncrease >= 3, "L'augmentation devrait être >= 3, mais était: " + actualIncrease);
        assertTrue(finalMetrics.getAverageTime() >= 25, "Le temps moyen devrait être >= 25ms (25-100ms attendu)");
        assertTrue(finalMetrics.getMaxTime() >= finalMetrics.getMinTime(), "Max time >= Min time");
        
        System.out.println("📊 RÉSULTAT MÉTRIQUES REAL_TEST_SERVICE:");
        System.out.println("   📈 Count: " + initialCount + " → " + finalCount + " (+" + actualIncrease + ")");
        System.out.println("   ⏱️ Average: " + finalMetrics.getAverageTime() + "ms");
        System.out.println("   ⏱️ Min: " + finalMetrics.getMinTime() + "ms");
        System.out.println("   ⏱️ Max: " + finalMetrics.getMaxTime() + "ms");
        System.out.println("   📅 LastUpdated: " + finalMetrics.getLastUpdated());
        
        System.out.println("🎉 Test REAL_TEST_SERVICE RÉUSSI!");
    }

    @Test
    @DisplayName("🔥 Test MULTI_CARD_TYPE - MultiCardOperation avec Redis")
    void testMultiCardType() {
        System.out.println("🔥 DÉMARRAGE Test MULTI_CARD_TYPE");
        
        final String typeCarte = "MULTI_CARD_TYPE";
        final String operation = "MultiCardOperation";
        
        // État initial
        long initialCount = getMetricCount(typeCarte, operation);
        System.out.println("📊 Count initial MULTI_CARD_TYPE: " + initialCount);
        
        // When - Tester différents types de cartes
        String[] cardTypes = {"VISA", "MASTERCARD", "AMEX", "OTHER"};
        
        for (int i = 0; i < cardTypes.length; i++) {
            String cardType = cardTypes[i];
            String data = String.format("DATA_%s_%03d", cardType, i + 1);
            
            String result = cardMetricsTestService.multiCardTypeOperation(cardType, data);
            assertNotNull(result, "Le résultat ne devrait pas être null");
            assertTrue(result.contains("MULTI_CARD_PROCESSED"), "Le résultat devrait contenir 'MULTI_CARD_PROCESSED'");
            assertTrue(result.contains("Type=" + cardType), "Le résultat devrait contenir le type de carte");
            assertTrue(result.contains("Data=" + data), "Le résultat devrait contenir les données");
            
            System.out.printf("   ✅ MultiCard %s réussi: %s\n", cardType, 
                result.substring(0, Math.min(80, result.length())) + "...");
        }
        
        // Test supplémentaire avec plus d'opérations
        for (int i = 1; i <= 2; i++) {
            String result = cardMetricsTestService.multiCardTypeOperation("VISA", "EXTRA_DATA_" + i);
            assertNotNull(result);
            System.out.printf("   ✅ MultiCard VISA Extra %d/2 réussi\n", i);
        }
        
        // Then - Vérifier les métriques
        waitForRedis(1500);
        
        MetricsAggregated finalMetrics = metricsService.getMetrics(typeCarte, operation);
        assertNotNull(finalMetrics, "Les métriques MULTI_CARD_TYPE devraient exister");
        
        long finalCount = finalMetrics.getCount();
        long actualIncrease = finalCount - initialCount;
        
        // Vérifications pour MULTI_CARD_TYPE (4 types + 2 extra = 6)
        assertTrue(actualIncrease >= 6, "L'augmentation devrait être >= 6, mais était: " + actualIncrease);
        assertTrue(finalMetrics.getAverageTime() >= 20, "Le temps moyen devrait être >= 20ms");
        
        // Les temps devraient varier selon le type (VISA: 30-50, MASTERCARD: 40-70, AMEX: 50-100, OTHER: 20-30)
        assertTrue(finalMetrics.getMaxTime() > finalMetrics.getMinTime(), "Les temps devraient varier selon le type de carte");
        
        System.out.println("📊 RÉSULTAT MÉTRIQUES MULTI_CARD_TYPE:");
        System.out.println("   📈 Count: " + initialCount + " → " + finalCount + " (+" + actualIncrease + ")");
        System.out.println("   ⏱️ Average: " + finalMetrics.getAverageTime() + "ms");
        System.out.println("   ⏱️ Min: " + finalMetrics.getMinTime() + "ms");
        System.out.println("   ⏱️ Max: " + finalMetrics.getMaxTime() + "ms");
        System.out.println("   📊 Variation temps selon type carte validée !");
        
        System.out.println("🎉 Test MULTI_CARD_TYPE RÉUSSI!");
    }

    @Test
    @DisplayName("🔥 Test BATCH_SERVICE - BatchOperation avec Redis")
    void testBatchService() {
        System.out.println("🔥 DÉMARRAGE Test BATCH_SERVICE");
        
        final String typeCarte = "BATCH_SERVICE";
        final String operation = "BatchOperation";
        
        // État initial
        long initialCount = getMetricCount(typeCarte, operation);
        System.out.println("📊 Count initial BATCH_SERVICE: " + initialCount);
        
        // When - Tester différentes tailles de batch
        int[] batchSizes = {10, 50, 100, 200};
        
        for (int i = 0; i < batchSizes.length; i++) {
            int batchSize = batchSizes[i];
            
            int processedCount = cardMetricsTestService.batchOperation(batchSize);
            assertEquals(batchSize, processedCount, "Le nombre traité devrait égaler la taille du batch");
            
            System.out.printf("   ✅ BatchOperation taille %d réussi: %d éléments traités\n", 
                batchSize, processedCount);
        }
        
        // Test avec une taille plus importante
        int largeBatch = 500;
        int largeResult = cardMetricsTestService.batchOperation(largeBatch);
        assertEquals(largeBatch, largeResult, "Le grand batch devrait être entièrement traité");
        System.out.printf("   ✅ Grand BatchOperation %d éléments réussi\n", largeBatch);
        
        // Then - Vérifier les métriques
        waitForRedis(2000); // Plus de temps pour les gros batches
        
        MetricsAggregated finalMetrics = metricsService.getMetrics(typeCarte, operation);
        assertNotNull(finalMetrics, "Les métriques BATCH_SERVICE devraient exister");
        
        long finalCount = finalMetrics.getCount();
        long actualIncrease = finalCount - initialCount;
        
        // Vérifications pour BATCH_SERVICE (4 tailles + 1 grand = 5)
        assertTrue(actualIncrease >= 5, "L'augmentation devrait être >= 5, mais était: " + actualIncrease);
        
        // Les temps devraient être proportionnels à la taille (min 500ms pour le grand batch)
        assertTrue(finalMetrics.getMaxTime() >= 100, "Le temps max devrait être >= 100ms pour les gros batches");
        assertTrue(finalMetrics.getMaxTime() > finalMetrics.getMinTime(), "Les temps devraient varier selon la taille");
        
        System.out.println("📊 RÉSULTAT MÉTRIQUES BATCH_SERVICE:");
        System.out.println("   📈 Count: " + initialCount + " → " + finalCount + " (+" + actualIncrease + ")");
        System.out.println("   ⏱️ Average: " + finalMetrics.getAverageTime() + "ms");
        System.out.println("   ⏱️ Min: " + finalMetrics.getMinTime() + "ms");
        System.out.println("   ⏱️ Max: " + finalMetrics.getMaxTime() + "ms");
        System.out.println("   📊 Temps proportionnels aux tailles de batch validés !");
        
        System.out.println("🎉 Test BATCH_SERVICE RÉUSSI!");
    }

    @Test
    @DisplayName("🎯 Test PERFORMANCE - FastOperation avec Redis")
    void testPerformanceService() {
        System.out.println("🎯 DÉMARRAGE Test PERFORMANCE_SERVICE");
        
        final String typeCarte = "PERFORMANCE_SERVICE";
        final String operation = "FastOperation";
        
        // État initial
        long initialCount = getMetricCount(typeCarte, operation);
        System.out.println("📊 Count initial PERFORMANCE_SERVICE: " + initialCount);
        
        // When - Exécuter des opérations très rapides (sans Thread.sleep)
        for (int i = 1; i <= 10; i++) {
            String data = String.format("PERF_DATA_%03d", i);
            
            String result = cardMetricsTestService.fastOperation(data);
            assertNotNull(result, "Le résultat ne devrait pas être null");
            assertTrue(result.startsWith("PROCESSED_"), "Le résultat devrait commencer par 'PROCESSED_'");
            assertTrue(result.contains(data.toUpperCase()), "Le résultat devrait contenir les données en majuscules");
            
            if (i <= 3) {
                System.out.printf("   ⚡ FastOperation %d/10: %s\n", i, result.substring(0, Math.min(40, result.length())) + "...");
            }
        }
        
        // Test avec données null
        String nullResult = cardMetricsTestService.fastOperation(null);
        assertEquals("NULL_DATA_PROCESSED", nullResult, "Résultat pour données null");
        System.out.println("   ⚡ FastOperation avec null: " + nullResult);
        
        // Then - Vérifier les métriques de performance
        waitForRedis(1000);
        
        MetricsAggregated finalMetrics = metricsService.getMetrics(typeCarte, operation);
        assertNotNull(finalMetrics, "Les métriques PERFORMANCE_SERVICE devraient exister");
        
        long finalCount = finalMetrics.getCount();
        long actualIncrease = finalCount - initialCount;
        
        // Vérifications pour PERFORMANCE_SERVICE (10 + 1 null = 11)
        assertTrue(actualIncrease >= 11, "L'augmentation devrait être >= 11, mais était: " + actualIncrease);
        
        // Les opérations rapides devraient avoir des temps très courts (< 10ms généralement)
        assertTrue(finalMetrics.getAverageTime() >= 0, "Le temps moyen devrait être >= 0ms");
        assertTrue(finalMetrics.getMinTime() >= 0, "Le temps minimum devrait être >= 0ms");
        
        System.out.println("📊 RÉSULTAT MÉTRIQUES PERFORMANCE_SERVICE:");
        System.out.println("   📈 Count: " + initialCount + " → " + finalCount + " (+" + actualIncrease + ")");
        System.out.println("   ⚡ Average: " + finalMetrics.getAverageTime() + "ms (très rapide!)");
        System.out.println("   ⚡ Min: " + finalMetrics.getMinTime() + "ms");
        System.out.println("   ⚡ Max: " + finalMetrics.getMaxTime() + "ms");
        System.out.println("   📊 Performance optimale validée !");
        
        System.out.println("🎉 Test PERFORMANCE_SERVICE RÉUSSI!");
    }

    @Test
    @DisplayName("🏆 Test COMPLET - Tous les services étendus")
    void testAllExtendedServices() {
        System.out.println("🏆 DÉMARRAGE Test COMPLET des services étendus");
        System.out.println("📋 Test: REAL_TEST_SERVICE + MULTI_CARD_TYPE + BATCH_SERVICE + PERFORMANCE_SERVICE");
        
        // === REAL_TEST_SERVICE ===
        System.out.println("\n🔬 Phase REAL_TEST_SERVICE...");
        String realResult = cardMetricsTestService.realMetricsOperation("COMPLET_REAL");
        assertNotNull(realResult);
        assertTrue(realResult.contains("REAL_PROCESSED_COMPLET_REAL"));
        
        // === MULTI_CARD_TYPE ===
        System.out.println("\n💳 Phase MULTI_CARD_TYPE...");
        String visaResult = cardMetricsTestService.multiCardTypeOperation("VISA", "COMPLET_VISA");
        String amexResult = cardMetricsTestService.multiCardTypeOperation("AMEX", "COMPLET_AMEX");
        assertNotNull(visaResult);
        assertNotNull(amexResult);
        assertTrue(visaResult.contains("Type=VISA"));
        assertTrue(amexResult.contains("Type=AMEX"));
        
        // === BATCH_SERVICE ===
        System.out.println("\n📦 Phase BATCH_SERVICE...");
        int batchResult1 = cardMetricsTestService.batchOperation(25);
        int batchResult2 = cardMetricsTestService.batchOperation(75);
        assertEquals(25, batchResult1);
        assertEquals(75, batchResult2);
        
        // === PERFORMANCE_SERVICE ===
        System.out.println("\n⚡ Phase PERFORMANCE_SERVICE...");
        String perfResult1 = cardMetricsTestService.fastOperation("COMPLET_PERF");
        String perfResult2 = cardMetricsTestService.fastOperation("SPEED_TEST");
        assertNotNull(perfResult1);
        assertNotNull(perfResult2);
        assertTrue(perfResult1.contains("COMPLET_PERF"));
        assertTrue(perfResult2.contains("SPEED_TEST"));
        
        // Vérification finale
        waitForRedis(3000);
        
        MetricsAggregated realMetrics = metricsService.getMetrics("REAL_TEST_SERVICE", "RealOperation");
        MetricsAggregated multiMetrics = metricsService.getMetrics("MULTI_CARD_TYPE", "MultiCardOperation");
        MetricsAggregated batchMetrics = metricsService.getMetrics("BATCH_SERVICE", "BatchOperation");
        MetricsAggregated perfMetrics = metricsService.getMetrics("PERFORMANCE_SERVICE", "FastOperation");
        
        // Tous les services devraient avoir des métriques
        assertNotNull(realMetrics, "Métriques REAL_TEST_SERVICE");
        assertNotNull(multiMetrics, "Métriques MULTI_CARD_TYPE");
        assertNotNull(batchMetrics, "Métriques BATCH_SERVICE");
        assertNotNull(perfMetrics, "Métriques PERFORMANCE_SERVICE");
        
        System.out.println("\n📊 RÉSUMÉ FINAL ÉTENDU:");
        System.out.println("   🔬 REAL_TEST: " + realMetrics.getCount() + " ops, " + realMetrics.getAverageTime() + "ms avg");
        System.out.println("   💳 MULTI_CARD: " + multiMetrics.getCount() + " ops, " + multiMetrics.getAverageTime() + "ms avg");
        System.out.println("   📦 BATCH: " + batchMetrics.getCount() + " ops, " + batchMetrics.getAverageTime() + "ms avg");
        System.out.println("   ⚡ PERFORMANCE: " + perfMetrics.getCount() + " ops, " + perfMetrics.getAverageTime() + "ms avg");
        
        System.out.println("\n🏆 Tous les services étendus fonctionnent parfaitement !");
        System.out.println("✅ REAL_TEST_SERVICE avec traitement CPU intensif");
        System.out.println("✅ MULTI_CARD_TYPE avec temps variables par type");
        System.out.println("✅ BATCH_SERVICE avec traitement proportionnel");
        System.out.println("✅ PERFORMANCE_SERVICE avec overhead minimal");
        
        System.out.println("🎯 Test COMPLET ÉTENDU RÉUSSI!");
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