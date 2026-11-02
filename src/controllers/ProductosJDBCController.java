package controllers;

import models.ProductoDAO;
import models.ProductoModel;
import models.CroquetasModel;
import models.PlatoModel;
import models.CamaModel;
import services.UtilidadesService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class ProductosJDBCController {

    private static final ProductoDAO dao = new ProductoDAO();

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            administrarProductos(scanner);
        }
    }

    public static void administrarProductos(Scanner scanner) {
        try {
            dao.crearTabla();
            dao.insertarEjemplosSiVacia();

            System.out.println("Conexión con la base de datos realizada correctamente.");

            int opcion;
            do {
                System.out.println("\n===== ADMINISTRAR PRODUCTOS =====");
                System.out.println("1. Registrar producto");
                System.out.println("2. Listar productos");
                System.out.println("3. Buscar producto por código");
                System.out.println("4. Actualizar producto");
                System.out.println("5. Eliminar producto");
                System.out.println("6. Registrar venta");
                System.out.println("7. Volver al menú principal");

                opcion = UtilidadesService.leerEntero(scanner, "Elige una opción: ");

                switch (opcion) {
                    case 1 -> registrarProducto(scanner);
                    case 2 -> listarProductos();
                    case 3 -> buscarProducto(scanner);
                    case 4 -> actualizarProducto(scanner);
                    case 5 -> eliminarProducto(scanner);
                    case 6 -> registrarVenta(scanner);
                    case 7 -> System.out.println("Volviendo al menú principal.");
                    default -> System.out.println("Opción inválida. Intenta nuevamente.");
                }

            } while (opcion != 7);

        } catch (SQLException e) {
            System.out.println("Error con la base de datos: " + e.getMessage());
        }
    }

    private static void registrarProducto(Scanner scanner) {
        System.out.println("\nTipo de producto:");
        System.out.println("1. Croquetas");
        System.out.println("2. Plato");
        System.out.println("3. Cama");

        int tipo = UtilidadesService.leerEntero(scanner, "Elige el tipo: ");

        if (tipo < 1 || tipo > 3) {
            System.out.println("Tipo inválido. No se registró el producto.");
            return;
        }

        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código: ");
        String nombre = UtilidadesService.leerTextoObligatorio(scanner, "Nombre: ");
        double precio = UtilidadesService.leerDecimalMinimo(scanner, "Precio: ", 0);
        double stock = UtilidadesService.leerDecimalMinimo(scanner, "Stock (unidades o kg): ", 0);
        String descripcion = UtilidadesService.leerTextoObligatorio(scanner, "Descripción: ");
        String marca = UtilidadesService.leerTextoObligatorio(scanner, "Marca: ");
        boolean activo = UtilidadesService.leerBooleano(scanner, "¿Está activo? (s/n): ");

        if (tipo != 1 && stock != Math.floor(stock)) {
            System.out.println("El stock de platos y camas debe ser un número entero.");
            return;
        }

        ProductoModel producto;

        if (tipo == 1) {
            String sabor = UtilidadesService.leerTextoObligatorio(scanner, "Sabor: ");
            double peso = UtilidadesService.leerDecimalMinimo(scanner, "Peso del paquete (kg): ", 0.01);
            String vencimiento = UtilidadesService.leerTextoObligatorio(scanner, "Fecha de vencimiento (AAAA-MM-DD): ");
            String edad = UtilidadesService.leerTextoObligatorio(scanner, "Edad recomendada: ");
            String animal = UtilidadesService.leerTextoObligatorio(scanner, "Animal que consume: ");

            producto = new CroquetasModel(
                    codigo, nombre, precio, stock, descripcion, marca, activo,
                    sabor, peso, vencimiento, edad, animal
            );

        } else if (tipo == 2) {
            String material = UtilidadesService.leerTextoObligatorio(scanner, "Material: ");
            int capacidad = UtilidadesService.leerEnteroMinimo(scanner, "Capacidad (ml): ", 1);
            boolean antideslizante = UtilidadesService.leerBooleano(scanner, "¿Es antideslizante? (s/n): ");
            double extra = UtilidadesService.leerDecimalMinimo(scanner, "Precio extra por material: ", 0);
            double recargo = UtilidadesService.leerDecimalMinimo(scanner, "Recargo antideslizante: ", 0);

            producto = new PlatoModel(
                    codigo, nombre, precio, stock, descripcion, marca, activo,
                    material, capacidad, antideslizante, extra, recargo
            );

        } else {
            String tamano = UtilidadesService.leerTextoObligatorio(scanner, "Tamaño: ");
            String material = UtilidadesService.leerTextoObligatorio(scanner, "Material: ");
            boolean desmontable = UtilidadesService.leerBooleano(scanner, "¿Es desmontable? (s/n): ");
            double extra = UtilidadesService.leerDecimalMinimo(scanner, "Precio extra del material: ", 0);

            producto = new CamaModel(
                    codigo, nombre, precio, stock, descripcion, marca, activo,
                    tamano, material, desmontable, extra
            );
        }

        try {
            boolean registrado = dao.registrarProducto(producto);

            if (registrado) {
                System.out.println("Producto registrado correctamente.");
            } else {
                System.out.println("No se registró el producto.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("Ya existe un producto con ese código.");
            } else {
                System.out.println("No se pudo registrar: " + e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Datos inválidos: " + e.getMessage());
        }
    }

    private static void listarProductos() {
        try {
            ArrayList<ProductoModel> productos = dao.listarProductos();

            if (productos.isEmpty()) {
                System.out.println("No hay productos registrados.");
                return;
            }

            for (ProductoModel producto : productos) {
                mostrarProducto(producto);
                System.out.println("----------------------------------------");
            }

        } catch (SQLException e) {
            System.out.println("No se pudieron listar los productos: " + e.getMessage());
        }
    }

    private static void buscarProducto(Scanner scanner) {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código del producto: ");

        try {
            ProductoModel producto = dao.buscarProducto(codigo);

            if (producto == null) {
                System.out.println("No se encontró un producto con ese código.");
            } else {
                mostrarProducto(producto);
            }

        } catch (SQLException e) {
            System.out.println("No se pudo buscar el producto: " + e.getMessage());
        }
    }

    private static void actualizarProducto(Scanner scanner) {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código del producto que deseas actualizar: ");

        try {
            ProductoModel producto = dao.buscarProducto(codigo);

            if (producto == null) {
                System.out.println("No se encontró un producto con ese código.");
                return;
            }

            String nombre = UtilidadesService.leerTextoObligatorio(scanner, "Nuevo nombre: ");
            double precio = UtilidadesService.leerDecimalMinimo(scanner, "Nuevo precio: ", 0);
            double stock = UtilidadesService.leerDecimalMinimo(scanner, "Nuevo stock: ", 0);

            if (!(producto instanceof CroquetasModel) && stock != Math.floor(stock)) {
                System.out.println("El stock de platos y camas debe ser un número entero.");
                return;
            }

            String descripcion = UtilidadesService.leerTextoObligatorio(scanner, "Nueva descripción: ");
            String marca = UtilidadesService.leerTextoObligatorio(scanner, "Nueva marca: ");
            boolean activo = UtilidadesService.leerBooleano(scanner, "¿Está activo? (s/n): ");

            boolean actualizado = dao.actualizarProducto(
                    codigo, nombre, precio, stock, descripcion, marca, activo
            );

            if (actualizado) {
                System.out.println("Producto actualizado correctamente.");
                System.out.println("Los datos específicos del tipo se mantienen.");
            } else {
                System.out.println("No se realizó ningún cambio.");
            }

        } catch (SQLException e) {
            System.out.println("No se pudo actualizar el producto: " + e.getMessage());
        }
    }

    private static void eliminarProducto(Scanner scanner) {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código del producto que deseas eliminar: ");

        try {
            boolean eliminado = dao.eliminarProducto(codigo);

            if (eliminado) {
                System.out.println("Producto eliminado correctamente.");
            } else {
                System.out.println("No se encontró un producto con ese código.");
            }

        } catch (SQLException e) {
            System.out.println("No se pudo eliminar el producto: " + e.getMessage());
        }
    }

    private static void registrarVenta(Scanner scanner) {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código del producto: ");
        double cantidad = UtilidadesService.leerDecimalMinimo(scanner, "Cantidad a vender (unidades o kg): ", 0.01);

        try {
            System.out.println(dao.registrarVenta(codigo, cantidad));

        } catch (SQLException e) {
            System.out.println("No se pudo registrar la venta: " + e.getMessage());
        }
    }

    public static void registrarVentaDesdeMenu(Scanner scanner) {
        try {
            dao.crearTabla();
            dao.insertarEjemplosSiVacia();
            registrarVenta(scanner);

        } catch (SQLException e) {
            System.out.println("No se pudo registrar la venta: " + e.getMessage());
        }
    }

    private static void mostrarProducto(ProductoModel producto) {
        System.out.println("Código: " + producto.getCodigo());

        if (producto instanceof CroquetasModel) {
            System.out.println("Tipo: Croquetas");
        } else if (producto instanceof PlatoModel) {
            System.out.println("Tipo: Plato");
        } else {
            System.out.println("Tipo: Cama");
        }

        System.out.println("Nombre: " + producto.getNombre());
        System.out.println("Precio: S/ " + producto.getPrecio());
        System.out.println("Stock: " + producto.getStock());
        System.out.println("Descripción: " + producto.getDescripcion());
        System.out.println("Marca: " + producto.getMarca());
        System.out.println("Activo: " + (producto.isActivo() ? "Sí" : "No"));

        if (producto instanceof CroquetasModel croquetas) {
            System.out.println("Sabor: " + croquetas.getSabor());
            System.out.println("Peso (kg): " + croquetas.getPesoKg());
            System.out.println("Vencimiento: " + croquetas.getFechaVencimiento());
            System.out.println("Edad recomendada: " + croquetas.getEdadConsumo());
            System.out.println("Animal: " + croquetas.getAnimalConsume());

        } else if (producto instanceof PlatoModel plato) {
            System.out.println("Material: " + plato.getMaterial());
            System.out.println("Capacidad (ml): " + plato.getCapacidadEnMl());
            System.out.println("Antideslizante: " + (plato.isAntideslizante() ? "Sí" : "No"));
            System.out.println("Extra por material: " + plato.getPrecioExtraMaterial());
            System.out.println("Recargo antideslizante: " + plato.getRecargoAntideslizante());

        } else if (producto instanceof CamaModel cama) {
            System.out.println("Tamaño: " + cama.getTamano());
            System.out.println("Material: " + cama.getMaterial());
            System.out.println("Desmontable: " + (cama.isDesmontable() ? "Sí" : "No"));
            System.out.println("Extra por material: " + cama.getPrecioExtraMaterial());
        }
    }
}
