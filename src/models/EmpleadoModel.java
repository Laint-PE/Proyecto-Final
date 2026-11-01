package models;


public abstract class EmpleadoModel {
    protected String codigo;
    protected String nombre;
    protected String dni;
    protected String telefono;
    protected boolean tiempoCompleto;

    public EmpleadoModel(String codigo, String nombre, String dni,
                         String telefono, boolean tiempoCompleto) {
        setCodigo(codigo);
        setNombre(nombre);
        setDni(dni);
        setTelefono(telefono);
        this.tiempoCompleto = tiempoCompleto;
    }

    private String texto(String valor, String campo) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException("El " + campo + " no puede estar vacío.");
        return valor.trim();
    }

    public abstract double calcularSueldo();
    public abstract String mostrarFunciones();

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = texto(codigo, "código");
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = texto(nombre, "nombre");
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = texto(dni, "DNI");
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = texto(telefono, "teléfono");
    }

    public boolean isTiempoCompleto() {
        return tiempoCompleto;
    }

    public void setTiempoCompleto(boolean tiempoCompleto) {
        this.tiempoCompleto = tiempoCompleto;
    }
}
