package com.mycompany.bigotes_colas.controlador;

import com.mycompany.bigotes_colas.doa.ClienteDAO;

import java.sql.SQLException;
import java.util.List;

/**
 * Cliente (controlador)
 * Maneja los eventos de ClientesFrame (Buscar, Agregar, Editar, Eliminar).
 * Aplica las reglas de negocio (validaciones) antes de llamar al DAO.
 *
 * NOTA: como esta clase se llama igual que el modelo (com.mycompany.bigotes_colas.model.Cliente),
 * aqui el modelo se referencia con su nombre COMPLETO en vez de solo "Cliente".
 */
public class Cliente {

    private final ClienteDAO clienteDAO = new com.mycompany.bigotes_colas.doa.impl.ClienteDAOImpl();

    public List<com.mycompany.bigotes_colas.model.Cliente> listar() throws SQLException {
        return clienteDAO.listarTodos();
    }

    public List<com.mycompany.bigotes_colas.model.Cliente> buscar(String texto) throws SQLException {
        if (texto == null || texto.isBlank()) {
            return listar();
        }
        return clienteDAO.buscar(texto);
    }

    public void agregar(String nombre, String dpi, String telefono, String direccion) throws SQLException {
        validarCamposObligatorios(nombre, dpi);
        clienteDAO.crear(new com.mycompany.bigotes_colas.model.Cliente(nombre, dpi, telefono, direccion));
    }

    public void editar(com.mycompany.bigotes_colas.model.Cliente cliente) throws SQLException {
        validarCamposObligatorios(cliente.getNombre(), cliente.getDpi());
        clienteDAO.actualizar(cliente);
    }

    public void eliminar(int idCliente) throws SQLException {
        clienteDAO.eliminar(idCliente);
    }

    private void validarCamposObligatorios(String nombre, String dpi) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (dpi == null || dpi.isBlank()) {
            throw new IllegalArgumentException("El DPI es obligatorio.");
        }
    }
}