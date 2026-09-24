/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bigotes_colas.controlador;


import com.mycompany.bigotes_colas.doa.UsuarioDAO;
import com.mycompany.bigotes_colas.doa.impl.UsuarioDAOImpl;
import com.mycompany.bigotes_colas.model.Usuario;
import com.mycompany.bigotes_colas.util.Sesion;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 *
 * @author gbcya
 */
public class Login {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    /** Devuelve el Usuario si las credenciales son correctas, o null si no. */
    public Usuario iniciarSesion(String usuario, String claveTextoPlano) throws SQLException {
        String hash = hashSHA256(claveTextoPlano);
        Usuario u = usuarioDAO.validarLogin(usuario, hash);
        if (u != null) {
            Sesion.setRolActivo(u.getRol());
        }
        return u;
    }

    private String hashSHA256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(texto.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 no disponible", e);
        }
    }
}