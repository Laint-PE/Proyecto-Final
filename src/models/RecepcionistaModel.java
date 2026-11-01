package models;


public class RecepcionistaModel extends EmpleadoModel {
    private String turno;

    public RecepcionistaModel(String codigo, String nombre, String dni,
                              String telefono, boolean tiempoCompleto,
                              String turno) {
        super(codigo, nombre, dni, telefono, tiempoCompleto);
        if (turno == null || turno.isBlank()) throw new IllegalArgumentException("El turno no puede estar vacío.");
        this.turno = turno.trim();
    }

    public String getTurno() { return turno; }

    @Override
    public double calcularSueldo() {
        return tiempoCompleto ? 1500.0 : 900.0;
    }

    @Override
    public String mostrarFunciones() {
        return "Registrar citas, recibir clientes y organizar la atención. Turno: " + turno;
    }
}
