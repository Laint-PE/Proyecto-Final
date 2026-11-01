package models;


public class CamaModel extends ProductoModel {
    private String tamano;
    private String material;
    private boolean desmontable;
    private double precioExtraMaterial;

    public CamaModel(String codigo, String nombre, double precio,
                     double stock, String descripcion, String marca,
                     boolean activo, String tamano, String material,
                     boolean desmontable, double precioExtraMaterial) {

        super(codigo, nombre, precio, stock, descripcion, marca, activo);
        if (tamano == null || tamano.isBlank() || material == null || material.isBlank()) throw new IllegalArgumentException("El tamaño y material son obligatorios.");
        if (precioExtraMaterial < 0) throw new IllegalArgumentException("El recargo no puede ser negativo.");
        this.tamano = tamano;
        this.material = material;
        this.desmontable = desmontable;
        this.precioExtraMaterial = precioExtraMaterial;
    }

    public String getTamano() {
        return tamano;
    }

    public void setTamano(String tamano) {
        this.tamano = tamano;
    }

    public boolean isDesmontable() {
        return desmontable;
    }

    public void setDesmontable(boolean desmontable) {
        this.desmontable = desmontable;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public double getPrecioExtraMaterial() {
        return precioExtraMaterial;
    }

    public void setPrecioExtraMaterial(double precioExtraMaterial) {
        if (!Double.isFinite(precioExtraMaterial) || precioExtraMaterial < 0) throw new IllegalArgumentException("El recargo no puede ser negativo.");
        this.precioExtraMaterial = precioExtraMaterial;
    }

    @Override
    public double calcularPrecio(double cantidad) {
        if (cantidad <= 0) {
            return 0;
        }

        double precioFinal = precio + precioExtraMaterial;
        return precioFinal * cantidad;
    }
}
