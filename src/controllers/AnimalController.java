package controllers;

import models.AnimalDAO;
import models.AnimalModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AnimalController {

    private final AnimalDAO dao = new AnimalDAO();
    private final ArrayList<AnimalModel> animales = new ArrayList<>();

    public void inicializar() throws SQLException {
        dao.crearTabla();
        dao.insertarEjemplosSiVacia();
        cargarDesdeBD();
    }

    public void cargarDesdeBD() throws SQLException {
        animales.clear();
        animales.addAll(dao.listarAnimales());
    }

    public List<AnimalModel> getAnimales() {
        return Collections.unmodifiableList(animales);
    }

    public void registrar(AnimalModel animal) throws SQLException {
        dao.registrarAnimal(animal);
        cargarDesdeBD();
    }

    public boolean existe(String codigo) throws SQLException {
        return dao.existeAnimal(codigo);
    }

    public AnimalModel buscar(String codigo) throws SQLException {
        return dao.buscarAnimal(codigo);
    }

    public boolean actualizar(AnimalModel animal) throws SQLException {
        if (!dao.existeAnimal(animal.getCodigo())) {
            return false;
        }
        dao.actualizarAnimal(animal);
        cargarDesdeBD();
        return true;
    }

    public boolean eliminar(String codigo) throws SQLException {
        if (!dao.existeAnimal(codigo)) {
            return false;
        }
        dao.eliminarAnimal(codigo);
        cargarDesdeBD();
        return true;
    }
}