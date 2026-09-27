/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bigotes_colas.views;
 
import com.mycompany.bigotes_colas.model.Cliente;
import com.mycompany.bigotes_colas.model.Mascota;
import com.mycompany.bigotes_colas.model.Producto;
import com.mycompany.bigotes_colas.reportes.CsvExportador;
import com.mycompany.bigotes_colas.reportes.ReporteClientes;
import com.mycompany.bigotes_colas.reportes.ReporteInventario;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gbcya
 */


public class Reportes extends JFrame {
 
    public Reportes() {
        super("Bigotes & Colas - Reportes");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1000, 640);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
 
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(construirEncabezado(), BorderLayout.NORTH);
        raiz.add(construirSidebar(), BorderLayout.WEST);
        raiz.add(construirContenido(), BorderLayout.CENTER);
        add(raiz, BorderLayout.CENTER);
    }
 
    private JPanel construirEncabezado() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x1B, 0x3B, 0x3B));
        header.setPreferredSize(new Dimension(100, 64));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));
 
        JLabel marca = new JLabel("Bigotes & Colas");
        marca.setFont(new Font("SansSerif", Font.BOLD, 20));
        marca.setForeground(Color.WHITE);
 
        JLabel subtitulo = new JLabel("  |  Reportes");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitulo.setForeground(new Color(0xCF, 0xE3, 0xDF));
 
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izquierda.setOpaque(false);
        izquierda.add(marca);
        izquierda.add(subtitulo);
 
        JLabel usuario = new JLabel("Sesion: Administrador  ");
        usuario.setForeground(Color.WHITE);
        usuario.setFont(new Font("SansSerif", Font.PLAIN, 13));
 
        header.add(izquierda, BorderLayout.WEST);
        header.add(usuario, BorderLayout.EAST);
        return header;
    }
 
    private JPanel construirSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(0xE9, 0xF3, 0xF1));
        sidebar.setPreferredSize(new Dimension(170, 100));
        sidebar.setBorder(new EmptyBorder(16, 10, 16, 10));
 
        String[] opciones = {"Clientes", "Mascotas", "Historial", "Citas", "Inventario", "Reportes", "Usuarios"};
        for (String opcion : opciones) {
            boolean activa = "Reportes".equals(opcion);
            JButton boton = new JButton(opcion);
            boton.setAlignmentX(Component.LEFT_ALIGNMENT);
            boton.setMaximumSize(new Dimension(160, 36));
            boton.setFocusPainted(false);
            boton.setHorizontalAlignment(SwingConstants.LEFT);
            boton.setBorder(new EmptyBorder(8, 14, 8, 8));
            boton.setFont(new Font("SansSerif", activa ? Font.BOLD : Font.PLAIN, 13));
            if (activa) {
                boton.setBackground(new Color(0x2E, 0x7D, 0x6B));
                boton.setForeground(Color.WHITE);
            } else {
                boton.setBackground(new Color(0xE9, 0xF3, 0xF1));
                boton.setForeground(new Color(0x4A, 0x4A, 0x4A));
                boton.setBorderPainted(false);
            }
            boton.addActionListener(e -> {
                if (activa) return;
                JFrame siguiente = switch (opcion) {
                    case "Clientes" -> new Clientes();
                    case "Mascotas" -> new Mascotas();
                    case "Historial" -> new Historial();
                    case "Citas" -> new Citas();
                    case "Inventario" -> new Inventario();
                    case "Usuarios" -> new Usuarios();
                    default -> null;
                };
                if (siguiente != null) {
                    siguiente.setVisible(true);
                    this.dispose();
                }
            });
            sidebar.add(boton);
            sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
        }
        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }
 
    private JPanel construirContenido() {
        JPanel contenido = new JPanel();
        contenido.setBackground(Color.WHITE);
        contenido.setBorder(new EmptyBorder(30, 30, 30, 30));
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
 
        JLabel titulo = new JLabel("Generar reportes");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(new Color(0x1B, 0x3B, 0x3B));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(titulo);
        contenido.add(Box.createRigidArea(new Dimension(0, 24)));
 
        contenido.add(crearSeccionReporte("Clientes", this::generarClientesPDF, this::generarClientesCSV));
        contenido.add(Box.createRigidArea(new Dimension(0, 16)));
        contenido.add(crearSeccionReporte("Inventario", this::generarInventarioPDF, this::generarInventarioCSV));
        contenido.add(Box.createRigidArea(new Dimension(0, 16)));
        contenido.add(crearSeccionReporte("Historial clinico (por mascota)", this::generarHistorialPDF, this::generarHistorialCSV));
 
        return contenido;
    }
 
    private JPanel crearSeccionReporte(String titulo, Runnable accionPdf, Runnable accionCsv) {
        JPanel seccion = new JPanel();
        seccion.setLayout(new BoxLayout(seccion, BoxLayout.Y_AXIS));
        seccion.setAlignmentX(Component.LEFT_ALIGNMENT);
        seccion.setBackground(new Color(0xF5, 0xF8, 0xF7));
        seccion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(titulo),
                new EmptyBorder(10, 14, 14, 14)));
        seccion.setMaximumSize(new Dimension(500, 90));
 
        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filaBotones.setOpaque(false);
        filaBotones.add(crearBoton("PDF", accionPdf));
        filaBotones.add(crearBoton("CSV", accionCsv));
 
        seccion.add(filaBotones);
        return seccion;
    }
 
    private JButton crearBoton(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.setBackground(new Color(0x2E, 0x7D, 0x6B));
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(90, 34));
        boton.addActionListener(e -> accion.run());
        return boton;
    }
 
    // ---------------------------------------------------------------
    // Clientes
    // ---------------------------------------------------------------
 
    private void generarClientesPDF() {
        String rutaArchivo = elegirRutaArchivo("reporte_clientes.pdf");
        if (rutaArchivo == null) return;
        try {
            List<Cliente> clientes = obtenerClientes();
            if (clientes == null) return;
            ReporteClientes.generarPDF(clientes, rutaArchivo);
            avisarExito(rutaArchivo);
        } catch (SQLException | net.sf.dynamicreports.report.exception.DRException | java.io.IOException ex) {
            avisarError(ex);
        }
    }
 
    private void generarClientesCSV() {
        String rutaArchivo = elegirRutaArchivo("reporte_clientes.csv");
        if (rutaArchivo == null) return;
        try {
            List<Cliente> clientes = obtenerClientes();
            if (clientes == null) return;
 
            String[] encabezados = {"DPI", "Nombre", "Telefono", "Direccion"};
            List<String[]> filas = new ArrayList<>();
            for (Cliente c : clientes) {
                filas.add(new String[]{c.getDpi(), c.getNombre(), c.getTelefono(), c.getDireccion()});
            }
            CsvExportador.exportar(rutaArchivo, encabezados, filas);
            avisarExito(rutaArchivo);
        } catch (SQLException | java.io.IOException ex) {
            avisarError(ex);
        }
    }
 
    private List<Cliente> obtenerClientes() throws SQLException {
        com.mycompany.bigotes_colas.controlador.Cliente controlador =
                new com.mycompany.bigotes_colas.controlador.Cliente();
        List<Cliente> clientes = controlador.listar();
        if (clientes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sin registros para generar el reporte.");
            return null;
        }
        return clientes;
    }
 
    // ---------------------------------------------------------------
    // Inventario
    // ---------------------------------------------------------------
 
    private void generarInventarioPDF() {
        String rutaArchivo = elegirRutaArchivo("reporte_inventario.pdf");
        if (rutaArchivo == null) return;
        try {
            List<Producto> productos = obtenerProductos();
            if (productos == null) return;
            ReporteInventario.generarPDF(productos, rutaArchivo);
            avisarExito(rutaArchivo);
        } catch (SQLException | net.sf.dynamicreports.report.exception.DRException | java.io.IOException ex) {
            avisarError(ex);
        }
    }
 
    private void generarInventarioCSV() {
        String rutaArchivo = elegirRutaArchivo("reporte_inventario.csv");
        if (rutaArchivo == null) return;
        try {
            List<Producto> productos = obtenerProductos();
            if (productos == null) return;
 
            String[] encabezados = {"Codigo", "Producto", "Categoria", "Stock", "Vence"};
            List<String[]> filas = new ArrayList<>();
            for (Producto p : productos) {
                filas.add(new String[]{
                        p.getCodigo(), p.getNombre(), p.getCategoria(),
                        String.valueOf(p.getStock()),
                        p.getVence() != null ? p.getVence().toString() : "-"
                });
            }
            CsvExportador.exportar(rutaArchivo, encabezados, filas);
            avisarExito(rutaArchivo);
        } catch (SQLException | java.io.IOException ex) {
            avisarError(ex);
        }
    }
 
    private List<Producto> obtenerProductos() throws SQLException {
        com.mycompany.bigotes_colas.controlador.Inventario controlador =
                new com.mycompany.bigotes_colas.controlador.Inventario();
        List<Producto> productos = controlador.listarProductos();
        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sin registros para generar el reporte.");
            return null;
        }
        return productos;
    }
 
    // ---------------------------------------------------------------
    // Historial clinico (pide la mascota primero)
    // ---------------------------------------------------------------
 
    private void generarHistorialPDF() {
        Mascota mascota = elegirMascota();
        if (mascota == null) return;
 
        String rutaArchivo = elegirRutaArchivo("historial_" + mascota.getNombre() + ".pdf");
        if (rutaArchivo == null) return;
 
        try {
            com.mycompany.bigotes_colas.controlador.CitaConsulta controlador =
                    new com.mycompany.bigotes_colas.controlador.CitaConsulta();
            controlador.generarHistorialPDF(mascota.getIdMascota(), mascota.getNombre(), rutaArchivo);
            avisarExito(rutaArchivo);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException | net.sf.dynamicreports.report.exception.DRException | java.io.IOException ex) {
            avisarError(ex);
        }
    }
 
    private void generarHistorialCSV() {
        Mascota mascota = elegirMascota();
        if (mascota == null) return;
 
        String rutaArchivo = elegirRutaArchivo("historial_" + mascota.getNombre() + ".csv");
        if (rutaArchivo == null) return;
 
        try {
            com.mycompany.bigotes_colas.controlador.CitaConsulta controlador =
                    new com.mycompany.bigotes_colas.controlador.CitaConsulta();
            controlador.exportarHistorialCSV(mascota.getIdMascota(), rutaArchivo);
            avisarExito(rutaArchivo);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException | java.io.IOException ex) {
            avisarError(ex);
        }
    }
 
    /** Pide el nombre de la mascota y devuelve la primera coincidencia, o null si cancela/no encuentra. */
    private Mascota elegirMascota() {
        String nombreBuscado = JOptionPane.showInputDialog(this, "Nombre de la mascota:");
        if (nombreBuscado == null || nombreBuscado.isBlank()) return null;
 
        try {
            com.mycompany.bigotes_colas.controlador.Mascota controlador =
                    new com.mycompany.bigotes_colas.controlador.Mascota();
            List<Mascota> encontradas = controlador.filtrar("Todas", nombreBuscado);
            if (encontradas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontro ninguna mascota con ese nombre.");
                return null;
            }
            return encontradas.get(0);
        } catch (SQLException ex) {
            avisarError(ex);
            return null;
        }
    }
 
    // ---------------------------------------------------------------
    // Utilidades comunes
    // ---------------------------------------------------------------
 
    private String elegirRutaArchivo(String nombreSugerido) {
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new File(nombreSugerido));
        int resultado = selector.showSaveDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return selector.getSelectedFile().getAbsolutePath();
    }
 
    private void avisarExito(String rutaArchivo) {
        JOptionPane.showMessageDialog(this, "Reporte generado:\n" + rutaArchivo);
    }
 
    private void avisarError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Error al generar el reporte: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Reportes().setVisible(true));
    }
}