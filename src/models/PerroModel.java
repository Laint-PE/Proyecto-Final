package models;


public class PerroModel extends AnimalModel {
    private boolean requiereBozalEnConsulta;
    private String nivelAdiestramiento;

    public PerroModel(String codigo, String raza, String nombre, int edad, double peso,
                      String sexo, String nombreDueno, boolean pedigri, String tipoPelaje,
                      boolean requiereBozalEnConsulta, String nivelAdiestramiento) {
        super( codigo,raza, nombre, edad, peso, sexo, nombreDueno, pedigri, tipoPelaje);
        this.requiereBozalEnConsulta = requiereBozalEnConsulta;
        this.nivelAdiestramiento = nivelAdiestramiento;
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
        this.nivelAdiestramiento = nivelAdiestramiento;
    }

    @Override
    public String getTipoAnimal() {
        return "Perro";
    }
}
