package services;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

public class UtilidadesService {

    public static String leerTexto(Scanner scanner, String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    public static String leerTextoObligatorio(Scanner scanner, String mensaje) {
        while (true) {
            String valor = leerTexto(scanner, mensaje);
            if (!valor.isBlank()) return valor;
            System.out.println("Este campo no puede quedar vacío.");
        }
    }

    public static int leerEntero(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un número entero válido.");
            }
        }
    }

    public static int leerEnteroMinimo(Scanner scanner, String mensaje, int minimo) {
        while (true) {
            int valor = leerEntero(scanner, mensaje);
            if (valor >= minimo) return valor;
            System.out.println("El valor debe ser mayor o igual que " + minimo + ".");
        }
    }

    public static double leerDecimal(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                double valor = Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
                if (Double.isFinite(valor)) return valor;
                System.out.println("Ingresa un número finito válido.");
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un número válido. Usa punto o coma para los decimales.");
            }
        }
    }

    public static double leerDecimalMinimo(Scanner scanner, String mensaje, double minimo) {
        while (true) {
            double valor = leerDecimal(scanner, mensaje);
            if (valor >= minimo) return valor;
            System.out.println("El valor debe ser mayor o igual que " + minimo + ".");
        }
    }

    public static boolean leerBooleano(Scanner scanner, String mensaje) {
        while (true) {
            String respuesta = leerTexto(scanner, mensaje).toLowerCase();
            if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")
                    || respuesta.equals("true") || respuesta.equals("1")) {
                return true;
            }
            if (respuesta.equals("n") || respuesta.equals("no")
                    || respuesta.equals("false") || respuesta.equals("0")) {
                return false;
            }
            System.out.println("Responde s o n.");
        }
    }


    public static void setStringONull(PreparedStatement ps, int posicion, String valor) throws SQLException {
        if (valor == null) {
            ps.setNull(posicion, Types.VARCHAR);
        } else {
            ps.setString(posicion, valor);
        }
    }

    public static void setDoubleONull(PreparedStatement ps, int posicion, Double valor) throws SQLException {
        if (valor == null) {
            ps.setNull(posicion, Types.DOUBLE);
        } else {
            ps.setDouble(posicion, valor);
        }
    }

    public static void setIntONull(PreparedStatement ps, int posicion, Integer valor) throws SQLException {
        if (valor == null) {
            ps.setNull(posicion, Types.INTEGER);
        } else {
            ps.setInt(posicion, valor);
        }
    }

    public static void setBooleanONull(PreparedStatement ps, int posicion, Boolean valor) throws SQLException {
        if (valor == null) {
            ps.setNull(posicion, Types.TINYINT);
        } else {
            ps.setInt(posicion, valor ? 1 : 0);
        }
    }
}