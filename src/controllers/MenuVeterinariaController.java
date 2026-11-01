package controllers;

import models.AnimalModel;
import models.GatoModel;
import models.PerroModel;
import services.GestorAnimalesService;
import services.UtilidadesService;

import java.util.Scanner;

public class MenuVeterinariaController {

    public static void registrarAnimal(Scanner scanner, GestorAnimalesService verificarAnimal) {

        System.out.println("1. Perro");
        System.out.println("2. Gato");
        int tipo = UtilidadesService.leerEntero(scanner, "Elige el tipo de animal: ");

        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código: ");

        String raza = UtilidadesService.leerTextoObligatorio(scanner, "Raza: ");

        String nombre = UtilidadesService.leerTextoObligatorio(scanner, "Nombre: ");

        int edad = UtilidadesService.leerEnteroMinimo(scanner, "Edad: ", 0);

        double peso = UtilidadesService.leerDecimalMinimo(scanner, "Peso: ", 0.01);

        String sexo = UtilidadesService.leerTextoObligatorio(scanner, "Sexo: ");

        String nombreDueno = UtilidadesService.leerTextoObligatorio(scanner, "Nombre del dueño: ");

        System.out.print("¿Tiene pedigrí? (true/false): ");
        boolean pedigri = Boolean.parseBoolean(scanner.nextLine());

        String tipoPelaje = UtilidadesService.leerTextoObligatorio(scanner, "Tipo de pelaje: ");

        AnimalModel animalModel;

        if (tipo == 1) {
            System.out.print("¿Requiere bozal en consulta? (true/false): ");
            boolean requiereBozal = Boolean.parseBoolean(scanner.nextLine());

            System.out.print("Nivel de adiestramiento: ");
            String nivelAdiestramiento = scanner.nextLine();

            animalModel = new PerroModel(codigo, raza, nombre, edad, peso, sexo, nombreDueno, pedigri, tipoPelaje, requiereBozal, nivelAdiestramiento);
        } else if (tipo == 2) {
            System.out.print("¿Requiere transportadora? (true/false): ");
            boolean requiereTransportadora = Boolean.parseBoolean(scanner.nextLine());

            System.out.print("Temperamento: ");
            String temperamento = scanner.nextLine();

            animalModel = new GatoModel(codigo, raza, nombre, edad, peso, sexo, nombreDueno, pedigri, tipoPelaje, requiereTransportadora, temperamento);
        } else {
            System.out.println("Tipo de animal inválido.");
            return;
        }

        if (verificarAnimal.registrarAnimal(animalModel)) {
            System.out.println("Animal registrado.");
        } else {
            System.out.println("Ya existe un animal con ese código.");
        }
    }

    public static void eliminarAnimal(Scanner scanner, GestorAnimalesService verificarAnimal) {
        System.out.print("Código del animal que deseas eliminar: ");
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código: ");

        if (verificarAnimal.eliminarAnimal(codigo)) {
            System.out.println("Animal eliminado correctamente.");
        } else {
            System.out.println("No se encontró un animal con ese código.");
        }
    }

    public static void listarAnimales(GestorAnimalesService verificarAnimal) {
        verificarAnimal.listarAnimales();
    }
}
