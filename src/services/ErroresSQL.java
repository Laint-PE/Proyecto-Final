package services;

/**
 * Códigos de error de MySQL usados en el proyecto (evita números mágicos).
 */
public final class ErroresSQL {
    public static final int DUPLICADO = 1062;        // clave primaria o única repetida
    public static final int LLAVE_FORANEA = 1451;    // no se puede eliminar: tiene registros relacionados

    private ErroresSQL() {
    }
}