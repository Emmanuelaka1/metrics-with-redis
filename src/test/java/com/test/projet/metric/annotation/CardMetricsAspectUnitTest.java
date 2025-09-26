package com.test.projet.metric.annotation;

import com.test.projet.metric.MetricsService;
import com.test.projet.metric.aspect.CardMetricsAspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test unitaire pur avec @ExtendWith(MockitoExtension.class) et @InjectMocks
 * 
 * Ce test démontre l'utilisation moderne de Mockito avec :
 * - @ExtendWith(MockitoExtension.class) : Active Mockito pour JUnit 5
 * - @InjectMocks : Injecte automatiquement les mocks dans l'objet testé
 * - @Mock : Crée des mocks automatiquement
 * - @MockitoSettings(strictness = Strictness.LENIENT) : Mode souple pour éviter les erreurs de stubbing inutile
 * 
 * @author Generated
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CardMetricsAspectUnitTest {

    @Mock
    private MetricsService metricsService;
    
    @Mock 
    private ProceedingJoinPoint joinPoint;
    
    @Mock
    private CardMetrics cardMetricsAnnotation;

    @InjectMocks
    private CardMetricsAspect cardMetricsAspect;

    @BeforeEach
    void setUp() {
        // Les mocks sont automatiquement initialisés par @ExtendWith(MockitoExtension.class)
        // @InjectMocks injecte automatiquement metricsService dans cardMetricsAspect
    }

    @Test
    @DisplayName("Test unitaire pur de CardMetricsAspect avec @InjectMocks")
    void testCardMetricsAspectWithInjectMocks() throws Throwable {
        // Given
        when(cardMetricsAnnotation.value()).thenReturn("TestOperation");
        when(cardMetricsAnnotation.typeCarte()).thenReturn("TEST_CARD");
        when(cardMetricsAnnotation.collectOnException()).thenReturn(false);
        
        when(joinPoint.getTarget()).thenReturn(new Object());
        when(joinPoint.getSignature()).thenReturn(mock(org.aspectj.lang.Signature.class));
        when(joinPoint.getSignature().toShortString()).thenReturn("TestClass#testMethod");
        when(joinPoint.proceed()).thenReturn("success");

        // When
        Object result = cardMetricsAspect.collectMetrics(joinPoint, cardMetricsAnnotation);

        // Then
        assertNotNull(result);
        assertEquals("success", result);
        
        // Vérifier que le service de métriques a été appelé
        verify(metricsService, times(1)).collectAndStoreMetrics(
            eq("TEST_CARD"),
            eq("TestOperation"), 
            anyLong()
        );
        
        // Vérifier que proceed() a été appelé
        verify(joinPoint, times(1)).proceed();
        
        System.out.println("✅ Test unitaire réussi avec @InjectMocks");
        System.out.println("   - CardMetricsAspect correctement testé");
        System.out.println("   - MetricsService mocké et injecté automatiquement");
        System.out.println("   - ProceedingJoinPoint simulé avec succès");
    }

    @Test
    @DisplayName("Test gestion d'exception avec @InjectMocks")
    void testExceptionHandlingWithInjectMocks() throws Throwable {
        // Given
        when(cardMetricsAnnotation.value()).thenReturn("ErrorOperation");
        when(cardMetricsAnnotation.typeCarte()).thenReturn("ERROR_CARD");
        when(cardMetricsAnnotation.collectOnException()).thenReturn(true);
        
        when(joinPoint.getTarget()).thenReturn(new Object());
        when(joinPoint.getSignature()).thenReturn(mock(org.aspectj.lang.Signature.class));
        when(joinPoint.getSignature().toShortString()).thenReturn("TestClass#errorMethod");
        
        RuntimeException testException = new RuntimeException("Test exception");
        when(joinPoint.proceed()).thenThrow(testException);

        // When & Then
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            cardMetricsAspect.collectMetrics(joinPoint, cardMetricsAnnotation);
        });
        
        assertEquals("Test exception", thrownException.getMessage());
        
        // Vérifier que les métriques d'erreur ont été collectées
        verify(metricsService, times(1)).collectAndStoreMetrics(
            eq("ERROR_CARD"),
            eq("ErrorOperation_ERROR"),
            anyLong()
        );
        
        System.out.println("✅ Test d'exception réussi avec @InjectMocks");
        System.out.println("   - Exception propagée correctement");
        System.out.println("   - Métriques d'erreur collectées avec suffixe _ERROR");
    }

    @Test
    @DisplayName("Test avec type de carte par défaut")
    void testDefaultCardTypeWithInjectMocks() throws Throwable {
        // Given
        when(cardMetricsAnnotation.value()).thenReturn("DefaultTest");
        when(cardMetricsAnnotation.typeCarte()).thenReturn(""); // Type vide = défaut
        when(cardMetricsAnnotation.collectOnException()).thenReturn(false);
        
        Object targetObject = new TestService();
        when(joinPoint.getTarget()).thenReturn(targetObject);
        when(joinPoint.getSignature()).thenReturn(mock(org.aspectj.lang.Signature.class));
        when(joinPoint.getSignature().toShortString()).thenReturn("TestService#defaultMethod");
        when(joinPoint.proceed()).thenReturn("default_success");

        // When
        Object result = cardMetricsAspect.collectMetrics(joinPoint, cardMetricsAnnotation);

        // Then
        assertNotNull(result);
        assertEquals("default_success", result);
        
        // Le type par défaut devrait être le nom de la classe
        verify(metricsService, times(1)).collectAndStoreMetrics(
            eq("TestService"), // Nom de la classe par défaut
            eq("DefaultTest"),
            anyLong()
        );
        
        System.out.println("✅ Test type par défaut réussi avec @InjectMocks");
        System.out.println("   - Type de carte dérivé du nom de classe");
    }

    /**
     * Classe de test simple pour simuler un service
     */
    static class TestService {
        public String defaultMethod() {
            return "test";
        }
    }
}