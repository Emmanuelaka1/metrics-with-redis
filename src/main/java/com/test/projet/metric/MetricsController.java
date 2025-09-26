package com.test.projet.metric;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/metrics")
@Tag(name = "Metrics Management", description = "API pour la gestion et la consultation des métriques")
public class MetricsController {

    @Autowired
    private MetricsService metricsService;

    @PostMapping("/collect")
    @Operation(summary = "Collecter et stocker des métriques", description = "Collecte les métriques pour un type de carte donné et les stocke dans Redis")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "Métriques collectées et stockées avec succès"),
                    @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<String> collectMetrics(
                    @Parameter(description = "Type de carte (VISA, MASTERCARD, AMEX, etc.)", required = true) @RequestParam String typeCarte,

                    @Parameter(description = "Type d'opération (PAYMENT, REFUND, AUTHORIZATION, CAPTURE)", required = true) @RequestParam String operationType,

                    @Parameter(description = "Temps d'exécution en millisecondes", required = true) @RequestParam long executionTime) {

        try {
            metricsService.collectAndStoreMetrics(typeCarte, operationType, executionTime);
            return ResponseEntity.ok("Métriques collectées et stockées avec succès pour " + typeCarte);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Erreur lors de la collecte des métriques: " + e.getMessage());
        }
    }

    @GetMapping("/{typeCarte}")
    @Operation(summary = "Récupérer les métriques", description = "Récupère les métriques stockées pour un type de carte donné")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "Métriques récupérées avec succès"),
                    @ApiResponse(responseCode = "404", description = "Aucune métrique trouvée pour ce type de carte"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<MetricsDto> getMetrics(
                    @Parameter(description = "Type de carte (VISA, MASTERCARD, AMEX, etc.)", required = true) @PathVariable String typeCarte) {

        try {
            MetricsDto metrics = metricsService.getMetricsFromRedis(typeCarte);
            if (metrics != null) {
                return ResponseEntity.ok(metrics);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/types")
    @Operation(summary = "Lister les types de cartes disponibles", description = "Retourne la liste des types de cartes supportés")
    @ApiResponse(responseCode = "200", description = "Liste des types de cartes")
    public ResponseEntity<String[]> getSupportedCardTypes() {
        String[] cardTypes = {
                        "VISA", "MASTERCARD", "AMEX", "DISCOVER", "JCB", "DINERS"
        };
        return ResponseEntity.ok(cardTypes);
    }

    @GetMapping("/operations")
    @Operation(summary = "Lister les types d'opérations disponibles", description = "Retourne la liste des types d'opérations supportés")
    @ApiResponse(responseCode = "200", description = "Liste des types d'opérations")
    public ResponseEntity<String[]> getSupportedOperationTypes() {
        String[] operationTypes = {
                        "PAYMENT", "REFUND", "AUTHORIZATION", "CAPTURE", "VOID", "SETTLEMENT"
        };
        return ResponseEntity.ok(operationTypes);
    }

    @DeleteMapping("/{typeCarte}")
    @Operation(summary = "Supprimer les métriques", description = "Supprime les métriques stockées pour un type de carte donné")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "Métriques supprimées avec succès"),
                    @ApiResponse(responseCode = "404", description = "Aucune métrique trouvée pour ce type de carte"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<String> deleteMetrics(
                    @Parameter(description = "Type de carte (VISA, MASTERCARD, AMEX, etc.)", required = true) @PathVariable String typeCarte) {

        try {
            boolean deleted = metricsService.deleteMetricsFromRedis(typeCarte);
            if (deleted) {
                return ResponseEntity.ok("Métriques supprimées avec succès pour " + typeCarte);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Erreur lors de la suppression des métriques: " + e.getMessage());
        }
    }

    @PostMapping("/collect/batch")
    @Operation(summary = "Collecte de métriques en lot", description = "Collecte plusieurs métriques en une seule requête")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "Toutes les métriques collectées avec succès"),
                    @ApiResponse(responseCode = "207", description = "Certaines métriques collectées avec succès, d'autres en échec"),
                    @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<BatchCollectionResponse> collectMetricsBatch(
                    @RequestBody BatchCollectionRequest request) {

        BatchCollectionResponse response = new BatchCollectionResponse();

        for (MetricRequest metricRequest : request.getMetrics()) {
            try {
                metricsService.collectAndStoreMetrics(
                                metricRequest.getTypeCarte(),
                                metricRequest.getOperationType(),
                                metricRequest.getExecutionTime());
                response.addSuccess(metricRequest.getTypeCarte());
            } catch (Exception e) {
                response.addFailure(metricRequest.getTypeCarte(), e.getMessage());
            }
        }

        if (response.getFailures().isEmpty()) {
            return ResponseEntity.ok(response);
        } else if (response.getSuccesses().isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(response);
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Vérification de santé du service de métriques", description = "Vérifie si le service de métriques est opérationnel")
    @ApiResponse(responseCode = "200", description = "Service opérationnel")
    public ResponseEntity<HealthStatus> getHealth() {
        HealthStatus status = new HealthStatus();
        status.setStatus("UP");
        status.setMessage("Service de métriques opérationnel");
        status.setTimestamp(System.currentTimeMillis());
        return ResponseEntity.ok(status);
    }

    @GetMapping("/getAllMetrics")
    @Operation(summary = "Récupérer toutes les clés Redis", description = "Récupère toutes les clés Redis utilisées pour stocker les métriques")
    @ApiResponse(responseCode = "200", description = "Clés Redis récupérées avec succès")
    public ResponseEntity<Map<String, MetricsDto>> getAllRedisKeys() {
        Map<String, MetricsDto> keys = metricsService.getAllRedisKeys();
        if (keys != null) {
            return ResponseEntity.ok(keys);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}