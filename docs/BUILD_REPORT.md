# Build Report — A Tu Manera v1.0.0

Fecha del informe: 2026-08-17 (UTC). Todo lo que sigue son hechos reales verificados en el entorno de construcción, no proyecciones ni resultados simulados.

## Stack declarado

Kotlin 1.9.24 · AGP 8.5.2 · KSP 1.9.24-1.0.20 · Compose BOM 2024.06.00 · Material3 1.2.1 · Navigation Compose 2.7.7 · Room 2.6.1 · Coroutines 1.8.1 · JDK 17 · compileSdk/targetSdk 34 · minSdk 24.

## ⚠️ COMPILACIÓN NO VERIFICADA con Gradle

El entorno de construcción usado para este proyecto tiene una lista blanca de dominios de red que **no incluye** `dl.google.com` (repositorio Maven de Google, necesario para AGP, AndroidX, Compose y Room) ni `services.gradle.org` (distribución de Gradle).

Evidencia real capturada al intentar `./gradlew clean`:

```
$ ./gradlew clean
Downloading https://services.gradle.org/distributions/gradle-8.7-bin.zip
Exception in thread "main" java.io.IOException: Server returned HTTP response code: 403 for URL: https://services.gradle.org/distributions/gradle-8.7-bin.zip
	at org.gradle.wrapper.Install.forceFetch(SourceFile:2)
	...
```

```
$ curl -sI https://services.gradle.org/distributions/gradle-8.7-bin.zip
HTTP/2 403
x-deny-reason: host_not_allowed
```

Por tanto:

| Comando | Estado |
|---|---|
| `./gradlew clean` | ❌ No ejecutado (no se puede ni descargar la distribución de Gradle) |
| `./gradlew testDebugUnitTest` | ❌ No ejecutado por el mismo motivo |
| `./gradlew lintDebug` | ❌ No ejecutado por el mismo motivo |
| `./gradlew assembleDebug` | ❌ No ejecutado por el mismo motivo |

**No se ha generado ningún APK.** No existe `app-debug.apk`, no hay SHA-256 que reportar, y `deliverables/` no contiene un `.apk`. Cualquier afirmación de "build exitoso" sería falsa y no se hace.

## ✅ Verificaciones reales que sí se realizaron

Como el `gradlew` no era viable, se usó el **compilador oficial de Kotlin 1.9.24** (obtenido directamente de las release assets oficiales de `github.com/JetBrains/kotlin`, verificado con `kotlinc -version`) para compilar y ejecutar todo lo que no depende del SDK de Android:

### 1. Compilación real de la capa de dominio (100% del motor de reglas)

```
$ kotlinc domain/model/*.kt domain/logic/*.kt -d out/
```
Resultado: **compilación exitosa, 0 errores, 0 warnings**, tras corregir un bug real encontrado en el primer intento (un comentario Kotlin mal cerrado en `MissionEvaluator.kt` que rompía la compilación).

### 2. Compilación real de los datos semilla puros

```
$ kotlinc domain/model/Enums.kt data/seed/MissionSeed.kt -d out/
```
Resultado: **compilación exitosa**, tras corregir un segundo bug real encontrado: `data class MissionSeed` y `object MissionSeed` estaban declarados con el mismo nombre en el mismo archivo (colisión de identificador que Kotlin no permite). Se renombró la clase de datos a `MissionSeedDefinition`.

### 3. Ejecución real de 45 tests de dominio puro

Se compilaron los 5 archivos de test de `domain/logic/` (`GridEngineTest`, `MetricsCalculatorTest`, `BudgetManagerTest`, `MissionEvaluatorTest`, `UnlockEvaluatorTest`) contra un shim local mínimo de la API de JUnit4 (sólo `@Test` y `Assert`, sin lógica propia — el shim no reemplaza ninguna regla de negocio, sólo permite ejecutar assertions fuera de Gradle) y se ejecutaron con un runner por reflexión.

**Resultado real: `TOTAL=45 PASSED=45 FAILED=0`.**

Durante este proceso se encontraron y corrigieron **2 bugs adicionales** en los propios tests (un escenario de cobertura mal construido donde el servicio no tocaba la carretera, y un cálculo de `servicesScore` esperado incorrectamente).

Log completo de la ejecución: [`tools/domain_test_run.log`](../tools/domain_test_run.log).

### 4. Validación real del SQL de base de datos

`database/schema.sql` y `database/sample_data.sql` se ejecutaron contra un motor **SQLite real** (Python `sqlite3`, versión 3.45.1):
- Las 16 tablas se crean sin error.
- Los datos de ejemplo se insertan sin error.
- `PRAGMA foreign_key_check` no reporta **ninguna** violación de integridad referencial.
- Conteos verificados: 13 tipos de infraestructura, 12 insignias, 8 decoraciones, 30 misiones, 10 requisitos de ejemplo, 16 casillas de ejemplo, 2 construcciones de ejemplo.

### 5. Análisis estático de los 41 archivos Kotlin restantes

Se ejecutó un análisis automatizado de balanceo de llaves/paréntesis y detección de declaraciones top-level duplicadas sobre **todos** los archivos `.kt` del proyecto (dominio, datos, UI, tests). Resultado: sin desbalances; el único "duplicado" detectado (`class Success` en `CityRepository.kt`) es un falso positivo confirmado manualmente — son dos `data class Success` anidadas en `sealed class` distintas (`PlacementOutcome.Success` y `RemovalOutcome.Success`), válido en Kotlin.

### 6. Lo que **no** se pudo verificar

- Las capas que dependen de `androidx.room.*`, `androidx.compose.*`, `androidx.lifecycle.*` y del resto de AndroidX (`Entities.kt`, todos los DAO, `AppDatabase.kt`, `CityRepository.kt`, todos los ViewModels y todas las pantallas Compose) **no se han compilado** en este entorno, porque hacerlo requiere las bibliotecas reales de AndroidX/Compose/Room, descargables únicamente desde `dl.google.com`, que no está en la lista de dominios permitidos.
- Los 12 tests de integración Room/Robolectric (`CityRepositoryTest.kt`) están escritos con la API real de Room/Robolectric pero **no se han ejecutado**, por el mismo motivo.
- El lint de Android (`lintDebug`) no se ha ejecutado.

Estas partes se han revisado manualmente con la misma atención que el resto (tipos, nombres de columnas, `Flow`/`StateFlow`, ciclo de vida de Composables), pero **no cuentan con verificación por compilador real** y podrían contener errores no detectados hasta que se compilen en un entorno con acceso al Maven de Google (Android Studio, o una CI como la incluida en `.github/workflows/`).

## Tests: recuento

| Categoría | Cantidad | Ejecutados de verdad en este entorno |
|---|---|---|
| Dominio puro (`domain/logic`) | 45 | ✅ Sí — 45/45 PASS |
| Integración Room/Robolectric (`data/repository`) | 12 | ❌ No (requiere Android SDK) |
| **Total** | **57** | 45 verificados / 12 pendientes de Gradle |

## PDFs

Ver estado de generación en la sección correspondiente del mensaje de entrega. Los PDF, si se generaron, están en `docs/pdf/` y `deliverables/`.

## Entregables generados

- `deliverables/ATuManera-v1.0.0-source.zip` — código fuente completo (169 archivos; descomprime directo a `app/`, `database/`, `docs/`, `gradle/`, `tools/`, `.github/`, sin carpeta anidada).
  - El SHA-256 exacto de este ZIP se indica en el mensaje de entrega final (no se hardcodea aquí dentro para evitar el problema de autorreferencia: el hash del ZIP cambiaría cada vez que se documentase su propio valor). Puede recalcularse en cualquier momento con `sha256sum ATuManera-v1.0.0-source.zip`.
- **No** se genera `ATuManera-v1.0.0.apk` porque no hay compilación verificada (ver honestidad más arriba).
- `deliverables/MEMORIA_DESCRIPTIVA.pdf` (6 páginas, 13 998 bytes), `deliverables/MANUAL_USUARIO.pdf` (3 páginas, 6 552 bytes), `deliverables/MANUAL_TECNICO.pdf` (4 páginas, 9 123 bytes) — los tres abiertos y verificados con `pypdf` (páginas, tamaño y caracteres españoles confirmados).

## Cómo completar la verificación

En cualquier equipo con Android Studio (o una máquina con acceso a `dl.google.com` y `services.gradle.org`):

```bash
cd ATuManera
./gradlew clean
./gradlew testDebugUnitTest   # ejecuta los 57 tests, incluidos los 12 no verificados aquí
./gradlew lintDebug
./gradlew assembleDebug       # genera app/build/outputs/apk/debug/app-debug.apk
```

El workflow `.github/workflows/android-build.yml` incluido hace exactamente esto automáticamente en cada `push`, en un entorno de GitHub Actions que sí tiene acceso completo a Internet.
