USE veterinaria;


ALTER TABLE EMPLEADOS
    ADD COLUMN IF NOT EXISTS disponible TINYINT NOT NULL DEFAULT 1;

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
