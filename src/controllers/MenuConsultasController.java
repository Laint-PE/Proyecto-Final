package controllers;

import models.ConsultaDAO;
import services.UtilidadesService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class MenuConsultasController {

    private static final ConsultaDAO dao = new ConsultaDAO();

    public void iniciar(Scanner scanner) {
        try {
            dao.prepararTablas();

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
                    case 1 -> registrarConsulta(scanner);
                    case 2 -> listarConsultas();
                    case 3 -> buscarConsulta(scanner);
                    case 4 -> eliminarConsulta(scanner);
                    case 5 -> System.out.println("Volviendo al menú principal.");
                    default -> System.out.println("Opción inválida.");
                }

            } while (opcion != 5);

        } catch (SQLException e) {
            System.out.println("Error con la base de datos: " + e.getMessage());
        }
    }

    private static void registrarConsulta(Scanner scanner) {
        try {
            String codigo = UtilidadesService.leerTextoObligatorio(
                    scanner, "Código de consulta: "
            );

            if (dao.existeConsulta(codigo)) {
                System.out.println("Ya existe una consulta con ese código.");
                return;
            }

            System.out.println("\nAnimales registrados:");

            ArrayList<String[]> animales = dao.listarAnimales();

            if (animales.isEmpty()) {
                System.out.println("No hay animales registrados. Registra uno primero.");
                return;
            }

            for (String[] animal : animales) {
                System.out.println("  " + animal[0] + " - " + animal[1]
                        + " (" + animal[2] + ")");
            }

            String codigoAnimal = UtilidadesService.leerTextoObligatorio(
                    scanner, "Código del animal: "
            );

            if (!dao.existeAnimal(codigoAnimal)) {
                System.out.println("No se encontró el animal.");
                return;
            }

            System.out.println("\nVeterinarios disponibles:");

            ArrayList<String[]> veterinarios = dao.listarVeterinariosDisponibles();

            if (veterinarios.isEmpty()) {
                System.out.println("No hay veterinarios disponibles en este momento.");
                return;
            }

            for (String[] veterinario : veterinarios) {
                System.out.println("  " + veterinario[0] + " - "
                        + veterinario[1] + " " + veterinario[2]);
            }

            String codigoVet = UtilidadesService.leerTextoObligatorio(
                    scanner, "Código del veterinario: "
            );

            if (!dao.veterinarioDisponible(codigoVet)) {
                System.out.println("El veterinario no existe, no es veterinario o está ocupado.");
                return;
            }

            System.out.println("\nRecepcionistas:");

            ArrayList<String[]> recepcionistas = dao.listarRecepcionistas();

            if (recepcionistas.isEmpty()) {
                System.out.println("No hay recepcionistas registrados.");
                return;
            }

            for (String[] recepcionista : recepcionistas) {
                System.out.println("  " + recepcionista[0] + " - "
                        + recepcionista[1] + " " + recepcionista[2]);
            }

            String codigoRec = UtilidadesService.leerTextoObligatorio(
                    scanner, "Código del recepcionista: "
            );

            if (!dao.recepcionistaActivo(codigoRec)) {
                System.out.println("El recepcionista no existe o no está activo.");
                return;
            }

            LocalDate fecha = null;

            while (fecha == null) {
                String texto = UtilidadesService.leerTextoObligatorio(
                        scanner, "Fecha (AAAA-MM-DD): "
                );

                try {
                    fecha = LocalDate.parse(texto);
                } catch (DateTimeParseException e) {
                    System.out.println("La fecha debe tener formato AAAA-MM-DD.");
                }
            }

            String motivo = UtilidadesService.leerTextoObligatorio(
                    scanner, "Motivo: "
            );

            boolean registrado = dao.registrarConsulta(
                    codigo, fecha, motivo, codigoAnimal, codigoVet, codigoRec
            );

            if (registrado) {
                System.out.println("Consulta registrada. El veterinario quedó ocupado.");
            } else {
                System.out.println("El veterinario ya no está disponible. No se registró la consulta.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("Ya existe una consulta con ese código.");
            } else {
                System.out.println("No se pudo registrar la consulta: " + e.getMessage());
            }
        }
    }

    private static void listarConsultas() {
        try {
            ArrayList<String[]> consultas = dao.listarConsultas();

            if (consultas.isEmpty()) {
                System.out.println("No hay consultas registradas.");
                return;
            }

            for (String[] consulta : consultas) {
                mostrarConsulta(consulta);
                System.out.println("--------------------");
            }

        } catch (SQLException e) {
            System.out.println("No se pudieron listar las consultas: " + e.getMessage());
        }
    }

    private static void buscarConsulta(Scanner scanner) {
        String codigo = UtilidadesService.leerTextoObligatorio(
                scanner, "Código de consulta: "
        );

        try {
            String[] consulta = dao.buscarConsulta(codigo);

            if (consulta != null) {
                mostrarConsulta(consulta);
            } else {
                System.out.println("No se encontró la consulta.");
            }

        } catch (SQLException e) {
            System.out.println("No se pudo buscar la consulta: " + e.getMessage());
        }
    }

    private static void eliminarConsulta(Scanner scanner) {
        String codigo = UtilidadesService.leerTextoObligatorio(
                scanner, "Código de consulta a eliminar: "
        );

        try {
            boolean eliminado = dao.eliminarConsulta(codigo);

            if (eliminado) {
                System.out.println("Consulta eliminada y veterinario liberado.");
            } else {
                System.out.println("No se encontró la consulta.");
            }

        } catch (SQLException e) {
            System.out.println("No se pudo eliminar la consulta: " + e.getMessage());
        }
    }

    private static void mostrarConsulta(String[] consulta) {
        System.out.println("Código: " + consulta[0]);
        System.out.println("Fecha: " + consulta[1]);
        System.out.println("Motivo: " + consulta[2]);
        System.out.println("Animal: " + consulta[3] + " (" + consulta[4] + ")");
        System.out.println("Veterinario: " + consulta[5]);
        System.out.println("Recepcionista: " + consulta[6]);
    }
}