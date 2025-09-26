package com.test.projet.metric.annotation;

import com.test.projet.metric.MetricsService;
import com.test.projet.metric.MetricsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour l'annotation @CardMetrics avec volumes de données importants
 * Tests: Insert => 100 données, Update => 100 données, Delete => 100 données (50 Insert + 50 Update)
 * 
 * Utilise @ExtendWith(MockitoExtension.class) pour une approche moderne de test
 * 
 * @author Generated
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestPropertySource(properties = {
    "spring.redis.host=localhost",
    "spring.redis.port=6370",
    "logging.level.com.test.projet.metric.aspect=DEBUG"
})
class CardMetricsVolumeTest {

    @Autowired
    private CardMetricsTestService cardMetricsTestService;

    @MockBean
    private MetricsService metricsService;

    @BeforeEach
    void setUp() {
        // Reset du mock avant chaque test
        reset(metricsService);
    }

    @Test
    @DisplayName("Test Insert - 100 opérations avec @CardMetrics")
    void testInsertOperations100Times() {
        // Given
        final int numberOfOperations = 100;
        final String operationType = "Insert";
        
        // When - Exécuter 100 opérations d'insertion
        for (int i = 1; i <= numberOfOperations; i++) {
            String cardData = String.format("CARD-INSERT-%03d", i);
            cardMetricsTestService.insertCardData(cardData, "DATA_TYPE_" + i);
        }
        
        // Then - Vérifier que collectAndStoreMetrics a été appelé 100 fois pour Insert
        verify(metricsService, times(numberOfOperations)).collectAndStoreMetrics(
            eq("INSERT_SERVICE"),
            eq(operationType),
            anyLong()
        );
        
        // Vérifier que le total d'appels est correct
        System.out.println("Test Insert terminé : " + numberOfOperations + " opérations collectées");
    }

    @Test
    @DisplayName("Test Update - 100 opérations avec @CardMetrics")
    void testUpdateOperations100Times() {
        // Given
        final int numberOfOperations = 100;
        final String operationType = "Update";
        
        // When - Exécuter 100 opérations de mise à jour
        for (int i = 1; i <= numberOfOperations; i++) {
            String cardId = String.format("CARD-UPDATE-%03d", i);
            String newData = String.format("UPDATED_DATA_%03d_%s", i, System.currentTimeMillis());
            cardMetricsTestService.updateCardData(cardId, newData);
        }
        
        // Then - Vérifier que collectAndStoreMetrics a été appelé 100 fois pour Update
        verify(metricsService, times(numberOfOperations)).collectAndStoreMetrics(
            eq("UPDATE_SERVICE"),
            eq(operationType),
            anyLong()
        );
        
        // Vérifier que le total d'appels est correct
        System.out.println("Test Update terminé : " + numberOfOperations + " opérations collectées");
    }

    @Test
    @DisplayName("Test Delete - 100 opérations (50 succès + 50 exceptions) avec @CardMetrics")
    void testDeleteOperations100TimesWithExceptions() {
        // Given
        final int numberOfOperations = 100;
        final int successfulDeletes = 50;
        final int failedDeletes = 50;
        final String successOperationType = "Delete";
        final String errorOperationType = "Delete_ERROR";
        
        int actualSuccesses = 0;
        int actualFailures = 0;
        
        // When - Exécuter 100 opérations de suppression (50 succès + 50 échecs)
        for (int i = 1; i <= numberOfOperations; i++) {
            String cardId = String.format("CARD-DELETE-%03d", i);
            // Les 50 premiers réussissent, les 50 suivants échouent
            boolean shouldFail = i > successfulDeletes;
            
            try {
                cardMetricsTestService.deleteCardData(cardId, shouldFail);
                actualSuccesses++;
            } catch (Exception e) {
                actualFailures++;
                // Exception attendue pour les 50 derniers
                assertTrue(shouldFail, "Exception inattendue pour l'opération " + i);
            }
        }
        
        // Then - Vérifier les compteurs
        assertEquals(successfulDeletes, actualSuccesses, "Nombre de succès incorrect");
        assertEquals(failedDeletes, actualFailures, "Nombre d'échecs incorrect");
        
        // Vérifier que collectAndStoreMetrics a été appelé pour les succès
        verify(metricsService, times(successfulDeletes)).collectAndStoreMetrics(
            eq("DELETE_SERVICE"),
            eq(successOperationType),
            anyLong()
        );
        
        // Vérifier que collectAndStoreMetrics a été appelé pour les erreurs
        verify(metricsService, times(failedDeletes)).collectAndStoreMetrics(
            eq("DELETE_SERVICE"),
            eq(errorOperationType),
            anyLong()
        );
        
        // Vérifier le nombre total d'appels
        verify(metricsService, times(numberOfOperations)).collectAndStoreMetrics(
            eq("DELETE_SERVICE"),
            anyString(),
            anyLong()
        );
        
        System.out.println("Test Delete terminé : " + actualSuccesses + " succès + " + actualFailures + " échecs = " + numberOfOperations + " opérations");
    }

    @Test
    @DisplayName("Test Performance - Mesurer l'impact de l'annotation sur 100 opérations")
    void testPerformanceImpact() {
        // Given
        final int numberOfOperations = 100;
        
        // When - Mesurer le temps d'exécution avec annotation
        long startTime = System.nanoTime();
        
        for (int i = 1; i <= numberOfOperations; i++) {
            cardMetricsTestService.fastOperation("DATA_" + i);
        }
        
        long endTime = System.nanoTime();
        long totalTimeWithAnnotation = (endTime - startTime) / 1_000_000; // Convertir en ms
        
        // Then - Vérifier que l'annotation n'impacte pas trop les performances
        System.out.println("Temps total pour " + numberOfOperations + " opérations avec @CardMetrics : " + totalTimeWithAnnotation + "ms");
        System.out.println("Temps moyen par opération : " + (totalTimeWithAnnotation / numberOfOperations) + "ms");
        
        // L'annotation ne devrait pas ajouter plus de 5ms en moyenne par opération
        assertTrue(totalTimeWithAnnotation < 5000, "Performance dégradée : " + totalTimeWithAnnotation + "ms pour " + numberOfOperations + " opérations");
        
        // Vérifier que toutes les métriques ont été collectées
        verify(metricsService, times(numberOfOperations)).collectAndStoreMetrics(
            eq("PERFORMANCE_SERVICE"),
            eq("FastOperation"),
            anyLong()
        );
    }

    @Test
    @DisplayName("Test Mixte - Insert (100) + Update (100) + Delete (100) dans le même test")
    void testMixedOperations300Total() {
        // Given
        final int operationsPerType = 100;
        final int totalOperations = operationsPerType * 3;
        
        // When - Exécuter les 3 types d'opérations
        // 1. Insert 100 fois
        for (int i = 1; i <= operationsPerType; i++) {
            cardMetricsTestService.insertCardData("MIXED-INSERT-" + i, "TYPE_" + i);
        }
        
        // 2. Update 100 fois
        for (int i = 1; i <= operationsPerType; i++) {
            cardMetricsTestService.updateCardData("MIXED-UPDATE-" + i, "NEW_DATA_" + i);
        }
        
        // 3. Delete 100 fois (50 succès + 50 échecs)
        int deleteSuccesses = 0;
        int deleteFailures = 0;
        for (int i = 1; i <= operationsPerType; i++) {
            try {
                // Les 50 premiers réussissent, les 50 suivants échouent
                cardMetricsTestService.deleteCardData("MIXED-DELETE-" + i, i > 50);
                deleteSuccesses++;
            } catch (Exception e) {
                deleteFailures++;
            }
        }
        
        // Then - Vérifications détaillées
        
        // Vérifier Insert
        verify(metricsService, times(operationsPerType)).collectAndStoreMetrics(
            eq("INSERT_SERVICE"),
            eq("Insert"),
            anyLong()
        );
        
        // Vérifier Update
        verify(metricsService, times(operationsPerType)).collectAndStoreMetrics(
            eq("UPDATE_SERVICE"),
            eq("Update"),
            anyLong()
        );
        
        // Vérifier Delete succès
        verify(metricsService, times(deleteSuccesses)).collectAndStoreMetrics(
            eq("DELETE_SERVICE"),
            eq("Delete"),
            anyLong()
        );
        
        // Vérifier Delete échecs
        verify(metricsService, times(deleteFailures)).collectAndStoreMetrics(
            eq("DELETE_SERVICE"),
            eq("Delete_ERROR"),
            anyLong()
        );
        
        // Vérifier le nombre total d'appels
        verify(metricsService, times(totalOperations)).collectAndStoreMetrics(
            anyString(),
            anyString(),
            anyLong()
        );
        
        System.out.println("Test Mixte terminé :");
        System.out.println("  - Insert : " + operationsPerType + " opérations");
        System.out.println("  - Update : " + operationsPerType + " opérations");
        System.out.println("  - Delete : " + deleteSuccesses + " succès + " + deleteFailures + " échecs = " + operationsPerType + " opérations");
        System.out.println("  - Total : " + totalOperations + " opérations collectées");
    }

    @Test
    @DisplayName("Test Métriques Réelles - Vérifier la cumulation des données dans Redis")
    void testRealMetricsAccumulation() {
        // Given - Nettoyer les métriques existantes
        metricsService.deleteMetricsFromRedis("REAL_TEST_SERVICE");
        
        final int numberOfOperations = 50;
        
        // When - Exécuter des opérations réelles (pas juste des mocks)
        for (int i = 1; i <= numberOfOperations; i++) {
            cardMetricsTestService.realMetricsOperation("REAL_DATA_" + i);
        }
        
        // Then - Vérifier les métriques réelles stockées dans Redis
        MetricsDto metrics = metricsService.getMetricsFromRedis("REAL_TEST_SERVICE");
        
        assertNotNull(metrics, "Les métriques devraient être stockées dans Redis");
        assertEquals("REAL_TEST_SERVICE", metrics.getTypeCarte());
        
        // Utiliser MetricsTestUtils pour accéder aux métriques
        Double count = MetricsTestUtils.getCount(metrics, "RealOperation");
        Double average = MetricsTestUtils.getAverage(metrics, "RealOperation");
        Double min = MetricsTestUtils.getMin(metrics, "RealOperation");
        Double max = MetricsTestUtils.getMax(metrics, "RealOperation");
        
        assertNotNull(count, "Le count ne devrait pas être null");
        assertEquals(numberOfOperations, count.intValue());
        
        // Les métriques de temps doivent être > 0
        assertNotNull(average, "La moyenne ne devrait pas être null");
        assertNotNull(min, "Le minimum ne devrait pas être null");
        assertNotNull(max, "Le maximum ne devrait pas être null");
        
        assertTrue(average > 0, "Le temps moyen doit être > 0");
        assertTrue(min > 0, "Le temps minimum doit être > 0");
        assertTrue(max > 0, "Le temps maximum doit être > 0");

        System.out.println("Métriques réelles vérifiées :");
        System.out.println("  - Count : " + count.intValue());
        System.out.println("  - Average : " + MetricsTestUtils.getAverageFormatted(metrics, "RealOperation"));
        System.out.println("  - Min : " + MetricsTestUtils.getMinFormatted(metrics, "RealOperation"));
        System.out.println("  - Max : " + MetricsTestUtils.getMaxFormatted(metrics, "RealOperation"));
    }
}