package models;


public class VeterinarioModel extends EmpleadoModel {
    private String especialidad;
    private boolean disponible;

    public VeterinarioModel(String codigo, String nombre, String dni,
                            String telefono, boolean tiempoCompleto,
                            String especialidad, boolean disponible) {
        super(codigo, nombre, dni, telefono, tiempoCompleto);
        setEspecialidad(especialidad);
        this.disponible = disponible;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.isBlank()) throw new IllegalArgumentException("La especialidad no puede estar vacía.");
        this.especialidad = especialidad.trim();
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public double calcularSueldo() {
        return tiempoCompleto ? 2500.0 : 1500.0;
    }

    @Override
    public String mostrarFunciones() {
        return "Atender consultas, evaluar animales y recomendar tratamientos. Especialidad: " + especialidad;
    }
}
