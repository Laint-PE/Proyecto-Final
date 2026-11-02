package controllers;

import services.ConexionBDService;
import services.UtilidadesService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ProductosJDBCController {

    private static final String SQL_INSERT = "INSERT INTO PRODUCTOS (codigo, tipo, nombre, precio, stock, "
            + "descripcion, marca, activo, sabor, peso_kg, fecha_vencimiento, edad_consumo, animal_consume, "
            + "material, capacidad_ml, antideslizante, precio_extra_material, recargo_antideslizante, "
            + "tamano, desmontable) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

    // Sentencias fijas, sin datos del usuario: Statement es suficiente.
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

        if (cantidad > 0) {
            return;
        }

        DatosProducto croquetas = base("CRQ001", "Croquetas", "Croquetas para perro adulto", 75.00, 20,
                "Alimento seco para perro adulto", "PetVida", true);
        croquetas.sabor = "Pollo";
        croquetas.pesoKg = 3.00;
        croquetas.fechaVencimiento = "2027-12-31";
        croquetas.edadConsumo = "Adulto";
        croquetas.animalConsume = "Perro";

        DatosProducto plato = base("PLA001", "Plato", "Plato antideslizante", 18.00, 15,
                "Plato para alimento", "AnimalHome", true);
        plato.material = "Acero";
        plato.capacidadMl = 500;
        plato.antideslizante = true;
        plato.precioExtraMaterial = 3.00;
        plato.recargoAntideslizante = 2.00;

        DatosProducto cama = base("CAMA001", "Cama", "Cama acolchada mediana", 95.00, 8,
                "Cama lavable para mascota", "AnimalHome", true);
        cama.tamano = "Mediana";
        cama.material = "Tela";
        cama.desmontable = true;
        cama.precioExtraMaterial = 10.00;

        // Un solo PreparedStatement reutilizado para los tres ejemplos.
        try (PreparedStatement ps = conexion.prepareStatement(SQL_INSERT)) {
            insertarProducto(ps, croquetas);
            insertarProducto(ps, plato);
            insertarProducto(ps, cama);
        }
        System.out.println("Se cargaron tres productos de ejemplo.");
    }

    private static DatosProducto base(String codigo, String tipo, String nombre, double precio, double stock,
                                      String descripcion, String marca, boolean activo) {
        DatosProducto d = new DatosProducto();
        d.codigo = codigo;
        d.tipo = tipo;
        d.nombre = nombre;
        d.precio = precio;
        d.stock = stock;
        d.descripcion = descripcion;
        d.marca = marca;
        d.activo = activo;
        return d;
    }

    // Asigna los 20 parámetros en el mismo orden que las columnas de SQL_INSERT.
    private static int insertarProducto(PreparedStatement ps, DatosProducto d) throws SQLException {
        ps.setString(1, d.codigo);
        ps.setString(2, d.tipo);
        ps.setString(3, d.nombre);
        ps.setDouble(4, d.precio);
        ps.setDouble(5, d.stock);
        ps.setString(6, d.descripcion);
        ps.setString(7, d.marca);
        ps.setInt(8, d.activo ? 1 : 0);
        UtilidadesService.setStringONull(ps, 9, d.sabor);
        UtilidadesService.setDoubleONull(ps, 10, d.pesoKg);
        UtilidadesService.setStringONull(ps, 11, d.fechaVencimiento);
        UtilidadesService.setStringONull(ps, 12, d.edadConsumo);
        UtilidadesService.setStringONull(ps, 13, d.animalConsume);
        UtilidadesService.setStringONull(ps, 14, d.material);
        UtilidadesService.setIntONull(ps, 15, d.capacidadMl);
        UtilidadesService.setBooleanONull(ps, 16, d.antideslizante);
        UtilidadesService.setDoubleONull(ps, 17, d.precioExtraMaterial);
        UtilidadesService.setDoubleONull(ps, 18, d.recargoAntideslizante);
        UtilidadesService.setStringONull(ps, 19, d.tamano);
        UtilidadesService.setBooleanONull(ps, 20, d.desmontable);
        return ps.executeUpdate();
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

        DatosProducto datos = new DatosProducto();
        datos.tipo = tipo;
        datos.codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código: ");
        datos.nombre = UtilidadesService.leerTextoObligatorio(scanner, "Nombre: ");
        datos.precio = UtilidadesService.leerDecimalMinimo(scanner, "Precio: ", 0);
        datos.stock = UtilidadesService.leerDecimalMinimo(scanner, "Stock (unidades o kg): ", 0);
        datos.descripcion = UtilidadesService.leerTextoObligatorio(scanner, "Descripción: ");
        datos.marca = UtilidadesService.leerTextoObligatorio(scanner, "Marca: ");
        datos.activo = UtilidadesService.leerBooleano(scanner, "¿Está activo? (s/n): ");

        if (tipoOpcion != 1 && datos.stock != Math.floor(datos.stock)) {
            System.out.println("El stock de platos y camas debe ser un número entero.");
            return;
        }

        // Los campos que no aplican al tipo quedan en null y se guardan como NULL.
        if (tipoOpcion == 1) {
            datos.sabor = UtilidadesService.leerTextoObligatorio(scanner, "Sabor: ");
            datos.pesoKg = UtilidadesService.leerDecimalMinimo(scanner, "Peso del paquete (kg): ", 0.01);
            datos.fechaVencimiento = UtilidadesService.leerTextoObligatorio(scanner, "Fecha de vencimiento (AAAA-MM-DD): ");
            datos.edadConsumo = UtilidadesService.leerTextoObligatorio(scanner, "Edad recomendada (cachorro/adulto/senior): ");
            datos.animalConsume = UtilidadesService.leerTextoObligatorio(scanner, "Animal que consume (perro/gato/ambos): ");
        } else if (tipoOpcion == 2) {
            datos.material = UtilidadesService.leerTextoObligatorio(scanner, "Material: ");
            datos.capacidadMl = UtilidadesService.leerEnteroMinimo(scanner, "Capacidad (ml): ", 1);
            datos.antideslizante = UtilidadesService.leerBooleano(scanner, "¿Es antideslizante? (s/n): ");
            datos.precioExtraMaterial = UtilidadesService.leerDecimalMinimo(scanner, "Precio extra por material: ", 0);
            datos.recargoAntideslizante = UtilidadesService.leerDecimalMinimo(scanner, "Recargo antideslizante: ", 0);
        } else {
            datos.tamano = UtilidadesService.leerTextoObligatorio(scanner, "Tamaño: ");
            datos.material = UtilidadesService.leerTextoObligatorio(scanner, "Material: ");
            datos.desmontable = UtilidadesService.leerBooleano(scanner, "¿Es desmontable? (s/n): ");
            datos.precioExtraMaterial = UtilidadesService.leerDecimalMinimo(scanner, "Precio extra del material: ", 0);
        }

        try (PreparedStatement ps = conexion.prepareStatement(SQL_INSERT)) {
            int filas = insertarProducto(ps, datos);
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
        boolean encontrado = false;

        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery("SELECT * FROM PRODUCTOS ORDER BY codigo")) {
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
        String sql = "SELECT * FROM PRODUCTOS WHERE codigo = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet resultado = ps.executeQuery()) {
                if (resultado.next()) {
                    mostrarProducto(resultado);
                } else {
                    System.out.println("No se encontró un producto con ese código.");
                }
            }
        }
    }

    private static void actualizarProducto(Connection conexion, Scanner scanner) throws SQLException {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código del producto que deseas actualizar: ");

        // Una sola consulta: confirma que existe y obtiene el tipo.
        String tipo = null;
        try (PreparedStatement ps = conexion.prepareStatement("SELECT tipo FROM PRODUCTOS WHERE codigo = ?")) {
            ps.setString(1, codigo);
            try (ResultSet resultado = ps.executeQuery()) {
                if (resultado.next()) {
                    tipo = resultado.getString("tipo");
                }
            }
        }
        if (tipo == null) {
            System.out.println("No se encontró un producto con ese código.");
            return;
        }

        String nombre = UtilidadesService.leerTextoObligatorio(scanner, "Nuevo nombre: ");
        double precio = UtilidadesService.leerDecimalMinimo(scanner, "Nuevo precio: ", 0);
        double stock = UtilidadesService.leerDecimalMinimo(scanner, "Nuevo stock: ", 0);
        if (!"Croquetas".equals(tipo) && stock != Math.floor(stock)) {
            System.out.println("El stock de platos y camas debe ser un número entero.");
            return;
        }
        String descripcion = UtilidadesService.leerTextoObligatorio(scanner, "Nueva descripción: ");
        String marca = UtilidadesService.leerTextoObligatorio(scanner, "Nueva marca: ");
        boolean activo = UtilidadesService.leerBooleano(scanner, "¿Está activo? (s/n): ");

        String sql = "UPDATE PRODUCTOS SET nombre = ?, precio = ?, stock = ?, descripcion = ?, "
                + "marca = ?, activo = ? WHERE codigo = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setDouble(2, precio);
            ps.setDouble(3, stock);
            ps.setString(4, descripcion);
            ps.setString(5, marca);
            ps.setInt(6, activo ? 1 : 0);
            ps.setString(7, codigo);

            int filas = ps.executeUpdate();
            System.out.println(filas > 0 ? "Producto actualizado correctamente." : "No se realizó ningún cambio.");
            System.out.println("Se actualizaron los datos generales; los datos específicos del tipo se mantienen.");
        }
    }

    private static void eliminarProducto(Connection conexion, Scanner scanner) throws SQLException {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código del producto que deseas eliminar: ");
        String sql = "DELETE FROM PRODUCTOS WHERE codigo = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, codigo);
            int filas = ps.executeUpdate();
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

        String nombre;
        double stock;
        double total;

        try (PreparedStatement ps = conexion.prepareStatement("SELECT * FROM PRODUCTOS WHERE codigo = ?")) {
            ps.setString(1, codigo);
            try (ResultSet r = ps.executeQuery()) {
                if (!r.next()) { System.out.println("No se encontró el producto."); return; }
                if (r.getInt("activo") != 1) { System.out.println("No se puede vender: el producto está inactivo."); return; }

                String tipo = r.getString("tipo");
                nombre = r.getString("nombre");
                stock = r.getDouble("stock");

                if (!"Croquetas".equals(tipo) && cantidad != Math.floor(cantidad)) {
                    System.out.println("La cantidad de platos y camas debe ser un número entero.");
                    return;
                }
                if (cantidad > stock) { System.out.println("Stock insuficiente. Disponible: " + stock); return; }

                double precio = r.getDouble("precio");
                if ("Croquetas".equals(tipo)) {
                    double pesoPaquete = r.getDouble("peso_kg");
                    if (pesoPaquete <= 0) { System.out.println("El peso del paquete no es válido."); return; }
                    total = (precio / pesoPaquete) * cantidad;
                } else if ("Plato".equals(tipo)) {
                    total = (precio + r.getDouble("precio_extra_material")
                            + (r.getInt("antideslizante") == 1 ? r.getDouble("recargo_antideslizante") : 0)) * cantidad;
                } else {
                    total = (precio + r.getDouble("precio_extra_material")) * cantidad;
                }
            }
        }

        double nuevoStock = stock - cantidad;
        try (PreparedStatement ps = conexion.prepareStatement("UPDATE PRODUCTOS SET stock = ? WHERE codigo = ?")) {
            ps.setDouble(1, nuevoStock);
            ps.setString(2, codigo);
            ps.executeUpdate();
        }
        System.out.printf("Venta registrada: %s%nTotal: S/ %.2f%nStock restante: %.2f%n", nombre, total, nuevoStock);
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

    // Los campos específicos de cada tipo usan tipos envoltorio: null significa NULL en la BD.
    private static class DatosProducto {
        private String codigo;
        private String tipo;
        private String nombre;
        private double precio;
        private double stock;
        private String descripcion;
        private String marca;
        private boolean activo;
        private String sabor;
        private Double pesoKg;
        private String fechaVencimiento;
        private String edadConsumo;
        private String animalConsume;
        private String material;
        private Integer capacidadMl;
        private Boolean antideslizante;
        private Double precioExtraMaterial;
        private Double recargoAntideslizante;
        private String tamano;
        private Boolean desmontable;
    }
}