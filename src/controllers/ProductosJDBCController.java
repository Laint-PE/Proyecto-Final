package controllers;

import services.ConexionBDService;
import services.UtilidadesService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ProductosJDBCController {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            administrarProductos(scanner);
        }
    }

    public static void administrarProductos(Scanner scanner) {
        try (Connection conexion = ConexionBDService.conectar()) {
            crearTabla(conexion);
            insertarEjemplosSiVacia(conexion);
            System.out.println("Conexión con la base de datos realizada correctamente.");

            int opcion;
            do {
                System.out.println("===== ADMINISTRAR PRODUCTOS =====");
                System.out.println("1. Registrar producto");
                System.out.println("2. Listar productos");
                System.out.println("3. Buscar producto por código");
                System.out.println("4. Actualizar producto");
                System.out.println("5. Eliminar producto");
                System.out.println("6. Registrar venta");
                System.out.println("7. Volver al menú principal");
                opcion = UtilidadesService.leerEntero(scanner, "Elige una opción: ");

                switch (opcion) {
                    case 1 -> registrarProducto(conexion, scanner);
                    case 2 -> listarProductos(conexion);
                    case 3 -> buscarProducto(conexion, scanner);
                    case 4 -> actualizarProducto(conexion, scanner);
                    case 5 -> eliminarProducto(conexion, scanner);
                    case 6 -> registrarVenta(conexion, scanner);
                    case 7 -> System.out.println("Volviendo al menú principal.");
                    default -> System.out.println("Opción inválida. Intenta nuevamente.");
                }
            } while (opcion != 7);
        } catch (SQLException e) {
            System.out.println("Error con la base de datos: " + e.getMessage());
        }
    }

    private static void crearTabla(Connection conexion) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS PRODUCTOS ("
                + "codigo VARCHAR(50) PRIMARY KEY, "
                + "tipo VARCHAR(20) NOT NULL, "
                + "nombre VARCHAR(100) NOT NULL, "
                + "precio DOUBLE NOT NULL, "
                + "stock DOUBLE NOT NULL, "
                + "descripcion VARCHAR(255), "
                + "marca VARCHAR(100), "
                + "activo TINYINT NOT NULL, "
                + "sabor VARCHAR(100), "
                + "peso_kg DOUBLE, "
                + "fecha_vencimiento VARCHAR(20), "
                + "edad_consumo VARCHAR(50), "
                + "animal_consume VARCHAR(50), "
                + "material VARCHAR(100), "
                + "capacidad_ml INT, "
                + "antideslizante TINYINT, "
                + "precio_extra_material DOUBLE, "
                + "recargo_antideslizante DOUBLE, "
                + "tamano VARCHAR(50), "
                + "desmontable TINYINT"
                + ")";

        try (Statement sentencia = conexion.createStatement()) {
            sentencia.executeUpdate(sql);
        }
    }

    private static void insertarEjemplosSiVacia(Connection conexion) throws SQLException {
        int cantidad = 0;
        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery("SELECT COUNT(*) FROM PRODUCTOS")) {
            if (resultado.next()) {
                cantidad = resultado.getInt(1);
            }
        }

        if (cantidad == 0) {
            try (Statement sentencia = conexion.createStatement()) {
                sentencia.executeUpdate("INSERT INTO PRODUCTOS "
                        + "(codigo, tipo, nombre, precio, stock, descripcion, marca, activo, sabor, peso_kg, fecha_vencimiento, edad_consumo, animal_consume) VALUES ("
                        + "'CRQ001', 'Croquetas', 'Croquetas para perro adulto', 75.00, 20, 'Alimento seco para perro adulto', 'PetVida', 1, 'Pollo', 3.00, '2027-12-31', 'Adulto', 'Perro')");

                sentencia.executeUpdate("INSERT INTO PRODUCTOS "
                        + "(codigo, tipo, nombre, precio, stock, descripcion, marca, activo, material, capacidad_ml, antideslizante, precio_extra_material, recargo_antideslizante) VALUES ("
                        + "'PLA001', 'Plato', 'Plato antideslizante', 18.00, 15, 'Plato para alimento', 'AnimalHome', 1, 'Acero', 500, 1, 3.00, 2.00)");

                sentencia.executeUpdate("INSERT INTO PRODUCTOS "
                        + "(codigo, tipo, nombre, precio, stock, descripcion, marca, activo, tamano, material, desmontable, precio_extra_material) VALUES ("
                        + "'CAMA001', 'Cama', 'Cama acolchada mediana', 95.00, 8, 'Cama lavable para mascota', 'AnimalHome', 1, 'Mediana', 'Tela', 1, 10.00)");
            }
            System.out.println("Se cargaron tres productos de ejemplo.");
        }
    }

    private static void registrarProducto(Connection conexion, Scanner scanner) {
        System.out.println("\nTipo de producto:");
        System.out.println("1. Croquetas");
        System.out.println("2. Plato");
        System.out.println("3. Cama");
        int tipoOpcion = UtilidadesService.leerEntero(scanner, "Elige el tipo: ");

        String tipo;
        if (tipoOpcion == 1) {
            tipo = "Croquetas";
        } else if (tipoOpcion == 2) {
            tipo = "Plato";
        } else if (tipoOpcion == 3) {
            tipo = "Cama";
        } else {
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

        String sabor = "NULL";
        String pesoKg = "NULL";
        String fechaVencimiento = "NULL";
        String edadConsumo = "NULL";
        String animalConsume = "NULL";
        String material = "NULL";
        String capacidadMl = "NULL";
        String antideslizante = "NULL";
        String precioExtraMaterial = "NULL";
        String recargoAntideslizante = "NULL";
        String tamano = "NULL";
        String desmontable = "NULL";

        if (tipoOpcion == 1) {
            sabor = UtilidadesService.textoSql(UtilidadesService.leerTextoObligatorio(scanner, "Sabor: "));
            pesoKg = String.valueOf(UtilidadesService.leerDecimalMinimo(scanner, "Peso del paquete (kg): ", 0.01));
            fechaVencimiento = UtilidadesService.textoSql(UtilidadesService.leerTextoObligatorio(scanner, "Fecha de vencimiento (AAAA-MM-DD): "));
            edadConsumo = UtilidadesService.textoSql(UtilidadesService.leerTextoObligatorio(scanner, "Edad recomendada (cachorro/adulto/senior): "));
            animalConsume = UtilidadesService.textoSql(UtilidadesService.leerTextoObligatorio(scanner, "Animal que consume (perro/gato/ambos): "));
        } else if (tipoOpcion == 2) {
            material = UtilidadesService.textoSql(UtilidadesService.leerTextoObligatorio(scanner, "Material: "));
            capacidadMl = String.valueOf(UtilidadesService.leerEnteroMinimo(scanner, "Capacidad (ml): ", 1));
            antideslizante = UtilidadesService.numeroBooleano(UtilidadesService.leerBooleano(scanner, "¿Es antideslizante? (s/n): "));
            precioExtraMaterial = String.valueOf(UtilidadesService.leerDecimalMinimo(scanner, "Precio extra por material: ", 0));
            recargoAntideslizante = String.valueOf(UtilidadesService.leerDecimalMinimo(scanner, "Recargo antideslizante: ", 0));
        } else {
            tamano = UtilidadesService.textoSql(UtilidadesService.leerTextoObligatorio(scanner, "Tamaño: "));
            material = UtilidadesService.textoSql(UtilidadesService.leerTextoObligatorio(scanner, "Material: "));
            desmontable = UtilidadesService.numeroBooleano(UtilidadesService.leerBooleano(scanner, "¿Es desmontable? (s/n): "));
            precioExtraMaterial = String.valueOf(UtilidadesService.leerDecimalMinimo(scanner, "Precio extra del material: ", 0));
        }

        if (tipoOpcion != 1 && stock != Math.floor(stock)) {
            System.out.println("El stock de platos y camas debe ser un número entero.");
            return;
        }
        String sql = "INSERT INTO PRODUCTOS (codigo, tipo, nombre, precio, stock, descripcion, marca, activo, "
                + "sabor, peso_kg, fecha_vencimiento, edad_consumo, animal_consume, material, capacidad_ml, "
                + "antideslizante, precio_extra_material, recargo_antideslizante, tamano, desmontable) VALUES ("
                + UtilidadesService.textoSql(codigo) + ", " + UtilidadesService.textoSql(tipo) + ", " + UtilidadesService.textoSql(nombre) + ", "
                + precio + ", " + stock + ", " + UtilidadesService.textoSql(descripcion) + ", " + UtilidadesService.textoSql(marca) + ", "
                + UtilidadesService.numeroBooleano(activo) + ", " + sabor + ", " + pesoKg + ", " + fechaVencimiento + ", "
                + edadConsumo + ", " + animalConsume + ", " + material + ", " + capacidadMl + ", "
                + antideslizante + ", " + precioExtraMaterial + ", " + recargoAntideslizante + ", "
                + tamano + ", " + desmontable + ")";

        try (Statement sentencia = conexion.createStatement()) {
            int filas = sentencia.executeUpdate(sql);
            System.out.println(filas > 0 ? "Producto registrado correctamente." : "No se registró el producto.");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("Ya existe un producto con ese código.");
            } else {
                System.out.println("No se pudo registrar: " + e.getMessage());
            }
        }
    }

    private static void listarProductos(Connection conexion) throws SQLException {
        String sql = "SELECT * FROM PRODUCTOS ORDER BY codigo";
        boolean encontrado = false;

        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {
            while (resultado.next()) {
                encontrado = true;
                mostrarProducto(resultado);
                System.out.println("----------------------------------------");
            }
        }

        if (!encontrado) {
            System.out.println("No hay productos registrados.");
        }
    }

    private static void buscarProducto(Connection conexion, Scanner scanner) throws SQLException {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código del producto: ");
        String sql = "SELECT * FROM PRODUCTOS WHERE codigo = " + UtilidadesService.textoSql(codigo);

        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {
            if (resultado.next()) {
                mostrarProducto(resultado);
            } else {
                System.out.println("No se encontró un producto con ese código.");
            }
        }
    }

    private static void actualizarProducto(Connection conexion, Scanner scanner) throws SQLException {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código del producto que deseas actualizar: ");

        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(
                     "SELECT codigo FROM PRODUCTOS WHERE codigo = " + UtilidadesService.textoSql(codigo))) {
            if (!resultado.next()) {
                System.out.println("No se encontró un producto con ese código.");
                return;
            }
        }

        String nombre = UtilidadesService.leerTextoObligatorio(scanner, "Nuevo nombre: ");
        double precio = UtilidadesService.leerDecimalMinimo(scanner, "Nuevo precio: ", 0);
        double stock = UtilidadesService.leerDecimalMinimo(scanner, "Nuevo stock: ", 0);
        try (Statement sentencia = conexion.createStatement(); ResultSet tipoR = sentencia.executeQuery("SELECT tipo FROM PRODUCTOS WHERE codigo = " + UtilidadesService.textoSql(codigo))) {
            if (tipoR.next() && !"Croquetas".equals(tipoR.getString("tipo")) && stock != Math.floor(stock)) {
                System.out.println("El stock de platos y camas debe ser un número entero.");
                return;
            }
        }
        String descripcion = UtilidadesService.leerTextoObligatorio(scanner, "Nueva descripción: ");
        String marca = UtilidadesService.leerTextoObligatorio(scanner, "Nueva marca: ");
        boolean activo = UtilidadesService.leerBooleano(scanner, "¿Está activo? (s/n): ");

        String sql = "UPDATE PRODUCTOS SET nombre = " + UtilidadesService.textoSql(nombre)
                + ", precio = " + precio
                + ", stock = " + stock
                + ", descripcion = " + UtilidadesService.textoSql(descripcion)
                + ", marca = " + UtilidadesService.textoSql(marca)
                + ", activo = " + UtilidadesService.numeroBooleano(activo)
                + " WHERE codigo = " + UtilidadesService.textoSql(codigo);

        try (Statement sentencia = conexion.createStatement()) {
            int filas = sentencia.executeUpdate(sql);
            System.out.println(filas > 0 ? "Producto actualizado correctamente." : "No se realizó ningún cambio.");
            System.out.println("Se actualizaron los datos generales; los datos específicos del tipo se mantienen.");
        }
    }

    private static void eliminarProducto(Connection conexion, Scanner scanner) throws SQLException {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código del producto que deseas eliminar: ");
        String sql = "DELETE FROM PRODUCTOS WHERE codigo = " + UtilidadesService.textoSql(codigo);

        try (Statement sentencia = conexion.createStatement()) {
            int filas = sentencia.executeUpdate(sql);
            System.out.println(filas > 0 ? "Producto eliminado correctamente." : "No se encontró un producto con ese código.");
        }
    }


    public static void registrarVentaDesdeMenu(Scanner scanner) {
        try (Connection conexion = ConexionBDService.conectar()) {
            crearTabla(conexion);
            insertarEjemplosSiVacia(conexion);
            registrarVenta(conexion, scanner);
        } catch (SQLException e) {
            System.out.println("No se pudo registrar la venta: " + e.getMessage());
        }
    }

    private static void registrarVenta(Connection conexion, Scanner scanner) throws SQLException {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código del producto: ");
        double cantidad = UtilidadesService.leerDecimalMinimo(scanner, "Cantidad a vender (unidades o kg): ", 0.01);
        String sql = "SELECT * FROM PRODUCTOS WHERE codigo = " + UtilidadesService.textoSql(codigo);
        try (Statement sentencia = conexion.createStatement(); ResultSet r = sentencia.executeQuery(sql)) {
            if (!r.next()) { System.out.println("No se encontró el producto."); return; }
            if (r.getInt("activo") != 1) { System.out.println("No se puede vender: el producto está inactivo."); return; }
            double stock = r.getDouble("stock");
            if (!"Croquetas".equals(r.getString("tipo")) && cantidad != Math.floor(cantidad)) {
                System.out.println("La cantidad de platos y camas debe ser un número entero.");
                return;
            }
            if (cantidad > stock) { System.out.println("Stock insuficiente. Disponible: " + stock); return; }
            String tipo = r.getString("tipo");
            double precio = r.getDouble("precio");
            double total;
            if ("Croquetas".equals(tipo)) {
                double pesoPaquete = r.getDouble("peso_kg");
                if (pesoPaquete <= 0) { System.out.println("El peso del paquete no es válido."); return; }
                total = (precio / pesoPaquete) * cantidad;
            } else if ("Plato".equals(tipo)) {
                total = (precio + r.getDouble("precio_extra_material") + (r.getInt("antideslizante") == 1 ? r.getDouble("recargo_antideslizante") : 0)) * cantidad;
            } else {
                total = (precio + r.getDouble("precio_extra_material")) * cantidad;
            }
            String update = "UPDATE PRODUCTOS SET stock = " + (stock - cantidad) + " WHERE codigo = " + UtilidadesService.textoSql(codigo);
            try (Statement actualizacion = conexion.createStatement()) {
                actualizacion.executeUpdate(update);
            }
            System.out.printf("Venta registrada: %s%nTotal: S/ %.2f%nStock restante: %.2f%n", r.getString("nombre"), total, stock - cantidad);
        }
    }

    private static void mostrarProducto(ResultSet resultado) throws SQLException {
        System.out.println("Código: " + resultado.getString("codigo"));
        System.out.println("Tipo: " + resultado.getString("tipo"));
        System.out.println("Nombre: " + resultado.getString("nombre"));
        System.out.println("Precio: S/ " + resultado.getDouble("precio"));
        System.out.println("Stock: " + resultado.getDouble("stock"));
        System.out.println("Descripción: " + resultado.getString("descripcion"));
        System.out.println("Marca: " + resultado.getString("marca"));
        System.out.println("Activo: " + (resultado.getInt("activo") == 1 ? "Sí" : "No"));

        String tipo = resultado.getString("tipo");
        if ("Croquetas".equals(tipo)) {
            System.out.println("Sabor: " + resultado.getString("sabor"));
            System.out.println("Peso (kg): " + resultado.getString("peso_kg"));
            System.out.println("Vencimiento: " + resultado.getString("fecha_vencimiento"));
            System.out.println("Edad recomendada: " + resultado.getString("edad_consumo"));
            System.out.println("Animal: " + resultado.getString("animal_consume"));
        } else if ("Plato".equals(tipo)) {
            System.out.println("Material: " + resultado.getString("material"));
            System.out.println("Capacidad (ml): " + resultado.getString("capacidad_ml"));
            System.out.println("Antideslizante: " + valorBooleano(resultado, "antideslizante"));
            System.out.println("Extra por material: " + resultado.getString("precio_extra_material"));
            System.out.println("Recargo antideslizante: " + resultado.getString("recargo_antideslizante"));
        } else if ("Cama".equals(tipo)) {
            System.out.println("Tamaño: " + resultado.getString("tamano"));
            System.out.println("Material: " + resultado.getString("material"));
            System.out.println("Desmontable: " + valorBooleano(resultado, "desmontable"));
            System.out.println("Extra por material: " + resultado.getString("precio_extra_material"));
        }
    }

    private static String valorBooleano(ResultSet resultado, String columna) throws SQLException {
        Object valor = resultado.getObject(columna);
        if (valor == null) {
            return "No corresponde";
        }
        return resultado.getInt(columna) == 1 ? "Sí" : "No";
    }
}
