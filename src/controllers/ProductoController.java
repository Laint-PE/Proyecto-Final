package controllers;

import models.CroquetasModel;
import models.ProductoDAO;
import models.ProductoModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ProductoController {

    private final ProductoDAO dao = new ProductoDAO();
    private final ArrayList<ProductoModel> productos = new ArrayList<>();

    public void inicializar() throws SQLException {
        dao.crearTabla();
        dao.insertarEjemplosSiVacia();
        cargarDesdeBD();
    }

    public void cargarDesdeBD() throws SQLException {
        productos.clear();
        productos.addAll(dao.listarProductos());
    }

    public List<ProductoModel> getProductos() {
        return Collections.unmodifiableList(productos);
    }

    public int cantidadProductos() {
        return productos.size();
    }

    public boolean registrar(ProductoModel producto) throws SQLException {
        boolean registrado = dao.registrarProducto(producto);
        if (registrado) {
            cargarDesdeBD();
        }
        return registrado;
    }

    public ProductoModel buscar(String codigo) throws SQLException {
        return dao.buscarProducto(codigo);
    }

    public boolean actualizar(String codigo, String nombre, double precio, double stock,
                              String descripcion, String marca, boolean activo) throws SQLException {
        boolean actualizado = dao.actualizarProducto(codigo, nombre, precio, stock,
                descripcion, marca, activo);
        if (actualizado) {
            cargarDesdeBD();
        }
        return actualizado;
    }

    public boolean eliminar(String codigo) throws SQLException {
        boolean eliminado = dao.eliminarProducto(codigo);
        if (eliminado) {
            cargarDesdeBD();
        }
        return eliminado;
    }

    public String registrarVenta(String codigo, double cantidad) throws SQLException {
        String resultado = dao.registrarVenta(codigo, cantidad);
        cargarDesdeBD();
        return resultado;
    }

    public boolean permiteDecimales(ProductoModel producto) {
        return producto instanceof CroquetasModel;
    }

    public boolean esEntero(double valor) {
        return valor == Math.floor(valor);
    }
}