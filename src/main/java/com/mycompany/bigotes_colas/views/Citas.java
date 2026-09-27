package com.mycompany.bigotes_colas.views;

import com.mycompany.bigotes_colas.model.CitaConsulta;
import com.mycompany.bigotes_colas.model.Cliente;
import com.mycompany.bigotes_colas.model.Mascota;
import com.mycompany.bigotes_colas.model.Usuario;
import com.mycompany.bigotes_colas.util.Sesion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * RF-04: Agenda de citas.
 * La fecha se elige en un calendario y la hora de una lista que solo muestra
 * los horarios libres del veterinario seleccionado.
 *
 * @author gbcya
 */
public class Citas extends JFrame {

    private static final Logger logger = Logger.getLogger(Citas.class.getName());

    // Horario de atención: de 08:00 a 18:00, citas cada 30 minutos
    private static final LocalTime HORA_INICIO = LocalTime.of(8, 0);
    private static final LocalTime HORA_FIN = LocalTime.of(18, 0);
    private static final int INTERVALO_MINUTOS = 30;
    // Cuántos días hacia adelante se pueden elegir para una cita
    private static final int DIAS_DISPONIBLES = 60;

    private static final Color VERDE_OSCURO = new Color(27, 59, 59);
    private static final Color VERDE = new Color(46, 125, 107);
    private static final Color FONDO_SIDEBAR = new Color(233, 243, 241);
    private static final Color GRIS_TEXTO = new Color(74, 74, 74);
    private static final Color TEXTO_SUAVE = new Color(207, 227, 223);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA_LARGA =
            DateTimeFormatter.ofPattern("EEEE dd/MM/yyyy", Locale.forLanguageTag("es-GT"));

    private final com.mycompany.bigotes_colas.controlador.CitaConsulta controlador =
            new com.mycompany.bigotes_colas.controlador.CitaConsulta();

    private final Map<Integer, Mascota> mascotas = new LinkedHashMap<>();
    private final Map<Integer, String> nombresClientes = new HashMap<>();
    private final Map<Integer, String> nombresVeterinarios = new LinkedHashMap<>();
    private List<CitaConsulta> citasPendientes = new ArrayList<>();

    private JLabel lblUsuarioSesion;
    private JLabel lblContexto;
    private JLabel lblHorasInfo;
    private JTable tablaCitas;
    private DefaultTableModel modeloTabla;
    private JComboBox<Opcion> comboMascota;
    private JComboBox<Opcion> comboVeterinario;
    private JComboBox<LocalDate> comboFecha;
    private JComboBox<LocalTime> comboHora;
    private JTextField txtMotivo;
    private JButton btnAgendar;

    public Citas() {
        initComponents();
        mostrarSesion();
        cargarDatos();
        pack();
        setMinimumSize(new Dimension(900, 700));
        setLocationRelativeTo(null);
    }

    // ===================== Construcción de la interfaz =====================

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Bigotes & Colas - Citas");

        JPanel raiz = new JPanel(new BorderLayout(20, 0));
        raiz.setBorder(new EmptyBorder(10, 10, 10, 10));
        raiz.add(crearSidebar(), BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout(0, 15));
        centro.setBorder(new EmptyBorder(20, 0, 10, 10));
        centro.add(crearEncabezado(), BorderLayout.NORTH);
        centro.add(crearPanelTabla(), BorderLayout.CENTER);
        centro.add(crearFormulario(), BorderLayout.SOUTH);
        raiz.add(centro, BorderLayout.CENTER);

        setContentPane(raiz);
        setPreferredSize(new Dimension(980, 780));
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(FONDO_SIDEBAR);
        sidebar.setBorder(new EmptyBorder(18, 12, 18, 12));

        agregarNav(sidebar, "Clientes", false, () -> new Clientes().setVisible(true));
        agregarNav(sidebar, "Mascotas", false, () -> new Mascotas().setVisible(true));
        agregarNav(sidebar, "Historial", false, () -> new Historial().setVisible(true));
        agregarNav(sidebar, "Citas", true, null);
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

        JLabel lblSubtitulo = new JLabel("| Citas");
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

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));

        JPanel barra = new JPanel(new BorderLayout());
        lblContexto = new JLabel(" ");
        lblContexto.setFont(lblContexto.getFont().deriveFont(Font.BOLD, 13f));
        lblContexto.setForeground(VERDE_OSCURO);
        barra.add(lblContexto, BorderLayout.WEST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnAtender = new JButton("Atender");
        btnAtender.setToolTipText("Abrir el historial de la mascota para registrar el diagnóstico");
        btnAtender.addActionListener(e -> atenderCita());
        JButton btnCancelar = new JButton("Cancelar cita");
        btnCancelar.addActionListener(e -> cancelarCita());
        acciones.add(btnAtender);
        acciones.add(btnCancelar);
        barra.add(acciones, BorderLayout.EAST);

        modeloTabla = new DefaultTableModel(
                new String[] { "Fecha", "Hora", "Mascota", "Dueño", "Veterinario", "Motivo" }, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaCitas = new JTable(modeloTabla);
        tablaCitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCitas.getTableHeader().setReorderingAllowed(false);
        tablaCitas.setRowHeight(22);
        int[] anchos = { 80, 55, 90, 110, 110, 170 };
        for (int i = 0; i < anchos.length; i++) {
            tablaCitas.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        panel.add(barra, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaCitas), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearFormulario() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(210, 220, 218)),
                new EmptyBorder(12, 30, 0, 30)));

        JLabel lblTitulo = new JLabel("Nueva cita");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        GridBagConstraints gTitulo = new GridBagConstraints();
        gTitulo.gridx = 0;
        gTitulo.gridy = 0;
        gTitulo.gridwidth = 3;
        gTitulo.insets = new Insets(0, 0, 10, 0);
        form.add(lblTitulo, gTitulo);

        comboMascota = new JComboBox<>();
        comboVeterinario = new JComboBox<>();
        comboVeterinario.addActionListener(e -> actualizarHorasDisponibles());

        // --- Fecha: lista de días (solo se elige, no se escribe) ---
        comboFecha = new JComboBox<>();
        LocalDate hoy = LocalDate.now();
        for (int i = 0; i < DIAS_DISPONIBLES; i++) {
            comboFecha.addItem(hoy.plusDays(i));
        }
        comboFecha.setMaximumRowCount(12);
        comboFecha.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                          boolean seleccionado, boolean foco) {
                Object texto = (valor instanceof LocalDate f) ? textoFecha(f) : valor;
                return super.getListCellRendererComponent(lista, texto, indice, seleccionado, foco);
            }
        });
        comboFecha.addActionListener(e -> actualizarHorasDisponibles());

        // --- Hora: solo horarios libres ---
        comboHora = new JComboBox<>();
        comboHora.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                          boolean seleccionado, boolean foco) {
                Object texto = (valor instanceof LocalTime h) ? h.format(FORMATO_HORA) : valor;
                return super.getListCellRendererComponent(lista, texto, indice, seleccionado, foco);
            }
        });
        lblHorasInfo = new JLabel(" ");
        lblHorasInfo.setForeground(new Color(110, 110, 110));
        lblHorasInfo.setFont(lblHorasInfo.getFont().deriveFont(Font.ITALIC, 11f));

        txtMotivo = new JTextField(26);

        agregarFila(form, 1, "Mascota", comboMascota, null);
        agregarFila(form, 2, "Veterinario", comboVeterinario, null);
        agregarFila(form, 3, "Fecha", comboFecha, null);
        agregarFila(form, 4, "Hora", comboHora, lblHorasInfo);
        agregarFila(form, 5, "Motivo", txtMotivo, null);

        JLabel lblNota = new JLabel("Elige la fecha y la hora de la lista: solo aparecen los horarios libres del veterinario.");
        lblNota.setFont(new Font("Arial", Font.PLAIN, 11));
        lblNota.setForeground(new Color(110, 110, 110));
        GridBagConstraints gNota = new GridBagConstraints();
        gNota.gridx = 0;
        gNota.gridy = 6;
        gNota.gridwidth = 3;
        gNota.insets = new Insets(10, 0, 8, 0);
        form.add(lblNota, gNota);

        btnAgendar = new JButton("Agendar cita");
        btnAgendar.setBackground(VERDE);
        btnAgendar.setForeground(Color.WHITE);
        btnAgendar.setFont(new Font("Arial", Font.BOLD, 12));
        btnAgendar.setFocusPainted(false);
        btnAgendar.addActionListener(e -> agendarCita());
        GridBagConstraints gBoton = new GridBagConstraints();
        gBoton.gridx = 0;
        gBoton.gridy = 7;
        gBoton.gridwidth = 3;
        gBoton.insets = new Insets(0, 0, 4, 0);
        form.add(btnAgendar, gBoton);

        return form;
    }

    private void agregarFila(JPanel form, int fila, String etiqueta, JComponent campo, JComponent extra) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = fila;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(5, 0, 5, 20);
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        form.add(lbl, g);

        g.gridx = 1;
        g.insets = new Insets(5, 0, 5, 10);
        form.add(campo, g);

        if (extra != null) {
            g.gridx = 2;
            form.add(extra, g);
        }
    }

    // ===================== Carga de datos =====================

    private void mostrarSesion() {
        Usuario usuario = Sesion.getUsuarioActual();
        if (usuario != null) {
            lblUsuarioSesion.setText("Sesión: " + usuario.getNombre() + " (" + usuario.getRol() + ")");
        }
    }

    private void cargarDatos() {
        try {
            for (Cliente c : new com.mycompany.bigotes_colas.controlador.Cliente().listar()) {
                nombresClientes.put(c.getIdCliente(), c.getNombre());
            }

            comboMascota.removeAllItems();
            for (Mascota m : new com.mycompany.bigotes_colas.controlador.Mascota().listar()) {
                mascotas.put(m.getIdMascota(), m);
                String dueno = nombresClientes.getOrDefault(m.getIdCliente(), "sin dueño");
                comboMascota.addItem(new Opcion(m.getIdMascota(), m.getNombre() + "  (" + dueno + ")"));
            }

            comboVeterinario.removeAllItems();
            for (Usuario u : controlador.listarVeterinarios()) {
                nombresVeterinarios.put(u.getIdUsuario(), u.getNombre());
                comboVeterinario.addItem(new Opcion(u.getIdUsuario(), u.getNombre()));
            }
            preseleccionarVeterinarioEnSesion();

            citasPendientes = controlador.listarCitasPendientes();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error al cargar datos de citas", ex);
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        llenarTabla();
        actualizarHorasDisponibles();
    }

    private void recargarCitas() {
        try {
            citasPendientes = controlador.listarCitasPendientes();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al recargar citas: " + ex.getMessage());
        }
        llenarTabla();
        actualizarHorasDisponibles();
    }

    /** Si quien inició sesión es veterinario, queda seleccionado por defecto. */
    private void preseleccionarVeterinarioEnSesion() {
        Usuario usuario = Sesion.getUsuarioActual();
        if (usuario == null) return;
        for (int i = 0; i < comboVeterinario.getItemCount(); i++) {
            if (comboVeterinario.getItemAt(i).id == usuario.getIdUsuario()) {
                comboVeterinario.setSelectedIndex(i);
                return;
            }
        }
    }

    private void llenarTabla() {
        modeloTabla.setRowCount(0);
        for (CitaConsulta c : citasPendientes) {
            Mascota m = mascotas.get(c.getIdMascota());
            String nombreMascota = (m != null) ? m.getNombre() : "#" + c.getIdMascota();
            String dueno = (m != null) ? nombresClientes.getOrDefault(m.getIdCliente(), "") : "";
            modeloTabla.addRow(new Object[] {
                c.getFecha() != null ? c.getFecha().format(FORMATO_FECHA) : "-",
                c.getHora() != null ? c.getHora().format(FORMATO_HORA) : "-",
                nombreMascota,
                dueno,
                nombresVeterinarios.getOrDefault(c.getIdVeterinario(), "Desconocido"),
                c.getMotivo()
            });
        }
        int total = citasPendientes.size();
        lblContexto.setText(total == 1 ? "1 cita pendiente" : total + " citas pendientes");
    }

    /**
     * Llena el combo de horas con los horarios libres del veterinario en la
     * fecha elegida (quita los ocupados y, si es hoy, los que ya pasaron).
     */
    private void actualizarHorasDisponibles() {
        if (comboHora == null || comboFecha == null || btnAgendar == null) return; // aún construyendo

        LocalTime horaPrevia = (LocalTime) comboHora.getSelectedItem();
        comboHora.removeAllItems();

        LocalDate fecha = (LocalDate) comboFecha.getSelectedItem();
        Opcion veterinario = (Opcion) comboVeterinario.getSelectedItem();

        if (fecha != null && veterinario != null) {
            Set<LocalTime> ocupadas = citasPendientes.stream()
                    .filter(c -> c.getIdVeterinario() == veterinario.id)
                    .filter(c -> fecha.equals(c.getFecha()) && c.getHora() != null)
                    .map(c -> c.getHora().withSecond(0).withNano(0))
                    .collect(Collectors.toSet());

            boolean esHoy = fecha.equals(LocalDate.now());
            LocalTime ahora = LocalTime.now();
            LocalTime ultimaHora = HORA_FIN.minusMinutes(INTERVALO_MINUTOS);

            for (LocalTime h = HORA_INICIO; !h.isAfter(ultimaHora); h = h.plusMinutes(INTERVALO_MINUTOS)) {
                if (esHoy && !h.isAfter(ahora)) continue;
                if (ocupadas.contains(h)) continue;
                comboHora.addItem(h);
            }
        }

        int libres = comboHora.getItemCount();
        boolean hayHoras = libres > 0;
        comboHora.setEnabled(hayHoras);
        btnAgendar.setEnabled(hayHoras && comboMascota.getItemCount() > 0);

        if (veterinario == null) {
            lblHorasInfo.setText("Primero elige un veterinario");
        } else if (hayHoras) {
            lblHorasInfo.setText(libres == 1 ? "1 horario libre" : libres + " horarios libres");
        } else {
            lblHorasInfo.setText("Sin horarios libres ese día, elige otra fecha");
        }

        if (horaPrevia != null) {
            comboHora.setSelectedItem(horaPrevia); // conserva la hora si sigue libre
        }
    }

    // ===================== Acciones =====================

    private void agendarCita() {
        Opcion mascota = (Opcion) comboMascota.getSelectedItem();
        Opcion veterinario = (Opcion) comboVeterinario.getSelectedItem();
        LocalDate fecha = (LocalDate) comboFecha.getSelectedItem();
        LocalTime hora = (LocalTime) comboHora.getSelectedItem();
        String motivo = txtMotivo.getText().trim();

        if (mascota == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una mascota.");
            return;
        }
        if (veterinario == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un veterinario.");
            return;
        }
        if (fecha == null || hora == null) {
            JOptionPane.showMessageDialog(this, "Selecciona la fecha y la hora.");
            return;
        }
        if (motivo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escribe el motivo de la cita.");
            txtMotivo.requestFocus();
            return;
        }

        try {
            controlador.agendarCita(mascota.id, veterinario.id, fecha, hora, motivo);
            JOptionPane.showMessageDialog(this,
                    "Cita agendada:\n" + mascotas.get(mascota.id).getNombre()
                    + " con " + veterinario.texto
                    + "\n" + fecha.format(FORMATO_FECHA) + " a las " + hora.format(FORMATO_HORA),
                    "Cita agendada", JOptionPane.INFORMATION_MESSAGE);
            txtMotivo.setText("");
            recargarCitas();
        } catch (IllegalStateException ex) { // cruce de horario
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Horario ocupado", JOptionPane.WARNING_MESSAGE);
            recargarCitas();
        } catch (IllegalArgumentException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al agendar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarCita() {
        CitaConsulta cita = obtenerSeleccionada();
        if (cita == null) return;

        Mascota m = mascotas.get(cita.getIdMascota());
        String nombre = (m != null) ? m.getNombre() : "la mascota";
        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Cancelar la cita de " + nombre + " del "
                + (cita.getFecha() != null ? cita.getFecha().format(FORMATO_FECHA) : "-")
                + (cita.getHora() != null ? " a las " + cita.getHora().format(FORMATO_HORA) : "") + "?",
                "Cancelar cita", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmar != JOptionPane.YES_OPTION) return;

        try {
            controlador.eliminar(cita.getIdCitaConsulta());
            recargarCitas();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cancelar: " + ex.getMessage());
        }
    }

    /** Abre el historial de la mascota; ahí se usa "Atender cita" para registrar el diagnóstico. */
    private void atenderCita() {
        CitaConsulta cita = obtenerSeleccionada();
        if (cita == null) return;

        Mascota m = mascotas.get(cita.getIdMascota());
        if (m == null) {
            JOptionPane.showMessageDialog(this, "No se encontró la mascota de esta cita.");
            return;
        }
        new Historial(m).setVisible(true);
        dispose();
    }

    // ===================== Utilidades =====================

    private CitaConsulta obtenerSeleccionada() {
        int filaVista = tablaCitas.getSelectedRow();
        if (filaVista == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una cita de la tabla primero.");
            return null;
        }
        int fila = tablaCitas.convertRowIndexToModel(filaVista);
        return (fila < citasPendientes.size()) ? citasPendientes.get(fila) : null;
    }

    /** "Hoy - lunes 28/09/2026", "Mañana - martes 29/09/2026", "miércoles 30/09/2026"... */
    private static String textoFecha(LocalDate fecha) {
        String larga = fecha.format(FORMATO_FECHA_LARGA);
        larga = Character.toUpperCase(larga.charAt(0)) + larga.substring(1);
        LocalDate hoy = LocalDate.now();
        if (fecha.equals(hoy)) return "Hoy - " + larga;
        if (fecha.equals(hoy.plusDays(1))) return "Mañana - " + larga;
        return larga;
    }

    /** Elemento de combo con id + texto visible. */
    private static final class Opcion {
        final int id;
        final String texto;

        Opcion(int id, String texto) {
            this.id = id;
            this.texto = texto;
        }

        @Override
        public String toString() {
            return texto;
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new Citas().setVisible(true));
    }
}