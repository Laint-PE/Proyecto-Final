
package models;

import services.ConexionBDService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

public class AnimalDAO {

    public void crearTabla() throws SQLException {
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

        try (Connection conexion = ConexionBDService.conectar();
             Statement sentencia = conexion.createStatement()) {
            sentencia.executeUpdate(sql);
        }
    }

    public void registrarAnimal(AnimalModel animal) throws SQLException {
        String sql = "INSERT INTO ANIMALES "
                + "(codigo, tipo_animal, raza, nombre, edad, peso, sexo, "
                + "nombre_dueno, pedigri, tipo_pelaje, requiere_bozal, "
                + "nivel_adiestramiento, requiere_transportadora, temperamento) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, animal.getCodigo());
            asignarCampos(ps, animal, 2);
            ps.executeUpdate();
        }
    }

    public void actualizarAnimal(AnimalModel animal) throws SQLException {
        String sql = "UPDATE ANIMALES SET tipo_animal = ?, raza = ?, nombre = ?, "
                + "edad = ?, peso = ?, sexo = ?, nombre_dueno = ?, pedigri = ?, "
                + "tipo_pelaje = ?, requiere_bozal = ?, nivel_adiestramiento = ?, "
                + "requiere_transportadora = ?, temperamento = ? WHERE codigo = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            asignarCampos(ps, animal, 1);
            ps.setString(14, animal.getCodigo());
            ps.executeUpdate();
        }
    }

    private void asignarCampos(PreparedStatement ps, AnimalModel animal, int inicio)
            throws SQLException {

        int i = inicio;

        ps.setString(i++, animal.getTipoAnimal());
        ps.setString(i++, animal.getRaza());
        ps.setString(i++, animal.getNombre());
        ps.setInt(i++, animal.getEdad());
        ps.setDouble(i++, animal.getPeso());
        ps.setString(i++, animal.getSexo());
        ps.setString(i++, animal.getNombreDueno());
        ps.setInt(i++, animal.isPedigri() ? 1 : 0);
        ps.setString(i++, animal.getTipoPelaje());

        if (animal instanceof PerroModel perro) {
            ps.setInt(i++, perro.isRequiereBozalEnConsulta() ? 1 : 0);
            ps.setString(i++, perro.getNivelAdiestramiento());
            ps.setNull(i++, Types.TINYINT);
            ps.setNull(i, Types.VARCHAR);

        } else if (animal instanceof GatoModel gato) {
            ps.setNull(i++, Types.TINYINT);
            ps.setNull(i++, Types.VARCHAR);
            ps.setInt(i++, gato.isRequiereTransportadora() ? 1 : 0);
            ps.setString(i, gato.getTemperamento());
        }
    }

    public boolean existeAnimal(String codigo) throws SQLException {
        String sql = "SELECT 1 FROM ANIMALES WHERE codigo = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet resultado = ps.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public void eliminarAnimal(String codigo) throws SQLException {
        String sql = "DELETE FROM ANIMALES WHERE codigo = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ps.executeUpdate();
        }
    }

    public ArrayList<AnimalModel> listarAnimales() throws SQLException {
        ArrayList<AnimalModel> animales = new ArrayList<>();
        String sql = "SELECT * FROM ANIMALES ORDER BY codigo";

        try (Connection conexion = ConexionBDService.conectar();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {

            while (resultado.next()) {
                animales.add(convertirAnimal(resultado));
            }
        }

        return animales;
    }

    public AnimalModel buscarAnimal(String codigo) throws SQLException {
        String sql = "SELECT * FROM ANIMALES WHERE codigo = ?";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet resultado = ps.executeQuery()) {
                if (resultado.next()) {
                    return convertirAnimal(resultado);
                }
            }
        }

        return null;
    }

    private AnimalModel convertirAnimal(ResultSet resultado) throws SQLException {
        String codigo = resultado.getString("codigo");
        String tipo = resultado.getString("tipo_animal");
        String raza = resultado.getString("raza");
        String nombre = resultado.getString("nombre");
        int edad = resultado.getInt("edad");
        double peso = resultado.getDouble("peso");
        String sexo = resultado.getString("sexo");
        String dueno = resultado.getString("nombre_dueno");
        boolean pedigri = resultado.getInt("pedigri") == 1;
        String pelaje = resultado.getString("tipo_pelaje");

        if ("Perro".equals(tipo)) {
            return new PerroModel(
                    codigo, raza, nombre, edad, peso, sexo, dueno,
                    pedigri, pelaje,
                    resultado.getBoolean("requiere_bozal"),
                    resultado.getString("nivel_adiestramiento")
            );
        } else {
            return new GatoModel(
                    codigo, raza, nombre, edad, peso, sexo, dueno,
                    pedigri, pelaje,
                    resultado.getBoolean("requiere_transportadora"),
                    resultado.getString("temperamento")
            );
        }
    }

    public void insertarEjemplosSiVacia() throws SQLException {
        String sql = "SELECT COUNT(*) FROM ANIMALES";
        int cantidad = 0;

        try (Connection conexion = ConexionBDService.conectar();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {

            if (resultado.next()) {
                cantidad = resultado.getInt(1);
            }
        }

        if (cantidad > 0) {
            return;
        }

        registrarAnimal(new PerroModel(
                "ANM001", "Labrador", "Simba", 3, 25.5,
                "Macho", "Carlos Díaz", true, "Corto",
                false, "Intermedio"
        ));

        registrarAnimal(new GatoModel(
                "ANM002", "Persa", "Luna", 2, 4.2,
                "Hembra", "Ana Torres", false, "Largo",
                true, "Tranquilo"
        ));

        registrarAnimal(new PerroModel(
                "ANM003", "Mestizo", "Max", 5, 18.0,
                "Macho", "Jorge Silva", false, "Medio",
                true, "Básico"
        ));
    }
}
