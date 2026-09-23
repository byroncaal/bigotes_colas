package com.mycompany.bigotes_colas.model;

/**
 * Usuario
 * Representa una fila de la tabla 'usuario'.
 * El rol define los permisos (Administrador / Veterinario / Recepcionista).
 */
public class Usuario {

    private int idUsuario;
    private String nombre;       
    private String usuario;     
    private String claveHash;   
    private String rol;          

    public Usuario() {
    }

    public Usuario(int idUsuario, String nombre, String usuario, String claveHash, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.usuario = usuario;
        this.claveHash = claveHash;
        this.rol = rol;
    }

    /** Constructor sin id, util antes de insertar un nuevo usuario (el id lo genera la BD). */
    public Usuario(String nombre, String usuario, String claveHash, String rol) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.claveHash = claveHash;
        this.rol = rol;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClaveHash() {
        return claveHash;
    }

    public void setClaveHash(String claveHash) {
        this.claveHash = claveHash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean esAdministrador() {
        return "Administrador".equals(rol);
    }

    public boolean esVeterinario() {
        return "Veterinario".equals(rol);
    }

    @Override
    public String toString() {
        // Util para mostrar el nombre en combos (ej. comboVeterinario)
        return nombre;
    }
}