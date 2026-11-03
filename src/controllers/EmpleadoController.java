package controllers;

import models.EmpleadoDAO;
import models.EmpleadoModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class EmpleadoController {

    private final EmpleadoDAO dao = new EmpleadoDAO();
    private final ArrayList<EmpleadoModel> empleados = new ArrayList<>();

    public void inicializar() throws SQLException {
        dao.crearTabla();
        dao.insertarEjemplosSiVacia();
        cargarDesdeBD();
    }

    public void cargarDesdeBD() throws SQLException {
        empleados.clear();
        empleados.addAll(dao.listarEmpleados());
    }

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