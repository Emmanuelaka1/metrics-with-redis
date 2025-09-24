# Script de test de la logique de cumul des métriques
$baseUrl = "http://localhost:8081/api/metrics"

Write-Host "🧮 Test de la logique de CUMUL des métriques" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green

# Test 1: Supprimer les métriques existantes pour MASTERCARD (clean start)
Write-Host "`n🗑️ 1. Nettoyage des métriques MASTERCARD:" -ForegroundColor Yellow
try {
    Invoke-RestMethod -Uri "$baseUrl/MASTERCARD" -Method DELETE
    Write-Host "✅ Métriques MASTERCARD supprimées" -ForegroundColor Green
} catch {
    Write-Host "ℹ️ Aucune métrique MASTERCARD à supprimer" -ForegroundColor Cyan
}

# Test 2: Première collecte pour MASTERCARD REFUND (100ms)
Write-Host "`n📊 2. Première collecte - MASTERCARD REFUND (100ms):" -ForegroundColor Yellow
try {
    $collectParams1 = @{
        typeCarte = "MASTERCARD"
        operationType = "REFUND" 
        executionTime = 100
    }
    $response = Invoke-RestMethod -Uri "$baseUrl/collect" -Method POST -Body $collectParams1
    Write-Host "✅ $response" -ForegroundColor Green
    
    # Vérifier le résultat
    $metrics = Invoke-RestMethod -Uri "$baseUrl/MASTERCARD" -Method GET
    Write-Host "📈 Résultat après 1ère collecte:" -ForegroundColor Cyan
    $metrics.metrics | ForEach-Object { 
        Write-Host "  - $($_.name): $($_.value) ($($_.type))" -ForegroundColor White
    }
} catch {
    Write-Host "❌ Erreur: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Deuxième collecte pour MASTERCARD REFUND (200ms)
Write-Host "`n📊 3. Deuxième collecte - MASTERCARD REFUND (200ms):" -ForegroundColor Yellow
try {
    $collectParams2 = @{
        typeCarte = "MASTERCARD"
        operationType = "REFUND" 
        executionTime = 200
    }
    $response = Invoke-RestMethod -Uri "$baseUrl/collect" -Method POST -Body $collectParams2
    Write-Host "✅ $response" -ForegroundColor Green
    
    # Vérifier le résultat
    $metrics = Invoke-RestMethod -Uri "$baseUrl/MASTERCARD" -Method GET
    Write-Host "📈 Résultat après 2ème collecte:" -ForegroundColor Cyan
    $metrics.metrics | ForEach-Object { 
        Write-Host "  - $($_.name): $($_.value) ($($_.type))" -ForegroundColor White
    }
} catch {
    Write-Host "❌ Erreur: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Troisième collecte pour MASTERCARD REFUND (125ms)
Write-Host "`n📊 4. Troisième collecte - MASTERCARD REFUND (125ms):" -ForegroundColor Yellow
try {
    $collectParams3 = @{
        typeCarte = "MASTERCARD"
        operationType = "REFUND" 
        executionTime = 125
    }
    $response = Invoke-RestMethod -Uri "$baseUrl/collect" -Method POST -Body $collectParams3
    Write-Host "✅ $response" -ForegroundColor Green
    
    # Vérifier le résultat final
    $metrics = Invoke-RestMethod -Uri "$baseUrl/MASTERCARD" -Method GET
    Write-Host "📈 Résultat FINAL après 3 collectes:" -ForegroundColor Cyan
    $metrics.metrics | ForEach-Object { 
        Write-Host "  - $($_.name): $($_.value) ($($_.type))" -ForegroundColor White
    }
} catch {
    Write-Host "❌ Erreur: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Validation des calculs attendus
Write-Host "`n✅ 5. Validation des résultats attendus:" -ForegroundColor Yellow
Write-Host "  📊 Valeurs attendues pour MASTERCARD REFUND:" -ForegroundColor Cyan
Write-Host "    - Number: 3 (nombre de transactions)" -ForegroundColor White
Write-Host "    - Average: 141.67 ((100+200+125)/3)" -ForegroundColor White
Write-Host "    - Max: 200 (maximum)" -ForegroundColor White
Write-Host "    - Min: 100 (minimum)" -ForegroundColor White

# Test 6: Test avec un autre type d'opération
Write-Host "`n📊 6. Test avec PAYMENT sur MASTERCARD:" -ForegroundColor Yellow
try {
    $collectParams4 = @{
        typeCarte = "MASTERCARD"
        operationType = "PAYMENT" 
        executionTime = 50
    }
    $response = Invoke-RestMethod -Uri "$baseUrl/collect" -Method POST -Body $collectParams4
    Write-Host "✅ $response" -ForegroundColor Green
    
    # Vérifier le résultat avec les deux types d'opération
    $metrics = Invoke-RestMethod -Uri "$baseUrl/MASTERCARD" -Method GET
    Write-Host "📈 Résultat avec REFUND + PAYMENT:" -ForegroundColor Cyan
    $refundMetrics = $metrics.metrics | Where-Object { $_.type -eq "REFUND" }
    $paymentMetrics = $metrics.metrics | Where-Object { $_.type -eq "PAYMENT" }
    
    Write-Host "  🔄 REFUND métriques:" -ForegroundColor Yellow
    $refundMetrics | ForEach-Object { 
        Write-Host "    - $($_.name): $($_.value)" -ForegroundColor White
    }
    
    Write-Host "  💳 PAYMENT métriques:" -ForegroundColor Yellow
    $paymentMetrics | ForEach-Object { 
        Write-Host "    - $($_.name): $($_.value)" -ForegroundColor White
    }
} catch {
    Write-Host "❌ Erreur: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎉 Tests de cumul terminés!" -ForegroundColor Green