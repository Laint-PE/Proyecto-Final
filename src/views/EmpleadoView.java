package views;

import controllers.EmpleadoController;
import models.EmpleadoModel;
import models.RecepcionistaModel;
import models.VeterinarioModel;
import services.ErroresSQL;
import services.UtilidadesService;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * Vista de consola para empleados.
 * Aquí viven Scanner y System.out. Los datos se envían al controlador.
 */
public class EmpleadoView {

    private static final int VETERINARIO = 1;
    private static final int RECEPCIONISTA = 2;

    private final Scanner scanner;
    private final EmpleadoController controlador = new EmpleadoController();

    public EmpleadoView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void iniciar() {
        try {
            controlador.inicializar();
            System.out.println("Conexión con la base de datos realizada correctamente.");

            int opcion;
            do {
                System.out.println("\n===== GESTIONAR EMPLEADOS =====");
                System.out.println("1. Registrar empleado");
                System.out.println("2. Listar empleados");
                System.out.println("3. Buscar empleado por código");
                System.out.println("4. Actualizar empleado");
                System.out.println("5. Eliminar empleado");
                System.out.println("6. Volver al menú principal");

                opcion = UtilidadesService.leerEntero(scanner, "Elige una opción: ");

                switch (opcion) {
                    case 1 -> registrarEmpleado();
                    case 2 -> listarEmpleados();
                    case 3 -> buscarEmpleado();
                    case 4 -> actualizarEmpleado();
                    case 5 -> eliminarEmpleado();
                    case 6 -> System.out.println("Volviendo al menú principal.");
                    default -> System.out.println("Opción inválida. Intenta nuevamente.");
                }
            } while (opcion != 6);

        } catch (SQLException e) {
            System.out.println("Error con la base de datos: " + e.getMessage());
        }
    }

    private void registrarEmpleado() {
        System.out.println("\nTipo de empleado:");
        System.out.println("1. Veterinario");
        System.out.println("2. Recepcionista");

        int tipo = UtilidadesService.leerEntero(scanner, "Elige el tipo: ");

        if (tipo != VETERINARIO && tipo != RECEPCIONISTA) {
            System.out.println("Tipo de empleado inválido.");
            return;
        }

        try {
            // Un veterinario nuevo siempre empieza disponible.
            EmpleadoModel empleado = leerDatosEmpleado(tipo, null, true);

            if (controlador.registrar(empleado)) {
                System.out.println("Empleado registrado correctamente.");
            } else {
                System.out.println("No se registró el empleado.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == ErroresSQL.DUPLICADO) {
                System.out.println("Ya existe un empleado con ese código o ese DNI.");
            } else {
                System.out.println("No se pudo registrar: " + e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Datos inválidos: " + e.getMessage());
        }
    }

    // Si codigoExistente es null, pide el código (registro); si no, lo reutiliza (actualización).
    private EmpleadoModel leerDatosEmpleado(int tipo, String codigoExistente, boolean disponible) {
        String codigo = (codigoExistente == null)
                ? UtilidadesService.leerTextoObligatorio(scanner, "Código del empleado (ej. EMP006): ")
                : codigoExistente;

        String nombre = UtilidadesService.leerTextoObligatorio(scanner, "Nombre completo: ");
        String dni = leerDni();
        String telefono = UtilidadesService.leerTextoObligatorio(scanner, "Teléfono: ");
        boolean tiempoCompleto = UtilidadesService.leerBooleano(scanner, "¿Es tiempo completo? (s/n): ");

        if (tipo == VETERINARIO) {
            String especialidad = UtilidadesService.leerTextoObligatorio(scanner, "Especialidad: ");
            return new VeterinarioModel(codigo, nombre, dni, telefono,
                    tiempoCompleto, especialidad, disponible);
        }

        String turno = UtilidadesService.leerTextoObligatorio(scanner, "Turno (Mañana/Tarde): ");
        return new RecepcionistaModel(codigo, nombre, dni, telefono, tiempoCompleto, turno);
    }

    private String leerDni() {
        while (true) {
            String dni = UtilidadesService.leerTextoObligatorio(scanner, "DNI (8 dígitos): ");
            if (dni.matches("\\d{8}")) {
                return dni;
            }
            System.out.println("El DNI debe tener exactamente 8 dígitos.");
        }
    }

    private void listarEmpleados() {
        try {
            controlador.cargarDesdeBD();

            if (controlador.getEmpleados().isEmpty()) {
                System.out.println("No hay empleados registrados.");
                return;
            }

            for (EmpleadoModel empleado : controlador.getEmpleados()) {
                mostrarEmpleado(empleado);
                System.out.println("----------------------------------------");
            }

        } catch (SQLException e) {
            System.out.println("No se pudieron listar los empleados: " + e.getMessage());
        }
    }

    private void buscarEmpleado() {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código del empleado: ");

        try {
            EmpleadoModel empleado = controlador.buscar(codigo);

            if (empleado != null) {
                mostrarEmpleado(empleado);
            } else {
                System.out.println("No se encontró un empleado con ese código.");
            }

        } catch (SQLException e) {
            System.out.println("No se pudo buscar el empleado: " + e.getMessage());
        }
    }

    private void actualizarEmpleado() {
        String codigo = UtilidadesService.leerTextoObligatorio(
                scanner, "Código del empleado que deseas actualizar: ");

        try {
            EmpleadoModel actual = controlador.buscar(codigo);

            if (actual == null) {
                System.out.println("No se encontró un empleado con ese código.");
                return;
            }

            // El tipo no se cambia; "disponible" se conserva porque lo manejan las consultas.
            int tipo = RECEPCIONISTA;
            boolean disponible = true;

            if (actual instanceof VeterinarioModel vet) {
                tipo = VETERINARIO;
                disponible = vet.isDisponible();
            }

            System.out.println("Actualizando a un " + (tipo == VETERINARIO ? "veterinario" : "recepcionista") + ".");

            EmpleadoModel nuevo = leerDatosEmpleado(tipo, codigo, disponible);

            if (controlador.actualizar(nuevo)) {
                System.out.println("Empleado actualizado correctamente.");
            } else {
                System.out.println("No se realizó ningún cambio.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == ErroresSQL.DUPLICADO) {
                System.out.println("Ya existe otro empleado con ese DNI.");
            } else {
                System.out.println("No se pudo actualizar: " + e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Datos inválidos: " + e.getMessage());
        }
    }

    private void eliminarEmpleado() {
        String codigo = UtilidadesService.leerTextoObligatorio(
                scanner, "Código del empleado que deseas eliminar: ");

        try {
            if (controlador.eliminar(codigo)) {
                System.out.println("Empleado eliminado correctamente.");
            } else {
                System.out.println("No se encontró un empleado con ese código.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == ErroresSQL.LLAVE_FORANEA) {
                System.out.println("No se puede eliminar: el empleado tiene consultas registradas.");
            } else {
                System.out.println("No se pudo eliminar: " + e.getMessage());
            }
        }
    }

    private void mostrarEmpleado(EmpleadoModel empleado) {
        String tipo = (empleado instanceof VeterinarioModel) ? "Veterinario" : "Recepcionista";

        System.out.println("Código: " + empleado.getCodigo());
        System.out.println("Tipo: " + tipo);
        System.out.println("Nombre: " + empleado.getNombre());
        System.out.println("DNI: " + empleado.getDni());
        System.out.println("Teléfono: " + empleado.getTelefono());
        System.out.println("Tiempo completo: " + (empleado.isTiempoCompleto() ? "Sí" : "No"));
        System.out.printf("Sueldo: S/ %.2f%n", empleado.calcularSueldo());
        System.out.println("Funciones: " + empleado.mostrarFunciones());

        if (empleado instanceof VeterinarioModel vet) {
            System.out.println("Disponible: " + (vet.isDisponible() ? "Sí" : "No"));
        }
    }
}