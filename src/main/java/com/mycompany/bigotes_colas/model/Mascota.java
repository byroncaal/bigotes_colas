/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.bigotes_colas.model;

import java.time.LocalDate;
import java.time.Period;

/**
 *
 * @author gbcya
 */
public class Mascota {

    private int idMascota;
    private String nombre;
    private String especie;   // "Perro", "Gato", "Ave" u "Otro"
    private String raza;
    private LocalDate fechaNac;
    private int idCliente;    // FK -> cliente.id

    public Mascota() {
    }

    public Mascota(int idMascota, String nombre, String especie, String raza,
                   LocalDate fechaNac, int idCliente) {
        this.idMascota = idMascota;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.fechaNac = fechaNac;
        this.idCliente = idCliente;
    }

    /** Constructor sin id, para antes de insertar. */
    public Mascota(String nombre, String especie, String raza, LocalDate fechaNac, int idCliente) {
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.fechaNac = fechaNac;
        this.idCliente = idCliente;
    }

    public int getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public LocalDate getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(LocalDate fechaNac) {
        this.fechaNac = fechaNac;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    /** Calcula la edad en anios a partir de fechaNac (reemplaza al viejo campo "edadAnios"). */
    public int getEdadAnios() {
        if (fechaNac == null) {
            return 0;
        }
        return Period.between(fechaNac, LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return nombre;
    }
}