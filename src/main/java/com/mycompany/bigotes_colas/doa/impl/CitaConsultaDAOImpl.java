/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bigotes_colas.doa.impl;

import com.mycompany.bigotes_colas.doa.CitaConsultaDAO;
import com.mycompany.bigotes_colas.model.CitaConsulta;
import com.mycompany.bigotes_colas.util.ConexionBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gbcya
 */
public class CitaConsultaDAOImpl implements CitaConsultaDAO {

    /** Separador que usaba la versión anterior para pegar el tratamiento al motivo. */
    private static final String SEPARADOR_ANTIGUO = " | ";

    @Override
    public List<CitaConsulta> listarPendientes() throws SQLException {
        List<CitaConsulta> lista = new ArrayList<>();
        String sql = "SELECT * FROM cita_consulta WHERE diagnostico IS NULL ORDER BY fecha, hora";
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
    public List<CitaConsulta> listarHistorialPorMascota(int idMascota) throws SQLException {
        List<CitaConsulta> lista = new ArrayList<>();
        String sql = "SELECT * FROM cita_consulta WHERE mascota_id = ? AND diagnostico IS NOT NULL "
                + "ORDER BY fecha DESC, hora DESC";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public boolean existeCruceDeHorario(int idVeterinario, LocalDate fecha, LocalTime hora) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cita_consulta WHERE vet_id = ? AND fecha = ? AND hora = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVeterinario);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setTime(3, Time.valueOf(hora));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    @Override
    public void agendarCita(CitaConsulta c) throws SQLException {
        String sql = "INSERT INTO cita_consulta (fecha, hora, motivo, diagnostico, tratamiento_vacuna, mascota_id, vet_id) "
                + "VALUES (?, ?, ?, NULL, NULL, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(c.getFecha()));
            ps.setTime(2, Time.valueOf(c.getHora()));
            ps.setString(3, c.getMotivo());
            ps.setInt(4, c.getIdMascota());
            ps.setInt(5, c.getIdVeterinario());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setIdCitaConsulta(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void registrarConsulta(int idCitaConsulta, String diagnostico, String tratamientoVacuna) throws SQLException {
        // Antes: motivo = CONCAT(motivo, ' | ', ?) -> mezclaba datos y borraba el motivo si el tratamiento era NULL
        String sql = "UPDATE cita_consulta SET diagnostico = ?, tratamiento_vacuna = ? WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, diagnostico);
            if (tratamientoVacuna == null || tratamientoVacuna.isBlank()) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, tratamientoVacuna.trim());
            }
            ps.setInt(3, idCitaConsulta);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se encontró la cita con id " + idCitaConsulta + ".");
            }
        }
    }

    @Override
    public void eliminar(int idCitaConsulta) throws SQLException {
        String sql = "DELETE FROM cita_consulta WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCitaConsulta);
            ps.executeUpdate();
        }
    }

    private CitaConsulta mapear(ResultSet rs) throws SQLException {
        Date fecha = rs.getDate("fecha");
        Time hora = rs.getTime("hora");
        String motivo = rs.getString("motivo");
        String diagnostico = rs.getString("diagnostico");
        String tratamiento = rs.getString("tratamiento_vacuna");

        // Registros viejos: el tratamiento venía pegado al motivo ("motivo | tratamiento")
        if (tratamiento == null && diagnostico != null && motivo != null) {
            int pos = motivo.lastIndexOf(SEPARADOR_ANTIGUO);
            if (pos >= 0) {
                tratamiento = motivo.substring(pos + SEPARADOR_ANTIGUO.length()).trim();
                motivo = motivo.substring(0, pos).trim();
            }
        }

        return new CitaConsulta(
                rs.getInt("id"),
                fecha != null ? fecha.toLocalDate() : null,
                hora != null ? hora.toLocalTime() : null,
                motivo,
                diagnostico,
                tratamiento,
                rs.getInt("mascota_id"),
                rs.getInt("vet_id")
        );
    }
}