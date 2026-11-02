package controllers;

import services.ConexionBDService;
import services.UtilidadesService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class AnimalesJDBCController {

    private static final String SQL_INSERT = "INSERT INTO ANIMALES (codigo, tipo_animal, raza, nombre, edad, peso, "
            + "sexo, nombre_dueno, pedigri, tipo_pelaje, requiere_bozal, nivel_adiestramiento, "
            + "requiere_transportadora, temperamento) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE = "UPDATE ANIMALES SET tipo_animal = ?, raza = ?, nombre = ?, edad = ?, "
            + "peso = ?, sexo = ?, nombre_dueno = ?, pedigri = ?, tipo_pelaje = ?, requiere_bozal = ?, "
            + "nivel_adiestramiento = ?, requiere_transportadora = ?, temperamento = ? WHERE codigo = ?";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            administrarAnimales(scanner);
        }
    }

    public static void administrarAnimales(Scanner scanner) {
        try (Connection conexion = ConexionBDService.conectar()) {
            crearTabla(conexion);
            insertarEjemplosSiVacia(conexion);
            System.out.println("Conexión con la base de datos realizada correctamente.");

            int opcion;
            do {
                System.out.println("\n===== GESTIONAR ANIMALES =====");
                System.out.println("1. Registrar animal");
                System.out.println("2. Listar animales");
                System.out.println("3. Buscar animal por código");
                System.out.println("4. Actualizar animal");
                System.out.println("5. Eliminar animal");
                System.out.println("6. Volver al menú principal");
                opcion = UtilidadesService.leerEntero(scanner, "Elige una opción: ");

                switch (opcion) {
                    case 1 -> registrarAnimal(conexion, scanner);
                    case 2 -> listarAnimales(conexion);
                    case 3 -> buscarAnimal(conexion, scanner);
                    case 4 -> actualizarAnimal(conexion, scanner);
                    case 5 -> eliminarAnimal(conexion, scanner);
                    case 6 -> System.out.println("Volviendo al menú principal.");
                    default -> System.out.println("Opción inválida. Intenta nuevamente.");
                }
            } while (opcion != 6);
        } catch (SQLException e) {
            System.out.println("Error con la base de datos: " + e.getMessage());
        }
    }

    private static void crearTabla(Connection conexion) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS ANIMALES ("
                + "codigo VARCHAR(50) PRIMARY KEY, "
                + "tipo_animal VARCHAR(20) NOT NULL, "
                + "raza VARCHAR(100) NOT NULL, "
                + "nombre VARCHAR(100) NOT NULL, "
                + "edad INT NOT NULL, "
                + "peso DOUBLE NOT NULL, "
                + "sexo VARCHAR(20), "
                + "nombre_dueno VARCHAR(100), "
                + "pedigri TINYINT NOT NULL, "
                + "tipo_pelaje VARCHAR(50), "
                + "requiere_bozal TINYINT NULL, "
                + "nivel_adiestramiento VARCHAR(100), "
                + "requiere_transportadora TINYINT NULL, "
                + "temperamento VARCHAR(100)"
                + ")";

        try (Statement sentencia = conexion.createStatement()) {
            sentencia.executeUpdate(sql);
        }
    }

    private static void insertarEjemplosSiVacia(Connection conexion) throws SQLException {
        int cantidad = 0;
        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery("SELECT COUNT(*) FROM ANIMALES")) {
            if (resultado.next()) {
                cantidad = resultado.getInt(1);
            }
        }

        if (cantidad > 0) {
            return;
        }

        try (PreparedStatement ps = conexion.prepareStatement(SQL_INSERT)) {
            insertarAnimal(ps, perroEjemplo("ANM001", "Labrador", "Simba", 3, 25.5, "Macho", "Carlos Díaz", true, "Corto", false, "Intermedio"));
            insertarAnimal(ps, gatoEjemplo("ANM002", "Persa", "Luna", 2, 4.2, "Hembra", "Ana Torres", false, "Largo", true, "Tranquilo"));
            insertarAnimal(ps, perroEjemplo("ANM003", "Mestizo", "Max", 5, 18.0, "Macho", "Jorge Silva", false, "Medio", true, "Básico"));
        }
        System.out.println("Se cargaron tres animales de ejemplo.");
    }

    private static DatosAnimal perroEjemplo(String codigo, String raza, String nombre, int edad, double peso,
                                            String sexo, String dueno, boolean pedigri, String pelaje,
                                            boolean bozal, String adiestramiento) {
        DatosAnimal d = new DatosAnimal();
        d.codigo = codigo;
        d.tipo = "Perro";
        d.raza = raza;
        d.nombre = nombre;
        d.edad = edad;
        d.peso = peso;
        d.sexo = sexo;
        d.nombreDueno = dueno;
        d.pedigri = pedigri;
        d.tipoPelaje = pelaje;
        d.requiereBozal = bozal;
        d.nivelAdiestramiento = adiestramiento;
        return d;
    }

    private static DatosAnimal gatoEjemplo(String codigo, String raza, String nombre, int edad, double peso,
                                           String sexo, String dueno, boolean pedigri, String pelaje,
                                           boolean transportadora, String temperamento) {
        DatosAnimal d = new DatosAnimal();
        d.codigo = codigo;
        d.tipo = "Gato";
        d.raza = raza;
        d.nombre = nombre;
        d.edad = edad;
        d.peso = peso;
        d.sexo = sexo;
        d.nombreDueno = dueno;
        d.pedigri = pedigri;
        d.tipoPelaje = pelaje;
        d.requiereTransportadora = transportadora;
        d.temperamento = temperamento;
        return d;
    }

    private static int insertarAnimal(PreparedStatement ps, DatosAnimal d) throws SQLException {
        ps.setString(1, d.codigo);
        asignarCampos(ps, d, 2);
        return ps.executeUpdate();
    }

    private static void asignarCampos(PreparedStatement ps, DatosAnimal d, int inicio) throws SQLException {
        int i = inicio;
        ps.setString(i++, d.tipo);
        ps.setString(i++, d.raza);
        ps.setString(i++, d.nombre);
        ps.setInt(i++, d.edad);
        ps.setDouble(i++, d.peso);
        ps.setString(i++, d.sexo);
        ps.setString(i++, d.nombreDueno);
        ps.setInt(i++, d.pedigri ? 1 : 0);
        ps.setString(i++, d.tipoPelaje);
        UtilidadesService.setBooleanONull(ps, i++, d.requiereBozal);
        UtilidadesService.setStringONull(ps, i++, d.nivelAdiestramiento);
        UtilidadesService.setBooleanONull(ps, i++, d.requiereTransportadora);
        UtilidadesService.setStringONull(ps, i, d.temperamento);
    }

    private static void registrarAnimal(Connection conexion, Scanner scanner) {
        DatosAnimal datos = leerDatosAnimal(scanner, null);
        if (datos == null) {
            return;
        }

        try (PreparedStatement ps = conexion.prepareStatement(SQL_INSERT)) {
            int filas = insertarAnimal(ps, datos);
            System.out.println(filas > 0 ? "Animal registrado correctamente." : "No se registró el animal.");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("Ya existe un animal con ese código.");
            } else {
                System.out.println("No se pudo registrar el animal: " + e.getMessage());
            }
        }
    }

    private static DatosAnimal leerDatosAnimal(Scanner scanner, String codigoExistente) {
        System.out.println("\nTipo de animal:");
        System.out.println("1. Perro");
        System.out.println("2. Gato");
        int tipoOpcion = UtilidadesService.leerEntero(scanner, "Elige el tipo: ");

        String tipo;
        if (tipoOpcion == 1) {
            tipo = "Perro";
        } else if (tipoOpcion == 2) {
            tipo = "Gato";
        } else {
            System.out.println("Tipo de animal inválido.");
            return null;
        }

        DatosAnimal datos = new DatosAnimal();
        datos.codigo = codigoExistente == null
                ? UtilidadesService.leerTextoObligatorio(scanner, "Código: ")
                : codigoExistente;
        datos.tipo = tipo;
        datos.raza = UtilidadesService.leerTextoObligatorio(scanner, "Raza: ");
        datos.nombre = UtilidadesService.leerTextoObligatorio(scanner, "Nombre: ");
        datos.edad = UtilidadesService.leerEnteroMinimo(scanner, "Edad: ", 0);
        datos.peso = UtilidadesService.leerDecimalMinimo(scanner, "Peso en kg: ", 0.01);
        datos.sexo = UtilidadesService.leerTextoObligatorio(scanner, "Sexo: ");
        datos.nombreDueno = UtilidadesService.leerTextoObligatorio(scanner, "Nombre del dueño: ");
        datos.pedigri = UtilidadesService.leerBooleano(scanner, "¿Tiene pedigrí? (s/n): ");
        datos.tipoPelaje = UtilidadesService.leerTextoObligatorio(scanner, "Tipo de pelaje: ");

        if (tipoOpcion == 1) {
            datos.requiereBozal = UtilidadesService.leerBooleano(scanner, "¿Requiere bozal en consulta? (s/n): ");
            datos.nivelAdiestramiento = UtilidadesService.leerTexto(scanner, "Nivel de adiestramiento: ");
        } else {
            datos.requiereTransportadora = UtilidadesService.leerBooleano(scanner, "¿Requiere transportadora? (s/n): ");
            datos.temperamento = UtilidadesService.leerTexto(scanner, "Temperamento: ");
        }
        return datos;
    }

    private static void listarAnimales(Connection conexion) throws SQLException {
        boolean encontrado = false;
        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery("SELECT * FROM ANIMALES ORDER BY codigo")) {
            while (resultado.next()) {
                encontrado = true;
                mostrarAnimal(resultado);
                System.out.println("----------------------------------------");
            }
        }
        if (!encontrado) {
            System.out.println("No hay animales registrados.");
        }
    }

    private static void buscarAnimal(Connection conexion, Scanner scanner) throws SQLException {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código del animal: ");
        String sql = "SELECT * FROM ANIMALES WHERE codigo = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet resultado = ps.executeQuery()) {
                if (resultado.next()) {
                    mostrarAnimal(resultado);
                } else {
                    System.out.println("No se encontró un animal con ese código.");
                }
            }
        }
    }

    private static void actualizarAnimal(Connection conexion, Scanner scanner) throws SQLException {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código del animal que deseas actualizar: ");

        if (!existeAnimal(conexion, codigo)) {
            System.out.println("No se encontró un animal con ese código.");
            return;
        }

        DatosAnimal datos = leerDatosAnimal(scanner, codigo);
        if (datos == null) {
            return;
        }

        try (PreparedStatement ps = conexion.prepareStatement(SQL_UPDATE)) {
            asignarCampos(ps, datos, 1);   // parámetros 1 al 13: nuevos valores
            ps.setString(14, codigo);      // parámetro 14: condición WHERE
            int filas = ps.executeUpdate();
            System.out.println(filas > 0 ? "Animal actualizado correctamente." : "No se realizó ningún cambio.");
        }
    }

    private static void eliminarAnimal(Connection conexion, Scanner scanner) throws SQLException {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código del animal que deseas eliminar: ");
        String sql = "DELETE FROM ANIMALES WHERE codigo = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, codigo);
            int filas = ps.executeUpdate();
            System.out.println(filas > 0 ? "Animal eliminado correctamente." : "No se encontró un animal con ese código.");
        }
    }

    private static boolean existeAnimal(Connection conexion, String codigo) throws SQLException {
        String sql = "SELECT 1 FROM ANIMALES WHERE codigo = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet resultado = ps.executeQuery()) {
                return resultado.next();
            }
        }
    }

    private static void mostrarAnimal(ResultSet resultado) throws SQLException {
        System.out.println("Código: " + resultado.getString("codigo"));
        System.out.println("Tipo: " + resultado.getString("tipo_animal"));
        System.out.println("Raza: " + resultado.getString("raza"));
        System.out.println("Nombre: " + resultado.getString("nombre"));
        System.out.println("Edad: " + resultado.getInt("edad"));
        System.out.println("Peso: " + resultado.getDouble("peso") + " kg");
        System.out.println("Sexo: " + resultado.getString("sexo"));
        System.out.println("Dueño: " + resultado.getString("nombre_dueno"));
        System.out.println("Pedigrí: " + (resultado.getInt("pedigri") == 1 ? "Sí" : "No"));
        System.out.println("Tipo de pelaje: " + resultado.getString("tipo_pelaje"));

        String tipo = resultado.getString("tipo_animal");
        if ("Perro".equals(tipo)) {
            System.out.println("Requiere bozal: " + valorBooleano(resultado, "requiere_bozal"));
            System.out.println("Nivel de adiestramiento: " + resultado.getString("nivel_adiestramiento"));
        } else if ("Gato".equals(tipo)) {
            System.out.println("Requiere transportadora: " + valorBooleano(resultado, "requiere_transportadora"));
            System.out.println("Temperamento: " + resultado.getString("temperamento"));
        }
    }

    private static String valorBooleano(ResultSet resultado, String columna) throws SQLException {
        Object valor = resultado.getObject(columna);
        if (valor == null) {
            return "No corresponde";
        }
        return resultado.getInt(columna) == 1 ? "Sí" : "No";
    }

    private static class DatosAnimal {
        private String codigo;
        private String tipo;
        private String raza;
        private String nombre;
        private int edad;
        private double peso;
        private String sexo;
        private String nombreDueno;
        private boolean pedigri;
        private String tipoPelaje;
        private Boolean requiereBozal;
        private String nivelAdiestramiento;
        private Boolean requiereTransportadora;
        private String temperamento;
    }
}