package controllers;

import services.ConexionBDService;
import services.UtilidadesService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class EmpleadosJDBCController {

    public static void administrarEmpleados(Scanner scanner) {
        try (Connection conexion = ConexionBDService.conectar()) {
            crearTabla(conexion);
            insertarEjemplosSiVacia(conexion);
            System.out.println("Conexión con la base de datos realizada correctamente.");

            int opcion;
            do {
                System.out.println("===== GESTIONAR PERSONAS / EMPLEADOS =====");
                System.out.println("1. Registrar empleado");
                System.out.println("2. Listar empleados");
                System.out.println("3. Buscar empleado por código");
                System.out.println("4. Actualizar empleado");
                System.out.println("5. Eliminar empleado");
                System.out.println("6. Volver al menú principal");
                opcion = UtilidadesService.leerEntero(scanner, "Elige una opción: ");

                switch (opcion) {
                    case 1 -> registrarEmpleado(conexion, scanner);
                    case 2 -> listarEmpleados(conexion);
                    case 3 -> buscarEmpleado(conexion, scanner);
                    case 4 -> actualizarEmpleado(conexion, scanner);
                    case 5 -> eliminarEmpleado(conexion, scanner);
                    case 6 -> System.out.println("Volviendo al menú principal.");
                    default -> System.out.println("Opción inválida. Intenta nuevamente.");
                }
            } while (opcion != 6);
        } catch (SQLException e) {
            System.out.println("Error con la base de datos: " + e.getMessage());
        }
    }

    private static void crearTabla(Connection conexion) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS EMPLEADOS ("
                + "id_empleado VARCHAR(50) PRIMARY KEY, "
                + "nombre VARCHAR(100) NOT NULL, "
                + "apellido VARCHAR(100) NOT NULL, "
                + "cargo VARCHAR(100) NOT NULL, "
                + "sueldo DOUBLE NOT NULL, "
                + "fecha_contratacion VARCHAR(20), "
                + "telefono VARCHAR(20), "
                + "email VARCHAR(100), "
                + "activo TINYINT NOT NULL"
                + ")";
        try (Statement sentencia = conexion.createStatement()) {
            sentencia.executeUpdate(sql);
        }
    }

    private static void insertarEjemplosSiVacia(Connection conexion) throws SQLException {
        int cantidad = 0;
        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery("SELECT COUNT(*) FROM EMPLEADOS")) {
            if (resultado.next()) {
                cantidad = resultado.getInt(1);
            }
        }

        if (cantidad == 0) {
            String cols = "INSERT INTO EMPLEADOS (id_empleado, nombre, apellido, cargo, sueldo, "
                    + "fecha_contratacion, telefono, email, activo) VALUES ";
            try (Statement sentencia = conexion.createStatement()) {
                sentencia.executeUpdate(cols + "('EMP001', 'Carlos', 'Gómez', 'Veterinario', 2500.00, '2023-01-15', '987654321', 'carlos.gomez@veterinaria.pe', 1)");
                sentencia.executeUpdate(cols + "('EMP002', 'Ana', 'Torres', 'Recepcionista', 1500.00, '2023-05-10', '912345678', 'ana.torres@veterinaria.pe', 1)");
                sentencia.executeUpdate(cols + "('EMP003', 'Luis', 'Pérez', 'Asistente', 1200.00, '2024-02-01', '998877665', 'luis.perez@veterinaria.pe', 1)");
                sentencia.executeUpdate(cols + "('EMP004', 'María', 'Rojas', 'Administradora', 2800.00, '2022-11-01', '976543210', 'maria.rojas@veterinaria.pe', 1)");
                sentencia.executeUpdate(cols + "('EMP005', 'Jorge', 'Silva', 'Peluquero Canino', 1600.00, '2023-08-20', '954321098', 'jorge.silva@veterinaria.pe', 1)");
            }
            System.out.println("Se cargaron cinco empleados de ejemplo.");
        }
    }

    private static void registrarEmpleado(Connection conexion, Scanner scanner) {
        String id = UtilidadesService.leerTextoObligatorio(scanner, "Código del empleado (ej. EMP006): ");
        String nombre = UtilidadesService.leerTextoObligatorio(scanner, "Nombre: ");
        String apellido = UtilidadesService.leerTexto(scanner, "Apellido: ");
        String cargo = UtilidadesService.leerTextoObligatorio(scanner, "Cargo: ");
        double sueldo = UtilidadesService.leerDecimalMinimo(scanner, "Sueldo: ", 0);
        String fecha = UtilidadesService.leerTextoObligatorio(scanner, "Fecha de contratación (AAAA-MM-DD): ");
        String telefono = UtilidadesService.leerTextoObligatorio(scanner, "Teléfono: ");
        String email = UtilidadesService.leerTextoObligatorio(scanner, "Email: ");
        boolean activo = UtilidadesService.leerBooleano(scanner, "¿Está activo? (s/n): ");

        String sql = "INSERT INTO EMPLEADOS (id_empleado, nombre, apellido, cargo, sueldo, "
                + "fecha_contratacion, telefono, email, activo) VALUES ("
                + UtilidadesService.textoSql(id) + ", " + UtilidadesService.textoSql(nombre) + ", "
                + UtilidadesService.textoSql(apellido) + ", " + UtilidadesService.textoSql(cargo) + ", "
                + sueldo + ", " + UtilidadesService.textoSql(fecha) + ", " + UtilidadesService.textoSql(telefono) + ", "
                + UtilidadesService.textoSql(email) + ", " + UtilidadesService.numeroBooleano(activo) + ")";

        try (Statement sentencia = conexion.createStatement()) {
            int filas = sentencia.executeUpdate(sql);
            System.out.println(filas > 0 ? "Empleado registrado correctamente." : "No se registró el empleado.");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("Ya existe un empleado con ese código.");
            } else {
                System.out.println("No se pudo registrar: " + e.getMessage());
            }
        }
    }

    private static void listarEmpleados(Connection conexion) throws SQLException {
        boolean encontrado = false;
        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery("SELECT * FROM EMPLEADOS ORDER BY id_empleado")) {
            while (resultado.next()) {
                encontrado = true;
                mostrarEmpleado(resultado);
                System.out.println("----------------------------------------");
            }
        }
        if (!encontrado) {
            System.out.println("No hay empleados registrados.");
        }
    }

    private static void buscarEmpleado(Connection conexion, Scanner scanner) throws SQLException {
        String id = UtilidadesService.leerTextoObligatorio(scanner, "Código del empleado: ");
        String sql = "SELECT * FROM EMPLEADOS WHERE id_empleado = " + UtilidadesService.textoSql(id);

        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {
            if (resultado.next()) {
                mostrarEmpleado(resultado);
            } else {
                System.out.println("No se encontró un empleado con ese código.");
            }
        }
    }

    private static void actualizarEmpleado(Connection conexion, Scanner scanner) throws SQLException {
        String id = UtilidadesService.leerTextoObligatorio(scanner, "Código del empleado que deseas actualizar: ");

        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(
                     "SELECT id_empleado FROM EMPLEADOS WHERE id_empleado = " + UtilidadesService.textoSql(id))) {
            if (!resultado.next()) {
                System.out.println("No se encontró un empleado con ese código.");
                return;
            }
        }

        String nombre = UtilidadesService.leerTextoObligatorio(scanner, "Nuevo nombre: ");
        String apellido = UtilidadesService.leerTextoObligatorio(scanner, "Nuevo apellido: ");
        String cargo = UtilidadesService.leerTextoObligatorio(scanner, "Nuevo cargo: ");
        double sueldo = UtilidadesService.leerDecimalMinimo(scanner, "Nuevo sueldo: ", 0);
        String telefono = UtilidadesService.leerTextoObligatorio(scanner, "Nuevo teléfono: ");
        String email = UtilidadesService.leerTextoObligatorio(scanner, "Nuevo email: ");
        boolean activo = UtilidadesService.leerBooleano(scanner, "¿Está activo? (s/n): ");

        String sql = "UPDATE EMPLEADOS SET nombre = " + UtilidadesService.textoSql(nombre)
                + ", apellido = " + UtilidadesService.textoSql(apellido)
                + ", cargo = " + UtilidadesService.textoSql(cargo)
                + ", sueldo = " + sueldo
                + ", telefono = " + UtilidadesService.textoSql(telefono)
                + ", email = " + UtilidadesService.textoSql(email)
                + ", activo = " + UtilidadesService.numeroBooleano(activo)
                + " WHERE id_empleado = " + UtilidadesService.textoSql(id);

        try (Statement sentencia = conexion.createStatement()) {
            int filas = sentencia.executeUpdate(sql);
            System.out.println(filas > 0 ? "Empleado actualizado correctamente." : "No se realizó ningún cambio.");
        }
    }

    private static void eliminarEmpleado(Connection conexion, Scanner scanner) throws SQLException {
        String id = UtilidadesService.leerTextoObligatorio(scanner, "Código del empleado que deseas eliminar: ");
        String sql = "DELETE FROM EMPLEADOS WHERE id_empleado = " + UtilidadesService.textoSql(id);

        try (Statement sentencia = conexion.createStatement()) {
            int filas = sentencia.executeUpdate(sql);
            System.out.println(filas > 0 ? "Empleado eliminado correctamente." : "No se encontró un empleado con ese código.");
        }
    }

    private static void mostrarEmpleado(ResultSet resultado) throws SQLException {
        System.out.println("Código: " + resultado.getString("id_empleado"));
        System.out.println("Nombre: " + resultado.getString("nombre") + " " + resultado.getString("apellido"));
        System.out.println("Cargo: " + resultado.getString("cargo"));
        System.out.println("Sueldo: S/ " + resultado.getDouble("sueldo"));
        System.out.println("Contratación: " + resultado.getString("fecha_contratacion"));
        System.out.println("Teléfono: " + resultado.getString("telefono"));
        System.out.println("Email: " + resultado.getString("email"));
        System.out.println("Activo: " + (resultado.getInt("activo") == 1 ? "Sí" : "No"));
    }
}
