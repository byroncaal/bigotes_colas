/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bigotes_colas.model;

/**
 *
 * @author gbcya
 */
public class Cliente {

    private int idCliente;
    private String nombre;
    private String dpi;
    private String telefono;
    private String direccion;

    public Cliente() {
    }

    public Cliente(int idCliente, String nombre, String dpi, String telefono, String direccion) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.dpi = dpi;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    /** Constructor sin id, para antes de insertar. */
    public Cliente(String nombre, String dpi, String telefono, String direccion) {
        this.nombre = nombre;
        this.dpi = dpi;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDpi() {
        return dpi;
    }

    public void setDpi(String dpi) {
        this.dpi = dpi;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}