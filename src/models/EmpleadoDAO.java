package models;

import services.ConexionBDService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class EmpleadoDAO {

    public void crearTabla() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS EMPLEADOS ("
                + "id_empleado VARCHAR(50) PRIMARY KEY, "
                + "nombre VARCHAR(100) NOT NULL, "
                + "apellido VARCHAR(100) NOT NULL, "
                + "cargo VARCHAR(100) NOT NULL, "
                + "sueldo DOUBLE NOT NULL, "
                + "fecha_contratacion VARCHAR(20), "
                + "telefono VARCHAR(20), "
                + "email VARCHAR(100), "
                + "activo TINYINT NOT NULL, "
                + "disponible TINYINT NOT NULL DEFAULT 1"
                + ")";

        try (Connection conexion = ConexionBDService.conectar();
             Statement sentencia = conexion.createStatement()) {
            sentencia.executeUpdate(sql);
        }
    }

    public void insertarEjemplosSiVacia() throws SQLException {
        int cantidad = 0;

        try (Connection conexion = ConexionBDService.conectar();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery("SELECT COUNT(*) FROM EMPLEADOS")) {

            if (resultado.next()) {
                cantidad = resultado.getInt(1);
            }
        }

        if (cantidad > 0) {
            return;
        }

        String sql = "INSERT INTO EMPLEADOS (id_empleado, nombre, apellido, cargo, sueldo, "
                + "fecha_contratacion, telefono, email, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            insertarFila(ps, "EMP001", "Carlos", "Gómez", "Veterinario",
                    2500, "2023-01-15", "987654321", "carlos.gomez@veterinaria.pe", true);

            insertarFila(ps, "EMP002", "Ana", "Torres", "Recepcionista",
                    1500, "2023-05-10", "912345678", "ana.torres@veterinaria.pe", true);

            insertarFila(ps, "EMP003", "Luis", "Pérez", "Asistente",
                    1200, "2024-02-01", "998877665", "luis.perez@veterinaria.pe", true);

            insertarFila(ps, "EMP004", "María", "Rojas", "Administradora",
                    2800, "2022-11-01", "976543210", "maria.rojas@veterinaria.pe", true);

            insertarFila(ps, "EMP005", "Jorge", "Silva", "Peluquero Canino",
                    1600, "2023-08-20", "954321098", "jorge.silva@veterinaria.pe", true);
        }
    }

    private void insertarFila(PreparedStatement ps, String id, String nombre,
                              String apellido, String cargo, double sueldo,
                              String fecha, String telefono, String email,
                              boolean activo) throws SQLException {

        ps.setString(1, id);
        ps.setString(2, nombre);
        ps.setString(3, apellido);
        ps.setString(4, cargo);
        ps.setDouble(5, sueldo);
        ps.setString(6, fecha);
        ps.setString(7, telefono);
        ps.setString(8, email);
        ps.setInt(9, activo ? 1 : 0);
        ps.executeUpdate();
    }

    public boolean registrarEmpleado(String id, String nombre, String apellido,
                                     String cargo, double sueldo, String fecha,
                                     String telefono, String email, boolean activo)
            throws SQLException {

        String sql = "INSERT INTO EMPLEADOS (id_empleado, nombre, apellido, cargo, sueldo, "
                + "fecha_contratacion, telefono, email, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setString(2, nombre);
            ps.setString(3, apellido);
            ps.setString(4, cargo);
            ps.setDouble(5, sueldo);
            ps.setString(6, fecha);
            ps.setString(7, telefono);
            ps.setString(8, email);
            ps.setInt(9, activo ? 1 : 0);

            return ps.executeUpdate() > 0;
        }
    }

    public ArrayList<String[]> listarEmpleados() throws SQLException {
        ArrayList<String[]> empleados = new ArrayList<>();
        String sql = "SELECT * FROM EMPLEADOS ORDER BY id_empleado";

        try (Connection conexion = ConexionBDService.conectar();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {

            while (resultado.next()) {
                empleados.add(convertirEmpleado(resultado));
            }
        }

        return empleados;
    }

    public String[] buscarEmpleado(String id) throws SQLException {
        String sql = "SELECT * FROM EMPLEADOS WHERE id_empleado = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, id);

            try (ResultSet resultado = ps.executeQuery()) {
                if (resultado.next()) {
                    return convertirEmpleado(resultado);
                }
            }
        }

        return null;
    }

    public boolean actualizarEmpleado(String id, String nombre, String apellido,
                                      String cargo, double sueldo, String telefono,
                                      String email, boolean activo) throws SQLException {

        String sql = "UPDATE EMPLEADOS SET nombre = ?, apellido = ?, cargo = ?, "
                + "sueldo = ?, telefono = ?, email = ?, activo = ? WHERE id_empleado = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, cargo);
            ps.setDouble(4, sueldo);
            ps.setString(5, telefono);
            ps.setString(6, email);
            ps.setInt(7, activo ? 1 : 0);
            ps.setString(8, id);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminarEmpleado(String id) throws SQLException {
        String sql = "DELETE FROM EMPLEADOS WHERE id_empleado = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean existeEmpleado(String id) throws SQLException {
        String sql = "SELECT 1 FROM EMPLEADOS WHERE id_empleado = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, id);

            try (ResultSet resultado = ps.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public void asegurarColumnaDisponible() throws SQLException {
        boolean existe = false;

        try (Connection conexion = ConexionBDService.conectar();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(
                     "SHOW COLUMNS FROM EMPLEADOS LIKE 'disponible'")) {

            existe = resultado.next();
        }

        if (!existe) {
            try (Connection conexion = ConexionBDService.conectar();
                 Statement sentencia = conexion.createStatement()) {

                sentencia.executeUpdate(
                        "ALTER TABLE EMPLEADOS ADD COLUMN disponible TINYINT NOT NULL DEFAULT 1"
                );
            }
        }
    }

    private String[] convertirEmpleado(ResultSet resultado) throws SQLException {
        return new String[]{
                resultado.getString("id_empleado"),
                resultado.getString("nombre"),
                resultado.getString("apellido"),
                resultado.getString("cargo"),
                String.valueOf(resultado.getDouble("sueldo")),
                resultado.getString("fecha_contratacion"),
                resultado.getString("telefono"),
                resultado.getString("email"),
                resultado.getInt("activo") == 1 ? "Sí" : "No",
                resultado.getInt("disponible") == 1 ? "Sí" : "No"
        };
    }
}
