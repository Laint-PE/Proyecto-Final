package controllers;

import models.AnimalModel;
import models.ConsultaModel;
import models.PerroModel;
import models.RecepcionistaModel;
import models.VeterinarioModel;
import services.UtilidadesService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MenuConsultasController {
    private final Map<String, ConsultaModel> consultas = new HashMap<>();
    private final Map<String, AnimalModel> animales = new HashMap<>();
    private final Map<String, VeterinarioModel> veterinarios = new HashMap<>();
    private final Map<String, RecepcionistaModel> recepcionistas = new HashMap<>();

    public MenuConsultasController() {
        AnimalModel perro = new PerroModel("ANM001", "Mestizo", "Max", 4, 12.5, "Macho", "Carlos", false, "Corto", false, "Básico");
        animales.put(perro.getCodigo(), perro);
        VeterinarioModel vet = new VeterinarioModel("VET001", "Laura Pérez", "12345678", "987654321", true, "Medicina general", true);
        veterinarios.put(vet.getCodigo(), vet);
        RecepcionistaModel rec = new RecepcionistaModel("REC001", "Ana Torres", "87654321", "912345678", true, "Mañana");
        recepcionistas.put(rec.getCodigo(), rec);
    }

    public void iniciar(Scanner scanner) {
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
                case 1 -> registrar(scanner);
                case 2 -> listar();
                case 3 -> buscar(scanner);
                case 4 -> eliminar(scanner);
                case 5 -> System.out.println("Volviendo al menú principal.");
                default -> System.out.println("Opción inválida.");
            }
        } while (opcion != 5);
    }

    private void registrar(Scanner scanner) {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código de consulta: ");
        if (consultas.containsKey(codigo)) { System.out.println("Ya existe ese código."); return; }
        System.out.println("Animal disponible: ANM001 - Max");
        String codigoAnimal = UtilidadesService.leerTextoObligatorio(scanner, "Código del animal: ");
        AnimalModel animalModel = animales.get(codigoAnimal);
        if (animalModel == null) { System.out.println("No se encontró el animal en esta gestión de consultas."); return; }
        System.out.println("Veterinario disponible: VET001 - Laura Pérez");
        String codigoVet = UtilidadesService.leerTextoObligatorio(scanner, "Código del veterinario: ");
        VeterinarioModel veterinarioModel = veterinarios.get(codigoVet);
        if (veterinarioModel == null || !veterinarioModel.isDisponible()) { System.out.println("El veterinario no existe o está ocupado."); return; }
        System.out.println("Recepcionista: REC001 - Ana Torres");
        String codigoRec = UtilidadesService.leerTextoObligatorio(scanner, "Código del recepcionista: ");
        RecepcionistaModel recepcionistaModel = recepcionistas.get(codigoRec);
        if (recepcionistaModel == null) { System.out.println("No se encontró el recepcionista."); return; }
        String fecha = UtilidadesService.leerTextoObligatorio(scanner, "Fecha (AAAA-MM-DD, vacío no permitido): ");
        try { LocalDate.parse(fecha); } catch (Exception e) { System.out.println("La fecha debe tener formato AAAA-MM-DD."); return; }
        String motivo = UtilidadesService.leerTextoObligatorio(scanner, "Motivo: ");
        ConsultaModel consultaModel = new ConsultaModel(codigo, fecha, motivo, animalModel, veterinarioModel, recepcionistaModel);
        if (registrarConsulta(consultaModel)) System.out.println("Consulta registrada. El veterinario quedó ocupado.");
        else System.out.println("No se pudo registrar la consulta.");
    }

    private boolean registrarConsulta(ConsultaModel consultaModel) {
        if (consultas.containsKey(consultaModel.getCodigo()) || !consultaModel.registrarConsulta()) return false;
        consultas.put(consultaModel.getCodigo(), consultaModel);
        return true;
    }

    private void listar() {
        if (consultas.isEmpty()) { System.out.println("No hay consultas registradas."); return; }
        consultas.values().forEach(c -> { c.mostrarInformacion(); System.out.println("--------------------"); });
    }

    private void buscar(Scanner scanner) {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código de consulta: ");
        ConsultaModel consultaModel = consultas.get(codigo);
        if (consultaModel == null) System.out.println("No se encontró la consulta."); else consultaModel.mostrarInformacion();
    }

    private void eliminar(Scanner scanner) {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código de consulta a eliminar: ");
        ConsultaModel consultaModel = consultas.remove(codigo);
        if (consultaModel == null) System.out.println("No se encontró la consulta.");
        else { consultaModel.getVeterinario().setDisponible(true); System.out.println("Consulta eliminada y veterinario liberado."); }
    }
}
