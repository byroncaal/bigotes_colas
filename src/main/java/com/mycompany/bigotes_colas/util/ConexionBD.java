/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.bigotes_colas.util;
 
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
 
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
/**
 *
 * @author gbcya
 */


public class ConexionBD {
 
    private static final HikariDataSource dataSource;
 
    static {
        Properties props = new Properties();
        try (InputStream in = ConexionBD.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new RuntimeException(
                        "No se encontro config.properties en src/main/resources. "
                                + "Copia config.properties.example, renombralo a config.properties "
                                + "y llena tus datos reales del servidor.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo config.properties", e);
        }
 
        String host = props.getProperty("db.host");
        String puerto = props.getProperty("db.port");
        String baseDatos = props.getProperty("db.database");
        String usuario = props.getProperty("db.user");
        String contrasena = props.getProperty("db.password");
 
        String url = "jdbc:mysql://" + host + ":" + puerto + "/" + baseDatos
                + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
 
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(usuario);
        config.setPassword(contrasena);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setPoolName("BigotesColasPool");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource = new HikariDataSource(config);
    }
 
    private ConexionBD() {
        // Clase de utilidad: no se instancia
    }
 
    public static Connection obtenerConexion() throws SQLException {
        return dataSource.getConnection();
    }
}