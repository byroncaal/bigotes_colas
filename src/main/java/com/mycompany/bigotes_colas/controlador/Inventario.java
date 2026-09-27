/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bigotes_colas.controlador;
 
import com.mycompany.bigotes_colas.doa.MovimientoDAO;
import com.mycompany.bigotes_colas.doa.ProductoDAO;
import com.mycompany.bigotes_colas.doa.impl.MovimientoDAOImpl;
import com.mycompany.bigotes_colas.doa.impl.ProductoDAOImpl;
import com.mycompany.bigotes_colas.model.Movimiento;
import com.mycompany.bigotes_colas.model.Producto;
 
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author gbcya
 */

public class Inventario {
 
    private final ProductoDAO productoDAO = new ProductoDAOImpl();
    private final MovimientoDAO movimientoDAO = new MovimientoDAOImpl();
 
    public List<Producto> listarProductos() throws SQLException {
        return productoDAO.listarTodos();
    }
 
    public List<Producto> buscar(String texto) throws SQLException {
        if (texto == null || texto.isBlank()) {
            return listarProductos();
        }
        return productoDAO.buscar(texto);
    }
 

    public List<Producto> listarBajoStockMinimo() throws SQLException {
        return productoDAO.listarBajoStockMinimo();
    }

    public List<Producto> listarProximosAVencer() throws SQLException {
        return productoDAO.listarProximosAVencer(30);
    }
 
    public void agregarProducto(String codigo, String nombre, String categoria, double precio,
                                 int stockMin, LocalDate vence) throws SQLException {
        if (codigo == null || codigo.isBlank() || nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Codigo y nombre son obligatorios.");
        }
        productoDAO.crear(new Producto(codigo, nombre, categoria, precio, stockMin, vence));
    }
 
    public void editarProducto(Producto p) throws SQLException {
        productoDAO.actualizar(p);
    }
 
    public void eliminarProducto(int idProducto) throws SQLException {
        productoDAO.eliminar(idProducto);
    }

    public void registrarEntrada(int idProducto, int cantidad) throws SQLException {
        validarCantidad(cantidad);
        movimientoDAO.registrarMovimiento(new Movimiento("Entrada", cantidad, LocalDate.now(), idProducto));
    }
 
 
    public void registrarSalida(int idProducto, int cantidad, int stockActual) throws SQLException {
        validarCantidad(cantidad);
        if (cantidad > stockActual) {
            throw new StockInsuficienteException(
                    "No hay suficiente stock para esa salida. Stock actual: " + stockActual);
        }
        movimientoDAO.registrarMovimiento(new Movimiento("Salida", cantidad, LocalDate.now(), idProducto));
    }
 
    private void validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
    }
}
 
