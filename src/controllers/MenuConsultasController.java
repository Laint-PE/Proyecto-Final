package controllers;

import services.ConexionBDService;
import services.UtilidadesService;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class MenuConsultasController {

    // Consulta base con JOIN: trae los nombres del animal, veterinario y recepcionista.
    private static final String SQL_SELECT_CONSULTAS =
            "SELECT c.codigo, c.fecha, c.motivo, a.nombre AS animal, a.tipo_animal, "
                    + "CONCAT(v.nombre, ' ', v.apellido) AS veterinario, "
                    + "CONCAT(r.nombre, ' ', r.apellido) AS recepcionista "
                    + "FROM CONSULTAS c "
                    + "JOIN ANIMALES a ON c.id_animal = a.codigo "
                    + "JOIN EMPLEADOS v ON c.id_veterinario = v.id_empleado "
                    + "JOIN EMPLEADOS r ON c.id_recepcionista = r.id_empleado ";

    private static final String SQL_VETERINARIOS_LIBRES =
            "SELECT id_empleado, nombre, apellido FROM EMPLEADOS "
                    + "WHERE cargo = 'Veterinario' AND activo = 1 AND disponible = 1 ORDER BY id_empleado";

    private static final String SQL_RECEPCIONISTAS =
            "SELECT id_empleado, nombre, apellido FROM EMPLEADOS "
                    + "WHERE cargo = 'Recepcionista' AND activo = 1 ORDER BY id_empleado";

    public void iniciar(Scanner scanner) {
        try (Connection conexion = ConexionBDService.conectar()) {
            prepararTablas(conexion);

            int opcion;
            do {
                System.out.println("\n===== GESTIÓN DE CONSULTAS =====");
                System.out.println("1. Registrar consulta");
                System.out.println("2. Listar consultas");
                System.out.println("3. Buscar consulta");
                System.out.println("4. Eliminar consulta");
                System.out.println("5. Volver al menú principal");
                opcion = UtilidadesService.leerEntero(scanner, "Elige una opción: ");

                switch (opcion) {
                    case 1 -> registrarConsulta(conexion, scanner);
                    case 2 -> listarConsultas(conexion);
                    case 3 -> buscarConsulta(conexion, scanner);
                    case 4 -> eliminarConsulta(conexion, scanner);
                    case 5 -> System.out.println("Volviendo al menú principal.");
                    default -> System.out.println("Opción inválida.");
                }
            } while (opcion != 5);
        } catch (SQLException e) {
            System.out.println("Error con la base de datos: " + e.getMessage());
        }
    }

    // ---------- Preparación de tablas (sentencias fijas: Statement) ----------

    private static void prepararTablas(Connection conexion) throws SQLException {
        // Las claves foráneas de CONSULTAS necesitan que estas tablas existan primero.
        AnimalesJDBCController.crearTabla(conexion);
        AnimalesJDBCController.insertarEjemplosSiVacia(conexion);
        EmpleadosJDBCController.crearTabla(conexion);
        EmpleadosJDBCController.insertarEjemplosSiVacia(conexion);
        asegurarColumnaDisponible(conexion);
        crearTablaConsultas(conexion);
    }

    // Si la tabla EMPLEADOS se creó antes de este módulo, le agrega la columna disponible.
    private static void asegurarColumnaDisponible(Connection conexion) throws SQLException {
        boolean existe;
        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery("SHOW COLUMNS FROM EMPLEADOS LIKE 'disponible'")) {
            existe = resultado.next();
        }
        if (!existe) {
            try (Statement sentencia = conexion.createStatement()) {
                sentencia.executeUpdate("ALTER TABLE EMPLEADOS ADD COLUMN disponible TINYINT NOT NULL DEFAULT 1");
            }
        }
    }

    private static void crearTablaConsultas(Connection conexion) throws SQLException {
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
        try (Statement sentencia = conexion.createStatement()) {
            sentencia.executeUpdate(sql);
        }
    }

    // ---------- Registrar ----------

    private static void registrarConsulta(Connection conexion, Scanner scanner) throws SQLException {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código de consulta: ");
        if (existeFila(conexion, "SELECT 1 FROM CONSULTAS WHERE codigo = ?", codigo)) {
            System.out.println("Ya existe una consulta con ese código.");
            return;
        }

        System.out.println("\nAnimales registrados:");
        if (!mostrarAnimales(conexion)) {
            System.out.println("No hay animales registrados. Registra uno primero.");
            return;
        }
        String codigoAnimal = UtilidadesService.leerTextoObligatorio(scanner, "Código del animal: ");
        if (!existeFila(conexion, "SELECT 1 FROM ANIMALES WHERE codigo = ?", codigoAnimal)) {
            System.out.println("No se encontró el animal.");
            return;
        }

        System.out.println("\nVeterinarios disponibles:");
        if (!mostrarEmpleados(conexion, SQL_VETERINARIOS_LIBRES)) {
            System.out.println("No hay veterinarios disponibles en este momento.");
            return;
        }
        String codigoVet = UtilidadesService.leerTextoObligatorio(scanner, "Código del veterinario: ");
        if (!existeFila(conexion, "SELECT 1 FROM EMPLEADOS WHERE id_empleado = ? "
                + "AND cargo = 'Veterinario' AND activo = 1 AND disponible = 1", codigoVet)) {
            System.out.println("El veterinario no existe, no es veterinario o está ocupado.");
            return;
        }

        System.out.println("\nRecepcionistas:");
        if (!mostrarEmpleados(conexion, SQL_RECEPCIONISTAS)) {
            System.out.println("No hay recepcionistas registrados.");
            return;
        }
        String codigoRec = UtilidadesService.leerTextoObligatorio(scanner, "Código del recepcionista: ");
        if (!existeFila(conexion, "SELECT 1 FROM EMPLEADOS WHERE id_empleado = ? "
                + "AND cargo = 'Recepcionista' AND activo = 1", codigoRec)) {
            System.out.println("El recepcionista no existe o no es recepcionista.");
            return;
        }

        LocalDate fecha = null;
        while (fecha == null) {
            String texto = UtilidadesService.leerTextoObligatorio(scanner, "Fecha (AAAA-MM-DD): ");
            try {
                fecha = LocalDate.parse(texto);
            } catch (DateTimeParseException e) {
                System.out.println("La fecha debe tener formato AAAA-MM-DD.");
            }
        }
        String motivo = UtilidadesService.leerTextoObligatorio(scanner, "Motivo: ");

        String sqlInsert = "INSERT INTO CONSULTAS (codigo, fecha, motivo, id_animal, id_veterinario, "
                + "id_recepcionista) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlOcupar = "UPDATE EMPLEADOS SET disponible = 0 WHERE id_empleado = ? AND disponible = 1";

        // Transacción: o se guardan la consulta y el cambio del veterinario, o no se guarda nada.
        try {
            conexion.setAutoCommit(false);

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
                System.out.println("El veterinario ya no está disponible. No se registró la consulta.");
                return;
            }

            conexion.commit();
            System.out.println("Consulta registrada. El veterinario quedó ocupado.");
        } catch (SQLException e) {
            conexion.rollback();
            System.out.println("No se pudo registrar la consulta: " + e.getMessage());
        } finally {
            conexion.setAutoCommit(true);
        }
    }

    // ---------- Listar y buscar ----------

    private static void listarConsultas(Connection conexion) throws SQLException {
        boolean encontrado = false;
        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(SQL_SELECT_CONSULTAS + "ORDER BY c.codigo")) {
            while (resultado.next()) {
                encontrado = true;
                mostrarConsulta(resultado);
                System.out.println("--------------------");
            }
        }
        if (!encontrado) {
            System.out.println("No hay consultas registradas.");
        }
    }

    private static void buscarConsulta(Connection conexion, Scanner scanner) throws SQLException {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código de consulta: ");

        try (PreparedStatement ps = conexion.prepareStatement(SQL_SELECT_CONSULTAS + "WHERE c.codigo = ?")) {
            ps.setString(1, codigo);
            try (ResultSet resultado = ps.executeQuery()) {
                if (resultado.next()) {
                    mostrarConsulta(resultado);
                } else {
                    System.out.println("No se encontró la consulta.");
                }
            }
        }
    }

    // ---------- Eliminar ----------

    private static void eliminarConsulta(Connection conexion, Scanner scanner) throws SQLException {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código de consulta a eliminar: ");

        String idVeterinario = null;
        try (PreparedStatement ps = conexion.prepareStatement("SELECT id_veterinario FROM CONSULTAS WHERE codigo = ?")) {
            ps.setString(1, codigo);
            try (ResultSet resultado = ps.executeQuery()) {
                if (resultado.next()) {
                    idVeterinario = resultado.getString("id_veterinario");
                }
            }
        }
        if (idVeterinario == null) {
            System.out.println("No se encontró la consulta.");
            return;
        }

        try {
            conexion.setAutoCommit(false);

            try (PreparedStatement ps = conexion.prepareStatement("DELETE FROM CONSULTAS WHERE codigo = ?")) {
                ps.setString(1, codigo);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conexion.prepareStatement("UPDATE EMPLEADOS SET disponible = 1 WHERE id_empleado = ?")) {
                ps.setString(1, idVeterinario);
                ps.executeUpdate();
            }

            conexion.commit();
            System.out.println("Consulta eliminada y veterinario liberado.");
        } catch (SQLException e) {
            conexion.rollback();
            System.out.println("No se pudo eliminar la consulta: " + e.getMessage());
        } finally {
            conexion.setAutoCommit(true);
        }
    }

    // ---------- Auxiliares ----------

    // El SQL es fijo y el valor del usuario siempre va como parámetro.
    private static boolean existeFila(Connection conexion, String sql, String valor) throws SQLException {
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, valor);
            try (ResultSet resultado = ps.executeQuery()) {
                return resultado.next();
            }
        }
    }

    private static boolean mostrarAnimales(Connection conexion) throws SQLException {
        boolean hay = false;
        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery("SELECT codigo, nombre, tipo_animal FROM ANIMALES ORDER BY codigo")) {
            while (resultado.next()) {
                hay = true;
                System.out.println("  " + resultado.getString("codigo") + " - " + resultado.getString("nombre")
                        + " (" + resultado.getString("tipo_animal") + ")");
            }
        }
        return hay;
    }

    private static boolean mostrarEmpleados(Connection conexion, String sql) throws SQLException {
        boolean hay = false;
        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {
            while (resultado.next()) {
                hay = true;
                System.out.println("  " + resultado.getString("id_empleado") + " - "
                        + resultado.getString("nombre") + " " + resultado.getString("apellido"));
            }
        }
        return hay;
    }

    private static void mostrarConsulta(ResultSet resultado) throws SQLException {
        System.out.println("Código: " + resultado.getString("codigo"));
        System.out.println("Fecha: " + resultado.getString("fecha"));
        System.out.println("Motivo: " + resultado.getString("motivo"));
        System.out.println("Animal: " + resultado.getString("animal")
                + " (" + resultado.getString("tipo_animal") + ")");
        System.out.println("Veterinario: " + resultado.getString("veterinario"));
        System.out.println("Recepcionista: " + resultado.getString("recepcionista"));
    }
}