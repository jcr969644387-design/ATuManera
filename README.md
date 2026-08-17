# A Tu Manera

Construye y haz crecer tu propia ciudad: calles, viviendas, escuelas, hospitales, parques, agua y transporte, con reglas de conectividad y presupuesto **reales**, 30 misiones y un sistema de insignias y monumentos coleccionables. 100% offline, sin cuentas ni datos personales.

- **Público:** 10-15 años
- **Plataforma:** Android nativo (Kotlin + Jetpack Compose), minSdk 24
- **Package:** `com.educalab.atumanera`
- **Versión:** 1.0.0

> ⚠️ **Estado de compilación:** este proyecto se construyó en un entorno sin acceso al repositorio Maven de Google, por lo que `./gradlew assembleDebug` **no se ha podido ejecutar ni verificar aquí**. El código fuente está completo. Ver [`docs/BUILD_REPORT.md`](docs/BUILD_REPORT.md) para el detalle honesto de qué se verificó realmente (compilación y ejecución real de 45 tests de dominio con el compilador oficial de Kotlin, validación real del SQL) y qué queda pendiente de un build con Android Studio o el workflow de GitHub Actions incluido.

## Cómo compilar

Requiere Android Studio (Koala o superior) o un entorno con JDK 17 y acceso a Internet:

```bash
git clone <este-repositorio>
cd ATuManera
./gradlew clean
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
```

El APK resultante queda en `app/build/outputs/apk/debug/app-debug.apk`.

También puedes simplemente hacer `git push`: el workflow en `.github/workflows/android-build.yml` compila, prueba y publica el APK como artefacto automáticamente.

## Qué construye el jugador

7 módulos de construcción sobre una cuadrícula de 10×10: **Carreteras, Vivienda, Educación, Salud, Parques, Agua y servicios, Transporte**. Cada construcción cuesta presupuesto real; la cobertura de cada servicio se calcula mediante un motor de conectividad (BFS) sobre la red de carreteras — no son valores decorativos.

## Documentación

- [`docs/MEMORIA_DESCRIPTIVA.md`](docs/MEMORIA_DESCRIPTIVA.md) — objetivos, alcance, requisitos, casos de uso, arquitectura.
- [`docs/MANUAL_USUARIO.md`](docs/MANUAL_USUARIO.md) — instalación y uso paso a paso.
- [`docs/MANUAL_TECNICO.md`](docs/MANUAL_TECNICO.md) — stack, arquitectura, mantenimiento.
- [`docs/BASE_DE_DATOS.md`](docs/BASE_DE_DATOS.md) — esquema completo, índices, diagrama Mermaid.
- [`docs/BUILD_REPORT.md`](docs/BUILD_REPORT.md) — estado real y honesto de la construcción y las pruebas.
- [`database/schema.sql`](database/schema.sql) / [`database/sample_data.sql`](database/sample_data.sql) — verificados contra un motor SQLite real.

## Estructura

```
app/src/main/kotlin/com/educalab/atumanera/
├── domain/    → modelos y motor de reglas puro (sin Android), 100% testeable
├── data/      → Room (16 entidades), repositorio, seeders
└── ui/        → ViewModels, pantallas Compose, navegación
app/src/test/kotlin/  → 57 tests JUnit (45 verificados en este entorno, ver BUILD_REPORT.md)
tools/generate_icons.py → generador de los 52 recursos vectoriales propios de la app
database/    → schema.sql y sample_data.sql (verificados con SQLite real)
docs/        → documentación completa + PDFs
.github/workflows/ → CI que compila el APK real en cada push
```

## Privacidad

Sin Internet, sin backend, sin cuentas, sin anuncios, sin analítica. El perfil usa un alias y un avatar local; no se pide ningún dato personal real.
