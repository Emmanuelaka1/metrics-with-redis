# Annotation @CardMetrics - Documentation

## Vue d'ensemble

L'annotation `@CardMetrics` permet de collecter automatiquement les métriques d'exécution sur les méthodes en utilisant AspectJ. Cette annotation simplifie grandement la collecte de métriques en éliminant le besoin d'appeler manuellement `MetricsService.collectAndStoreMetrics()`.

## Fonctionnalités

- ✅ **Collecte automatique** des métriques d'exécution
- ✅ **Type de carte personnalisable** via le paramètre `typeCarte`
- ✅ **Gestion des exceptions** avec `collectOnException`
- ✅ **Cumulation des métriques** (pas d'écrasement)
- ✅ **Logging automatique** pour le debugging
- ✅ **Compatible avec Spring AOP**

## Utilisation

### Syntaxe de base

```java
@CardMetrics("OperationType")
public void maMethode() {
    // Votre logique métier
}
```

### Paramètres

| Paramètre | Type | Obligatoire | Description | Défaut |
|-----------|------|-------------|-------------|---------|
| `value` | String | ✅ Oui | Type d'opération (ex: "Create", "Update", "Delete") | - |
| `typeCarte` | String | ❌ Non | Type de carte pour les métriques | Nom de la classe |
| `collectOnException` | boolean | ❌ Non | Collecter les métriques même en cas d'exception | `false` |

## Exemples d'utilisation

### Exemple 1 : Usage basique
```java
@Service
public class CardService {
    
    @CardMetrics("Create")
    public String createCard(String cardNumber) {
        // Logique de création
        return "Carte créée: " + cardNumber;
    }
}
```
**Résultat :** Métriques stockées sous `"CardService"` avec opération `"Create"`

### Exemple 2 : Type de carte personnalisé
```java
@CardMetrics(value = "Update", typeCarte = "VISA_CARD")
public String updateVisaCard(String cardNumber) {
    // Logique de mise à jour spécifique VISA
    return "VISA mise à jour: " + cardNumber;
}
```
**Résultat :** Métriques stockées sous `"VISA_CARD"` avec opération `"Update"`

### Exemple 3 : Gestion des exceptions
```java
@CardMetrics(value = "Delete", typeCarte = "MASTER_CARD", collectOnException = true)
public void deleteCard(String cardNumber) throws Exception {
    if (cardNumber.startsWith("INVALID")) {
        throw new Exception("Carte invalide");
    }
    // Logique de suppression
}
```
**Résultat :** 
- Succès → Métriques sous `"MASTER_CARD"` avec opération `"Delete"`
- Exception → Métriques sous `"MASTER_CARD"` avec opération `"Delete_ERROR"`

## Métriques collectées

Pour chaque méthode annotée, les métriques suivantes sont collectées :

- **Count** : Nombre d'exécutions
- **Average** : Temps d'exécution moyen (ms)
- **Min** : Temps d'exécution minimum (ms)
- **Max** : Temps d'exécution maximum (ms)

## API REST générée

Les métriques collectées sont automatiquement disponibles via l'API REST :

### Récupérer les métriques par type
```http
GET /api/metrics/{typeCarte}
```

### Exemple de réponse
```json
{
  "typeCarte": "CardService",
  "timestamp": "2025-09-26T10:30:00.000Z",
  "metrics": {
    "count": {"value": 5},
    "average": {"value": 125.6, "formattedValue": "125.6 ms"},
    "min": {"value": 89.0, "formattedValue": "89.0 ms"},
    "max": {"value": 201.0, "formattedValue": "201.0 ms"}
  }
}
```

## Configuration

### Spring Boot Application
Assurez-vous que votre application Spring Boot a les dépendances suivantes :

```gradle
implementation 'org.springframework.boot:spring-boot-starter-aop'
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
```

### Activation d'AspectJ
L'aspect est automatiquement activé via `@Component` et Spring AOP.

## Tests

### Test unitaire exemple
```java
@SpringBootTest
class MonServiceTest {
    
    @Autowired
    private MonService monService;
    
    @SpyBean
    private MetricsService metricsService;
    
    @Test
    void testMetricsCollection() {
        // When
        monService.maMethodeAnnotee();
        
        // Then
        verify(metricsService, times(1)).collectAndStoreMetrics(
            eq("MonService"),
            eq("MonOperation"),
            anyLong()
        );
    }
}
```

### Script de test PowerShell
Un script complet est disponible : `test-cardmetrics-annotation.ps1`

```powershell
# Lancer les tests
./test-cardmetrics-annotation.ps1
```

## Avantages

### Avant (manuel)
```java
public String createCard(String cardNumber) {
    long startTime = System.currentTimeMillis();
    try {
        // Logique métier
        String result = processCard(cardNumber);
        
        long executionTime = System.currentTimeMillis() - startTime;
        metricsService.collectAndStoreMetrics("CardService", "Create", executionTime);
        
        return result;
    } catch (Exception e) {
        long executionTime = System.currentTimeMillis() - startTime;
        metricsService.collectAndStoreMetrics("CardService", "Create_ERROR", executionTime);
        throw e;
    }
}
```

### Après (avec annotation)
```java
@CardMetrics(value = "Create", collectOnException = true)
public String createCard(String cardNumber) {
    // Seulement la logique métier !
    return processCard(cardNumber);
}
```

## Logging

L'aspect génère automatiquement des logs de debug :

```
DEBUG c.t.p.m.aspect.CardMetricsAspect : Début de la collecte de métriques pour CardService#createCard - Type: CardService, Opération: Create
DEBUG c.t.p.m.aspect.CardMetricsAspect : Métriques collectées avec succès pour CardService#createCard - Temps: 125ms
```

Activez les logs avec :
```yaml
logging:
  level:
    com.test.projet.metric.aspect: DEBUG
```

## Architecture

```
┌─────────────────────────────────────────┐
│              @CardMetrics               │
│            (Annotation)                 │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│           CardMetricsAspect             │
│             (@Around)                   │
│  - Mesure le temps d'exécution         │
│  - Gère les exceptions                  │
│  - Appelle MetricsService               │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│            MetricsService               │
│  - collectAndStoreMetrics()             │
│  - Cumulation des métriques             │
│  - Stockage Redis                       │
└─────────────────────────────────────────┘
```

## Bonnes pratiques

1. **Nommage des opérations** : Utilisez des noms clairs comme "Create", "Update", "Delete", "Validate"
2. **Types de carte** : Groupez les métriques par type logique ("VISA_CARD", "MASTER_CARD", etc.)
3. **Gestion d'exceptions** : Activez `collectOnException=true` pour les opérations critiques
4. **Performance** : L'overhead de l'aspect est minimal (quelques microsecondes)
5. **Tests** : Utilisez `@SpyBean` pour vérifier la collecte de métriques dans les tests

## Troubleshooting

### L'aspect ne se déclenche pas
- Vérifiez que `@EnableAspectJAutoProxy` est activé (automatique avec Spring Boot AOP)
- Assurez-vous que la méthode est appelée depuis l'extérieur de la classe (proxy Spring)

### Métriques non trouvées
- Vérifiez la configuration Redis
- Consultez les logs de niveau DEBUG
- Utilisez l'endpoint `/api/metrics/export/json` pour lister toutes les métriques

### Erreurs de sérialisation
- Les métriques utilisent Jackson pour la sérialisation JSON
- Vérifiez la configuration `ObjectMapper`

## Performance

L'annotation `@CardMetrics` ajoute un overhead minimal :
- **Temps d'exécution** : < 1ms par méthode
- **Mémoire** : Impact négligeable
- **CPU** : Mesure de temps très optimisée

L'impact est largement compensé par la valeur des métriques collectées pour le monitoring et l'optimisation.