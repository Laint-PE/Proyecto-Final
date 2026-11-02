package models;

import services.ConexionBDService;
import services.UtilidadesService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ProductoDAO {

    private static final String SQL_INSERT = "INSERT INTO PRODUCTOS (codigo, tipo, nombre, precio, stock, "
            + "descripcion, marca, activo, sabor, peso_kg, fecha_vencimiento, edad_consumo, animal_consume, "
            + "material, capacidad_ml, antideslizante, precio_extra_material, recargo_antideslizante, "
            + "tamano, desmontable) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public void crearTabla() throws SQLException {
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

        try (Connection conexion = ConexionBDService.conectar();
             Statement sentencia = conexion.createStatement()) {
            sentencia.executeUpdate(sql);
        }
    }

    public void insertarEjemplosSiVacia() throws SQLException {
        String sql = "SELECT COUNT(*) FROM PRODUCTOS";

        try (Connection conexion = ConexionBDService.conectar();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {

            if (resultado.next() && resultado.getInt(1) > 0) {
                return;
            }
        }

        CroquetasModel croquetas = new CroquetasModel(
                "CRQ001", "Croquetas para perro adulto", 75, 20,
                "Alimento seco para perro adulto", "PetVida", true,
                "Pollo", 3, "2027-12-31", "Adulto", "Perro"
        );

        PlatoModel plato = new PlatoModel(
                "PLA001", "Plato antideslizante", 18, 15,
                "Plato para alimento", "AnimalHome", true,
                "Acero", 500, true, 3, 2
        );

        CamaModel cama = new CamaModel(
                "CAMA001", "Cama acolchada mediana", 95, 8,
                "Cama lavable para mascota", "AnimalHome", true,
                "Mediana", "Tela", true, 10
        );

        registrarProducto(croquetas);
        registrarProducto(plato);
        registrarProducto(cama);

        System.out.println("Se cargaron tres productos de ejemplo.");
    }

    public boolean registrarProducto(ProductoModel producto) throws SQLException {
        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(SQL_INSERT)) {

            ps.setString(1, producto.getCodigo());

            if (producto instanceof CroquetasModel) {
                ps.setString(2, "Croquetas");
            } else if (producto instanceof PlatoModel) {
                ps.setString(2, "Plato");
            } else {
                ps.setString(2, "Cama");
            }

            ps.setString(3, producto.getNombre());
            ps.setDouble(4, producto.getPrecio());
            ps.setDouble(5, producto.getStock());
            ps.setString(6, producto.getDescripcion());
            ps.setString(7, producto.getMarca());
            ps.setInt(8, producto.isActivo() ? 1 : 0);

            if (producto instanceof CroquetasModel croquetas) {
                UtilidadesService.setStringONull(ps, 9, croquetas.getSabor());
                UtilidadesService.setDoubleONull(ps, 10, croquetas.getPesoKg());
                UtilidadesService.setStringONull(ps, 11, croquetas.getFechaVencimiento());
                UtilidadesService.setStringONull(ps, 12, croquetas.getEdadConsumo());
                UtilidadesService.setStringONull(ps, 13, croquetas.getAnimalConsume());

                UtilidadesService.setStringONull(ps, 14, null);
                UtilidadesService.setIntONull(ps, 15, null);
                UtilidadesService.setBooleanONull(ps, 16, null);
                UtilidadesService.setDoubleONull(ps, 17, null);
                UtilidadesService.setDoubleONull(ps, 18, null);
                UtilidadesService.setStringONull(ps, 19, null);
                UtilidadesService.setBooleanONull(ps, 20, null);

            } else if (producto instanceof PlatoModel plato) {
                for (int i = 9; i <= 13; i++) {
                    if (i == 10) {
                        UtilidadesService.setDoubleONull(ps, i, null);
                    } else {
                        UtilidadesService.setStringONull(ps, i, null);
                    }
                }

                UtilidadesService.setStringONull(ps, 14, plato.getMaterial());
                UtilidadesService.setIntONull(ps, 15, plato.getCapacidadEnMl());
                UtilidadesService.setBooleanONull(ps, 16, plato.isAntideslizante());
                UtilidadesService.setDoubleONull(ps, 17, plato.getPrecioExtraMaterial());
                UtilidadesService.setDoubleONull(ps, 18, plato.getRecargoAntideslizante());
                UtilidadesService.setStringONull(ps, 19, null);
                UtilidadesService.setBooleanONull(ps, 20, null);

            } else if (producto instanceof CamaModel cama) {
                for (int i = 9; i <= 13; i++) {
                    if (i == 10) {
                        UtilidadesService.setDoubleONull(ps, i, null);
                    } else {
                        UtilidadesService.setStringONull(ps, i, null);
                    }
                }

                UtilidadesService.setStringONull(ps, 14, cama.getMaterial());
                UtilidadesService.setIntONull(ps, 15, null);
                UtilidadesService.setBooleanONull(ps, 16, null);
                UtilidadesService.setDoubleONull(ps, 17, cama.getPrecioExtraMaterial());
                UtilidadesService.setDoubleONull(ps, 18, null);
                UtilidadesService.setStringONull(ps, 19, cama.getTamano());
                UtilidadesService.setBooleanONull(ps, 20, cama.isDesmontable());
            }

            return ps.executeUpdate() > 0;
        }
    }

    public ArrayList<ProductoModel> listarProductos() throws SQLException {
        ArrayList<ProductoModel> productos = new ArrayList<>();

        String sql = "SELECT * FROM PRODUCTOS ORDER BY codigo";

        try (Connection conexion = ConexionBDService.conectar();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {

            while (resultado.next()) {
                productos.add(convertirProducto(resultado));
            }
        }

        return productos;
    }

    public ProductoModel buscarProducto(String codigo) throws SQLException {
        String sql = "SELECT * FROM PRODUCTOS WHERE codigo = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet resultado = ps.executeQuery()) {
                if (resultado.next()) {
                    return convertirProducto(resultado);
                }
            }
        }

        return null;
    }

    public boolean actualizarProducto(String codigo, String nombre, double precio,
                                      double stock, String descripcion,
                                      String marca, boolean activo) throws SQLException {

        String sql = "UPDATE PRODUCTOS SET nombre = ?, precio = ?, stock = ?, "
                + "descripcion = ?, marca = ?, activo = ? WHERE codigo = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setDouble(2, precio);
            ps.setDouble(3, stock);
            ps.setString(4, descripcion);
            ps.setString(5, marca);
            ps.setInt(6, activo ? 1 : 0);
            ps.setString(7, codigo);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminarProducto(String codigo) throws SQLException {
        String sql = "DELETE FROM PRODUCTOS WHERE codigo = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);
            return ps.executeUpdate() > 0;
        }
    }

    public String registrarVenta(String codigo, double cantidad) throws SQLException {
        ProductoModel producto = buscarProducto(codigo);

        if (producto == null) {
            return "No se encontró el producto.";
        }

        if (!producto.isActivo()) {
            return "No se puede vender: el producto está inactivo.";
        }

        if (!(producto instanceof CroquetasModel) && cantidad != Math.floor(cantidad)) {
            return "La cantidad de platos y camas debe ser un número entero.";
        }

        if (cantidad > producto.getStock()) {
            return "Stock insuficiente. Disponible: " + producto.getStock();
        }

        double total = producto.calcularPrecio(cantidad);

        if (!producto.descontarStock(cantidad)) {
            return "No se pudo realizar la venta.";
        }

        String sql = "UPDATE PRODUCTOS SET stock = ? WHERE codigo = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setDouble(1, producto.getStock());
            ps.setString(2, codigo);

            if (ps.executeUpdate() > 0) {
                return String.format(
                        "Venta registrada: %s%nTotal: S/ %.2f%nStock restante: %.2f",
                        producto.getNombre(), total, producto.getStock()
                );
            }
        }

        return "No se pudo actualizar el stock.";
    }

    private ProductoModel convertirProducto(ResultSet resultado) throws SQLException {
        String codigo = resultado.getString("codigo");
        String tipo = resultado.getString("tipo");
        String nombre = resultado.getString("nombre");
        double precio = resultado.getDouble("precio");
        double stock = resultado.getDouble("stock");
        String descripcion = resultado.getString("descripcion");
        String marca = resultado.getString("marca");
        boolean activo = resultado.getInt("activo") == 1;

        if ("Croquetas".equals(tipo)) {
            return new CroquetasModel(
                    codigo, nombre, precio, stock, descripcion, marca, activo,
                    resultado.getString("sabor"),
                    resultado.getDouble("peso_kg"),
                    resultado.getString("fecha_vencimiento"),
                    resultado.getString("edad_consumo"),
                    resultado.getString("animal_consume")
            );

        } else if ("Plato".equals(tipo)) {
            return new PlatoModel(
                    codigo, nombre, precio, stock, descripcion, marca, activo,
                    resultado.getString("material"),
                    resultado.getInt("capacidad_ml"),
                    resultado.getInt("antideslizante") == 1,
                    resultado.getDouble("precio_extra_material"),
                    resultado.getDouble("recargo_antideslizante")
            );

        } else {
            return new CamaModel(
                    codigo, nombre, precio, stock, descripcion, marca, activo,
                    resultado.getString("tamano"),
                    resultado.getString("material"),
                    resultado.getInt("desmontable") == 1,
                    resultado.getDouble("precio_extra_material")
            );
        }
    }
}
