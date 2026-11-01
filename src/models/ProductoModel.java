package models;


public abstract class ProductoModel implements IVendibleModel {
    protected String codigo;
    protected String nombre;
    protected double precio;
    protected double stock;
    protected String descripcion;
    protected String marca;
    protected boolean activo;

    public ProductoModel(String codigo, String nombre, double precio,
                         double stock, String descripcion, String marca,
                         boolean activo) {
        setCodigo(codigo);
        setNombre(nombre);
        setPrecio(precio);
        setStock(stock);
        setDescripcion(descripcion);
        setMarca(marca);
        this.activo = activo;
    }

    private String texto(String valor, String campo) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException("La " + campo + " no puede estar vacía.");
        return valor.trim();
    }

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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        if (!Double.isFinite(precio) || precio < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");
        this.precio = precio;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        if (!Double.isFinite(stock) || stock < 0) throw new IllegalArgumentException("El stock no puede ser negativo.");
        this.stock = stock;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = texto(marca, "marca");
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = texto(descripcion, "descripción");
    }

    @Override
    public abstract double calcularPrecio(double cantidad);

    public boolean descontarStock(double cantidad) {
        if (!activo || cantidad <= 0 || cantidad > stock) {
            return false;
        }

        stock = stock - cantidad;
        return true;
    }
}
