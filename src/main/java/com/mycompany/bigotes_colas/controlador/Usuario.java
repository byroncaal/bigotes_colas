package com.mycompany.bigotes_colas.controlador;

import com.mycompany.bigotes_colas.doa.UsuarioDAO;
import com.mycompany.bigotes_colas.doa.impl.UsuarioDAOImpl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HexFormat;
import java.util.List;

/**
 * Reglas de negocio para la gestión de usuarios (pantalla Usuarios).
 */
public class Usuario {

    public static final List<String> ROLES = List.of("Administrador", "Veterinario", "Recepcionista");
    private static final int LONGITUD_MINIMA_CLAVE = 6;

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    public List<com.mycompany.bigotes_colas.model.Usuario> listar() throws SQLException {
        return usuarioDAO.listarTodos();
    }

    public void crear(String nombre, String usuario, String clave, String rol) throws SQLException {
        validarDatos(nombre, usuario, rol);
        validarClave(clave);
        validarUsuarioUnico(usuario, 0);

        var nuevo = new com.mycompany.bigotes_colas.model.Usuario(
                nombre.trim(), usuario.trim(), hash(clave), rol);
        usuarioDAO.crear(nuevo);
    }

    public void actualizar(int idUsuario, String nombre, String usuario, String rol) throws SQLException {
        validarDatos(nombre, usuario, rol);
        validarUsuarioUnico(usuario, idUsuario);

        var actual = buscarPorId(idUsuario);
        boolean pierdeAdmin = actual.esAdministrador() && !"Administrador".equals(rol);
        if (pierdeAdmin && actual.isActivo() && contarAdministradoresActivos() <= 1) {
            throw new IllegalStateException("Debe quedar al menos un Administrador activo.");
        }

        actual.setNombre(nombre.trim());
        actual.setUsuario(usuario.trim());
        actual.setRol(rol);
        usuarioDAO.actualizar(actual);
    }

    public void restablecerClave(int idUsuario, String nuevaClave) throws SQLException {
        validarClave(nuevaClave);
        usuarioDAO.actualizarClave(idUsuario, hash(nuevaClave));
    }

    /** activo = false suspende; activo = true reactiva. */
    public void cambiarEstado(int idUsuario, boolean activo, int idUsuarioSesion) throws SQLException {
        if (!activo && idUsuario == idUsuarioSesion) {
            throw new IllegalStateException("No puedes suspender tu propio usuario.");
        }
        var u = buscarPorId(idUsuario);
        if (!activo && u.esAdministrador() && u.isActivo() && contarAdministradoresActivos() <= 1) {
            throw new IllegalStateException("No puedes suspender al único Administrador activo.");
        }
        usuarioDAO.cambiarEstado(idUsuario, activo);
    }

    public void eliminar(int idUsuario, int idUsuarioSesion) throws SQLException {
        if (idUsuario == idUsuarioSesion) {
            throw new IllegalStateException("No puedes eliminar tu propio usuario.");
        }
        var u = buscarPorId(idUsuario);
        if (u.esAdministrador() && u.isActivo() && contarAdministradoresActivos() <= 1) {
            throw new IllegalStateException("No puedes eliminar al único Administrador activo.");
        }
        try {
            usuarioDAO.eliminar(idUsuario);
        } catch (SQLIntegrityConstraintViolationException ex) {
            // Tiene citas/consultas ligadas (llave foránea)
            throw new IllegalStateException(
                    "No se puede eliminar porque tiene citas o consultas registradas.\n"
                    + "Usa \"Suspender usuario\" para bloquear su acceso sin perder el historial.");
        }
    }

    /**
     * Cifra la contraseña. DEBE ser el mismo cálculo que usa la pantalla de Login;
     * si no, los usuarios creados aquí no podrán iniciar sesión.
     * Por defecto: SHA-256 en hexadecimal minúsculas.
     */
    public static String hash(String clave) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(clave.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 no disponible", ex);
        }
    }

    // ===================== Validaciones =====================

    private void validarDatos(String nombre, String usuario, String rol) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre completo es obligatorio.");
        }
        if (usuario == null || usuario.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (!usuario.trim().matches("[A-Za-z0-9._-]{3,30}")) {
            throw new IllegalArgumentException(
                    "El usuario debe tener de 3 a 30 caracteres, sin espacios (letras, números, punto, guion).");
        }
        if (rol == null || !ROLES.contains(rol)) {
            throw new IllegalArgumentException("Rol inválido.");
        }
    }

    private void validarClave(String clave) {
        if (clave == null || clave.length() < LONGITUD_MINIMA_CLAVE) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos " + LONGITUD_MINIMA_CLAVE + " caracteres.");
        }
    }

    private void validarUsuarioUnico(String usuario, int idExcluir) throws SQLException {
        for (var u : usuarioDAO.listarTodos()) {
            if (u.getIdUsuario() != idExcluir && u.getUsuario() != null
                    && u.getUsuario().equalsIgnoreCase(usuario.trim())) {
                throw new IllegalArgumentException("Ya existe un usuario con el nombre \"" + usuario.trim() + "\".");
            }
        }
    }

    private com.mycompany.bigotes_colas.model.Usuario buscarPorId(int idUsuario) throws SQLException {
        for (var u : usuarioDAO.listarTodos()) {
            if (u.getIdUsuario() == idUsuario) return u;
        }
        throw new IllegalArgumentException("No se encontró el usuario.");
    }

    private long contarAdministradoresActivos() throws SQLException {
        return usuarioDAO.listarTodos().stream()
                .filter(u -> u.esAdministrador() && u.isActivo())
                .count();
    }
}