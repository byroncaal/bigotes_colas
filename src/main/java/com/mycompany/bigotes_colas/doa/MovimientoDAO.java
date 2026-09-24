/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.bigotes_colas.doa;

import com.mycompany.bigotes_colas.util.ConexionBD;
import com.mycompany.bigotes_colas.model.Movimiento;

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
public class MovimientoDAO {

    public List<Movimiento> listarPorProducto(int idProducto) throws SQLException {
        List<Movimiento> lista = new ArrayList<>();
        String sql = "SELECT * FROM movimiento WHERE producto_id = ? ORDER BY fecha DESC";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Registra un movimiento (Entrada o Salida) y actualiza el stock del producto,
     * todo dentro de una misma transaccion.
     */
    public void registrarMovimiento(Movimiento mov) throws SQLException {
        String sqlInsertar = "INSERT INTO movimiento (tipo, cantidad, fecha, producto_id) VALUES (?, ?, ?, ?)";
        String sqlActualizarStock = mov.esEntrada()
                ? "UPDATE producto SET stock = stock + ? WHERE id = ?"
                : "UPDATE producto SET stock = stock - ? WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement psInsertar = con.prepareStatement(sqlInsertar, Statement.RETURN_GENERATED_KEYS)) {
                    psInsertar.setString(1, mov.getTipo());
                    psInsertar.setInt(2, mov.getCantidad());
                    psInsertar.setDate(3, Date.valueOf(mov.getFecha()));
                    psInsertar.setInt(4, mov.getIdProducto());
                    psInsertar.executeUpdate();
                    try (ResultSet rs = psInsertar.getGeneratedKeys()) {
                        if (rs.next()) {
                            mov.setIdMovimiento(rs.getInt(1));
                        }
                    }
                }

                try (PreparedStatement psStock = con.prepareStatement(sqlActualizarStock)) {
                    psStock.setInt(1, mov.getCantidad());
                    psStock.setInt(2, mov.getIdProducto());
                    psStock.executeUpdate();
                }

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    private Movimiento mapear(ResultSet rs) throws SQLException {
        return new Movimiento(
                rs.getInt("id"),
                rs.getString("tipo"),
                rs.getInt("cantidad"),
                rs.getDate("fecha").toLocalDate(),
                rs.getInt("producto_id")
        );
    }
}