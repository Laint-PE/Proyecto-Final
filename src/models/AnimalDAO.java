
package models;

import services.ConexionBDService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

            asignarCampos(ps, animal);
            ps.executeUpdate();
        }
    }

    private void asignarCampos(PreparedStatement ps, AnimalModel animal)
            throws SQLException {

        ps.setString(1, animal.getCodigo());
        ps.setString(2, animal.getTipoAnimal());
        ps.setString(3, animal.getRaza());
        ps.setString(4, animal.getNombre());
        ps.setInt(5, animal.getEdad());
        ps.setDouble(6, animal.getPeso());
        ps.setString(7, animal.getSexo());
        ps.setString(8, animal.getNombreDueno());
        ps.setInt(9, animal.isPedigri() ? 1 : 0);
        ps.setString(10, animal.getTipoPelaje());

        if (animal instanceof PerroModel perro) {
            ps.setInt(11, perro.isRequiereBozalEnConsulta() ? 1 : 0);
            ps.setString(12, perro.getNivelAdiestramiento());
            ps.setNull(13, java.sql.Types.TINYINT);
            ps.setNull(14, java.sql.Types.VARCHAR);

        } else if (animal instanceof GatoModel gato) {
            ps.setNull(11, java.sql.Types.TINYINT);
            ps.setNull(12, java.sql.Types.VARCHAR);
            ps.setInt(13, gato.isRequiereTransportadora() ? 1 : 0);
            ps.setString(14, gato.getTemperamento());
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
}