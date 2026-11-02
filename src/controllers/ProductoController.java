package controllers;

import models.CroquetasModel;
import models.ProductoDAO;
import models.ProductoModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controlador de productos.
 * Mantiene una colección temporal (ArrayList) con los objetos recuperados de la BD.
 * El CRUD se ejecuta directamente en la BD mediante ProductoDAO (JDBC).
 * No imprime ni lee datos del usuario: eso es responsabilidad de la vista.
 */
public class ProductoController {

    private final ProductoDAO dao = new ProductoDAO();
    private final ArrayList<ProductoModel> productos = new ArrayList<>();

    // Prepara la tabla, carga ejemplos si está vacía y llena la colección.
    public void inicializar() throws SQLException {
        dao.crearTabla();
        dao.insertarEjemplosSiVacia();
        cargarDesdeBD();
    }

    // Vacía la colección y la vuelve a llenar con lo que hay en la BD.
    public void cargarDesdeBD() throws SQLException {
        productos.clear();
        productos.addAll(dao.listarProductos());
    }

    // Devuelve la colección en modo solo lectura.
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

    // Busca directamente en la BD para tener el dato más reciente.
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

    // Devuelve el mensaje con el resultado de la venta.
    public String registrarVenta(String codigo, double cantidad) throws SQLException {
        String resultado = dao.registrarVenta(codigo, cantidad);
        cargarDesdeBD();
        return resultado;
    }

    // Regla de negocio: solo las croquetas se venden por kg (decimales).
    public boolean permiteDecimales(ProductoModel producto) {
        return producto instanceof CroquetasModel;
    }

    public boolean esEntero(double valor) {
        return valor == Math.floor(valor);
    }
}