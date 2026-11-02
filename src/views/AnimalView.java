package views;

import controllers.AnimalController;
import models.AnimalModel;
import models.GatoModel;
import models.PerroModel;
import services.ErroresSQL;
import services.UtilidadesService;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * Vista de consola para animales.
 * Aquí viven Scanner y System.out. Los datos se envían al controlador.
 */
public class AnimalView {

    private final Scanner scanner;
    private final AnimalController controlador = new AnimalController();

    public AnimalView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void iniciar() {
        try {
            controlador.inicializar();
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
                    case 1 -> registrarAnimal();
                    case 2 -> listarAnimales();
                    case 3 -> buscarAnimal();
                    case 4 -> actualizarAnimal();
                    case 5 -> eliminarAnimal();
                    case 6 -> System.out.println("Volviendo al menú principal.");
                    default -> System.out.println("Opción inválida. Intenta nuevamente.");
                }
            } while (opcion != 6);

        } catch (SQLException e) {
            System.out.println("Error con la base de datos: " + e.getMessage());
        }
    }

    private void registrarAnimal() {
        try {
            AnimalModel animal = leerDatosAnimal(null);

            if (animal == null) {
                return;
            }

            controlador.registrar(animal);
            System.out.println("Animal registrado correctamente.");

        } catch (SQLException e) {
            if (e.getErrorCode() == ErroresSQL.DUPLICADO) {
                System.out.println("Ya existe un animal con ese código.");
            } else {
                System.out.println("No se pudo registrar el animal: " + e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Datos inválidos: " + e.getMessage());
        }
    }

    // Si codigoExistente es null, pide el código (registro); si no, lo reutiliza (actualización).
    private AnimalModel leerDatosAnimal(String codigoExistente) {
        System.out.println("\nTipo de animal:");
        System.out.println("1. Perro");
        System.out.println("2. Gato");

        int tipoOpcion = UtilidadesService.leerEntero(scanner, "Elige el tipo: ");

        if (tipoOpcion != 1 && tipoOpcion != 2) {
            System.out.println("Tipo de animal inválido.");
            return null;
        }

        String codigo = (codigoExistente == null)
                ? UtilidadesService.leerTextoObligatorio(scanner, "Código: ")
                : codigoExistente;

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
            String adiestramiento = UtilidadesService.leerTextoObligatorio(scanner, "Nivel de adiestramiento: ");

            return new PerroModel(codigo, raza, nombre, edad, peso, sexo, dueno,
                    pedigri, pelaje, bozal, adiestramiento);
        }

        boolean transportadora = UtilidadesService.leerBooleano(scanner, "¿Requiere transportadora? (s/n): ");
        String temperamento = UtilidadesService.leerTextoObligatorio(scanner, "Temperamento: ");

        return new GatoModel(codigo, raza, nombre, edad, peso, sexo, dueno,
                pedigri, pelaje, transportadora, temperamento);
    }

    private void listarAnimales() {
        try {
            controlador.cargarDesdeBD();

            if (controlador.getAnimales().isEmpty()) {
                System.out.println("No hay animales registrados.");
                return;
            }

            for (AnimalModel animal : controlador.getAnimales()) {
                mostrarAnimal(animal);
                System.out.println("----------------------------------------");
            }

        } catch (SQLException e) {
            System.out.println("No se pudieron listar los animales: " + e.getMessage());
        }
    }

    private void buscarAnimal() {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código del animal: ");

        try {
            AnimalModel animal = controlador.buscar(codigo);

            if (animal != null) {
                mostrarAnimal(animal);
            } else {
                System.out.println("No se encontró un animal con ese código.");
            }

        } catch (SQLException e) {
            System.out.println("No se pudo buscar el animal: " + e.getMessage());
        }
    }

    private void actualizarAnimal() {
        String codigo = UtilidadesService.leerTextoObligatorio(
                scanner, "Código del animal que deseas actualizar: ");

        try {
            if (!controlador.existe(codigo)) {
                System.out.println("No se encontró un animal con ese código.");
                return;
            }

            AnimalModel animal = leerDatosAnimal(codigo);

            if (animal == null) {
                return;
            }

            if (controlador.actualizar(animal)) {
                System.out.println("Animal actualizado correctamente.");
            } else {
                System.out.println("No se realizó ningún cambio.");
            }

        } catch (SQLException e) {
            System.out.println("No se pudo actualizar el animal: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Datos inválidos: " + e.getMessage());
        }
    }

    private void eliminarAnimal() {
        String codigo = UtilidadesService.leerTextoObligatorio(
                scanner, "Código del animal que deseas eliminar: ");

        try {
            if (controlador.eliminar(codigo)) {
                System.out.println("Animal eliminado correctamente.");
            } else {
                System.out.println("No se encontró un animal con ese código.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == ErroresSQL.LLAVE_FORANEA) {
                System.out.println("No se puede eliminar: el animal tiene consultas registradas.");
            } else {
                System.out.println("No se pudo eliminar el animal: " + e.getMessage());
            }
        }
    }

    private void mostrarAnimal(AnimalModel animal) {
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
            System.out.println("Requiere bozal: " + (perro.isRequiereBozalEnConsulta() ? "Sí" : "No"));
            System.out.println("Nivel de adiestramiento: " + perro.getNivelAdiestramiento());

        } else if (animal instanceof GatoModel gato) {
            System.out.println("Requiere transportadora: " + (gato.isRequiereTransportadora() ? "Sí" : "No"));
            System.out.println("Temperamento: " + gato.getTemperamento());
        }
    }
}