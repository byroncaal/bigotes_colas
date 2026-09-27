/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bigotes_colas.util;
import com.mycompany.bigotes_colas.model.Usuario;

/**
 *
 * @author gbcya
 */

/**
 * Sesion
 * Guarda en memoria el rol del usuario que inicio sesion (RF-01),
 * para que las pantallas puedan mostrar u ocultar opciones segun el rol.
 * Prototipo NO funcional: no hay autenticacion real, solo se recuerda
 * el rol elegido en el combo de LoginFrame.
 */

public class Sesion {
 
    private static Usuario usuarioActual;
 
    private Sesion() {
        // Clase de utilidad: no se instancia
    }
 
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }
 
    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }
 
    public static boolean haySesionActiva() {
        return usuarioActual != null;
    }
 
    /** Atajo comodo: equivale a getUsuarioActual().esAdministrador(), pero seguro si no hay sesion. */
    public static boolean esAdministrador() {
        return usuarioActual != null && usuarioActual.esAdministrador();
    }
}
 