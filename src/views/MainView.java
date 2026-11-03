package views;

import java.util.Scanner;

public class MainView {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AnimalView animalView = new AnimalView(scanner);
        ProductoView productoView = new ProductoView(scanner);
        EmpleadoView empleadoView = new EmpleadoView(scanner);
        ConsultaView consultaView = new ConsultaView(scanner);
        int opcion = 0;

        while (opcion != 6) {
            System.out.println("\n===== SISTEMA DE VETERINARIA =====");
            System.out.println("1. Gestionar animales");
            System.out.println("2. Gestionar productos");
            System.out.println("3. Gestionar empleados");
            System.out.println("4. Registrar venta");
            System.out.println("5. Gestionar consultas");
            System.out.println("6. Salir");
            System.out.print("Elige una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ingresa una opción numérica válida.");
                continue;
            }

            switch (opcion) {
                case 1:
                    animalView.iniciar();
                    break;
                case 2:
                    productoView.iniciar();
                    break;
                case 3:
                    empleadoView.iniciar();
                    break;
                case 4:
                    productoView.registrarVentaDesdeMenu();
                    break;
                case 5:
                    consultaView.iniciar();
                    break;
                case 6:
                    System.out.println("Saliendo del sistema.");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }

        scanner.close();
    }
}