/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bigotes_colas.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author gbcya
 */
public class CitaConsulta {

    private int idCitaConsulta;
    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
    private String diagnostico;   // null = todavia es solo una cita agendada
    private int idMascota;        // FK -> mascota.id
    private int idVeterinario;    // FK -> usuario.id (rol = Veterinario)

    public CitaConsulta() {
    }

    public CitaConsulta(int idCitaConsulta, LocalDate fecha, LocalTime hora, String motivo,
                         String diagnostico, int idMascota, int idVeterinario) {
        this.idCitaConsulta = idCitaConsulta;
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.diagnostico = diagnostico;
        this.idMascota = idMascota;
        this.idVeterinario = idVeterinario;
    }

    /** Constructor para agendar una cita nueva (sin diagnostico todavia). */
    public CitaConsulta(LocalDate fecha, LocalTime hora, String motivo, int idMascota, int idVeterinario) {
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.idMascota = idMascota;
        this.idVeterinario = idVeterinario;
        this.diagnostico = null;
    }

    public int getIdCitaConsulta() {
        return idCitaConsulta;
    }

    public void setIdCitaConsulta(int idCitaConsulta) {
        this.idCitaConsulta = idCitaConsulta;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public int getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

    public int getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(int idVeterinario) {
        this.idVeterinario = idVeterinario;
    }

    /** true si ya se atendio (tiene diagnostico) -> aparece en Historial. */
    public boolean esConsultaRealizada() {
        return diagnostico != null && !diagnostico.isBlank();
    }

    /** true si todavia esta pendiente -> aparece en Citas. */
    public boolean esCitaPendiente() {
        return !esConsultaRealizada();
    }
}