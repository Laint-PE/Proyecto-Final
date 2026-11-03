package models;

public class ConsultaModel {
    private String codigo;
    private String fecha;
    private String motivo;
    private AnimalModel animalModel;
    private VeterinarioModel veterinarioModel;
    private RecepcionistaModel recepcionistaModel;

    public ConsultaModel(String codigo, String fecha, String motivo,
                         AnimalModel animalModel, VeterinarioModel veterinarioModel,
                         RecepcionistaModel recepcionistaModel) {
        setCodigo(codigo);
        setFecha(fecha);
        setMotivo(motivo);
        if (animalModel == null || veterinarioModel == null || recepcionistaModel == null) throw new IllegalArgumentException("Animal, veterinario y recepcionista son obligatorios.");
        this.animalModel = animalModel;
        this.veterinarioModel = veterinarioModel;
        this.recepcionistaModel = recepcionistaModel;
    }

    private String obligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException("El " + campo + " no puede estar vacío.");
        return valor.trim();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = obligatorio(codigo, "código");
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = obligatorio(fecha, "fecha");
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = obligatorio(motivo, "motivo");
    }

    public AnimalModel getAnimal() {
        return animalModel;
    }

    public void setAnimal(AnimalModel animalModel) {
        this.animalModel = animalModel;
    }

    public VeterinarioModel getVeterinario() {
        return veterinarioModel;
    }

    public void setVeterinario(VeterinarioModel veterinarioModel) {
        this.veterinarioModel = veterinarioModel;
    }

    public RecepcionistaModel getRecepcionista() {
        return recepcionistaModel;
    }

    public void setRecepcionista(RecepcionistaModel recepcionistaModel) {
        this.recepcionistaModel = recepcionistaModel;
    }

    public boolean registrarConsulta() {
        if (veterinarioModel.isDisponible()) {
            veterinarioModel.setDisponible(false);
            return true;
        }

        return false;
    }

    public void mostrarInformacion() {
        System.out.println("Código: " + codigo);
        System.out.println("Fecha: " + fecha);
        System.out.println("Motivo: " + motivo);
        System.out.println("Nombre: " + animalModel.getNombre()
                + " Animal: " + animalModel.getTipoAnimal());
        System.out.println("Veterinario: " + veterinarioModel.getNombre());
        System.out.println("Recepcionista: " + recepcionistaModel.getNombre());
    }
}
