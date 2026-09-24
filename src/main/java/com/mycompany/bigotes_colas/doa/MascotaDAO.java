/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bigotes_colas.doa;

import com.mycompany.bigotes_colas.util.ConexionBD;
import com.mycompany.bigotes_colas.model.Mascota;

import java.sql.Connection;
import java.sql.Date;
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
public class MascotaDAO {

    public List<Mascota> listarTodas() throws SQLException {
        List<Mascota> lista = new ArrayList<>();
        String sql = "SELECT * FROM mascota ORDER BY nombre";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /** Filtro por especie (comboEspecie) + busqueda por nombre, para MascotasFrame. */
    public List<Mascota> filtrar(String especie, String textoBusqueda) throws SQLException {
        List<Mascota> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM mascota WHERE nombre LIKE ?");
        if (especie != null && !"Todas".equals(especie)) {
            sql.append(" AND especie = ?");
        }
        sql.append(" ORDER BY nombre");

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setString(1, "%" + textoBusqueda + "%");
            if (especie != null && !"Todas".equals(especie)) {
                ps.setString(2, especie);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public List<Mascota> listarPorCliente(int idCliente) throws SQLException {
        List<Mascota> lista = new ArrayList<>();
        String sql = "SELECT * FROM mascota WHERE cliente_id = ? ORDER BY nombre";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public void crear(Mascota m) throws SQLException {
        String sql = "INSERT INTO mascota (nombre, especie, raza, fecha_nac, cliente_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getNombre());
            ps.setString(2, m.getEspecie());
            ps.setString(3, m.getRaza());
            ps.setDate(4, m.getFechaNac() != null ? Date.valueOf(m.getFechaNac()) : null);
            ps.setInt(5, m.getIdCliente());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    m.setIdMascota(rs.getInt(1));
                }
            }
        }
    }

    public void actualizar(Mascota m) throws SQLException {
        String sql = "UPDATE mascota SET nombre=?, especie=?, raza=?, fecha_nac=?, cliente_id=? WHERE id=?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getNombre());
            ps.setString(2, m.getEspecie());
            ps.setString(3, m.getRaza());
            ps.setDate(4, m.getFechaNac() != null ? Date.valueOf(m.getFechaNac()) : null);
            ps.setInt(5, m.getIdCliente());
            ps.setInt(6, m.getIdMascota());
            ps.executeUpdate();
        }
    }

    /** ON DELETE CASCADE en 'cita_consulta' se encarga de borrar sus citas/historial asociados. */
    public void eliminar(int idMascota) throws SQLException {
        String sql = "DELETE FROM mascota WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            ps.executeUpdate();
        }
    }

    private Mascota mapear(ResultSet rs) throws SQLException {
        Date fechaNac = rs.getDate("fecha_nac");
        return new Mascota(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("especie"),
                rs.getString("raza"),
                fechaNac != null ? fechaNac.toLocalDate() : null,
                rs.getInt("cliente_id")
        );
    }
}
