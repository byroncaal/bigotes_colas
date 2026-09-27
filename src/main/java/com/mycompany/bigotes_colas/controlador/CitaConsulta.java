package com.mycompany.bigotes_colas.controlador;

import com.mycompany.bigotes_colas.doa.CitaConsultaDAO;
import com.mycompany.bigotes_colas.doa.UsuarioDAO;
import com.mycompany.bigotes_colas.doa.impl.CitaConsultaDAOImpl;
import com.mycompany.bigotes_colas.doa.impl.UsuarioDAOImpl;
import com.mycompany.bigotes_colas.reportes.CsvExportador;
import com.mycompany.bigotes_colas.reportes.ReporteHistorial;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CitaConsulta {

    private final CitaConsultaDAO citaConsultaDAO = new CitaConsultaDAOImpl();

    /** RF-04: citas agendadas que todavia no se han atendido. */
    public List<com.mycompany.bigotes_colas.model.CitaConsulta> listarCitasPendientes() throws SQLException {
        return citaConsultaDAO.listarPendientes();
    }

    /** RF-05: consultas ya atendidas de una mascota. */
    public List<com.mycompany.bigotes_colas.model.CitaConsulta> listarHistorial(int idMascota) throws SQLException {
        return citaConsultaDAO.listarHistorialPorMascota(idMascota);
    }

    /**
     * RF-04: agenda una cita nueva, validando primero que no exista cruce
     * de horario con el mismo veterinario.
     */
    public void agendarCita(int idMascota, int idVeterinario, LocalDate fecha, LocalTime hora, String motivo) throws SQLException {
        if (fecha == null || hora == null) {
            throw new IllegalArgumentException("Fecha y hora son obligatorias.");
        }
        if (citaConsultaDAO.existeCruceDeHorario(idVeterinario, fecha, hora)) {
            throw new IllegalStateException("Ese veterinario ya tiene una cita a esa fecha y hora.");
        }
        citaConsultaDAO.agendarCita(new com.mycompany.bigotes_colas.model.CitaConsulta(fecha, hora, motivo, idMascota, idVeterinario));
    }

    /** RF-05: registra el diagnostico/tratamiento de una cita ya agendada -> pasa a Historial. */
    public void registrarConsulta(int idCitaConsulta, String diagnostico, String tratamientoVacuna) throws SQLException {
        if (diagnostico == null || diagnostico.isBlank()) {
            throw new IllegalArgumentException("El diagnostico es obligatorio.");
        }
        citaConsultaDAO.registrarConsulta(idCitaConsulta, diagnostico, tratamientoVacuna);
    }

    public void eliminar(int idCitaConsulta) throws SQLException {
        citaConsultaDAO.eliminar(idCitaConsulta);
    }

   
    public void exportarHistorialCSV(int idMascota, String rutaArchivo) throws SQLException, IOException {
        List<com.mycompany.bigotes_colas.model.CitaConsulta> historial = obtenerHistorialNoVacio(idMascota);
        Map<Integer, String> nombresVeterinarios = obtenerNombresVeterinarios();

        String[] encabezados = {"Fecha", "Motivo", "Diagnostico", "Veterinario"};
        List<String[]> filas = new ArrayList<>();
        for (com.mycompany.bigotes_colas.model.CitaConsulta c : historial) {
            filas.add(new String[]{
                    c.getFecha() != null ? c.getFecha().toString() : "",
                    c.getMotivo(),
                    c.getDiagnostico(),
                    nombresVeterinarios.getOrDefault(c.getIdVeterinario(), "Desconocido")
            });
        }

        CsvExportador.exportar(rutaArchivo, encabezados, filas);
    }

    /**
     * RF-08: genera el historial clinico de una mascota en PDF. Usado por
     * la pantalla de Reportes.
     */
    public void generarHistorialPDF(int idMascota, String nombreMascota, String rutaArchivo)
            throws SQLException, net.sf.dynamicreports.report.exception.DRException, IOException {
        List<com.mycompany.bigotes_colas.model.CitaConsulta> historial = obtenerHistorialNoVacio(idMascota);
        Map<Integer, String> nombresVeterinarios = obtenerNombresVeterinarios();

        ReporteHistorial.generarPDF(historial, nombresVeterinarios, nombreMascota, rutaArchivo);
    }

    
    public void registrarConsultaDirecta(int idMascota, int idVeterinario, String motivo,
                                          String diagnostico, String tratamientoVacuna) throws SQLException {
        if (diagnostico == null || diagnostico.isBlank()) {
            throw new IllegalArgumentException("El diagnostico es obligatorio.");
        }
        com.mycompany.bigotes_colas.model.CitaConsulta cita = new com.mycompany.bigotes_colas.model.CitaConsulta(
                LocalDate.now(), LocalTime.now(), motivo, idMascota, idVeterinario);
        citaConsultaDAO.agendarCita(cita);
        citaConsultaDAO.registrarConsulta(cita.getIdCitaConsulta(), diagnostico, tratamientoVacuna);
    }

    /** Lista de veterinarios, para elegir a quien atendio la consulta. */
    public List<com.mycompany.bigotes_colas.model.Usuario> listarVeterinarios() throws SQLException {
        UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
        return usuarioDAO.listarPorRol("Veterinario");
    }

    private List<com.mycompany.bigotes_colas.model.CitaConsulta> obtenerHistorialNoVacio(int idMascota) throws SQLException {
        List<com.mycompany.bigotes_colas.model.CitaConsulta> historial = listarHistorial(idMascota);
        if (historial.isEmpty()) {
            throw new IllegalStateException("Sin registros para exportar.");
        }
        return historial;
    }

    private Map<Integer, String> obtenerNombresVeterinarios() throws SQLException {
        UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
        Map<Integer, String> nombresVeterinarios = new HashMap<>();
        for (com.mycompany.bigotes_colas.model.Usuario u : usuarioDAO.listarPorRol("Veterinario")) {
            nombresVeterinarios.put(u.getIdUsuario(), u.getNombre());
        }
        return nombresVeterinarios;
    }
}