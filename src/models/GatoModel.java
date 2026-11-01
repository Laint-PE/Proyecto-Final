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
        this.temperamento = temperamento;
    }

    public String getTemperamento() {
        return temperamento;
    }

    public void setTemperamento(String temperamento) {
        this.temperamento = temperamento;
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
