package views;

import controllers.ConsultaController;
import models.AnimalModel;
import models.ConsultaModel;
import models.RecepcionistaModel;
import models.VeterinarioModel;
import services.ErroresSQL;
import services.UtilidadesService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Vista de consola para consultas.
 * Aquí viven Scanner y System.out. Los datos se envían al controlador.
 */
public class ConsultaView {

    private final Scanner scanner;
    private final ConsultaController controlador = new ConsultaController();

    public ConsultaView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void iniciar() {
        try {
            controlador.inicializar();
            System.out.println("Conexión con la base de datos realizada correctamente.");

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
                    case 1 -> registrarConsulta();
                    case 2 -> listarConsultas();
                    case 3 -> buscarConsulta();
                    case 4 -> eliminarConsulta();
                    case 5 -> System.out.println("Volviendo al menú principal.");
                    default -> System.out.println("Opción inválida. Intenta nuevamente.");
                }
            } while (opcion != 5);

        } catch (SQLException e) {
            System.out.println("Error con la base de datos: " + e.getMessage());
        }
    }

    private void registrarConsulta() {
        try {
            String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código de consulta: ");

            if (controlador.existe(codigo)) {
                System.out.println("Ya existe una consulta con ese código.");
                return;
            }

            // 1. Animal
            List<AnimalModel> animales = controlador.listarAnimales();

            if (animales.isEmpty()) {
                System.out.println("No hay animales registrados. Registra uno primero.");
                return;
            }

            System.out.println("\nAnimales registrados:");
            for (AnimalModel a : animales) {
                System.out.println("  " + a.getCodigo() + " - " + a.getNombre()
                        + " (" + a.getTipoAnimal() + ")");
            }

            String codigoAnimal = UtilidadesService.leerTextoObligatorio(scanner, "Código del animal: ");
            AnimalModel animal = controlador.buscarAnimal(codigoAnimal);

            if (animal == null) {
                System.out.println("No se encontró el animal.");
                return;
            }

            // 2. Veterinario disponible
            List<VeterinarioModel> veterinarios = controlador.listarVeterinariosDisponibles();

            if (veterinarios.isEmpty()) {
                System.out.println("No hay veterinarios disponibles en este momento.");
                return;
            }

            System.out.println("\nVeterinarios disponibles:");
            for (VeterinarioModel v : veterinarios) {
                System.out.println("  " + v.getCodigo() + " - " + v.getNombre()
                        + " (" + v.getEspecialidad() + ")");
            }

            String codigoVet = UtilidadesService.leerTextoObligatorio(scanner, "Código del veterinario: ");
            VeterinarioModel veterinario = controlador.buscarVeterinarioDisponible(codigoVet);

            if (veterinario == null) {
                System.out.println("El veterinario no existe, no es veterinario o está ocupado.");
                return;
            }

            // 3. Recepcionista
            List<RecepcionistaModel> recepcionistas = controlador.listarRecepcionistas();

            if (recepcionistas.isEmpty()) {
                System.out.println("No hay recepcionistas registrados.");
                return;
            }

            System.out.println("\nRecepcionistas:");
            for (RecepcionistaModel r : recepcionistas) {
                System.out.println("  " + r.getCodigo() + " - " + r.getNombre()
                        + " (Turno: " + r.getTurno() + ")");
            }

            String codigoRec = UtilidadesService.leerTextoObligatorio(scanner, "Código del recepcionista: ");
            RecepcionistaModel recepcionista = controlador.buscarRecepcionista(codigoRec);

            if (recepcionista == null) {
                System.out.println("El recepcionista no existe o no es recepcionista.");
                return;
            }

            // 4. Fecha y motivo
            LocalDate fecha = leerFecha();
            String motivo = UtilidadesService.leerTextoObligatorio(scanner, "Motivo: ");

            ConsultaModel consulta = new ConsultaModel(
                    codigo, fecha.toString(), motivo, animal, veterinario, recepcionista);

            if (controlador.registrar(consulta)) {
                System.out.println("Consulta registrada. El veterinario quedó ocupado.");
            } else {
                System.out.println("El veterinario ya no está disponible. No se registró la consulta.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == ErroresSQL.DUPLICADO) {
                System.out.println("Ya existe una consulta con ese código.");
            } else {
                System.out.println("No se pudo registrar la consulta: " + e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Datos inválidos: " + e.getMessage());
        }
    }

    private LocalDate leerFecha() {
        while (true) {
            String texto = UtilidadesService.leerTextoObligatorio(scanner, "Fecha (AAAA-MM-DD): ");

            try {
                return LocalDate.parse(texto);
            } catch (DateTimeParseException e) {
                System.out.println("La fecha debe tener formato AAAA-MM-DD.");
            }
        }
    }

    private void listarConsultas() {
        try {
            controlador.cargarDesdeBD();

            if (controlador.getConsultas().isEmpty()) {
                System.out.println("No hay consultas registradas.");
                return;
            }

            for (ConsultaModel consulta : controlador.getConsultas()) {
                mostrarConsulta(consulta);
                System.out.println("----------------------------------------");
            }

        } catch (SQLException e) {
            System.out.println("No se pudieron listar las consultas: " + e.getMessage());
        }
    }

    private void buscarConsulta() {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código de consulta: ");

        try {
            ConsultaModel consulta = controlador.buscar(codigo);

            if (consulta != null) {
                mostrarConsulta(consulta);
            } else {
                System.out.println("No se encontró la consulta.");
            }

        } catch (SQLException e) {
            System.out.println("No se pudo buscar la consulta: " + e.getMessage());
        }
    }

    private void eliminarConsulta() {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código de consulta a eliminar: ");

        try {
            if (controlador.eliminar(codigo)) {
                System.out.println("Consulta eliminada y veterinario liberado.");
            } else {
                System.out.println("No se encontró la consulta.");
            }

        } catch (SQLException e) {
            System.out.println("No se pudo eliminar la consulta: " + e.getMessage());
        }
    }

    private void mostrarConsulta(ConsultaModel consulta) {
        AnimalModel animal = consulta.getAnimal();

        System.out.println("Código: " + consulta.getCodigo());
        System.out.println("Fecha: " + consulta.getFecha());
        System.out.println("Motivo: " + consulta.getMotivo());
        System.out.println("Animal: " + animal.getNombre() + " (" + animal.getTipoAnimal()
                + ", dueño: " + animal.getNombreDueno() + ")");
        System.out.println("Veterinario: " + consulta.getVeterinario().getNombre()
                + " (" + consulta.getVeterinario().getEspecialidad() + ")");
        System.out.println("Recepcionista: " + consulta.getRecepcionista().getNombre()
                + " (Turno: " + consulta.getRecepcionista().getTurno() + ")");
    }
}