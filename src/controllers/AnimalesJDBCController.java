package controllers;

import models.AnimalDAO;
import models.AnimalModel;
import models.PerroModel;
import models.GatoModel;
import services.UtilidadesService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class AnimalesJDBCController {

    private static final AnimalDAO dao = new AnimalDAO();

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            administrarAnimales(scanner);
        }
    }

    public static void administrarAnimales(Scanner scanner) {
        try {
            dao.crearTabla();
            dao.insertarEjemplosSiVacia();

            System.out.println("Conexión con la base de datos realizada correctamente.");

            int opcion;

            do {
                System.out.println("\n===== GESTIONAR ANIMALES =====");
                System.out.println("1. Registrar animal");
                System.out.println("2. Listar animales");
                System.out.println("3. Buscar animal por código");
                System.out.println("4. Actualizar animal");
                System.out.println("5. Eliminar animal");
                System.out.println("6. Volver al menú principal");

                opcion = UtilidadesService.leerEntero(scanner, "Elige una opción: ");

                switch (opcion) {
                    case 1 -> registrarAnimal(scanner);
                    case 2 -> listarAnimales();
                    case 3 -> buscarAnimal(scanner);
                    case 4 -> actualizarAnimal(scanner);
                    case 5 -> eliminarAnimal(scanner);
                    case 6 -> System.out.println("Volviendo al menú principal.");
                    default -> System.out.println("Opción inválida. Intenta nuevamente.");
                }

            } while (opcion != 6);

        } catch (SQLException e) {
            System.out.println("Error con la base de datos: " + e.getMessage());
        }
    }

    private static void registrarAnimal(Scanner scanner) {
        AnimalModel animal = leerDatosAnimal(scanner, null);

        if (animal == null) {
            return;
        }

        try {
            dao.registrarAnimal(animal);
            System.out.println("Animal registrado correctamente.");

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("Ya existe un animal con ese código.");
            } else {
                System.out.println("No se pudo registrar el animal: " + e.getMessage());
            }
        }
    }

    private static AnimalModel leerDatosAnimal(Scanner scanner, String codigoExistente) {
        System.out.println("\nTipo de animal:");
        System.out.println("1. Perro");
        System.out.println("2. Gato");

        int tipoOpcion = UtilidadesService.leerEntero(scanner, "Elige el tipo: ");

        if (tipoOpcion != 1 && tipoOpcion != 2) {
            System.out.println("Tipo de animal inválido.");
            return null;
        }

        String codigo;

        if (codigoExistente == null) {
            codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código: ");
        } else {
            codigo = codigoExistente;
        }

        String raza = UtilidadesService.leerTextoObligatorio(scanner, "Raza: ");
        String nombre = UtilidadesService.leerTextoObligatorio(scanner, "Nombre: ");
        int edad = UtilidadesService.leerEnteroMinimo(scanner, "Edad: ", 0);
        double peso = UtilidadesService.leerDecimalMinimo(scanner, "Peso en kg: ", 0.01);
        String sexo = UtilidadesService.leerTextoObligatorio(scanner, "Sexo: ");
        String dueno = UtilidadesService.leerTextoObligatorio(scanner, "Nombre del dueño: ");
        boolean pedigri = UtilidadesService.leerBooleano(scanner, "¿Tiene pedigrí? (s/n): ");
        String pelaje = UtilidadesService.leerTextoObligatorio(scanner, "Tipo de pelaje: ");

        if (tipoOpcion == 1) {
            boolean bozal = UtilidadesService.leerBooleano(scanner, "¿Requiere bozal en consulta? (s/n): ");
            String adiestramiento = UtilidadesService.leerTexto(scanner, "Nivel de adiestramiento: ");

            return new PerroModel(
                    codigo, raza, nombre, edad, peso, sexo, dueno,
                    pedigri, pelaje, bozal, adiestramiento
            );

        } else {
            boolean transportadora = UtilidadesService.leerBooleano(scanner, "¿Requiere transportadora? (s/n): ");
            String temperamento = UtilidadesService.leerTexto(scanner, "Temperamento: ");

            return new GatoModel(
                    codigo, raza, nombre, edad, peso, sexo, dueno,
                    pedigri, pelaje, transportadora, temperamento
            );
        }
    }

    private static void listarAnimales() {
        try {
            ArrayList<AnimalModel> animales = dao.listarAnimales();

            if (animales.isEmpty()) {
                System.out.println("No hay animales registrados.");
                return;
            }

            for (AnimalModel animal : animales) {
                mostrarAnimal(animal);
                System.out.println("----------------------------------------");
            }

        } catch (SQLException e) {
            System.out.println("No se pudieron listar los animales: " + e.getMessage());
        }
    }

    private static void buscarAnimal(Scanner scanner) {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código del animal: ");

        try {
            AnimalModel animal = dao.buscarAnimal(codigo);

            if (animal != null) {
                mostrarAnimal(animal);
            } else {
                System.out.println("No se encontró un animal con ese código.");
            }

        } catch (SQLException e) {
            System.out.println("No se pudo buscar el animal: " + e.getMessage());
        }
    }

    private static void actualizarAnimal(Scanner scanner) {
        String codigo = UtilidadesService.leerTextoObligatorio(
                scanner, "Código del animal que deseas actualizar: "
        );

        try {
            if (!dao.existeAnimal(codigo)) {
                System.out.println("No se encontró un animal con ese código.");
                return;
            }

            AnimalModel animal = leerDatosAnimal(scanner, codigo);

            if (animal == null) {
                return;
            }

            dao.actualizarAnimal(animal);
            System.out.println("Animal actualizado correctamente.");

        } catch (SQLException e) {
            System.out.println("No se pudo actualizar el animal: " + e.getMessage());
        }
    }

    private static void eliminarAnimal(Scanner scanner) {
        String codigo = UtilidadesService.leerTextoObligatorio(
                scanner, "Código del animal que deseas eliminar: "
        );

        try {
            if (!dao.existeAnimal(codigo)) {
                System.out.println("No se encontró un animal con ese código.");
                return;
            }

            dao.eliminarAnimal(codigo);
            System.out.println("Animal eliminado correctamente.");

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                System.out.println("No se puede eliminar: el animal tiene consultas registradas.");
            } else {
                System.out.println("No se pudo eliminar el animal: " + e.getMessage());
            }
        }
    }

    private static void mostrarAnimal(AnimalModel animal) {
        System.out.println("Código: " + animal.getCodigo());
        System.out.println("Tipo: " + animal.getTipoAnimal());
        System.out.println("Raza: " + animal.getRaza());
        System.out.println("Nombre: " + animal.getNombre());
        System.out.println("Edad: " + animal.getEdad());
        System.out.println("Peso: " + animal.getPeso() + " kg");
        System.out.println("Sexo: " + animal.getSexo());
        System.out.println("Dueño: " + animal.getNombreDueno());
        System.out.println("Pedigrí: " + (animal.isPedigri() ? "Sí" : "No"));
        System.out.println("Tipo de pelaje: " + animal.getTipoPelaje());

        if (animal instanceof PerroModel perro) {
            System.out.println("Requiere bozal: " +
                    (perro.isRequiereBozalEnConsulta() ? "Sí" : "No"));
            System.out.println("Nivel de adiestramiento: " +
                    perro.getNivelAdiestramiento());

        } else if (animal instanceof GatoModel gato) {
            System.out.println("Requiere transportadora: " +
                    (gato.isRequiereTransportadora() ? "Sí" : "No"));
            System.out.println("Temperamento: " + gato.getTemperamento());
        }}
}