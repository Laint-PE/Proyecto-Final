package services;

import models.ConsultaModel;

import java.util.HashMap;
import java.util.Map;

public class GestorConsultaService {
    private final Map<String, ConsultaModel> consultas = new HashMap<>();

    public ConsultaModel buscarConsulta(String codigo) { return consultas.get(codigo); }

    public boolean eliminarConsulta(String codigo) {
        if (consultas.containsKey(codigo)) {
            ConsultaModel consultaModel = consultas.get(codigo);
            consultaModel.getVeterinario().setDisponible(true);
            consultas.remove(codigo);
            return true;
        }

        return false;
    }

    public boolean registrarConsulta(ConsultaModel consultaModel) {
        if (consultas.containsKey(consultaModel.getCodigo())) {
            return false;
        }

        if (!consultaModel.registrarConsulta()) {
            return false;
        }

        consultas.put(consultaModel.getCodigo(), consultaModel);
        return true;
    }

    public void mostrarConsultas() {
        if (consultas.isEmpty()) {
            System.out.println("No hay consultas registradas.");
            return;
        }

        for (ConsultaModel consultaModel : consultas.values()) {
            consultaModel.mostrarInformacion();
            System.out.println(" ");
        }
    }
}
