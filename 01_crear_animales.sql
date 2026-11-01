USE veterinaria;

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
);

INSERT IGNORE INTO ANIMALES
(codigo, tipo_animal, raza, nombre, edad, peso, sexo, nombre_dueno, pedigri, tipo_pelaje, requiere_bozal, nivel_adiestramiento)
VALUES
('ANM001', 'Perro', 'Labrador', 'Simba', 3, 25.5, 'Macho', 'Carlos Díaz', 1, 'Corto', 0, 'Intermedio');

INSERT IGNORE INTO ANIMALES
(codigo, tipo_animal, raza, nombre, edad, peso, sexo, nombre_dueno, pedigri, tipo_pelaje, requiere_transportadora, temperamento)
VALUES
('ANM002', 'Gato', 'Persa', 'Luna', 2, 4.2, 'Hembra', 'Ana Torres', 0, 'Largo', 1, 'Tranquilo');

INSERT IGNORE INTO ANIMALES
(codigo, tipo_animal, raza, nombre, edad, peso, sexo, nombre_dueno, pedigri, tipo_pelaje, requiere_bozal, nivel_adiestramiento)
VALUES
('ANM003', 'Perro', 'Mestizo', 'Max', 5, 18.0, 'Macho', 'Jorge Silva', 0, 'Medio', 1, 'Básico');
