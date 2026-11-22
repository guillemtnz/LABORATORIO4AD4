-- ============================================================================
-- SCRIPT DE INICIALIZACIÓN: SISTEMA HOLONET (SQLITE - ESTRATEGIA JOINED)
-- ============================================================================

PRAGMA foreign_keys = OFF;

DROP TABLE IF EXISTS destructores_estelares;
DROP TABLE IF EXISTS cazas_ligeros;
DROP TABLE IF EXISTS naves;
DROP TABLE IF EXISTS planetas;

PRAGMA foreign_keys = ON;

-- 1. Tabla de Planetas
CREATE TABLE planetas (
    planeta_id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE,
    clima TEXT,
    afiliacion TEXT DEFAULT 'Neutral'
);

-- 2. Tabla Padre de la Jerarquía (Datos comunes a cualquier nave)
CREATE TABLE naves (
    nave_id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    modelo TEXT NOT NULL,
    estado_sistema TEXT NOT NULL, -- 'Critico', 'Estable', 'Mantenimiento'
    planeta_origen_id INTEGER,
    FOREIGN KEY (planeta_origen_id) REFERENCES planetas(planeta_id) ON DELETE SET NULL
);

-- 3. Tabla Hija 1: Cazas Ligeros (Comparte PK con 'naves')
CREATE TABLE cazas_ligeros (
    caza_id INTEGER PRIMARY KEY,
    potencia_fuego INTEGER NOT NULL,
    FOREIGN KEY (caza_id) REFERENCES naves(nave_id) ON DELETE CASCADE
);

-- 4. Tabla Hija 2: Destructores Estelares (Comparte PK con 'naves')
CREATE TABLE destructores_estelares (
    destructor_id INTEGER PRIMARY KEY,
    capacidad_escudos INTEGER NOT NULL,
    FOREIGN KEY (destructor_id) REFERENCES naves(nave_id) ON DELETE CASCADE
);

-- ============================================================================
-- INSERCIÓN DE DATOS DE CONTROL
-- ============================================================================

INSERT INTO planetas (planeta_id, nombre, clima, afiliacion) VALUES
(1, 'Coruscant', 'Templado', 'Imperio'),
(2, 'Yavin IV', 'Jungla', 'Alianza Rebelde'),
(3, 'Tatooine', 'Árido', 'Neutral');

-- Registros en tabla padre 'naves'
INSERT INTO naves (nave_id, nombre, modelo, estado_sistema, planeta_origen_id) VALUES
(1, 'Caza X-Wing', 'T-65B', 'Estable', 2),
(2, 'Destructor Estelar', 'Clase Imperial I', 'Estable', 1),
(3, 'Superdestructor Ejecutor', 'Clase Ejecutor', 'Critico', 1),
(4, 'Caza TIE Alfa', 'TIE/ln', 'Critico', 1),
(5, 'Halcón Milenario', 'YT-1300', 'Mantenimiento', 3);

-- Registros específicos en 'cazas_ligeros' (IDs 1, 4 y 5)
INSERT INTO cazas_ligeros (caza_id, potencia_fuego) VALUES
(1, 85),
(4, 40),
(5, 120);

-- Registros específicos en 'destructores_estelares' (IDs 2 y 3)
INSERT INTO destructores_estelares (destructor_id, capacidad_escudos) VALUES
(2, 9500),
(3, 45000);
