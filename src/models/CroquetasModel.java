package models;


public class CroquetasModel extends ProductoModel {
    private String sabor;
    private double pesoKg;
    private String fechaVencimiento;
    private String edadConsumo;
    private String animalConsume;

    public CroquetasModel(String codigo, String nombre, double precio, double stock, String descripcion, String marca, boolean activo, String sabor, double pesoKg, String fechaVencimiento, String edadConsumo, String animalConsume) {
        super(codigo, nombre, precio, stock, descripcion, marca, activo);
        this.sabor = sabor;
        if (!Double.isFinite(pesoKg) || pesoKg <= 0) throw new IllegalArgumentException("El peso del paquete debe ser mayor que cero.");
        this.pesoKg = pesoKg;
        this.fechaVencimiento = fechaVencimiento;
        this.edadConsumo = edadConsumo;
        this.animalConsume = animalConsume;
    }

    public String getSabor() {
        return sabor;
    }
    public void setSabor(String sabor) {
        this.sabor = sabor;
    }
    public String getFechaVencimiento() {
        return fechaVencimiento;
    }
    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
    public double getPesoKg() {
        return pesoKg;
    }
    public void setPesoKg(double pesoKg) {
        if (!Double.isFinite(pesoKg) || pesoKg <= 0) throw new IllegalArgumentException("El peso del paquete debe ser mayor que cero.");
        this.pesoKg = pesoKg;
    }

    public String getEdadConsumo() {
        return edadConsumo;
    }

    public void setEdadConsumo(String edadConsumo) {
        this.edadConsumo = edadConsumo;
    }

    public String getAnimalConsume() {
        return animalConsume;
    }

    public void setAnimalConsume(String animalConsume) {
        this.animalConsume = animalConsume;
    }

    public double calcularPrecioKg(double kilos) {
        if (!Double.isFinite(kilos) || kilos <= 0 || pesoKg <= 0) {
            return 0;
        }

        double precioPorKg = precio / pesoKg;
        return precioPorKg * kilos;
    }

    @Override
    public double calcularPrecio(double kilos) {
        return calcularPrecioKg(kilos);
    }

}
