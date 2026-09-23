/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bigotes_colas.model;

import java.time.LocalDate;

/**
 *
 * @author gbcya
 */
public class Movimiento {

    private int idMovimiento;
    private String tipo;        // "Entrada" o "Salida"
    private int cantidad;
    private LocalDate fecha;
    private int idProducto;     // FK -> producto.id

    public Movimiento() {
    }

    public Movimiento(int idMovimiento, String tipo, int cantidad, LocalDate fecha, int idProducto) {
        this.idMovimiento = idMovimiento;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.idProducto = idProducto;
    }

    /** Constructor sin id, para antes de insertar (fecha se puede dejar en LocalDate.now()). */
    public Movimiento(String tipo, int cantidad, LocalDate fecha, int idProducto) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.idProducto = idProducto;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public boolean esEntrada() {
        return "Entrada".equals(tipo);
    }

    public boolean esSalida() {
        return "Salida".equals(tipo);
    }
}