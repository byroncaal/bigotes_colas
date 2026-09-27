/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bigotes_colas.controlador;
 
import com.mycompany.bigotes_colas.doa.MascotaDAO;
import com.mycompany.bigotes_colas.doa.impl.MascotaDAOImpl;
 
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author gbcya
 */

public class Mascota {
 
    private final MascotaDAO mascotaDAO = new MascotaDAOImpl();
 
    public List<com.mycompany.bigotes_colas.model.Mascota> listar() throws SQLException {
        return mascotaDAO.listarTodas();
    }
 
    public List<com.mycompany.bigotes_colas.model.Mascota> filtrar(String especie, String textoBusqueda) throws SQLException {
        return mascotaDAO.filtrar(especie, textoBusqueda == null ? "" : textoBusqueda);
    }
 
    public List<com.mycompany.bigotes_colas.model.Mascota> listarPorCliente(int idCliente) throws SQLException {
        return mascotaDAO.listarPorCliente(idCliente);
    }
 
    public void agregar(String nombre, String especie, String raza, LocalDate fechaNac, int idCliente) throws SQLException {
        validarCamposObligatorios(nombre, especie, idCliente);
        mascotaDAO.crear(new com.mycompany.bigotes_colas.model.Mascota(nombre, especie, raza, fechaNac, idCliente));
    }
 
    public void editar(com.mycompany.bigotes_colas.model.Mascota mascota) throws SQLException {
        validarCamposObligatorios(mascota.getNombre(), mascota.getEspecie(), mascota.getIdCliente());
        mascotaDAO.actualizar(mascota);
    }
 
    public void eliminar(int idMascota) throws SQLException {
        mascotaDAO.eliminar(idMascota);
    }
 
    private void validarCamposObligatorios(String nombre, String especie, int idCliente) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (especie == null || especie.isBlank()) {
            throw new IllegalArgumentException("La especie es obligatoria.");
        }
        if (idCliente <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un dueno.");
        }
    }
}
