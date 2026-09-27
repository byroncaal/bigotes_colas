/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bigotes_colas.controlador;
import com.mycompany.bigotes_colas.model.Usuario;

/**
 *
 * @author gbcya
 */
public class Sesion {

    private static Usuario usuarioActual;

    private Sesion() {
        // No se instancia
    }

    public static void iniciar(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static boolean haySesionIniciada() {
        return usuarioActual != null;
    }

    public static void cerrar() {
        usuarioActual = null;
    }
}