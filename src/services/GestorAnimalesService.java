package services;

import models.AnimalModel;

import java.util.HashMap;
import java.util.Map;

public class GestorAnimalesService {
    private final Map<String, AnimalModel> animales = new HashMap<>();

    public boolean registrarAnimal(AnimalModel animalModel) {
        if (animales.containsKey(animalModel.getCodigo())) {
            return false;
        }

        animales.put(animalModel.getCodigo(), animalModel);
        return true;
    }

    public AnimalModel buscarAnimal(String codigo) { return animales.get(codigo); }

    public boolean modificarAnimal(String codigo, AnimalModel actualizado) {
        if (!animales.containsKey(codigo) || actualizado == null || !codigo.equals(actualizado.getCodigo())) return false;
        animales.put(codigo, actualizado);
        return true;
    }

    public void listarAnimales() {
        if (animales.isEmpty()) {
            System.out.println("No hay animales registrados.");
            return;
        }

        for (AnimalModel animalModel : animales.values()) {
            System.out.println(
                    animalModel.getCodigo() + " - " + animalModel.getNombre()
                            + " (" + animalModel.getTipoAnimal() + ")"
            );
        }
    }

    public boolean eliminarAnimal(String codigo) {
        return animales.remove(codigo) != null;
    }
}
