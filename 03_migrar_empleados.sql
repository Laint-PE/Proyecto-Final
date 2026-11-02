-- Migración: alinea EMPLEADOS con EmpleadoModel / VeterinarioModel / RecepcionistaModel.
-- ADVERTENCIA: borra las consultas y los empleados actuales (CONSULTAS depende de EMPLEADOS).
USE veterinaria;

DROP TABLE IF EXISTS CONSULTAS;
DROP TABLE IF EXISTS EMPLEADOS;

CREATE TABLE EMPLEADOS (
    id_empleado VARCHAR(50) PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL,              -- 'Veterinario' o 'Recepcionista'
    nombre VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    telefono VARCHAR(20) NOT NULL,
    tiempo_completo TINYINT NOT NULL,
    especialidad VARCHAR(100) NULL,         -- solo veterinarios
    disponible TINYINT NULL,                -- solo veterinarios
    turno VARCHAR(50) NULL                  -- solo recepcionistas
);

INSERT INTO EMPLEADOS
(id_empleado, tipo, nombre, dni, telefono, tiempo_completo, especialidad, disponible, turno)
VALUES
('EMP001', 'Veterinario',   'Carlos Gómez', '40123456', '987654321', 1, 'Medicina general', 1, NULL),
('EMP002', 'Recepcionista', 'Ana Torres',   '40234567', '912345678', 1, NULL, NULL, 'Mañana'),
('EMP003', 'Veterinario',   'Luis Pérez',   '40345678', '998877665', 0, 'Cirugía', 1, NULL),
('EMP004', 'Recepcionista', 'María Rojas',  '40456789', '976543210', 0, NULL, NULL, 'Tarde');

-- CONSULTAS se vuelve a crear sola al abrir "Gestionar consultas",
-- pero puedes crearla ya con este bloque:
CREATE TABLE IF NOT EXISTS CONSULTAS (
    codigo VARCHAR(50) PRIMARY KEY,
    fecha DATE NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    id_animal VARCHAR(50) NOT NULL,
    id_veterinario VARCHAR(50) NOT NULL,
    id_recepcionista VARCHAR(50) NOT NULL,
    CONSTRAINT fk_consulta_animal
        FOREIGN KEY (id_animal) REFERENCES ANIMALES(codigo),
    CONSTRAINT fk_consulta_veterinario
        FOREIGN KEY (id_veterinario) REFERENCES EMPLEADOS(id_empleado),
    CONSTRAINT fk_consulta_recepcionista
        FOREIGN KEY (id_recepcionista) REFERENCES EMPLEADOS(id_empleado)
);
