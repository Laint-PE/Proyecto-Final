package models;


public class VentaModel {
    private ProductoModel productoModel;

    public VentaModel(ProductoModel productoModel) {
        this.productoModel = productoModel;
    }

    public double registrarVentas(double cantidad) {
        double stockInicial = productoModel.getStock();
        double total = productoModel.calcularPrecio(cantidad);

        if (total <= 0) {
            return 0;
        } else if (!productoModel.descontarStock(cantidad)) {
            return 0;
        } else {
            System.out.println("Venta registrada. Total: " + total);
            System.out.println("Stock Inicial: " + stockInicial);
            System.out.println("Stock restante: " + productoModel.getStock());
            return total;
        }
    }
}
