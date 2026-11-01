package models;


public abstract class AnimalModel {
    protected String codigo;
    protected String raza;
    protected String nombre;
    protected int edad;
    protected double peso;
    protected String sexo;
    protected String nombreDueno;
    protected boolean pedigri;
    protected String tipoPelaje;

    public AnimalModel(String codigo, String raza, String nombre, int edad,
                       double peso, String sexo, String nombreDueno,
                       boolean pedigri, String tipoPelaje) {
        setCodigo(codigo);
        setRaza(raza);
        setNombre(nombre);
        setEdad(edad);
        setPeso(peso);
        setSexo(sexo);
        setNombreDueno(nombreDueno);
        this.pedigri = pedigri;
        setTipoPelaje(tipoPelaje);
    }

    public void setCodigo(String codigo) {
        this.codigo = textoValido(codigo, "código");
    }

    private String textoValido(String valor, String campo) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException("El " + campo + " no puede estar vacío.");
        return valor.trim();
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = textoValido(raza, "raza");
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = textoValido(nombre, "nombre");
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        if (edad < 0) throw new IllegalArgumentException("La edad no puede ser negativa.");
        this.edad = edad;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        if (!Double.isFinite(peso) || peso <= 0) throw new IllegalArgumentException("El peso debe ser mayor que cero.");
        this.peso = peso;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = textoValido(sexo, "sexo");
    }

    public String getNombreDueno() {
        return nombreDueno;
    }

    public void setNombreDueno(String nombreDueno) {
        this.nombreDueno = textoValido(nombreDueno, "nombre del dueño");
    }

    public boolean isPedigri() {
        return pedigri;
    }

    public void setPedigri(boolean pedigri) {
        this.pedigri = pedigri;
    }

    public String getTipoPelaje() {
        return tipoPelaje;
    }

    public void setTipoPelaje(String tipoPelaje) {
        this.tipoPelaje = textoValido(tipoPelaje, "tipo de pelaje");
    }

    public String getCodigo() {
        return codigo;
    }

    public abstract String getTipoAnimal();
}
