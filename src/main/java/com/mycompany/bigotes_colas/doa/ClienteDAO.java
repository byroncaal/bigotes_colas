/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bigotes_colas.doa;
 
import com.mycompany.bigotes_colas.model.Cliente;
 
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author gbcya
 */

public interface ClienteDAO {
 
    List<Cliente> listarTodos() throws SQLException;
 
    List<Cliente> buscar(String texto) throws SQLException;
 
    Cliente buscarPorId(int idCliente) throws SQLException;
 
    void crear(Cliente cliente) throws SQLException;
 
    void actualizar(Cliente cliente) throws SQLException;
 
    void eliminar(int idCliente) throws SQLException;
}
 