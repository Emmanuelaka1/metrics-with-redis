# Script de test des endpoints MetricsController
$baseUrl = "http://localhost:8081/api/metrics"

Write-Host "🚀 Test des endpoints du MetricsController" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green

# Test 1: Vérifier les types de cartes disponibles
Write-Host "`n📋 1. Types de cartes disponibles:" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/types" -Method GET
    $response | ConvertTo-Json
} catch {
    Write-Host "❌ Erreur: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Vérifier les types d'opérations disponibles  
Write-Host "`n📋 2. Types d'opérations disponibles:" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/operations" -Method GET
    $response | ConvertTo-Json
} catch {
    Write-Host "❌ Erreur: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Collecter des métriques pour VISA
Write-Host "`n📊 3. Collecte de métriques pour VISA:" -ForegroundColor Yellow
try {
    $collectParams = @{
        typeCarte = "VISA"
        operationType = "PAYMENT" 
        executionTime = 1500
    }
    $response = Invoke-RestMethod -Uri "$baseUrl/collect" -Method POST -Body $collectParams
    Write-Host "✅ $response" -ForegroundColor Green
} catch {
    Write-Host "❌ Erreur: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Récupérer les métriques pour VISA
Write-Host "`n📈 4. Récupération des métriques pour VISA:" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/VISA" -Method GET
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "❌ Erreur: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Health check
Write-Host "`n❤️ 5. Vérification de santé du service:" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/health" -Method GET
    $response | ConvertTo-Json
} catch {
    Write-Host "❌ Erreur: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: Collection en lot
Write-Host "`n📦 6. Collection de métriques en lot:" -ForegroundColor Yellow
try {
    $batchData = @{
        metrics = @(
            @{
                typeCarte = "MASTERCARD"
                operationType = "REFUND"
                executionTime = 800
            },
            @{
                typeCarte = "AMEX"
                operationType = "AUTHORIZATION"
                executionTime = 1200
            }
        )
    }
    $jsonBody = $batchData | ConvertTo-Json -Depth 3
    $response = Invoke-RestMethod -Uri "$baseUrl/collect/batch" -Method POST -Body $jsonBody -ContentType "application/json"
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "❌ Erreur: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🏁 Tests terminés!" -ForegroundColor Green
Write-Host "📚 Documentation Swagger disponible sur: http://localhost:8081/swagger-ui.html" -ForegroundColor Cyan