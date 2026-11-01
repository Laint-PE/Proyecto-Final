package models;


public class PlatoModel extends ProductoModel {
    private String material;
    private int capacidadEnMl;
    private boolean antideslizante;
    private double precioExtraMaterial;
    private double recargoAntideslizante;

    public PlatoModel(String codigo, String nombre, double precio,
                      double stock, String descripcion, String marca,
                      boolean activo, String material, int capacidadEnMl,
                      boolean antideslizante, double precioExtraMaterial,
                      double recargoAntideslizante) {

        super(codigo, nombre, precio, stock, descripcion, marca, activo);
        this.material = material;
        if (capacidadEnMl <= 0) throw new IllegalArgumentException("La capacidad debe ser mayor que cero.");
        if (precioExtraMaterial < 0 || recargoAntideslizante < 0) throw new IllegalArgumentException("Los recargos no pueden ser negativos.");
        this.capacidadEnMl = capacidadEnMl;
        this.antideslizante = antideslizante;
        this.precioExtraMaterial = precioExtraMaterial;
        this.recargoAntideslizante = recargoAntideslizante;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public boolean isAntideslizante() {
        return antideslizante;
    }

    public void setAntideslizante(boolean antideslizante) {
        this.antideslizante = antideslizante;
    }

    public int getCapacidadEnMl() {
        return capacidadEnMl;
    }

    public void setCapacidadEnMl(int capacidadEnMl) {
        if (capacidadEnMl <= 0) throw new IllegalArgumentException("La capacidad debe ser mayor que cero.");
        this.capacidadEnMl = capacidadEnMl;
    }

    public double getPrecioExtraMaterial() {
        return precioExtraMaterial;
    }

    public void setPrecioExtraMaterial(double precioExtraMaterial) {
        if (!Double.isFinite(precioExtraMaterial) || precioExtraMaterial < 0) throw new IllegalArgumentException("El recargo no puede ser negativo.");
        this.precioExtraMaterial = precioExtraMaterial;
    }

    public double getRecargoAntideslizante() {
        return recargoAntideslizante;
    }

    public void setRecargoAntideslizante(double recargoAntideslizante) {
        if (!Double.isFinite(recargoAntideslizante) || recargoAntideslizante < 0) throw new IllegalArgumentException("El recargo no puede ser negativo.");
        this.recargoAntideslizante = recargoAntideslizante;
    }

    @Override
    public double calcularPrecio(double cantidad) {
        if (cantidad <= 0) {
            return 0;
        }

        double precioUnitario = precio + precioExtraMaterial;

        if (antideslizante) {
            precioUnitario = precioUnitario + recargoAntideslizante;
        }

        return precioUnitario * cantidad;
    }
}
