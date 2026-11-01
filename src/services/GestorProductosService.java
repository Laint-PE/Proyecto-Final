package services;

import models.ProductoModel;

import java.util.HashMap;
import java.util.Map;

public class GestorProductosService {
    private final Map<String, ProductoModel> productos = new HashMap<>();

    public boolean registrarProducto(ProductoModel productoModel) {
        if (productos.containsKey(productoModel.getCodigo())) {
            return false;
        }

        productos.put(productoModel.getCodigo(), productoModel);
        return true;
    }

    public ProductoModel buscarProducto(String codigo) { return productos.get(codigo); }

    public boolean modificarProducto(String codigo, ProductoModel actualizado) {
        if (!productos.containsKey(codigo) || actualizado == null || !codigo.equals(actualizado.getCodigo())) return false;
        productos.put(codigo, actualizado);
        return true;
    }

    public void listarProductos() {
        if (productos.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }

        for (ProductoModel productoModel : productos.values()) {
            System.out.println(
                    productoModel.getCodigo() + " - " + productoModel.getNombre()
                            + " - Stock: " + productoModel.getStock()
                            + " - Precio: " + productoModel.getPrecio()
            );
        }
    }

    public boolean eliminarProducto(String codigo) {
        return productos.remove(codigo) != null;
    }
}
