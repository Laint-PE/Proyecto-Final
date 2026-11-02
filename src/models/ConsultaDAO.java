package models;

import services.ConexionBDService;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class ConsultaDAO {

    public void prepararTablas() throws SQLException {
        AnimalDAO animalDAO = new AnimalDAO();
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();

        animalDAO.crearTabla();
        animalDAO.insertarEjemplosSiVacia();

        empleadoDAO.crearTabla();
        empleadoDAO.insertarEjemplosSiVacia();
        empleadoDAO.asegurarColumnaDisponible();

        crearTabla();
    }

    public void crearTabla() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS CONSULTAS ("
                + "codigo VARCHAR(50) PRIMARY KEY, "
                + "fecha DATE NOT NULL, "
                + "motivo VARCHAR(255) NOT NULL, "
                + "id_animal VARCHAR(50) NOT NULL, "
                + "id_veterinario VARCHAR(50) NOT NULL, "
                + "id_recepcionista VARCHAR(50) NOT NULL, "
                + "CONSTRAINT fk_consulta_animal FOREIGN KEY (id_animal) REFERENCES ANIMALES(codigo), "
                + "CONSTRAINT fk_consulta_veterinario FOREIGN KEY (id_veterinario) REFERENCES EMPLEADOS(id_empleado), "
                + "CONSTRAINT fk_consulta_recepcionista FOREIGN KEY (id_recepcionista) REFERENCES EMPLEADOS(id_empleado)"
                + ")";

        try (Connection conexion = ConexionBDService.conectar();
             Statement sentencia = conexion.createStatement()) {
            sentencia.executeUpdate(sql);
        }
    }

    public boolean existeConsulta(String codigo) throws SQLException {
        String sql = "SELECT 1 FROM CONSULTAS WHERE codigo = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet resultado = ps.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public boolean existeAnimal(String codigo) throws SQLException {
        String sql = "SELECT 1 FROM ANIMALES WHERE codigo = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet resultado = ps.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public boolean veterinarioDisponible(String codigo) throws SQLException {
        String sql = "SELECT 1 FROM EMPLEADOS WHERE id_empleado = ? "
                + "AND cargo = 'Veterinario' AND activo = 1 AND disponible = 1";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet resultado = ps.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public boolean recepcionistaActivo(String codigo) throws SQLException {
        String sql = "SELECT 1 FROM EMPLEADOS WHERE id_empleado = ? "
                + "AND cargo = 'Recepcionista' AND activo = 1";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet resultado = ps.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public ArrayList<String[]> listarAnimales() throws SQLException {
        ArrayList<String[]> animales = new ArrayList<>();
        String sql = "SELECT codigo, nombre, tipo_animal FROM ANIMALES ORDER BY codigo";

        try (Connection conexion = ConexionBDService.conectar();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {

            while (resultado.next()) {
                animales.add(new String[]{
                        resultado.getString("codigo"),
                        resultado.getString("nombre"),
                        resultado.getString("tipo_animal")
                });
            }
        }

        return animales;
    }

    public ArrayList<String[]> listarVeterinariosDisponibles() throws SQLException {
        ArrayList<String[]> empleados = new ArrayList<>();

        String sql = "SELECT id_empleado, nombre, apellido FROM EMPLEADOS "
                + "WHERE cargo = 'Veterinario' AND activo = 1 AND disponible = 1 "
                + "ORDER BY id_empleado";

        try (Connection conexion = ConexionBDService.conectar();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {

            while (resultado.next()) {
                empleados.add(new String[]{
                        resultado.getString("id_empleado"),
                        resultado.getString("nombre"),
                        resultado.getString("apellido")
                });
            }
        }

        return empleados;
    }

    public ArrayList<String[]> listarRecepcionistas() throws SQLException {
        ArrayList<String[]> empleados = new ArrayList<>();

        String sql = "SELECT id_empleado, nombre, apellido FROM EMPLEADOS "
                + "WHERE cargo = 'Recepcionista' AND activo = 1 ORDER BY id_empleado";

        try (Connection conexion = ConexionBDService.conectar();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {

            while (resultado.next()) {
                empleados.add(new String[]{
                        resultado.getString("id_empleado"),
                        resultado.getString("nombre"),
                        resultado.getString("apellido")
                });
            }
        }

        return empleados;
    }

    public boolean registrarConsulta(String codigo, LocalDate fecha, String motivo,
                                     String codigoAnimal, String codigoVet,
                                     String codigoRec) throws SQLException {

        String sqlInsert = "INSERT INTO CONSULTAS (codigo, fecha, motivo, id_animal, "
                + "id_veterinario, id_recepcionista) VALUES (?, ?, ?, ?, ?, ?)";

        String sqlOcupar = "UPDATE EMPLEADOS SET disponible = 0 "
                + "WHERE id_empleado = ? AND cargo = 'Veterinario' "
                + "AND activo = 1 AND disponible = 1";

        try (Connection conexion = ConexionBDService.conectar()) {
            conexion.setAutoCommit(false);

            try {
                try (PreparedStatement ps = conexion.prepareStatement(sqlInsert)) {
                    ps.setString(1, codigo);
                    ps.setDate(2, Date.valueOf(fecha));
                    ps.setString(3, motivo);
                    ps.setString(4, codigoAnimal);
                    ps.setString(5, codigoVet);
                    ps.setString(6, codigoRec);
                    ps.executeUpdate();
                }

                int filas;

                try (PreparedStatement ps = conexion.prepareStatement(sqlOcupar)) {
                    ps.setString(1, codigoVet);
                    filas = ps.executeUpdate();
                }

                if (filas != 1) {
                    conexion.rollback();
                    return false;
                }

                conexion.commit();
                return true;

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }

    public ArrayList<String[]> listarConsultas() throws SQLException {
        ArrayList<String[]> consultas = new ArrayList<>();

        String sql = "SELECT c.codigo, c.fecha, c.motivo, a.nombre AS animal, "
                + "a.tipo_animal, CONCAT(v.nombre, ' ', v.apellido) AS veterinario, "
                + "CONCAT(r.nombre, ' ', r.apellido) AS recepcionista "
                + "FROM CONSULTAS c "
                + "JOIN ANIMALES a ON c.id_animal = a.codigo "
                + "JOIN EMPLEADOS v ON c.id_veterinario = v.id_empleado "
                + "JOIN EMPLEADOS r ON c.id_recepcionista = r.id_empleado "
                + "ORDER BY c.codigo";

        try (Connection conexion = ConexionBDService.conectar();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {

            while (resultado.next()) {
                consultas.add(convertirConsulta(resultado));
            }
        }

        return consultas;
    }

    public String[] buscarConsulta(String codigo) throws SQLException {
        String sql = "SELECT c.codigo, c.fecha, c.motivo, a.nombre AS animal, "
                + "a.tipo_animal, CONCAT(v.nombre, ' ', v.apellido) AS veterinario, "
                + "CONCAT(r.nombre, ' ', r.apellido) AS recepcionista "
                + "FROM CONSULTAS c "
                + "JOIN ANIMALES a ON c.id_animal = a.codigo "
                + "JOIN EMPLEADOS v ON c.id_veterinario = v.id_empleado "
                + "JOIN EMPLEADOS r ON c.id_recepcionista = r.id_empleado "
                + "WHERE c.codigo = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet resultado = ps.executeQuery()) {
                if (resultado.next()) {
                    return convertirConsulta(resultado);
                }
            }
        }

        return null;
    }

    public boolean eliminarConsulta(String codigo) throws SQLException {
        String sqlBuscar = "SELECT id_veterinario FROM CONSULTAS WHERE codigo = ?";
        String sqlEliminar = "DELETE FROM CONSULTAS WHERE codigo = ?";
        String sqlLiberar = "UPDATE EMPLEADOS SET disponible = 1 WHERE id_empleado = ?";

        try (Connection conexion = ConexionBDService.conectar()) {
            conexion.setAutoCommit(false);

            try {
                String idVeterinario = null;

                try (PreparedStatement ps = conexion.prepareStatement(sqlBuscar)) {
                    ps.setString(1, codigo);

                    try (ResultSet resultado = ps.executeQuery()) {
                        if (resultado.next()) {
                            idVeterinario = resultado.getString("id_veterinario");
                        }
                    }
                }

                if (idVeterinario == null) {
                    conexion.rollback();
                    return false;
                }

                try (PreparedStatement ps = conexion.prepareStatement(sqlEliminar)) {
                    ps.setString(1, codigo);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conexion.prepareStatement(sqlLiberar)) {
                    ps.setString(1, idVeterinario);
                    ps.executeUpdate();
                }

                conexion.commit();
                return true;

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }

    private String[] convertirConsulta(ResultSet resultado) throws SQLException {
        return new String[]{
                resultado.getString("codigo"),
                resultado.getString("fecha"),
                resultado.getString("motivo"),
                resultado.getString("animal"),
                resultado.getString("tipo_animal"),
                resultado.getString("veterinario"),
                resultado.getString("recepcionista")
        };
    }
}
