-- =========================================================
-- INSTALACION INICIAL - SISTEMA DE VETERINARIA
-- Compatible con los DAO actuales del proyecto Java.
-- Ejecutar en MySQL Workbench sobre una base nueva.
-- NO es un script de migracion: no modifica tablas existentes.
-- =========================================================

CREATE DATABASE IF NOT EXISTS veterinaria
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE veterinaria;

-- ---------------------------------------------------------
-- PRODUCTOS
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS PRODUCTOS (
    codigo VARCHAR(50) PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    precio DOUBLE NOT NULL,
    stock DOUBLE NOT NULL,
    descripcion VARCHAR(255),
    marca VARCHAR(100),
    activo TINYINT NOT NULL,
    sabor VARCHAR(100),
    peso_kg DOUBLE,
    fecha_vencimiento VARCHAR(20),
    edad_consumo VARCHAR(50),
    animal_consume VARCHAR(50),
    material VARCHAR(100),
    capacidad_ml INT,
    antideslizante TINYINT,
    precio_extra_material DOUBLE,
    recargo_antideslizante DOUBLE,
    tamano VARCHAR(50),
    desmontable TINYINT
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- ANIMALES
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS ANIMALES (
    codigo VARCHAR(50) PRIMARY KEY,
    tipo_animal VARCHAR(20) NOT NULL,
    raza VARCHAR(100) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    edad INT NOT NULL,
    peso DOUBLE NOT NULL,
    sexo VARCHAR(20),
    nombre_dueno VARCHAR(100),
    pedigri TINYINT NOT NULL,
    tipo_pelaje VARCHAR(50),
    requiere_bozal TINYINT NULL,
    nivel_adiestramiento VARCHAR(100),
    requiere_transportadora TINYINT NULL,
    temperamento VARCHAR(100)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- EMPLEADOS
-- Veterinarios y recepcionistas se almacenan en una tabla.
-- Columnas especificas quedan NULL cuando no corresponden.
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS EMPLEADOS (
    id_empleado VARCHAR(50) PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    telefono VARCHAR(20) NOT NULL,
    tiempo_completo TINYINT NOT NULL,
    especialidad VARCHAR(100) NULL,
    disponible TINYINT NULL,
    turno VARCHAR(50) NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- CONSULTAS
-- Se crea al final porque depende de ANIMALES y EMPLEADOS.
-- ---------------------------------------------------------
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
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- DATOS DE EJEMPLO
-- INSERT IGNORE evita insertar de nuevo códigos ya existentes.
-- ---------------------------------------------------------

INSERT IGNORE INTO PRODUCTOS
(codigo, tipo, nombre, precio, stock, descripcion, marca, activo,
 sabor, peso_kg, fecha_vencimiento, edad_consumo, animal_consume)
VALUES
('CRQ001', 'Croquetas', 'Croquetas para perro adulto', 75.00, 20,
 'Alimento seco para perro adulto', 'PetVida', 1,
 'Pollo', 3.00, '2027-12-31', 'Adulto', 'Perro');

INSERT IGNORE INTO PRODUCTOS
(codigo, tipo, nombre, precio, stock, descripcion, marca, activo,
 material, capacidad_ml, antideslizante, precio_extra_material, recargo_antideslizante)
VALUES
('PLA001', 'Plato', 'Plato antideslizante', 18.00, 15,
 'Plato para alimento', 'AnimalHome', 1,
 'Acero', 500, 1, 3.00, 2.00);

INSERT IGNORE INTO PRODUCTOS
(codigo, tipo, nombre, precio, stock, descripcion, marca, activo,
 tamano, material, desmontable, precio_extra_material)
VALUES
('CAMA001', 'Cama', 'Cama acolchada mediana', 95.00, 8,
 'Cama lavable para mascota', 'AnimalHome', 1,
 'Mediana', 'Tela', 1, 10.00);

INSERT IGNORE INTO ANIMALES
(codigo, tipo_animal, raza, nombre, edad, peso, sexo, nombre_dueno,
 pedigri, tipo_pelaje, requiere_bozal, nivel_adiestramiento)
VALUES
('ANM001', 'Perro', 'Labrador', 'Simba', 3, 25.5, 'Macho',
 'Carlos Díaz', 1, 'Corto', 0, 'Intermedio');

INSERT IGNORE INTO ANIMALES
(codigo, tipo_animal, raza, nombre, edad, peso, sexo, nombre_dueno,
 pedigri, tipo_pelaje, requiere_transportadora, temperamento)
VALUES
('ANM002', 'Gato', 'Persa', 'Luna', 2, 4.2, 'Hembra',
 'Ana Torres', 0, 'Largo', 1, 'Tranquilo');

INSERT IGNORE INTO ANIMALES
(codigo, tipo_animal, raza, nombre, edad, peso, sexo, nombre_dueno,
 pedigri, tipo_pelaje, requiere_bozal, nivel_adiestramiento)
VALUES
('ANM003', 'Perro', 'Mestizo', 'Max', 5, 18.0, 'Macho',
 'Jorge Silva', 0, 'Medio', 1, 'Básico');

INSERT IGNORE INTO EMPLEADOS
(id_empleado, tipo, nombre, dni, telefono, tiempo_completo,
 especialidad, disponible, turno)
VALUES
('EMP001', 'Veterinario', 'Carlos Gómez', '40123456', '987654321',
 1, 'Medicina general', 1, NULL),
('EMP002', 'Recepcionista', 'Ana Torres', '40234567', '912345678',
 1, NULL, NULL, 'Mañana'),
('EMP003', 'Veterinario', 'Luis Pérez', '40345678', '998877665',
 0, 'Cirugía', 1, NULL),
('EMP004', 'Recepcionista', 'María Rojas', '40456789', '976543210',
 0, NULL, NULL, 'Tarde');

-- No se insertan consultas de ejemplo para evitar ocupar
-- veterinarios desde el inicio. La aplicación las registra.
