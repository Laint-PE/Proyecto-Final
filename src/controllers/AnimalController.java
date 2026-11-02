package controllers;

import models.AnimalDAO;
import models.AnimalModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controlador de animales.
 * Mantiene una colección temporal (ArrayList) con los objetos recuperados de la BD.
 * El CRUD se ejecuta directamente en la BD mediante AnimalDAO (JDBC).
 * No imprime ni lee datos del usuario: eso es responsabilidad de la vista.
 */
public class AnimalController {

    private final AnimalDAO dao = new AnimalDAO();
    private final ArrayList<AnimalModel> animales = new ArrayList<>();

    // Prepara la tabla, carga ejemplos si está vacía y llena la colección.
    public void inicializar() throws SQLException {
        dao.crearTabla();
        dao.insertarEjemplosSiVacia();
        cargarDesdeBD();
    }

    // Vacía la colección y la vuelve a llenar con lo que hay en la BD.
    public void cargarDesdeBD() throws SQLException {
        animales.clear();
        animales.addAll(dao.listarAnimales());
    }

    // Devuelve la colección en modo solo lectura.
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

    // Busca directamente en la BD para tener el dato más reciente.
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