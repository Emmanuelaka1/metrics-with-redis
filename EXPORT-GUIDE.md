# 📊 Guide d'Export des Métriques - metrics-with-redis-starter

Votre application Spring Boot dispose maintenant de plusieurs endpoints pour exporter les données de métriques dans différents formats.

## 🚀 Endpoints d'Export Disponibles

### 1. Export JSON complet
```
GET http://localhost:8080/api/metrics/export/json
```
**Description :** Exporte toutes les métriques au format JSON structuré
**Contenu :** Métriques système, métriques Redis personnalisées, timestamps

### 2. Export CSV
```
GET http://localhost:8080/api/metrics/export/csv
```
**Description :** Exporte les métriques au format CSV pour analyse dans Excel
**Format :** timestamp,metric_name,metric_type,tag_key,tag_value,statistic,value

### 3. Export métriques Redis
```
GET http://localhost:8080/api/metrics/export/redis
```
**Description :** Exporte uniquement les métriques stockées dans Redis
**Contenu :** Métriques CRUD personnalisées, compteurs, timers

### 4. Export métriques CRUD
```
GET http://localhost:8080/api/metrics/export/crud?entity=all
GET http://localhost:8080/api/metrics/export/crud?entity=Customer
```
**Description :** Exporte les métriques CRUD pour une entité spécifique ou toutes

### 5. Endpoints existants
```
GET http://localhost:8080/actuator/prometheus
GET http://localhost:8080/actuator/metrics
```

## 💻 Exemples d'utilisation PowerShell

### Export JSON avec sauvegarde
```powershell
$data = Invoke-RestMethod -Uri "http://localhost:8080/api/metrics/export/json"
$data | ConvertTo-Json -Depth 10 | Out-File "metrics_$(Get-Date -Format 'yyyyMMdd_HHmmss').json"
```

### Export CSV avec sauvegarde
```powershell
$csv = Invoke-RestMethod -Uri "http://localhost:8080/api/metrics/export/csv"
$csv | Out-File "metrics_$(Get-Date -Format 'yyyyMMdd_HHmmss').csv" -Encoding UTF8
```

### Export Prometheus avec sauvegarde
```powershell
$prometheus = Invoke-RestMethod -Uri "http://localhost:8080/actuator/prometheus"
$prometheus | Out-File "prometheus_$(Get-Date -Format 'yyyyMMdd_HHmmss').txt" -Encoding UTF8
```

## 🔧 Exemples curl (à adapter selon votre terminal)

### Export JSON
```bash
curl -X GET "http://localhost:8080/api/metrics/export/json" > metrics.json
```

### Export CSV
```bash
curl -X GET "http://localhost:8080/api/metrics/export/csv" > metrics.csv
```

## 📁 Structure des données exportées

### Format JSON
```json
{
  "timestamp": "2025-09-24T22:28:10",
  "application": "metrics-with-redis-starter",
  "customMetrics": {
    "metrics:Customer:*": {...}
  },
  "systemMetrics": {
    "http.server.requests": {...},
    "jvm.memory.used": {...}
  }
}
```

### Format CSV
```csv
timestamp,metric_name,metric_type,tag_key,tag_value,statistic,value
2025-09-24T22:28:10,http.server.requests,TIMER,method,GET,COUNT,1.000000
```

## 🎯 Cas d'usage

1. **Monitoring continu** : Utilisez l'endpoint JSON pour des dashboards
2. **Analyse historique** : Exportez en CSV pour Excel/Power BI
3. **Intégration Prometheus** : Utilisez l'endpoint Prometheus standard
4. **Debug CRUD** : Utilisez l'endpoint CRUD pour analyser les performances BDD

## 📈 Métriques incluses

- **HTTP Requests** : Temps de réponse, codes de statut, endpoints
- **JVM** : Mémoire, threads, garbage collection
- **Base de données** : Connexions HikariCP, requêtes JPA
- **Redis** : Commandes, latences (si Redis fonctionne)
- **CRUD personnalisées** : Operations sur entités, timing, compteurs
- **Actuator** : Santé, métriques système

## 🔄 Automatisation

Vous pouvez programmer des exports réguliers avec des tâches cron ou des scripts PowerShell schedulés.

---

**Application démarrée avec succès sur http://localhost:8080** 🎉

Tous les endpoints sont maintenant disponibles pour l'export de vos métriques !