/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bigotes_colas.doa;

import com.mycompany.bigotes_colas.model.CitaConsulta;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 *
 * @author gbcya
 */

public interface CitaConsultaDAO {

    List<CitaConsulta> listarPendientes() throws SQLException;

    List<CitaConsulta> listarHistorialPorMascota(int idMascota) throws SQLException;

    boolean existeCruceDeHorario(int idVeterinario, LocalDate fecha, LocalTime hora) throws SQLException;

    void agendarCita(CitaConsulta cita) throws SQLException;

    void registrarConsulta(int idCitaConsulta, String diagnostico, String tratamientoVacuna) throws SQLException;

    void eliminar(int idCitaConsulta) throws SQLException;
}