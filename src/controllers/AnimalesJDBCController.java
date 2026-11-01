package controllers;

import services.ConexionBDService;
import services.UtilidadesService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class AnimalesJDBCController {

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

        if (cantidad == 0) {
            try (Statement sentencia = conexion.createStatement()) {
                sentencia.executeUpdate("INSERT INTO ANIMALES "
                        + "(codigo, tipo_animal, raza, nombre, edad, peso, sexo, nombre_dueno, pedigri, tipo_pelaje, requiere_bozal, nivel_adiestramiento) VALUES ("
                        + "'ANM001', 'Perro', 'Labrador', 'Simba', 3, 25.5, 'Macho', 'Carlos Díaz', 1, 'Corto', 0, 'Intermedio')");

                sentencia.executeUpdate("INSERT INTO ANIMALES "
                        + "(codigo, tipo_animal, raza, nombre, edad, peso, sexo, nombre_dueno, pedigri, tipo_pelaje, requiere_transportadora, temperamento) VALUES ("
                        + "'ANM002', 'Gato', 'Persa', 'Luna', 2, 4.2, 'Hembra', 'Ana Torres', 0, 'Largo', 1, 'Tranquilo')");

                sentencia.executeUpdate("INSERT INTO ANIMALES "
                        + "(codigo, tipo_animal, raza, nombre, edad, peso, sexo, nombre_dueno, pedigri, tipo_pelaje, requiere_bozal, nivel_adiestramiento) VALUES ("
                        + "'ANM003', 'Perro', 'Mestizo', 'Max', 5, 18.0, 'Macho', 'Jorge Silva', 0, 'Medio', 1, 'Básico')");
            }
            System.out.println("Se cargaron tres animales de ejemplo.");
        }
    }

    private static void registrarAnimal(Connection conexion, Scanner scanner) {
        DatosAnimal datos = leerDatosAnimal(scanner, null);
        if (datos == null) {
            return;
        }

        String sql = "INSERT INTO ANIMALES (codigo, tipo_animal, raza, nombre, edad, peso, sexo, nombre_dueno, pedigri, "
                + "tipo_pelaje, requiere_bozal, nivel_adiestramiento, requiere_transportadora, temperamento) VALUES ("
                + UtilidadesService.textoSql(datos.codigo) + ", "
                + UtilidadesService.textoSql(datos.tipo) + ", "
                + UtilidadesService.textoSql(datos.raza) + ", "
                + UtilidadesService.textoSql(datos.nombre) + ", "
                + datos.edad + ", " + datos.peso + ", "
                + UtilidadesService.textoSql(datos.sexo) + ", "
                + UtilidadesService.textoSql(datos.nombreDueno) + ", "
                + UtilidadesService.numeroBooleano(datos.pedigri) + ", "
                + UtilidadesService.textoSql(datos.tipoPelaje) + ", "
                + datos.requiereBozal + ", "
                + datos.nivelAdiestramiento + ", "
                + datos.requiereTransportadora + ", "
                + datos.temperamento + ")";

        try (Statement sentencia = conexion.createStatement()) {
            int filas = sentencia.executeUpdate(sql);
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

        datos.requiereBozal = "NULL";
        datos.nivelAdiestramiento = "NULL";
        datos.requiereTransportadora = "NULL";
        datos.temperamento = "NULL";

        if (tipoOpcion == 1) {
            datos.requiereBozal = UtilidadesService.numeroBooleano(
                    UtilidadesService.leerBooleano(scanner, "¿Requiere bozal en consulta? (s/n): "));
            datos.nivelAdiestramiento = UtilidadesService.textoSql(
                    UtilidadesService.leerTexto(scanner, "Nivel de adiestramiento: "));
        } else {
            datos.requiereTransportadora = UtilidadesService.numeroBooleano(
                    UtilidadesService.leerBooleano(scanner, "¿Requiere transportadora? (s/n): "));
            datos.temperamento = UtilidadesService.textoSql(
                    UtilidadesService.leerTexto(scanner, "Temperamento: "));
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
        String sql = "SELECT * FROM ANIMALES WHERE codigo = " + UtilidadesService.textoSql(codigo);

        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {
            if (resultado.next()) {
                mostrarAnimal(resultado);
            } else {
                System.out.println("No se encontró un animal con ese código.");
            }
        }
    }

    private static void actualizarAnimal(Connection conexion, Scanner scanner) throws SQLException {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código del animal que deseas actualizar: ");
        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(
                     "SELECT codigo FROM ANIMALES WHERE codigo = " + UtilidadesService.textoSql(codigo))) {
            if (!resultado.next()) {
                System.out.println("No se encontró un animal con ese código.");
                return;
            }
        }

        DatosAnimal datos = leerDatosAnimal(scanner, codigo);
        if (datos == null) {
            return;
        }

        String sql = "UPDATE ANIMALES SET tipo_animal = " + UtilidadesService.textoSql(datos.tipo)
                + ", raza = " + UtilidadesService.textoSql(datos.raza)
                + ", nombre = " + UtilidadesService.textoSql(datos.nombre)
                + ", edad = " + datos.edad
                + ", peso = " + datos.peso
                + ", sexo = " + UtilidadesService.textoSql(datos.sexo)
                + ", nombre_dueno = " + UtilidadesService.textoSql(datos.nombreDueno)
                + ", pedigri = " + UtilidadesService.numeroBooleano(datos.pedigri)
                + ", tipo_pelaje = " + UtilidadesService.textoSql(datos.tipoPelaje)
                + ", requiere_bozal = " + datos.requiereBozal
                + ", nivel_adiestramiento = " + datos.nivelAdiestramiento
                + ", requiere_transportadora = " + datos.requiereTransportadora
                + ", temperamento = " + datos.temperamento
                + " WHERE codigo = " + UtilidadesService.textoSql(codigo);

        try (Statement sentencia = conexion.createStatement()) {
            int filas = sentencia.executeUpdate(sql);
            System.out.println(filas > 0 ? "Animal actualizado correctamente." : "No se realizó ningún cambio.");
        }
    }

    private static void eliminarAnimal(Connection conexion, Scanner scanner) throws SQLException {
        String codigo = UtilidadesService.leerTextoObligatorio(scanner, "Código del animal que deseas eliminar: ");
        String sql = "DELETE FROM ANIMALES WHERE codigo = " + UtilidadesService.textoSql(codigo);

        try (Statement sentencia = conexion.createStatement()) {
            int filas = sentencia.executeUpdate(sql);
            System.out.println(filas > 0 ? "Animal eliminado correctamente." : "No se encontró un animal con ese código.");
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
        private String requiereBozal;
        private String nivelAdiestramiento;
        private String requiereTransportadora;
        private String temperamento;
    }
}
