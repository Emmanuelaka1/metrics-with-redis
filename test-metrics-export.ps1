# Script de test des endpoints d'export de métriques
Write-Host "🧪 Test des endpoints d'export de métriques" -ForegroundColor Green

$baseUrl = "http://localhost:8080"

# Test de l'endpoint JSON
Write-Host "`n📊 Test export JSON..." -ForegroundColor Yellow
try {
    $jsonData = Invoke-RestMethod -Uri "$baseUrl/api/metrics/export/json" -Method Get -ErrorAction Stop
    Write-Host "✅ Export JSON réussi" -ForegroundColor Green
    Write-Host "Timestamp: $($jsonData.timestamp)" -ForegroundColor Cyan
    Write-Host "Application: $($jsonData.application)" -ForegroundColor Cyan
    Write-Host "Métriques système: $($jsonData.systemMetrics.Count) métriques" -ForegroundColor Cyan
    
    # Sauvegarder dans un fichier
    $jsonFilename = "metrics_export_$(Get-Date -Format 'yyyyMMdd_HHmmss').json"
    $jsonData | ConvertTo-Json -Depth 10 | Out-File -FilePath $jsonFilename -Encoding UTF8
    Write-Host "📁 Sauvegardé dans: $jsonFilename" -ForegroundColor Green
    
}
catch {
    Write-Host "❌ Erreur export JSON: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎯 Test JSON terminé!" -ForegroundColor Green