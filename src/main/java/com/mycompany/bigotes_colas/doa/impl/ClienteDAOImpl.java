package com.mycompany.bigotes_colas.doa.impl;

import com.mycompany.bigotes_colas.doa.ClienteDAO;
import com.mycompany.bigotes_colas.model.Cliente;
import com.mycompany.bigotes_colas.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * ClienteDAOImpl
 * Implementacion real de ClienteDAO: aqui vive el SQL con JDBC.
 */
public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente ORDER BY nombre";
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
    public List<Cliente> buscar(String texto) throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE nombre LIKE ? OR telefono LIKE ? ORDER BY nombre";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String comodin = "%" + texto + "%";
            ps.setString(1, comodin);
            ps.setString(2, comodin);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public Cliente buscarPorId(int idCliente) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public void crear(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (nombre, dpi, telefono, direccion) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDpi());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getDireccion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setIdCliente(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET nombre = ?, dpi = ?, telefono = ?, direccion = ? WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDpi());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getDireccion());
            ps.setInt(5, c.getIdCliente());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idCliente) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.executeUpdate();
        }
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("dpi"),
                rs.getString("telefono"),
                rs.getString("direccion")
        );
    }
}