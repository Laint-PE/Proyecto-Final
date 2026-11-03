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


public class ConsultaController {

    private final ConsultaDAO dao = new ConsultaDAO();
    private final AnimalDAO animalDAO = new AnimalDAO();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final ArrayList<ConsultaModel> consultas = new ArrayList<>();

    public void inicializar() throws SQLException {
        dao.prepararTablas();
        cargarDesdeBD();
    }

    public void cargarDesdeBD() throws SQLException {
        consultas.clear();
        consultas.addAll(dao.listarConsultas());
    }

    public List<ConsultaModel> getConsultas() {
        return Collections.unmodifiableList(consultas);
    }

    public boolean existe(String codigo) throws SQLException {
        return dao.existeConsulta(codigo);
    }

    public ConsultaModel buscar(String codigo) throws SQLException {
        return dao.buscarConsulta(codigo);
    }



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

    public RecepcionistaModel buscarRecepcionista(String codigo) throws SQLException {
        EmpleadoModel empleado = empleadoDAO.buscarEmpleado(codigo);

        if (empleado instanceof RecepcionistaModel rec) {
            return rec;
        }

        return null;
    }

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