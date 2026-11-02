package controllers;

import models.EmpleadoDAO;
import services.UtilidadesService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class EmpleadosJDBCController {

    private static final EmpleadoDAO dao = new EmpleadoDAO();

    public static void administrarEmpleados(Scanner scanner) {
        try {
            dao.crearTabla();
            dao.asegurarColumnaDisponible();
            dao.insertarEjemplosSiVacia();

            System.out.println("Conexión con la base de datos realizada correctamente.");

            int opcion;

            do {
                System.out.println("\n===== GESTIONAR PERSONAS / EMPLEADOS =====");
                System.out.println("1. Registrar empleado");
                System.out.println("2. Listar empleados");
                System.out.println("3. Buscar empleado por código");
                System.out.println("4. Actualizar empleado");
                System.out.println("5. Eliminar empleado");
                System.out.println("6. Volver al menú principal");

                opcion = UtilidadesService.leerEntero(scanner, "Elige una opción: ");

                switch (opcion) {
                    case 1 -> registrarEmpleado(scanner);
                    case 2 -> listarEmpleados();
                    case 3 -> buscarEmpleado(scanner);
                    case 4 -> actualizarEmpleado(scanner);
                    case 5 -> eliminarEmpleado(scanner);
                    case 6 -> System.out.println("Volviendo al menú principal.");
                    default -> System.out.println("Opción inválida. Intenta nuevamente.");
                }

            } while (opcion != 6);

        } catch (SQLException e) {
            System.out.println("Error con la base de datos: " + e.getMessage());
        }
    }

    private static void registrarEmpleado(Scanner scanner) {
        String id = UtilidadesService.leerTextoObligatorio(scanner, "Código del empleado (ej. EMP006): ");
        String nombre = UtilidadesService.leerTextoObligatorio(scanner, "Nombre: ");
        String apellido = UtilidadesService.leerTextoObligatorio(scanner, "Apellido: ");
        String cargo = UtilidadesService.leerTextoObligatorio(scanner, "Cargo: ");
        double sueldo = UtilidadesService.leerDecimalMinimo(scanner, "Sueldo: ", 0);
        String fecha = UtilidadesService.leerTextoObligatorio(scanner, "Fecha de contratación (AAAA-MM-DD): ");
        String telefono = UtilidadesService.leerTextoObligatorio(scanner, "Teléfono: ");
        String email = UtilidadesService.leerTextoObligatorio(scanner, "Email: ");
        boolean activo = UtilidadesService.leerBooleano(scanner, "¿Está activo? (s/n): ");

        try {
            boolean registrado = dao.registrarEmpleado(
                    id, nombre, apellido, cargo, sueldo,
                    fecha, telefono, email, activo
            );

            if (registrado) {
                System.out.println("Empleado registrado correctamente.");
            } else {
                System.out.println("No se registró el empleado.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("Ya existe un empleado con ese código.");
            } else {
                System.out.println("No se pudo registrar: " + e.getMessage());
            }
        }
    }

    private static void listarEmpleados() {
        try {
            ArrayList<String[]> empleados = dao.listarEmpleados();

            if (empleados.isEmpty()) {
                System.out.println("No hay empleados registrados.");
                return;
            }

            for (String[] empleado : empleados) {
                mostrarEmpleado(empleado);
                System.out.println("----------------------------------------");
            }

        } catch (SQLException e) {
            System.out.println("No se pudieron listar los empleados: " + e.getMessage());
        }
    }

    private static void buscarEmpleado(Scanner scanner) {
        String id = UtilidadesService.leerTextoObligatorio(scanner, "Código del empleado: ");

        try {
            String[] empleado = dao.buscarEmpleado(id);

            if (empleado != null) {
                mostrarEmpleado(empleado);
            } else {
                System.out.println("No se encontró un empleado con ese código.");
            }

        } catch (SQLException e) {
            System.out.println("No se pudo buscar el empleado: " + e.getMessage());
        }
    }

    private static void actualizarEmpleado(Scanner scanner) {
        String id = UtilidadesService.leerTextoObligatorio(
                scanner, "Código del empleado que deseas actualizar: "
        );

        try {
            if (!dao.existeEmpleado(id)) {
                System.out.println("No se encontró un empleado con ese código.");
                return;
            }

            String nombre = UtilidadesService.leerTextoObligatorio(scanner, "Nuevo nombre: ");
            String apellido = UtilidadesService.leerTextoObligatorio(scanner, "Nuevo apellido: ");
            String cargo = UtilidadesService.leerTextoObligatorio(scanner, "Nuevo cargo: ");
            double sueldo = UtilidadesService.leerDecimalMinimo(scanner, "Nuevo sueldo: ", 0);
            String telefono = UtilidadesService.leerTextoObligatorio(scanner, "Nuevo teléfono: ");
            String email = UtilidadesService.leerTextoObligatorio(scanner, "Nuevo email: ");
            boolean activo = UtilidadesService.leerBooleano(scanner, "¿Está activo? (s/n): ");

            boolean actualizado = dao.actualizarEmpleado(
                    id, nombre, apellido, cargo, sueldo,
                    telefono, email, activo
            );

            if (actualizado) {
                System.out.println("Empleado actualizado correctamente.");
            } else {
                System.out.println("No se realizó ningún cambio.");
            }

        } catch (SQLException e) {
            System.out.println("No se pudo actualizar: " + e.getMessage());
        }
    }

    private static void eliminarEmpleado(Scanner scanner) {
        String id = UtilidadesService.leerTextoObligatorio(
                scanner, "Código del empleado que deseas eliminar: "
        );

        try {
            boolean eliminado = dao.eliminarEmpleado(id);

            if (eliminado) {
                System.out.println("Empleado eliminado correctamente.");
            } else {
                System.out.println("No se encontró un empleado con ese código.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                System.out.println("No se puede eliminar: el empleado tiene consultas registradas.");
            } else {
                System.out.println("No se pudo eliminar: " + e.getMessage());
            }
        }
    }

    private static void mostrarEmpleado(String[] empleado) {
        System.out.println("Código: " + empleado[0]);
        System.out.println("Nombre: " + empleado[1] + " " + empleado[2]);
        System.out.println("Cargo: " + empleado[3]);
        System.out.println("Sueldo: S/ " + empleado[4]);
        System.out.println("Contratación: " + empleado[5]);
        System.out.println("Teléfono: " + empleado[6]);
        System.out.println("Email: " + empleado[7]);
        System.out.println("Activo: " + empleado[8]);
    }
}