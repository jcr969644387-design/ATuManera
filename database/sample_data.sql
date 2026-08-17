-- ============================================================
-- A Tu Manera — sample_data.sql
-- Datos de ejemplo equivalentes a los que el DatabaseSeeder inserta
-- en el primer arranque real de la app (ver data/seed/CatalogSeed.kt
-- y data/seed/MissionSeed.kt). Útil para inspeccionar la base de
-- datos fuera de la app o para pruebas manuales con sqlite3.
-- ============================================================

-- ---------- Catálogo de infraestructuras (13) ----------
INSERT INTO infrastructure_type (code, name, category, description, cost, coverageRadius, iconRes) VALUES
('ROAD_BASIC',      'Calle',                     'ROAD',        'Conecta casillas y da acceso a toda la ciudad.',            10, 0, 'ic_infra_road'),
('HOUSE_SMALL',     'Casa pequeña',              'HOUSING',     'Vivienda para pocas familias.',                             30, 0, 'ic_infra_house_small'),
('HOUSE_BLOCK',     'Bloque de viviendas',       'HOUSING',     'Vivienda de mayor densidad.',                               70, 0, 'ic_infra_house_block'),
('SCHOOL_PRIMARY',  'Escuela primaria',          'EDUCATION',   'Da acceso educativo a las casas cercanas por carretera.',  120, 6, 'ic_infra_school'),
('SCHOOL_LIBRARY',  'Biblioteca',                'EDUCATION',   'Refuerza la cobertura educativa del barrio.',               90, 5, 'ic_infra_library'),
('HEALTH_CENTER',   'Centro de salud',           'HEALTH',      'Atiende a la población cercana conectada por carretera.',  100, 6, 'ic_infra_health_center'),
('HOSPITAL',        'Hospital',                  'HEALTH',      'Mayor alcance de cobertura sanitaria.',                    190, 8, 'ic_infra_hospital'),
('PARK_SMALL',      'Plaza verde',               'PARK',        'Área verde de barrio.',                                     35, 4, 'ic_infra_park_small'),
('PARK_LARGE',      'Parque grande',             'PARK',        'Gran área verde para toda la zona.',                        80, 6, 'ic_infra_park_large'),
('WATER_TOWER',     'Torre de agua',             'WATER',       'Suministra agua a las casas conectadas.',                  110, 7, 'ic_infra_water_tower'),
('WATER_TREATMENT', 'Planta potabilizadora',     'WATER',       'Amplía la cobertura de agua potable.',                     160, 9, 'ic_infra_water_plant'),
('BUS_STOP',        'Parada de autobús',         'TRANSPORT',   'Transporte público de corto alcance.',                      45, 5, 'ic_infra_bus'),
('TRAIN_STATION',   'Estación de tren',          'TRANSPORT',   'Transporte de largo alcance para la ciudad.',              150, 8, 'ic_infra_train');

-- ---------- Insignias (12) ----------
INSERT INTO badge (code, name, description, iconRes, category) VALUES
('BADGE_FIRST_ROAD',      'Primera Calle',            'Construiste tu primera calle.',                         'ic_badge_road',       'ROAD'),
('BADGE_FIRST_HOUSE',     'Primer Hogar',              'Diste techo a tus primeros vecinos.',                   'ic_badge_house',      'HOUSING'),
('BADGE_FIRST_SCHOOL',    'Primera Escuela',           'Abriste la primera escuela de la ciudad.',              'ic_badge_school',     'EDUCATION'),
('BADGE_MOBILITY_MASTER', 'Maestro de la Movilidad',   'El 90% de tus casas tiene acceso a la calle.',          'ic_badge_mobility',   'ROAD'),
('BADGE_GREEN_CITY',      'Ciudad Verde',              'Lograste una puntuación verde de 80 o más.',            'ic_badge_green',      'PARK'),
('BADGE_HEALTH_HERO',     'Héroe de la Salud',         'Cobertura sanitaria del 90% o más.',                    'ic_badge_health',     'HEALTH'),
('BADGE_WATER_HERO',      'Héroe del Agua',            'Cobertura de agua del 90% o más.',                      'ic_badge_water',      'WATER'),
('BADGE_EDUCATION_HERO',  'Héroe de la Educación',     'Cobertura educativa del 90% o más.',                    'ic_badge_education',  'EDUCATION'),
('BADGE_BUDGET_WIZARD',   'Mago del Presupuesto',      'Buenos servicios gastando poco presupuesto.',           'ic_badge_budget',     'GENERAL'),
('BADGE_MISSION_10',      'Explorador Urbano',         'Completaste 10 misiones.',                              'ic_badge_missions10', 'GENERAL'),
('BADGE_MISSION_20',      'Planificador Experto',      'Completaste 20 misiones.',                              'ic_badge_missions20', 'GENERAL'),
('BADGE_XP_500',          'Leyenda de la Ciudad',      'Alcanzaste 500 puntos de experiencia.',                 'ic_badge_xp',         'GENERAL');

-- ---------- Decoraciones / monumentos (8) ----------
INSERT INTO decoration (code, name, description, iconRes, unlockConditionCode) VALUES
('DECO_FOUNTAIN',    'Fuente Central',            'Una fuente decorativa para la plaza principal.',        'ic_deco_fountain', 'DECO_FOUNTAIN'),
('DECO_CLOCK_TOWER', 'Torre del Reloj',           'Un símbolo de una ciudad bien conectada.',               'ic_deco_clock',    'DECO_CLOCK_TOWER'),
('DECO_STATUE',      'Estatua del Fundador',      'Homenaje a los primeros pasos de tu ciudad.',            'ic_deco_statue',   'DECO_STATUE'),
('DECO_GARDEN',      'Jardín Botánico',           'Un jardín exuberante para una ciudad muy verde.',        'ic_deco_garden',   'DECO_GARDEN'),
('DECO_BRIDGE',      'Puente Panorámico',         'Une los barrios más lejanos de la ciudad.',              'ic_deco_bridge',   'DECO_BRIDGE'),
('DECO_OBELISK',     'Obelisco Conmemorativo',    'Marca los grandes logros de tu ciudad.',                 'ic_deco_obelisk',  'DECO_OBELISK'),
('DECO_BANDSTAND',   'Quiosco de Música',         'Punto de encuentro para una ciudad con buenos servicios.','ic_deco_bandstand','DECO_BANDSTAND'),
('DECO_LIGHTHOUSE',  'Faro del Puerto',           'Homenaje a una red de agua impecable.',                  'ic_deco_lighthouse','DECO_LIGHTHOUSE');

-- ---------- Misiones (30) ----------
INSERT INTO mission (code, title, description, category, orderIndex, rewardXp, rewardBadgeCode) VALUES
('M01', 'Traza la primera calle', 'Toda ciudad empieza con un camino. Coloca tu primera calle.', 'ROAD', 1, 20, 'BADGE_FIRST_ROAD'),
('M02', 'Un techo para empezar', 'Construye tu primera vivienda junto a la calle.', 'HOUSING', 2, 20, 'BADGE_FIRST_HOUSE'),
('M03', 'Conecta tres casas', 'Une al menos 3 casas a la misma red de calles.', 'ROAD', 3, 25, NULL),
('M04', 'Amplía el barrio', 'Construye un bloque de viviendas para más familias.', 'HOUSING', 4, 25, NULL),
('M05', 'La primera escuela', 'Da acceso a la educación construyendo una escuela.', 'EDUCATION', 5, 30, 'BADGE_FIRST_SCHOOL'),
('M06', 'Camino a clase', 'Consigue que al menos 3 casas tengan acceso a la escuela.', 'EDUCATION', 6, 30, NULL),
('M07', 'Salud para todos', 'Construye un centro de salud accesible por carretera.', 'HEALTH', 7, 30, NULL),
('M08', 'Cobertura médica', 'Consigue que 3 casas tengan acceso al centro de salud.', 'HEALTH', 8, 30, NULL),
('M09', 'Un lugar para jugar', 'Crea una plaza verde para el barrio.', 'PARK', 9, 20, NULL),
('M10', 'Ciudad más verde', 'Alcanza una puntuación verde de al menos 40.', 'PARK', 10, 30, NULL),
('M11', 'Agua para el barrio', 'Construye una torre de agua conectada por carretera.', 'WATER', 11, 30, NULL),
('M12', 'Grifo abierto', 'Consigue que 3 casas tengan acceso al agua potable.', 'WATER', 12, 30, NULL),
('M13', 'Primer autobús', 'Instala una parada de autobús en tu ciudad.', 'TRANSPORT', 13, 25, NULL),
('M14', 'Red bien pensada', 'Construye 8 calles formando una sola red conectada.', 'ROAD', 14, 35, NULL),
('M15', 'Movilidad total', 'Consigue que el 60% de tus casas tenga acceso a la calle.', 'ROAD', 15, 35, NULL),
('M16', 'Barrio educado', 'Alcanza un 50% de cobertura educativa en la ciudad.', 'EDUCATION', 16, 35, NULL),
('M17', 'Barrio saludable', 'Alcanza un 50% de cobertura sanitaria en la ciudad.', 'HEALTH', 17, 35, NULL),
('M18', 'Segunda escuela', 'Amplía la educación con una biblioteca.', 'EDUCATION', 18, 25, NULL),
('M19', 'Hospital de ciudad', 'Construye un hospital para reforzar la salud.', 'HEALTH', 19, 40, NULL),
('M20', 'Parque mayor', 'Construye un parque grande en tu ciudad.', 'PARK', 20, 30, NULL),
('M21', 'Agua para todos', 'Alcanza un 60% de cobertura de agua potable.', 'WATER', 21, 35, 'BADGE_WATER_HERO'),
('M22', 'Estación de tren', 'Conecta tu ciudad con una estación de tren.', 'TRANSPORT', 22, 40, NULL),
('M23', 'Presupuesto inteligente', 'Consigue buenos servicios usando menos del 70% del presupuesto.', 'GENERAL', 23, 40, 'BADGE_BUDGET_WIZARD'),
('M24', 'Red ampliada', 'Haz crecer tu red de calles hasta 15 casillas conectadas.', 'ROAD', 24, 40, NULL),
('M25', 'Ciudad conectada', 'Alcanza un 80% de movilidad en toda la ciudad.', 'ROAD', 25, 45, 'BADGE_MOBILITY_MASTER'),
('M26', 'Barrio saludable avanzado', 'Alcanza un 80% de cobertura sanitaria.', 'HEALTH', 26, 45, 'BADGE_HEALTH_HERO'),
('M27', 'Barrio educado avanzado', 'Alcanza un 80% de cobertura educativa.', 'EDUCATION', 27, 45, 'BADGE_EDUCATION_HERO'),
('M28', 'Ciudad muy verde', 'Alcanza una puntuación verde de al menos 70.', 'PARK', 28, 45, NULL),
('M29', 'Todos los servicios', 'Alcanza al menos 70 en la puntuación general de servicios.', 'GENERAL', 29, 50, NULL),
('M30', 'Ciudad ejemplar', 'Combina buena movilidad, servicios y áreas verdes en tu ciudad.', 'GENERAL', 30, 60, NULL);

-- ---------- Requisitos de misión (ejemplos representativos; el resto sigue el mismo patrón, ver DatabaseSeeder.kt) ----------
-- M01: colocar 1 calle
INSERT INTO mission_requirement (missionId, type, requirementKey, targetValue)
SELECT id, 'PLACE_COUNT', 'ROAD_BASIC', 1 FROM mission WHERE code = 'M01';
-- M03: 3 casas + red de al menos 3 casillas
INSERT INTO mission_requirement (missionId, type, requirementKey, targetValue)
SELECT id, 'PLACE_COUNT', 'HOUSE_SMALL', 3 FROM mission WHERE code = 'M03';
INSERT INTO mission_requirement (missionId, type, requirementKey, targetValue)
SELECT id, 'ROAD_NETWORK_SIZE', 'ANY', 3 FROM mission WHERE code = 'M03';
-- M06: cobertura educativa de al menos 3 casas
INSERT INTO mission_requirement (missionId, type, requirementKey, targetValue)
SELECT id, 'COVERAGE_COUNT', 'EDUCATION', 3 FROM mission WHERE code = 'M06';
-- M15: 60% de movilidad
INSERT INTO mission_requirement (missionId, type, requirementKey, targetValue)
SELECT id, 'METRIC_THRESHOLD', 'MOBILITY', 60 FROM mission WHERE code = 'M15';
-- M23: eficiencia de presupuesto + servicios
INSERT INTO mission_requirement (missionId, type, requirementKey, targetValue)
SELECT id, 'BUDGET_EFFICIENCY', 'ANY', 30 FROM mission WHERE code = 'M23';
INSERT INTO mission_requirement (missionId, type, requirementKey, targetValue)
SELECT id, 'METRIC_THRESHOLD', 'SERVICES', 60 FROM mission WHERE code = 'M23';
-- M30: movilidad + servicios + verde combinados
INSERT INTO mission_requirement (missionId, type, requirementKey, targetValue)
SELECT id, 'METRIC_THRESHOLD', 'MOBILITY', 85 FROM mission WHERE code = 'M30';
INSERT INTO mission_requirement (missionId, type, requirementKey, targetValue)
SELECT id, 'METRIC_THRESHOLD', 'SERVICES', 75 FROM mission WHERE code = 'M30';
INSERT INTO mission_requirement (missionId, type, requirementKey, targetValue)
SELECT id, 'METRIC_THRESHOLD', 'GREEN', 60 FROM mission WHERE code = 'M30';
-- Nota: el listado completo de las 30 misiones y sus requisitos exactos vive en
-- app/src/main/kotlin/com/educalab/atumanera/data/seed/MissionSeed.kt y se
-- inserta automáticamente en el primer arranque mediante DatabaseSeeder.kt.

-- ---------- Ejemplo de perfil + ciudad de muestra (4x4, sólo a título ilustrativo) ----------
INSERT INTO user_profile (id, alias, avatarCode, createdAt) VALUES (1, 'Alcalde', 'avatar_1', 1755000000000);
INSERT INTO city (id, userId, name, budgetTotal, rows, cols, createdAt, updatedAt) VALUES (1, 1, 'Mi Ciudad', 2500, 10, 10, 1755000000000, 1755000000000);

-- Cuadrícula de ejemplo reducida (4x4) sólo para pruebas manuales rápidas con sqlite3.
INSERT INTO city_tile (cityId, row, col, buildable) VALUES
(1,0,0,1),(1,0,1,1),(1,0,2,1),(1,0,3,1),
(1,1,0,1),(1,1,1,1),(1,1,2,1),(1,1,3,1),
(1,2,0,1),(1,2,1,1),(1,2,2,1),(1,2,3,1),
(1,3,0,1),(1,3,1,1),(1,3,2,1),(1,3,3,1);

-- Ejemplo: una calle y una casa conectada
INSERT INTO placed_infrastructure (cityId, tileId, infrastructureTypeId, placedAt)
SELECT 1, t.id, it.id, 1755000100000 FROM city_tile t, infrastructure_type it
WHERE t.cityId = 1 AND t.row = 1 AND t.col = 0 AND it.code = 'ROAD_BASIC';

INSERT INTO placed_infrastructure (cityId, tileId, infrastructureTypeId, placedAt)
SELECT 1, t.id, it.id, 1755000200000 FROM city_tile t, infrastructure_type it
WHERE t.cityId = 1 AND t.row = 1 AND t.col = 1 AND it.code = 'HOUSE_SMALL';

INSERT INTO progress (userId, cityId, currentChapter, totalXp, missionsCompleted, updatedAt) VALUES (1, 1, 1, 20, 1, 1755000200000);
