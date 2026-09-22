/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bigotes_colas.util;

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

    private static String rolActivo = "Administrador"; // valor por defecto para pruebas

    private Sesion() {
        // Clase de utilidad: no se instancia
    }

    public static String getRolActivo() {
        return rolActivo;
    }

    public static void setRolActivo(String rol) {
        rolActivo = rol;
    }

    public static boolean esAdministrador() {
        return "Administrador".equals(rolActivo);
    }
}
