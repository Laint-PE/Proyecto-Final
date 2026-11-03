package controllers;

import models.AnimalDAO;
import models.AnimalModel;
import models.ConsultaDAO;
import models.ConsultaModel;
import models.EmpleadoDAO;
import models.EmpleadoModel;
import models.RecepcionistaModel;
import models.VeterinarioModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controlador de consultas.
 * Mantiene una colección temporal (ArrayList) con los objetos recuperados de la BD.
 * El CRUD se ejecuta directamente en la BD mediante ConsultaDAO (JDBC).
 * También entrega a la vista los animales, veterinarios y recepcionistas
 * necesarios para armar una consulta. No imprime ni lee datos del usuario.
 */
public class ConsultaController {

    private final ConsultaDAO dao = new ConsultaDAO();
    private final AnimalDAO animalDAO = new AnimalDAO();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final ArrayList<ConsultaModel> consultas = new ArrayList<>();

    // Prepara las tablas necesarias y llena la colección.
    public void inicializar() throws SQLException {
        dao.prepararTablas();
        cargarDesdeBD();
    }

    // Vacía la colección y la vuelve a llenar con lo que hay en la BD.
    public void cargarDesdeBD() throws SQLException {
        consultas.clear();
        consultas.addAll(dao.listarConsultas());
    }

    // Devuelve la colección en modo solo lectura.
    public List<ConsultaModel> getConsultas() {
        return Collections.unmodifiableList(consultas);
    }

    public boolean existe(String codigo) throws SQLException {
        return dao.existeConsulta(codigo);
    }

    // Busca directamente en la BD para tener el dato más reciente.
    public ConsultaModel buscar(String codigo) throws SQLException {
        return dao.buscarConsulta(codigo);
    }

    // --- Datos de apoyo para armar una consulta ---

    public List<AnimalModel> listarAnimales() throws SQLException {
        return animalDAO.listarAnimales();
    }

    public AnimalModel buscarAnimal(String codigo) throws SQLException {
        return animalDAO.buscarAnimal(codigo);
    }

    public List<VeterinarioModel> listarVeterinariosDisponibles() throws SQLException {
        ArrayList<VeterinarioModel> disponibles = new ArrayList<>();

        for (EmpleadoModel empleado : empleadoDAO.listarEmpleados()) {
            if (empleado instanceof VeterinarioModel vet && vet.isDisponible()) {
                disponibles.add(vet);
            }
        }

        return disponibles;
    }

    // Devuelve null si no existe, no es veterinario o está ocupado.
    public VeterinarioModel buscarVeterinarioDisponible(String codigo) throws SQLException {
        EmpleadoModel empleado = empleadoDAO.buscarEmpleado(codigo);

        if (empleado instanceof VeterinarioModel vet && vet.isDisponible()) {
            return vet;
        }

        return null;
    }

    public List<RecepcionistaModel> listarRecepcionistas() throws SQLException {
        ArrayList<RecepcionistaModel> recepcionistas = new ArrayList<>();

        for (EmpleadoModel empleado : empleadoDAO.listarEmpleados()) {
            if (empleado instanceof RecepcionistaModel rec) {
                recepcionistas.add(rec);
            }
        }

        return recepcionistas;
    }

    // Devuelve null si no existe o no es recepcionista.
    public RecepcionistaModel buscarRecepcionista(String codigo) throws SQLException {
        EmpleadoModel empleado = empleadoDAO.buscarEmpleado(codigo);

        if (empleado instanceof RecepcionistaModel rec) {
            return rec;
        }

        return null;
    }

    // --- Operaciones principales ---

    // La regla de negocio vive en el modelo: consulta.registrarConsulta() ocupa al veterinario.
    // Luego el DAO guarda la consulta y el cambio de disponibilidad en una transacción.
    public boolean registrar(ConsultaModel consulta) throws SQLException {
        if (!consulta.registrarConsulta()) {
            return false;
        }

        boolean registrada = dao.registrarConsulta(consulta);
        cargarDesdeBD();
        return registrada;
    }

    public boolean eliminar(String codigo) throws SQLException {
        boolean eliminada = dao.eliminarConsulta(codigo);
        if (eliminada) {
            cargarDesdeBD();
        }
        return eliminada;
    }
}