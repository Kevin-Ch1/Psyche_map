package com.psychemap.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private String url;
    private String user;
    private String password;
    private Connection connection;

    private DatabaseConnection() {
        cargarPropiedadesYConectar();
    }

    private void cargarPropiedadesYConectar() {
        Properties properties = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IllegalStateException("No se encontró el archivo database.properties en src/main/resources.");
            }
            properties.load(input);

            this.url = properties.getProperty("db.url");
            this.user = properties.getProperty("db.user");
            this.password = properties.getProperty("db.password");

            if (this.url == null || this.user == null || this.password == null) {
                throw new IllegalStateException("database.properties debe contener las claves db.url, db.user y db.password");
            }

            this.connection = DriverManager.getConnection(this.url, this.user, this.password);

            try (Statement st = connection.createStatement()) {
                st.execute("ALTER TABLE CriterioDiagnostico ADD COLUMN IF NOT EXISTS fuente_manual VARCHAR(50) DEFAULT 'DSM-5-TR'");
            } catch (SQLException ex) {
            }

        } catch (IOException e) {
            throw new IllegalStateException("Error al leer database.properties: " + e.getMessage(), e);
        } catch (SQLException e) {
            System.err.println("Error de conexión a PostgreSQL con las credenciales de database.properties: " + e.getMessage());
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        try {
            if (instance == null || instance.getConnection() == null || instance.getConnection().isClosed()) {
                instance = new DatabaseConnection();
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar el estado de la conexión: " + e.getMessage());
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public Connection getNewConnection() throws SQLException {
        if (this.url == null || this.user == null || this.password == null) {
            cargarPropiedadesYConectar();
        }
        return DriverManager.getConnection(this.url, this.user, this.password);
    }
}
