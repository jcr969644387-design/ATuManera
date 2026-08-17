-- ============================================================
-- A Tu Manera — schema.sql
-- Esquema SQLite real, generado a partir de las entidades Room
-- (app/src/main/kotlin/.../data/local/entity/Entities.kt).
-- Motor: SQLite (Room). No requiere backend ni conexión.
-- ============================================================

PRAGMA foreign_keys = ON;

-- ---------- Perfil local (sin datos personales reales) ----------
CREATE TABLE IF NOT EXISTS user_profile (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    alias       TEXT NOT NULL,
    avatarCode  TEXT NOT NULL,
    createdAt   INTEGER NOT NULL
);

-- ---------- Ciudad ----------
CREATE TABLE IF NOT EXISTS city (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    userId      INTEGER NOT NULL,
    name        TEXT NOT NULL,
    budgetTotal INTEGER NOT NULL,
    rows        INTEGER NOT NULL,
    cols        INTEGER NOT NULL,
    createdAt   INTEGER NOT NULL,
    updatedAt   INTEGER NOT NULL,
    FOREIGN KEY (userId) REFERENCES user_profile(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_city_userId ON city(userId);

-- ---------- Cuadrícula de la ciudad ----------
CREATE TABLE IF NOT EXISTS city_tile (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    cityId    INTEGER NOT NULL,
    row       INTEGER NOT NULL,
    col       INTEGER NOT NULL,
    buildable INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (cityId) REFERENCES city(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX IF NOT EXISTS index_city_tile_cityId_row_col ON city_tile(cityId, row, col);

-- ---------- Catálogo de infraestructuras ----------
CREATE TABLE IF NOT EXISTS infrastructure_type (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    code            TEXT NOT NULL,
    name            TEXT NOT NULL,
    category        TEXT NOT NULL,   -- ROAD | HOUSING | EDUCATION | HEALTH | PARK | WATER | TRANSPORT
    description     TEXT NOT NULL,
    cost            INTEGER NOT NULL,
    coverageRadius  INTEGER NOT NULL,
    iconRes         TEXT NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS index_infrastructure_type_code ON infrastructure_type(code);

-- ---------- Infraestructura colocada por el jugador ----------
CREATE TABLE IF NOT EXISTS placed_infrastructure (
    id                    INTEGER PRIMARY KEY AUTOINCREMENT,
    cityId                INTEGER NOT NULL,
    tileId                INTEGER NOT NULL,
    infrastructureTypeId  INTEGER NOT NULL,
    placedAt              INTEGER NOT NULL,
    FOREIGN KEY (cityId) REFERENCES city(id) ON DELETE CASCADE,
    FOREIGN KEY (tileId) REFERENCES city_tile(id) ON DELETE CASCADE,
    FOREIGN KEY (infrastructureTypeId) REFERENCES infrastructure_type(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX IF NOT EXISTS index_placed_infrastructure_cityId_tileId ON placed_infrastructure(cityId, tileId);
CREATE INDEX IF NOT EXISTS index_placed_infrastructure_infrastructureTypeId ON placed_infrastructure(infrastructureTypeId);

-- ---------- Conexiones de carretera (aristas del grafo de conectividad) ----------
CREATE TABLE IF NOT EXISTS road_connection (
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    cityId  INTEGER NOT NULL,
    tileAId INTEGER NOT NULL,
    tileBId INTEGER NOT NULL,
    FOREIGN KEY (cityId) REFERENCES city(id) ON DELETE CASCADE,
    FOREIGN KEY (tileAId) REFERENCES city_tile(id) ON DELETE CASCADE,
    FOREIGN KEY (tileBId) REFERENCES city_tile(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX IF NOT EXISTS index_road_connection_cityId_tileAId_tileBId ON road_connection(cityId, tileAId, tileBId);

-- ---------- Cobertura de servicios calculada (caché persistida) ----------
CREATE TABLE IF NOT EXISTS service_coverage (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    cityId      INTEGER NOT NULL,
    tileId      INTEGER NOT NULL,
    category    TEXT NOT NULL,      -- EDUCATION | HEALTH | WATER | PARK
    covered     INTEGER NOT NULL,   -- 0/1
    computedAt  INTEGER NOT NULL,
    FOREIGN KEY (cityId) REFERENCES city(id) ON DELETE CASCADE,
    FOREIGN KEY (tileId) REFERENCES city_tile(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX IF NOT EXISTS index_service_coverage_cityId_tileId_category ON service_coverage(cityId, tileId, category);

-- ---------- Catálogo de misiones ----------
CREATE TABLE IF NOT EXISTS mission (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    code            TEXT NOT NULL,
    title           TEXT NOT NULL,
    description     TEXT NOT NULL,
    category        TEXT NOT NULL,
    orderIndex      INTEGER NOT NULL,
    rewardXp        INTEGER NOT NULL,
    rewardBadgeCode TEXT
);
CREATE UNIQUE INDEX IF NOT EXISTS index_mission_code ON mission(code);

-- ---------- Requisitos de cada misión (filas flexibles) ----------
CREATE TABLE IF NOT EXISTS mission_requirement (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    missionId       INTEGER NOT NULL,
    type            TEXT NOT NULL,   -- PLACE_COUNT | METRIC_THRESHOLD | ROAD_NETWORK_SIZE | BUDGET_EFFICIENCY | COVERAGE_COUNT
    requirementKey  TEXT NOT NULL,
    targetValue     INTEGER NOT NULL,
    FOREIGN KEY (missionId) REFERENCES mission(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_mission_requirement_missionId ON mission_requirement(missionId);

-- ---------- Progreso de cada misión por usuario/ciudad ----------
CREATE TABLE IF NOT EXISTS mission_progress (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    userId          INTEGER NOT NULL,
    cityId          INTEGER NOT NULL,
    missionId       INTEGER NOT NULL,
    status          TEXT NOT NULL,   -- LOCKED | AVAILABLE | IN_PROGRESS | COMPLETED
    progressPercent INTEGER NOT NULL,
    completedAt     INTEGER,
    FOREIGN KEY (userId) REFERENCES user_profile(id) ON DELETE CASCADE,
    FOREIGN KEY (cityId) REFERENCES city(id) ON DELETE CASCADE,
    FOREIGN KEY (missionId) REFERENCES mission(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX IF NOT EXISTS index_mission_progress_userId_cityId_missionId ON mission_progress(userId, cityId, missionId);

-- ---------- Historial de métricas de la ciudad ----------
CREATE TABLE IF NOT EXISTS city_metric (
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    cityId             INTEGER NOT NULL,
    timestamp          INTEGER NOT NULL,
    mobility           INTEGER NOT NULL,
    servicesScore      INTEGER NOT NULL,
    greenScore         INTEGER NOT NULL,
    educationCoverage  INTEGER NOT NULL,
    healthCoverage     INTEGER NOT NULL,
    waterCoverage      INTEGER NOT NULL,
    budgetSpent        INTEGER NOT NULL,
    budgetTotal        INTEGER NOT NULL,
    FOREIGN KEY (cityId) REFERENCES city(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_city_metric_cityId ON city_metric(cityId);

-- ---------- Catálogo de decoraciones/monumentos ----------
CREATE TABLE IF NOT EXISTS decoration (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    code                TEXT NOT NULL,
    name                TEXT NOT NULL,
    description         TEXT NOT NULL,
    iconRes             TEXT NOT NULL,
    unlockConditionCode TEXT NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS index_decoration_code ON decoration(code);

-- ---------- Decoraciones desbloqueadas por usuario ----------
CREATE TABLE IF NOT EXISTS unlocked_decoration (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    userId        INTEGER NOT NULL,
    decorationId  INTEGER NOT NULL,
    unlockedAt    INTEGER NOT NULL,
    FOREIGN KEY (userId) REFERENCES user_profile(id) ON DELETE CASCADE,
    FOREIGN KEY (decorationId) REFERENCES decoration(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX IF NOT EXISTS index_unlocked_decoration_userId_decorationId ON unlocked_decoration(userId, decorationId);

-- ---------- Progreso general del jugador ----------
CREATE TABLE IF NOT EXISTS progress (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    userId            INTEGER NOT NULL,
    cityId            INTEGER NOT NULL,
    currentChapter    INTEGER NOT NULL,
    totalXp           INTEGER NOT NULL,
    missionsCompleted INTEGER NOT NULL,
    updatedAt         INTEGER NOT NULL,
    FOREIGN KEY (userId) REFERENCES user_profile(id) ON DELETE CASCADE,
    FOREIGN KEY (cityId) REFERENCES city(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX IF NOT EXISTS index_progress_userId ON progress(userId);

-- ---------- Catálogo de insignias ----------
CREATE TABLE IF NOT EXISTS badge (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    code        TEXT NOT NULL,
    name        TEXT NOT NULL,
    description TEXT NOT NULL,
    iconRes     TEXT NOT NULL,
    category    TEXT NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS index_badge_code ON badge(code);

-- ---------- Insignias ganadas por usuario ----------
CREATE TABLE IF NOT EXISTS user_badge (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    userId    INTEGER NOT NULL,
    badgeId   INTEGER NOT NULL,
    earnedAt  INTEGER NOT NULL,
    FOREIGN KEY (userId) REFERENCES user_profile(id) ON DELETE CASCADE,
    FOREIGN KEY (badgeId) REFERENCES badge(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX IF NOT EXISTS index_user_badge_userId_badgeId ON user_badge(userId, badgeId);
