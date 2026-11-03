package models;


public class GatoModel extends AnimalModel {
    private boolean requiereTransportadora;
    private String temperamento;

    public GatoModel(String codigo, String raza, String nombre, int edad,
                     double peso, String sexo, String nombreDueno,
                     boolean pedigri, String tipoPelaje,
                     boolean requiereTransportadora, String temperamento) {

        super(codigo, raza, nombre, edad, peso, sexo, nombreDueno,
                pedigri, tipoPelaje);

        this.requiereTransportadora = requiereTransportadora;
        setTemperamento(temperamento);
    }

    public String getTemperamento() {
        return temperamento;
    }

    public void setTemperamento(String temperamento) {
        if (temperamento == null || temperamento.isBlank()) {
            throw new IllegalArgumentException("El campo temperamento no puede estar vacío.");
        }
        this.temperamento = temperamento.trim();
    }

    public boolean isRequiereTransportadora() {
        return requiereTransportadora;
    }

    public void setRequiereTransportadora(boolean requiereTransportadora) {
        this.requiereTransportadora = requiereTransportadora;
    }

    @Override
    public String getTipoAnimal() {
        return "Gato";
    }
}