package com.mycompany.bigotes_colas.views;

import com.mycompany.bigotes_colas.model.CitaConsulta;
import com.mycompany.bigotes_colas.model.Mascota;
import com.mycompany.bigotes_colas.model.Usuario;
import com.mycompany.bigotes_colas.util.Sesion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * RF-05: Historial clínico.
 *  - new Historial()          -> consultas atendidas de todas las mascotas (menú lateral)
 *  - new Historial(mascota)   -> consultas de una sola mascota (desde la pantalla Mascotas)
 *
 * Usa el controlador existente controlador.CitaConsulta.
 *
 * @author gbcya
 */
public class Historial extends JFrame {

    private static final Logger logger = Logger.getLogger(Historial.class.getName());

    private static final Color VERDE_OSCURO = new Color(27, 59, 59);
    private static final Color VERDE = new Color(46, 125, 107);
    private static final Color FONDO_SIDEBAR = new Color(233, 243, 241);
    private static final Color GRIS_TEXTO = new Color(74, 74, 74);
    private static final Color TEXTO_SUAVE = new Color(207, 227, 223);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final com.mycompany.bigotes_colas.controlador.CitaConsulta controlador =
            new com.mycompany.bigotes_colas.controlador.CitaConsulta();

    /** Mascota filtrada; null = todas las mascotas. */
    private final Mascota mascotaFiltro;

    private final Map<Integer, String> nombresMascotas = new HashMap<>();
    private final Map<Integer, String> nombresVeterinarios = new HashMap<>();
    private List<CitaConsulta> consultasCargadas = new ArrayList<>();
    private List<CitaConsulta> consultasVisibles = new ArrayList<>();

    private JLabel lblUsuarioSesion;
    private JLabel lblContexto;
    private JTextField txtBuscar;
    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevaConsulta;
    private JButton btnAtenderCita;

    public Historial() {
        this(null);
    }

    public Historial(Mascota mascota) {
        this.mascotaFiltro = mascota;
        initComponents();
        aplicarPermisos();
        cargarHistorial();
        pack();
        setMinimumSize(new Dimension(850, 560));
        setLocationRelativeTo(null);
    }

    // ===================== Construcción de la interfaz =====================

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(mascotaFiltro == null
                ? "Bigotes & Colas - Historial clínico"
                : "Historial clínico - " + mascotaFiltro.getNombre());

        JPanel raiz = new JPanel(new BorderLayout(20, 0));
        raiz.setBorder(new EmptyBorder(10, 10, 10, 10));
        raiz.add(crearSidebar(), BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout(0, 15));
        centro.setBorder(new EmptyBorder(20, 0, 10, 10));
        centro.add(crearEncabezado(), BorderLayout.NORTH);
        centro.add(crearContenido(), BorderLayout.CENTER);
        centro.add(crearBotones(), BorderLayout.SOUTH);
        raiz.add(centro, BorderLayout.CENTER);

        setContentPane(raiz);
        setPreferredSize(new Dimension(980, 680));
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(FONDO_SIDEBAR);
        sidebar.setBorder(new EmptyBorder(18, 12, 18, 12));

        agregarNav(sidebar, "Clientes", false, () -> new Clientes().setVisible(true));
        agregarNav(sidebar, "Mascotas", false, () -> new Mascotas().setVisible(true));
        // Si estamos viendo una sola mascota, "Historial" abre el de todas
        agregarNav(sidebar, "Historial", true,
                mascotaFiltro != null ? () -> new Historial().setVisible(true) : null);
        agregarNav(sidebar, "Citas", false, () -> new Citas().setVisible(true));
        agregarNav(sidebar, "Inventario", false, () -> new Inventario().setVisible(true));
        agregarNav(sidebar, "Reportes", false, () -> new Reportes().setVisible(true));
        agregarNav(sidebar, "Usuarios", false, () -> new Usuarios().setVisible(true));
        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private void agregarNav(JPanel sidebar, String texto, boolean activo, Runnable abrir) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", activo ? Font.BOLD : Font.PLAIN, 12));
        boton.setBackground(activo ? VERDE : FONDO_SIDEBAR);
        boton.setForeground(activo ? Color.WHITE : GRIS_TEXTO);
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setPreferredSize(new Dimension(110, 26));
        boton.setMaximumSize(new Dimension(110, 26));
        if (abrir != null) {
            boton.addActionListener(e -> {
                abrir.run();
                dispose();
            });
        }
        sidebar.add(boton);
        sidebar.add(Box.createVerticalStrut(16));
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(VERDE_OSCURO);
        encabezado.setBorder(new EmptyBorder(36, 30, 40, 30));

        JPanel izquierda = new JPanel();
        izquierda.setLayout(new BoxLayout(izquierda, BoxLayout.X_AXIS));
        izquierda.setOpaque(false);

        JLabel lblMarca = new JLabel("Bigotes & Colas");
        lblMarca.setFont(new Font("Courier New", Font.BOLD, 20));
        lblMarca.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("| Historial clínico");
        lblSubtitulo.setForeground(TEXTO_SUAVE);

        izquierda.add(lblMarca);
        izquierda.add(Box.createHorizontalStrut(60));
        izquierda.add(lblSubtitulo);

        lblUsuarioSesion = new JLabel("Sesión: -");
        lblUsuarioSesion.setForeground(Color.WHITE);
        lblUsuarioSesion.setFont(lblUsuarioSesion.getFont().deriveFont(Font.BOLD));

        encabezado.add(izquierda, BorderLayout.WEST);
        encabezado.add(lblUsuarioSesion, BorderLayout.EAST);
        return encabezado;
    }

    private JPanel crearContenido() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));

        // Barra: búsqueda a la izquierda, exportar a la derecha (solo con mascota filtrada)
        JPanel barra = new JPanel(new BorderLayout());

        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        busqueda.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(18);
        txtBuscar.setToolTipText("Mascota, motivo o diagnóstico...");
        txtBuscar.addActionListener(e -> aplicarFiltroTexto());
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> aplicarFiltroTexto());
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setText("");
            aplicarFiltroTexto();
        });
        busqueda.add(txtBuscar);
        busqueda.add(btnBuscar);
        busqueda.add(btnLimpiar);
        barra.add(busqueda, BorderLayout.WEST);

        if (mascotaFiltro != null) {
            JPanel exportar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            JButton btnCsv = new JButton("Exportar CSV");
            btnCsv.addActionListener(e -> exportarCSV());
            JButton btnPdf = new JButton("Exportar PDF");
            btnPdf.addActionListener(e -> exportarPDF());
            exportar.add(btnCsv);
            exportar.add(btnPdf);
            barra.add(exportar, BorderLayout.EAST);
        }

        lblContexto = new JLabel(" ");
        lblContexto.setFont(lblContexto.getFont().deriveFont(Font.BOLD, 13f));
        lblContexto.setForeground(VERDE_OSCURO);
        lblContexto.setBorder(new EmptyBorder(0, 10, 0, 0));

        JPanel norte = new JPanel(new BorderLayout(0, 8));
        norte.add(barra, BorderLayout.NORTH);
        norte.add(lblContexto, BorderLayout.SOUTH);

        // Tabla
        modeloTabla = new DefaultTableModel(
                new String[] { "Fecha", "Mascota", "Motivo", "Diagnóstico", "Tratamiento / Vacuna", "Veterinario" }, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaHistorial.getTableHeader().setReorderingAllowed(false);
        tablaHistorial.setRowHeight(22);
        tablaHistorial.setToolTipText("Doble clic para ver el detalle");
        tablaHistorial.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaHistorial.getSelectedRow() != -1) {
                    verDetalle();
                }
            }
        });
        int[] anchos = { 80, 90, 150, 180, 170, 110 };
        for (int i = 0; i < anchos.length; i++) {
            tablaHistorial.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        panel.add(norte, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearBotones() {
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));

        btnNuevaConsulta = new JButton("Nueva consulta");
        btnNuevaConsulta.setToolTipText("Registrar una consulta sin cita previa");
        btnNuevaConsulta.addActionListener(e -> nuevaConsultaDirecta());

        btnAtenderCita = new JButton("Atender cita");
        btnAtenderCita.setToolTipText("Registrar el diagnóstico de una cita ya agendada");
        btnAtenderCita.addActionListener(e -> atenderCitaPendiente());

        JButton btnVerDetalle = new JButton("Ver detalle");
        btnVerDetalle.addActionListener(e -> verDetalle());

        JButton btnVolver = new JButton("Volver a Mascotas");
        btnVolver.addActionListener(e -> {
            new Mascotas().setVisible(true);
            dispose();
        });

        botones.add(btnNuevaConsulta);
        botones.add(btnAtenderCita);
        botones.add(btnVerDetalle);
        botones.add(btnVolver);
        return botones;
    }

    // ===================== Permisos y carga de datos =====================

    private void aplicarPermisos() {
        Usuario usuario = Sesion.getUsuarioActual();
        if (usuario != null) {
            lblUsuarioSesion.setText("Sesión: " + usuario.getNombre() + " (" + usuario.getRol() + ")");
        }
        boolean puedeRegistrar = usuario != null && (usuario.esAdministrador() || esVeterinario(usuario));
        btnNuevaConsulta.setEnabled(puedeRegistrar);
        btnAtenderCita.setEnabled(puedeRegistrar);
        if (!puedeRegistrar) {
            String aviso = "Solo Veterinarios o Administradores pueden registrar consultas";
            btnNuevaConsulta.setToolTipText(aviso);
            btnAtenderCita.setToolTipText(aviso);
        }
    }

    private void cargarHistorial() {
        try {
            cargarNombresVeterinarios();

            List<CitaConsulta> lista = new ArrayList<>();
            if (mascotaFiltro != null) {
                nombresMascotas.put(mascotaFiltro.getIdMascota(), mascotaFiltro.getNombre());
                lista.addAll(controlador.listarHistorial(mascotaFiltro.getIdMascota()));
            } else {
                // El controlador solo lista por mascota, así que se recorre cada una
                for (Mascota m : new com.mycompany.bigotes_colas.controlador.Mascota().listar()) {
                    nombresMascotas.put(m.getIdMascota(), m.getNombre());
                    lista.addAll(controlador.listarHistorial(m.getIdMascota()));
                }
            }
            // Más reciente primero
            lista.sort(Comparator.comparing(CitaConsulta::getFecha,
                    Comparator.nullsLast(Comparator.<LocalDate>reverseOrder())));
            consultasCargadas = lista;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error al cargar historial", ex);
            consultasCargadas = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Error al cargar el historial: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        aplicarFiltroTexto();
    }

    private void cargarNombresVeterinarios() throws SQLException {
        nombresVeterinarios.clear();
        for (Usuario u : controlador.listarVeterinarios()) {
            nombresVeterinarios.put(u.getIdUsuario(), u.getNombre());
        }
    }

    private void aplicarFiltroTexto() {
        String texto = txtBuscar.getText().trim().toLowerCase(Locale.ROOT);
        consultasVisibles = texto.isEmpty()
                ? consultasCargadas
                : consultasCargadas.stream()
                        .filter(c -> contiene(nombreMascota(c), texto)
                                || contiene(c.getMotivo(), texto)
                                || contiene(c.getDiagnostico(), texto))
                        .toList();

        modeloTabla.setRowCount(0);
        for (CitaConsulta c : consultasVisibles) {
            modeloTabla.addRow(new Object[] {
                formatearFecha(c.getFecha()),
                nombreMascota(c),
                c.getMotivo(),
                c.getDiagnostico(),
                c.getTratamientoVacuna(),
                nombreVeterinario(c)
            });
        }
        actualizarContexto();
    }

    private void actualizarContexto() {
        int total = consultasVisibles.size();
        String conteo = total == 1 ? "1 consulta" : total + " consultas";
        if (mascotaFiltro != null) {
            lblContexto.setText("Mascota: " + mascotaFiltro.getNombre()
                    + "  ·  " + valor(mascotaFiltro.getEspecie())
                    + "  ·  " + valor(mascotaFiltro.getRaza())
                    + "   —   " + conteo);
        } else {
            lblContexto.setText("Todas las mascotas   —   " + conteo);
        }
        if (total == 0 && txtBuscar.getText().isBlank()) {
            lblContexto.setText(lblContexto.getText() + "  (sin consultas atendidas todavía)");
        }
    }

    // ===================== Acciones =====================

    /** Consulta sin cita previa: usa controlador.registrarConsultaDirecta(...). */
    private void nuevaConsultaDirecta() {
        Mascota mascota = (mascotaFiltro != null) ? mascotaFiltro : elegirMascota();
        if (mascota == null) return;

        Integer idVeterinario = resolverVeterinario();
        if (idVeterinario == null) return;

        String[] datos = pedirDatosConsulta("Nueva consulta - " + mascota.getNombre(), true);
        if (datos == null) return;

        try {
            controlador.registrarConsultaDirecta(mascota.getIdMascota(), idVeterinario,
                    datos[0], datos[1], datos[2]);
            cargarHistorial();
            JOptionPane.showMessageDialog(this, "Consulta registrada.");
        } catch (IllegalArgumentException | IllegalStateException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar: " + ex.getMessage());
        }
    }

    /** Cita ya agendada (RF-04) que se atiende ahora: usa controlador.registrarConsulta(...). */
    private void atenderCitaPendiente() {
        try {
            List<CitaConsulta> pendientes = controlador.listarCitasPendientes();
            if (mascotaFiltro != null) {
                pendientes = pendientes.stream()
                        .filter(c -> c.getIdMascota() == mascotaFiltro.getIdMascota())
                        .toList();
            }
            if (pendientes.isEmpty()) {
                JOptionPane.showMessageDialog(this, mascotaFiltro != null
                        ? mascotaFiltro.getNombre() + " no tiene citas pendientes."
                        : "No hay citas pendientes.");
                return;
            }

            String[] opciones = new String[pendientes.size()];
            for (int i = 0; i < pendientes.size(); i++) {
                CitaConsulta c = pendientes.get(i);
                opciones[i] = (i + 1) + ". " + formatearFecha(c.getFecha())
                        + "  -  " + nombreMascota(c)
                        + "  -  " + valor(c.getMotivo());
            }
            String elegido = (String) JOptionPane.showInputDialog(this, "¿Qué cita vas a atender?",
                    "Atender cita", JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
            if (elegido == null) return;

            CitaConsulta cita = pendientes.get(indiceDe(opciones, elegido));

            String[] datos = pedirDatosConsulta("Atender cita - " + nombreMascota(cita), false);
            if (datos == null) return;

            controlador.registrarConsulta(cita.getIdCitaConsulta(), datos[1], datos[2]);
            cargarHistorial();
            JOptionPane.showMessageDialog(this, "Consulta registrada en el historial.");
        } catch (IllegalArgumentException | IllegalStateException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void verDetalle() {
        CitaConsulta c = obtenerSeleccionada();
        if (c == null) return;

        String detalle = "Fecha:  " + formatearFecha(c.getFecha())
                + "\nMascota:  " + nombreMascota(c)
                + "\nVeterinario:  " + nombreVeterinario(c)
                + "\n\nMotivo:\n" + valor(c.getMotivo())
                + "\n\nDiagnóstico:\n" + valor(c.getDiagnostico())
                + "\n\nTratamiento / Vacuna:\n" + valor(c.getTratamientoVacuna());

        JTextArea area = new JTextArea(detalle, 14, 42);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setCaretPosition(0);
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Detalle de consulta", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportarCSV() {
        File archivo = elegirArchivo("csv", "Archivo CSV");
        if (archivo == null) return;
        try {
            controlador.exportarHistorialCSV(mascotaFiltro.getIdMascota(), archivo.getAbsolutePath());
            JOptionPane.showMessageDialog(this, "Historial exportado en:\n" + archivo.getAbsolutePath());
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage()); // "Sin registros para exportar."
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error al exportar CSV", ex);
            JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage());
        }
    }

    private void exportarPDF() {
        File archivo = elegirArchivo("pdf", "Documento PDF");
        if (archivo == null) return;
        try {
            controlador.generarHistorialPDF(mascotaFiltro.getIdMascota(), mascotaFiltro.getNombre(),
                    archivo.getAbsolutePath());
            JOptionPane.showMessageDialog(this, "PDF generado en:\n" + archivo.getAbsolutePath());
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error al generar PDF", ex);
            JOptionPane.showMessageDialog(this, "Error al generar el PDF: " + ex.getMessage());
        }
    }

    // ===================== Diálogos auxiliares =====================

    /**
     * Pide motivo (opcional), diagnóstico (obligatorio) y tratamiento/vacuna.
     * Si falta el diagnóstico vuelve a mostrar el formulario sin perder lo escrito.
     * @return {motivo, diagnostico, tratamiento} o null si se cancela
     */
    private String[] pedirDatosConsulta(String titulo, boolean incluirMotivo) {
        JTextField txtMotivo = new JTextField(28);
        JTextArea txtDiagnostico = crearArea();
        JTextArea txtTratamiento = crearArea();

        JPanel form = new JPanel(new GridBagLayout());
        int fila = 0;
        if (incluirMotivo) {
            agregarFila(form, fila++, "Motivo:", txtMotivo);
        }
        agregarFila(form, fila++, "Diagnóstico *:", new JScrollPane(txtDiagnostico));
        agregarFila(form, fila, "Tratamiento / Vacuna:", new JScrollPane(txtTratamiento));

        while (true) {
            int respuesta = JOptionPane.showConfirmDialog(this, form, titulo,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (respuesta != JOptionPane.OK_OPTION) return null;

            if (txtDiagnostico.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "El diagnóstico es obligatorio.");
                continue;
            }
            String motivo = txtMotivo.getText().trim();
            return new String[] {
                motivo.isEmpty() ? "Consulta general" : motivo,
                txtDiagnostico.getText().trim(),
                txtTratamiento.getText().trim()
            };
        }
    }

    /** Si quien está en sesión es veterinario, es él; si es admin, elige de la lista. */
    private Integer resolverVeterinario() {
        Usuario usuario = Sesion.getUsuarioActual();
        if (usuario != null && esVeterinario(usuario)) {
            return usuario.getIdUsuario();
        }
        try {
            List<Usuario> veterinarios = controlador.listarVeterinarios();
            if (veterinarios.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay veterinarios registrados.");
                return null;
            }
            String[] opciones = new String[veterinarios.size()];
            for (int i = 0; i < veterinarios.size(); i++) {
                opciones[i] = (i + 1) + ". " + veterinarios.get(i).getNombre();
            }
            String elegido = (String) JOptionPane.showInputDialog(this, "¿Qué veterinario atendió?",
                    "Veterinario", JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
            if (elegido == null) return null;
            return veterinarios.get(indiceDe(opciones, elegido)).getIdUsuario();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar veterinarios: " + ex.getMessage());
            return null;
        }
    }

    private Mascota elegirMascota() {
        try {
            List<Mascota> mascotas = new com.mycompany.bigotes_colas.controlador.Mascota().listar();
            if (mascotas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay mascotas registradas.");
                return null;
            }
            String[] opciones = new String[mascotas.size()];
            for (int i = 0; i < mascotas.size(); i++) {
                Mascota m = mascotas.get(i);
                opciones[i] = "#" + m.getIdMascota() + " - " + m.getNombre() + " (" + valor(m.getEspecie()) + ")";
            }
            String elegido = (String) JOptionPane.showInputDialog(this, "¿Para qué mascota es la consulta?",
                    "Nueva consulta", JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
            if (elegido == null) return null;
            return mascotas.get(indiceDe(opciones, elegido));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar mascotas: " + ex.getMessage());
            return null;
        }
    }

    private File elegirArchivo(String extension, String descripcion) {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar historial");
        selector.setFileFilter(new FileNameExtensionFilter(descripcion, extension));
        selector.setSelectedFile(new File("historial_"
                + mascotaFiltro.getNombre().trim().replaceAll("\\s+", "_") + "." + extension));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return null;

        File archivo = selector.getSelectedFile();
        if (!archivo.getName().toLowerCase(Locale.ROOT).endsWith("." + extension)) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + "." + extension);
        }
        if (archivo.exists()) {
            int r = JOptionPane.showConfirmDialog(this, "El archivo ya existe. ¿Reemplazarlo?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (r != JOptionPane.YES_OPTION) return null;
        }
        return archivo;
    }

    // ===================== Utilidades =====================

    private CitaConsulta obtenerSeleccionada() {
        int filaVista = tablaHistorial.getSelectedRow();
        if (filaVista == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una consulta de la tabla primero.");
            return null;
        }
        int fila = tablaHistorial.convertRowIndexToModel(filaVista);
        return (fila < consultasVisibles.size()) ? consultasVisibles.get(fila) : null;
    }

    private String nombreMascota(CitaConsulta c) {
        return nombresMascotas.getOrDefault(c.getIdMascota(), "#" + c.getIdMascota());
    }

    private String nombreVeterinario(CitaConsulta c) {
        return nombresVeterinarios.getOrDefault(c.getIdVeterinario(), "Desconocido");
    }

    private static boolean esVeterinario(Usuario u) {
        return String.valueOf(u.getRol()).toLowerCase(Locale.ROOT).contains("veterin");
    }

    private static int indiceDe(String[] opciones, String elegido) {
        for (int i = 0; i < opciones.length; i++) {
            if (opciones[i].equals(elegido)) return i;
        }
        return 0;
    }

    private static String formatearFecha(LocalDate fecha) {
        return fecha != null ? fecha.format(FORMATO_FECHA) : "-";
    }

    private JTextArea crearArea() {
        JTextArea area = new JTextArea(3, 28);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private void agregarFila(JPanel form, int fila, String etiqueta, JComponent campo) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = fila;
        g.anchor = GridBagConstraints.NORTHWEST;
        g.insets = new Insets(5, 0, 5, 10);
        form.add(new JLabel(etiqueta), g);

        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.insets = new Insets(5, 0, 5, 0);
        form.add(campo, g);
    }

    private static boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase(Locale.ROOT).contains(texto);
    }

    private static String valor(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new Historial().setVisible(true));
    }
}