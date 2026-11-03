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

    private static final String SQL_SELECT = "SELECT codigo, fecha, motivo, id_animal, "
            + "id_veterinario, id_recepcionista FROM CONSULTAS";

    private final AnimalDAO animalDAO = new AnimalDAO();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    public void prepararTablas() throws SQLException {
        animalDAO.crearTabla();
        animalDAO.insertarEjemplosSiVacia();

        empleadoDAO.crearTabla();
        empleadoDAO.insertarEjemplosSiVacia();

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

    public boolean registrarConsulta(ConsultaModel consulta) throws SQLException {
        String sqlInsert = "INSERT INTO CONSULTAS (codigo, fecha, motivo, id_animal, "
                + "id_veterinario, id_recepcionista) VALUES (?, ?, ?, ?, ?, ?)";

        String sqlOcupar = "UPDATE EMPLEADOS SET disponible = 0 "
                + "WHERE id_empleado = ? AND tipo = 'Veterinario' AND disponible = 1";

        try (Connection conexion = ConexionBDService.conectar()) {
            conexion.setAutoCommit(false);

            try {
                try (PreparedStatement ps = conexion.prepareStatement(sqlInsert)) {
                    ps.setString(1, consulta.getCodigo());
                    ps.setDate(2, Date.valueOf(LocalDate.parse(consulta.getFecha())));
                    ps.setString(3, consulta.getMotivo());
                    ps.setString(4, consulta.getAnimal().getCodigo());
                    ps.setString(5, consulta.getVeterinario().getCodigo());
                    ps.setString(6, consulta.getRecepcionista().getCodigo());
                    ps.executeUpdate();
                }

                int filas;

                try (PreparedStatement ps = conexion.prepareStatement(sqlOcupar)) {
                    ps.setString(1, consulta.getVeterinario().getCodigo());
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

    public ArrayList<ConsultaModel> listarConsultas() throws SQLException {
        ArrayList<String[]> filas;
        String sql = SQL_SELECT + " ORDER BY codigo";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet resultado = ps.executeQuery()) {

            filas = leerFilas(resultado);
        }

        ArrayList<ConsultaModel> consultas = new ArrayList<>();

        for (String[] fila : filas) {
            consultas.add(construirConsulta(fila));
        }

        return consultas;
    }

    public ConsultaModel buscarConsulta(String codigo) throws SQLException {
        ArrayList<String[]> filas;
        String sql = SQL_SELECT + " WHERE codigo = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet resultado = ps.executeQuery()) {
                filas = leerFilas(resultado);
            }
        }

        if (filas.isEmpty()) {
            return null;
        }

        return construirConsulta(filas.get(0));
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


    private ArrayList<String[]> leerFilas(ResultSet resultado) throws SQLException {
        ArrayList<String[]> filas = new ArrayList<>();

        while (resultado.next()) {
            filas.add(new String[]{
                    resultado.getString("codigo"),
                    resultado.getString("fecha"),
                    resultado.getString("motivo"),
                    resultado.getString("id_animal"),
                    resultado.getString("id_veterinario"),
                    resultado.getString("id_recepcionista")
            });
        }

        return filas;
    }

    private ConsultaModel construirConsulta(String[] fila) throws SQLException {
        AnimalModel animal = animalDAO.buscarAnimal(fila[3]);
        EmpleadoModel veterinario = empleadoDAO.buscarEmpleado(fila[4]);
        EmpleadoModel recepcionista = empleadoDAO.buscarEmpleado(fila[5]);

        if (animal == null
                || !(veterinario instanceof VeterinarioModel vet)
                || !(recepcionista instanceof RecepcionistaModel rec)) {
            throw new SQLException("Datos inconsistentes en la consulta " + fila[0] + ".");
        }

        return new ConsultaModel(fila[0], fila[1], fila[2], animal, vet, rec);
    }
}