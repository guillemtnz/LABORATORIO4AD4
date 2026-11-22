-- ============================================================================
-- SCRIPT DE LIMPIEZA Y REINICIO DE DATOS (SQLITE - ESTRATEGIA JOINED)
-- ============================================================================

-- 1. Desactivamos temporalmente claves foráneas para evitar bloqueos al vaciar
PRAGMA foreign_keys = OFF;

-- 2. Borrado de registros respetando el orden jerárquico (primero hijas, luego padre)
DELETE FROM destructores_estelares;
DELETE FROM cazas_ligeros;
DELETE FROM naves;
DELETE FROM planetas;

-- 3. Reinicio de los contadores AUTOINCREMENT de SQLite para que los IDs vuelvan a 1
DELETE FROM sqlite_sequence WHERE name IN ('naves', 'planetas');

-- 4. Reactivamos la integridad referencial
PRAGMA foreign_keys = ON;

-- 5. Inserción exclusiva de los planetas de referencia necesarios para el CSV
INSERT INTO planetas (planeta_id, nombre, clima, afiliacion) VALUES
(1, 'Coruscant', 'Templado', 'Imperio'),
(2, 'Yavin IV', 'Jungla', 'Alianza Rebelde'),
(3, 'Tatooine', 'Árido', 'Neutral');

-- Verificación: Las tablas de naves deben devolver 0 registros
SELECT COUNT(*) AS total_naves FROM naves;
SELECT COUNT(*) AS total_planetas FROM planetas;
