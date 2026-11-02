package controllers;

import models.EmpleadoDAO;
import models.EmpleadoModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controlador de empleados.
 * Mantiene una colección temporal (ArrayList) con los objetos recuperados de la BD.
 * El CRUD se ejecuta directamente en la BD mediante EmpleadoDAO (JDBC).
 * No imprime ni lee datos del usuario: eso es responsabilidad de la vista.
 */
public class EmpleadoController {

    private final EmpleadoDAO dao = new EmpleadoDAO();
    private final ArrayList<EmpleadoModel> empleados = new ArrayList<>();

    // Prepara la tabla, carga ejemplos si está vacía y llena la colección.
    public void inicializar() throws SQLException {
        dao.crearTabla();
        dao.insertarEjemplosSiVacia();
        cargarDesdeBD();
    }

    // Vacía la colección y la vuelve a llenar con lo que hay en la BD.
    public void cargarDesdeBD() throws SQLException {
        empleados.clear();
        empleados.addAll(dao.listarEmpleados());
    }

    // Devuelve la colección en modo solo lectura.
    public List<EmpleadoModel> getEmpleados() {
        return Collections.unmodifiableList(empleados);
    }

    public boolean registrar(EmpleadoModel empleado) throws SQLException {
        boolean registrado = dao.registrarEmpleado(empleado);
        if (registrado) {
            cargarDesdeBD();
        }
        return registrado;
    }

    public boolean existe(String codigo) throws SQLException {
        return dao.existeEmpleado(codigo);
    }

    // Busca directamente en la BD para tener el dato más reciente.
    public EmpleadoModel buscar(String codigo) throws SQLException {
        return dao.buscarEmpleado(codigo);
    }

    public boolean actualizar(EmpleadoModel empleado) throws SQLException {
        if (!dao.existeEmpleado(empleado.getCodigo())) {
            return false;
        }
        boolean actualizado = dao.actualizarEmpleado(empleado);
        cargarDesdeBD();
        return actualizado;
    }

    public boolean eliminar(String codigo) throws SQLException {
        boolean eliminado = dao.eliminarEmpleado(codigo);
        if (eliminado) {
            cargarDesdeBD();
        }
        return eliminado;
    }
}