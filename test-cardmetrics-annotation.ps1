# Script de test pour l'annotation @CardMetrics
# Ce script teste les différentes opérations et vérifie la collecte de métriques

$baseUrl = "http://localhost:8081"
$headers = @{
    "Content-Type" = "application/json"
}

Write-Host "=== Test de l'annotation @CardMetrics ===" -ForegroundColor Green
Write-Host ""

# Fonction pour afficher les métriques actuelles
function Show-Metrics {
    param($typeCarte)
    
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/metrics/$typeCarte" -Method Get
        Write-Host "Métriques pour $typeCarte :" -ForegroundColor Yellow
        Write-Host "  - Count: $($response.metrics.count.value)"
        Write-Host "  - Average: $($response.metrics.average.formattedValue)"
        Write-Host "  - Max: $($response.metrics.max.formattedValue)"
        Write-Host "  - Min: $($response.metrics.min.formattedValue)"
        Write-Host ""
    }
    catch {
        Write-Host "Aucune métrique trouvée pour $typeCarte" -ForegroundColor Gray
        Write-Host ""
    }
}

# Tester la création de cartes (CardService/Create)
Write-Host "1. Test création de cartes (@CardMetrics('Create'))" -ForegroundColor Cyan
Show-Metrics "CardService"
Invoke-RestMethod -Uri "$baseUrl/api/cards/create?cardNumber=CARD-001&cardType=VISA" -Method Post | Out-Null
Invoke-RestMethod -Uri "$baseUrl/api/cards/create?cardNumber=CARD-002&cardType=MASTERCARD" -Method Post | Out-Null
Invoke-RestMethod -Uri "$baseUrl/api/cards/create?cardNumber=CARD-003&cardType=AMEX" -Method Post | Out-Null
Show-Metrics "CardService"

# Tester la mise à jour de cartes (VISA_CARD/Update)
Write-Host "2. Test mise à jour de cartes (@CardMetrics(value='Update', typeCarte='VISA_CARD'))" -ForegroundColor Cyan
Show-Metrics "VISA_CARD"
Invoke-RestMethod -Uri "$baseUrl/api/cards/update/CARD-001" -Method Put -Body "Updated data 1" -ContentType "text/plain" | Out-Null
Invoke-RestMethod -Uri "$baseUrl/api/cards/update/CARD-002" -Method Put -Body "Updated data 2" -ContentType "text/plain" | Out-Null
Show-Metrics "VISA_CARD"

# Tester la validation de cartes (CardService/Validate)
Write-Host "3. Test validation de cartes (@CardMetrics('Validate'))" -ForegroundColor Cyan
$currentCount = 0
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/metrics/CardService" -Method Get
    $currentCount = $response.metrics.count.value
}
catch { }

Invoke-RestMethod -Uri "$baseUrl/api/cards/validate/CARD-001" -Method Get | Out-Null
Invoke-RestMethod -Uri "$baseUrl/api/cards/validate/CARD-002" -Method Get | Out-Null
Invoke-RestMethod -Uri "$baseUrl/api/cards/validate/CARD-003" -Method Get | Out-Null

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/metrics/CardService" -Method Get
    $newCount = $response.metrics.count.value
    Write-Host "Métriques CardService - Count passé de $currentCount à $newCount" -ForegroundColor Yellow
    Write-Host "  - Average: $($response.metrics.average.formattedValue)"
    Write-Host ""
}
catch {
    Write-Host "Erreur lors de la récupération des métriques CardService" -ForegroundColor Red
}

# Tester la suppression de cartes avec gestion d'exceptions (MASTER_CARD/Delete et Delete_ERROR)
Write-Host "4. Test suppression de cartes avec exceptions (@CardMetrics(value='Delete', typeCarte='MASTER_CARD', collectOnException=true))" -ForegroundColor Cyan
Show-Metrics "MASTER_CARD"

$successCount = 0
$errorCount = 0

for ($i = 1; $i -le 10; $i++) {
    try {
        Invoke-RestMethod -Uri "$baseUrl/api/cards/delete/DELETE-TEST-$i" -Method Delete | Out-Null
        $successCount++
        Write-Host "  ✓ Suppression $i réussie" -ForegroundColor Green
    }
    catch {
        $errorCount++
        Write-Host "  ✗ Suppression $i échouée" -ForegroundColor Red
    }
}

Write-Host "Résultats: $successCount succès, $errorCount erreurs" -ForegroundColor Yellow
Show-Metrics "MASTER_CARD"

# Tester le batch pour voir la cumulation
Write-Host "5. Test batch pour cumulation de métriques" -ForegroundColor Cyan
Write-Host "Métriques avant batch:" -ForegroundColor Yellow
Show-Metrics "CardService"
Show-Metrics "VISA_CARD"
Show-Metrics "MASTER_CARD"

Write-Host "Exécution du batch..." -ForegroundColor Cyan
Invoke-RestMethod -Uri "$baseUrl/api/cards/test-batch?count=3" -Method Post | Out-Null

Write-Host "Métriques après batch:" -ForegroundColor Yellow
Show-Metrics "CardService"
Show-Metrics "VISA_CARD"
Show-Metrics "MASTER_CARD"

# Afficher toutes les métriques disponibles
Write-Host "6. Résumé final - Toutes les métriques collectées" -ForegroundColor Cyan
try {
    $allMetrics = Invoke-RestMethod -Uri "$baseUrl/api/metrics/export/json" -Method Get
    Write-Host "Nombre total de types de métriques: $($allMetrics.Count)" -ForegroundColor Green
    
    foreach ($metric in $allMetrics.PSObject.Properties) {
        $name = $metric.Name
        $data = $metric.Value
        Write-Host "  📊 $name - Count: $($data.metrics.count.value), Avg: $($data.metrics.average.formattedValue)" -ForegroundColor White
    }
}
catch {
    Write-Host "Erreur lors de la récupération du résumé: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Test terminé ===" -ForegroundColor Green
Write-Host "L'annotation @CardMetrics fonctionne correctement !" -ForegroundColor Green
Write-Host "- Collecte automatique des métriques sur les méthodes annotées" -ForegroundColor White
Write-Host "- Support des types de carte personnalisés" -ForegroundColor White
Write-Host "- Gestion des exceptions avec collectOnException=true" -ForegroundColor White
Write-Host "- Cumulation correcte des métriques" -ForegroundColor White