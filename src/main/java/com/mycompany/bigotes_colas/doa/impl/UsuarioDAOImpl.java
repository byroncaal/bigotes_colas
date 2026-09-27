package com.mycompany.bigotes_colas.doa.impl;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.mycompany.bigotes_colas.doa.UsuarioDAO;
import com.mycompany.bigotes_colas.model.Usuario;
import com.mycompany.bigotes_colas.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gbcya
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Usuario validarLogin(String usuario, String claveHash) throws SQLException {
        // Los usuarios suspendidos (activo = 0) no pueden iniciar sesión
        String sql = "SELECT * FROM usuario WHERE usuario = ? AND clave_hash = ? AND activo = 1";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, claveHash);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public Usuario buscarPorUsuario(String usuario) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE usuario = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY nombre";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Usuario> listarPorRol(String rol) throws SQLException {
        // Solo activos: un veterinario suspendido no debe aparecer para agendar citas
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE rol = ? AND activo = 1 ORDER BY nombre";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rol);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public void crear(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario (nombre, usuario, clave_hash, rol, activo) VALUES (?, ?, ?, ?, 1)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getUsuario());
            ps.setString(3, u.getClaveHash());
            ps.setString(4, u.getRol());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    u.setIdUsuario(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuario SET nombre = ?, usuario = ?, rol = ? WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getUsuario());
            ps.setString(3, u.getRol());
            ps.setInt(4, u.getIdUsuario());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizarClave(int idUsuario, String nuevaClaveHash) throws SQLException {
        String sql = "UPDATE usuario SET clave_hash = ? WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevaClaveHash);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }

    @Override
    public void cambiarEstado(int idUsuario, boolean activo) throws SQLException {
        String sql = "UPDATE usuario SET activo = ? WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idUsuario) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("usuario"),
                rs.getString("clave_hash"),
                rs.getString("rol")
        );
        u.setActivo(rs.getBoolean("activo"));
        return u;
    }
}