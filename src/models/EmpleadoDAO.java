package models;

import services.ConexionBDService;
import services.UtilidadesService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class EmpleadoDAO {

    public static final String TIPO_VETERINARIO = "Veterinario";
    public static final String TIPO_RECEPCIONISTA = "Recepcionista";

    private static final String SQL_INSERT = "INSERT INTO EMPLEADOS (id_empleado, tipo, nombre, dni, "
            + "telefono, tiempo_completo, especialidad, disponible, turno) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public void crearTabla() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS EMPLEADOS ("
                + "id_empleado VARCHAR(50) PRIMARY KEY, "
                + "tipo VARCHAR(20) NOT NULL, "
                + "nombre VARCHAR(100) NOT NULL, "
                + "dni VARCHAR(20) NOT NULL UNIQUE, "
                + "telefono VARCHAR(20) NOT NULL, "
                + "tiempo_completo TINYINT NOT NULL, "
                + "especialidad VARCHAR(100) NULL, "
                + "disponible TINYINT NULL, "
                + "turno VARCHAR(50) NULL"
                + ")";

        try (Connection conexion = ConexionBDService.conectar();
             Statement sentencia = conexion.createStatement()) {
            sentencia.executeUpdate(sql);
        }
    }

    public void insertarEjemplosSiVacia() throws SQLException {
        String sql = "SELECT COUNT(*) FROM EMPLEADOS";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet resultado = ps.executeQuery()) {

            if (resultado.next() && resultado.getInt(1) > 0) {
                return;
            }
        }

        registrarEmpleado(new VeterinarioModel(
                "EMP001", "Carlos Gómez", "40123456", "987654321",
                true, "Medicina general", true));

        registrarEmpleado(new RecepcionistaModel(
                "EMP002", "Ana Torres", "40234567", "912345678",
                true, "Mañana"));

        registrarEmpleado(new VeterinarioModel(
                "EMP003", "Luis Pérez", "40345678", "998877665",
                false, "Cirugía", true));

        registrarEmpleado(new RecepcionistaModel(
                "EMP004", "María Rojas", "40456789", "976543210",
                false, "Tarde"));
    }

    public boolean registrarEmpleado(EmpleadoModel empleado) throws SQLException {
        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(SQL_INSERT)) {

            ps.setString(1, empleado.getCodigo());
            ps.setString(3, empleado.getNombre());
            ps.setString(4, empleado.getDni());
            ps.setString(5, empleado.getTelefono());
            ps.setInt(6, empleado.isTiempoCompleto() ? 1 : 0);

            if (empleado instanceof VeterinarioModel vet) {
                ps.setString(2, TIPO_VETERINARIO);
                ps.setString(7, vet.getEspecialidad());
                ps.setInt(8, vet.isDisponible() ? 1 : 0);
                UtilidadesService.setStringONull(ps, 9, null);

            } else if (empleado instanceof RecepcionistaModel rec) {
                ps.setString(2, TIPO_RECEPCIONISTA);
                UtilidadesService.setStringONull(ps, 7, null);
                UtilidadesService.setBooleanONull(ps, 8, null);
                ps.setString(9, rec.getTurno());

            } else {
                throw new SQLException("Tipo de empleado no soportado.");
            }

            return ps.executeUpdate() > 0;
        }
    }

    public ArrayList<EmpleadoModel> listarEmpleados() throws SQLException {
        ArrayList<EmpleadoModel> empleados = new ArrayList<>();
        String sql = "SELECT * FROM EMPLEADOS ORDER BY id_empleado";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet resultado = ps.executeQuery()) {

            while (resultado.next()) {
                empleados.add(convertirEmpleado(resultado));
            }
        }

        return empleados;
    }

    public EmpleadoModel buscarEmpleado(String codigo) throws SQLException {
        String sql = "SELECT * FROM EMPLEADOS WHERE id_empleado = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet resultado = ps.executeQuery()) {
                if (resultado.next()) {
                    return convertirEmpleado(resultado);
                }
            }
        }

        return null;
    }

    public boolean existeEmpleado(String codigo) throws SQLException {
        String sql = "SELECT 1 FROM EMPLEADOS WHERE id_empleado = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet resultado = ps.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public boolean actualizarEmpleado(EmpleadoModel empleado) throws SQLException {
        String sql = "UPDATE EMPLEADOS SET nombre = ?, dni = ?, telefono = ?, "
                + "tiempo_completo = ?, especialidad = ?, turno = ? WHERE id_empleado = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getDni());
            ps.setString(3, empleado.getTelefono());
            ps.setInt(4, empleado.isTiempoCompleto() ? 1 : 0);

            if (empleado instanceof VeterinarioModel vet) {
                ps.setString(5, vet.getEspecialidad());
                UtilidadesService.setStringONull(ps, 6, null);

            } else if (empleado instanceof RecepcionistaModel rec) {
                UtilidadesService.setStringONull(ps, 5, null);
                ps.setString(6, rec.getTurno());

            } else {
                throw new SQLException("Tipo de empleado no soportado.");
            }

            ps.setString(7, empleado.getCodigo());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminarEmpleado(String codigo) throws SQLException {
        String sql = "DELETE FROM EMPLEADOS WHERE id_empleado = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);
            return ps.executeUpdate() > 0;
        }
    }

    private EmpleadoModel convertirEmpleado(ResultSet resultado) throws SQLException {
        String tipo = resultado.getString("tipo");
        String codigo = resultado.getString("id_empleado");
        String nombre = resultado.getString("nombre");
        String dni = resultado.getString("dni");
        String telefono = resultado.getString("telefono");
        boolean tiempoCompleto = resultado.getInt("tiempo_completo") == 1;

        if (TIPO_VETERINARIO.equals(tipo)) {
            return new VeterinarioModel(
                    codigo, nombre, dni, telefono, tiempoCompleto,
                    resultado.getString("especialidad"),
                    resultado.getInt("disponible") == 1
            );
        }

        return new RecepcionistaModel(
                codigo, nombre, dni, telefono, tiempoCompleto,
                resultado.getString("turno")
        );
    }
}