/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bigotes_colas.model;


import java.time.LocalDate;

import java.time.LocalDate;

import java.time.LocalDate;

import java.time.LocalDate;

/**
 *
 * @author gbcya
 */
public class Producto {

    private int idProducto;
    private String codigo;      // ej. "M-001"
    private String nombre;
    private String categoria;   // "Medicamento", "Vacuna", "Alimento" u "Otro"
    private double precio;
    private int stock;
    private int stockMin;
    private LocalDate vence;    // null = sin vencimiento

    public Producto() {
    }

    public Producto(int idProducto, String codigo, String nombre, String categoria,
                     double precio, int stock, int stockMin, LocalDate vence) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.stockMin = stockMin;
        this.vence = vence;
    }

    /** Constructor sin id, para antes de insertar (el stock inicial normalmente es 0,
     *  y se llena despues registrando un Movimiento tipo Entrada). */
    public Producto(String codigo, String nombre, String categoria, double precio, int stockMin, LocalDate vence) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = 0;
        this.stockMin = stockMin;
        this.vence = vence;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStockMin() {
        return stockMin;
    }

    public void setStockMin(int stockMin) {
        this.stockMin = stockMin;
    }

    public LocalDate getVence() {
        return vence;
    }

    public void setVence(LocalDate vence) {
        this.vence = vence;
    }

    /** RF-07: true si el stock actual esta por debajo del minimo -> dispara la alerta visual. */
    public boolean estaBajoStockMinimo() {
        return stock < stockMin;
    }

    @Override
    public String toString() {
        return nombre;
    }
}