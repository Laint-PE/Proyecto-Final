package services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class ConexionBDService {

    private static final String URL_POR_DEFECTO =
            "jdbc:mysql://localhost:3306/veterinaria?createDatabaseIfNotExist=true";
    private static final String USUARIO_POR_DEFECTO = "root";
    private static final String PASSWORD_POR_DEFECTO = "";

    private static final Properties CONFIG = cargarConfiguracion();

    private static Properties cargarConfiguracion() {
        Properties propiedades = new Properties();
        Path archivo = Path.of("db.properties");

        if (Files.exists(archivo)) {
            try (InputStream entrada = Files.newInputStream(archivo)) {
                propiedades.load(entrada);
            } catch (IOException e) {
                System.out.println("No se pudo leer db.properties. Se usará la configuración por defecto.");
            }
        }

        return propiedades;
    }

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el controlador MySQL. Revisa la dependencia mysql-connector-j en Maven.");
        }

        String url = CONFIG.getProperty("db.url", URL_POR_DEFECTO);
        String usuario = CONFIG.getProperty("db.user", USUARIO_POR_DEFECTO);
        String password = CONFIG.getProperty("db.password", PASSWORD_POR_DEFECTO);

        return DriverManager.getConnection(url, usuario, password);
    }
}