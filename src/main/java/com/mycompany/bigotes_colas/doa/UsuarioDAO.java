/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.bigotes_colas.doa;

import com.mycompany.bigotes_colas.model.Usuario;

import java.sql.SQLException;
import java.util.List;

public interface UsuarioDAO {

    Usuario validarLogin(String usuario, String claveHash) throws SQLException;

    List<Usuario> listarTodos() throws SQLException;

    List<Usuario> listarPorRol(String rol) throws SQLException;

    void crear(Usuario usuario) throws SQLException;

    void actualizar(Usuario usuario) throws SQLException;

    void actualizarClave(int idUsuario, String nuevaClaveHash) throws SQLException;

    void eliminar(int idUsuario) throws SQLException;
}