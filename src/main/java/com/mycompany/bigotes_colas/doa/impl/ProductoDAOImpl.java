package com.mycompany.bigotes_colas.doa.impl;

import com.mycompany.bigotes_colas.doa.ProductoDAO;
import com.mycompany.bigotes_colas.model.Producto;
import com.mycompany.bigotes_colas.util.ConexionBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl implements ProductoDAO {

    @Override
    public List<Producto> listarTodos() throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto ORDER BY nombre";
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
    public List<Producto> buscar(String texto) throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE nombre LIKE ? OR codigo LIKE ? ORDER BY nombre";
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
    public List<Producto> listarBajoStockMinimo() throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE stock < stock_min ORDER BY stock ASC";
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
    public List<Producto> listarProximosAVencer(int dias) throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE vence IS NOT NULL "
                + "AND vence <= DATE_ADD(CURDATE(), INTERVAL ? DAY) ORDER BY vence ASC";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dias);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public void crear(Producto p) throws SQLException {
        String sql = "INSERT INTO producto (codigo, nombre, categoria, precio, stock, stock_min, vence) "
                + "VALUES (?, ?, ?, ?, 0, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getCategoria());
            ps.setDouble(4, p.getPrecio());
            ps.setInt(5, p.getStockMin());
            ps.setDate(6, p.getVence() != null ? Date.valueOf(p.getVence()) : null);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setIdProducto(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Producto p) throws SQLException {
        String sql = "UPDATE producto SET codigo=?, nombre=?, categoria=?, precio=?, stock_min=?, vence=? WHERE id=?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getCategoria());
            ps.setDouble(4, p.getPrecio());
            ps.setInt(5, p.getStockMin());
            ps.setDate(6, p.getVence() != null ? Date.valueOf(p.getVence()) : null);
            ps.setInt(7, p.getIdProducto());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idProducto) throws SQLException {
        String sql = "DELETE FROM producto WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.executeUpdate();
        }
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        Date vence = rs.getDate("vence");
        return new Producto(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("categoria"),
                rs.getDouble("precio"),
                rs.getInt("stock"),
                rs.getInt("stock_min"),
                vence != null ? vence.toLocalDate() : null
        );
    }
}