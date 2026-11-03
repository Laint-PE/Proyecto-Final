package models;


public class PerroModel extends AnimalModel {
    private boolean requiereBozalEnConsulta;
    private String nivelAdiestramiento;

    public PerroModel(String codigo, String raza, String nombre, int edad, double peso,
                      String sexo, String nombreDueno, boolean pedigri, String tipoPelaje,
                      boolean requiereBozalEnConsulta, String nivelAdiestramiento) {
        super(codigo, raza, nombre, edad, peso, sexo, nombreDueno, pedigri, tipoPelaje);
        this.requiereBozalEnConsulta = requiereBozalEnConsulta;
        setNivelAdiestramiento(nivelAdiestramiento);
    }

    public boolean isRequiereBozalEnConsulta() {
        return requiereBozalEnConsulta;
    }

    public void setRequiereBozalEnConsulta(boolean requiereBozalEnConsulta) {
        this.requiereBozalEnConsulta = requiereBozalEnConsulta;
    }

    public String getNivelAdiestramiento() {
        return nivelAdiestramiento;
    }

    public void setNivelAdiestramiento(String nivelAdiestramiento) {
        if (nivelAdiestramiento == null || nivelAdiestramiento.isBlank()) {
            throw new IllegalArgumentException("El campo nivel de adiestramiento no puede estar vacío.");
        }
        this.nivelAdiestramiento = nivelAdiestramiento.trim();
    }

    @Override
    public String getTipoAnimal() {
        return "Perro";
    }
}