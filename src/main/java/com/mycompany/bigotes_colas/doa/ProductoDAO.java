package com.mycompany.bigotes_colas.doa;

import com.mycompany.bigotes_colas.model.Producto;

import java.sql.SQLException;
import java.util.List;

public interface ProductoDAO {

    List<Producto> listarTodos() throws SQLException;

    List<Producto> buscar(String texto) throws SQLException;

    List<Producto> listarBajoStockMinimo() throws SQLException;

    List<Producto> listarProximosAVencer(int dias) throws SQLException;

    void crear(Producto producto) throws SQLException;

    void actualizar(Producto producto) throws SQLException;

    void eliminar(int idProducto) throws SQLException;
}