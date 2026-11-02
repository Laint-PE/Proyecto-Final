package models;

import services.ConexionBDService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AnimalDAO {

    public void registrarAnimal(AnimalModel animal) throws SQLException {
        String sql = "INSERT INTO ANIMALES "
                + "(codigo, raza, nombre, edad, peso, sexo, nombre_dueno, pedigri, tipo_pelaje) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBDService.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, animal.getCodigo());
            ps.setString(2, animal.getRaza());
            ps.setString(3, animal.getNombre());
            ps.setInt(4, animal.getEdad());
            ps.setDouble(5, animal.getPeso());
            ps.setString(6, animal.getSexo());
            ps.setString(7, animal.getNombreDueno());
            ps.setInt(8, animal.isPedigri() ? 1 : 0);
            ps.setString(9, animal.getTipoPelaje());

            ps.executeUpdate();
        }
    }
}

