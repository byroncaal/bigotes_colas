/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.bigotes_colas.views;

import com.mycompany.bigotes_colas.model.Cliente;
import com.mycompany.bigotes_colas.model.Mascota;
import com.mycompany.bigotes_colas.model.Usuario;
import com.mycompany.bigotes_colas.util.Sesion;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 *
 * @author gbcya
 */
public class Mascotas extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Mascotas.class.getName());

    /** Especies válidas: se usan tanto en el filtro como al agregar una mascota. */
    private static final String[] ESPECIES = { "Perro", "Gato", "Ave", "Otro" };

    private static final String[] RAZAS = {
        "Labrador Retriever", "Pastor Aleman", "Golden Retriever", "Chihuahua", "Bulldog",
        "Poodle", "Beagle", "Husky Siberiano", "Rottweiler", "Dachshund", "Schnauzer",
        "Pug", "Cocker Spaniel", "Siames", "Persa", "Comun europeo", "Mestizo"
    };

    private static final String TODAS = "Todas";

    private List<Mascota> mascotasActuales;

    public Mascotas() {
        initComponents();
        configurarComponentes();
        cargarMascotas();
        aplicarPermisos();
        // Se recalcula el tamaño DESPUÉS de cambiar el texto de la sesión,
        // así el encabezado ya no queda cortado.
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Ajustes hechos fuera de initComponents() para que NetBeans no los
     * sobrescriba al regenerar el código del diseñador.
     */
    private void configurarComponentes() {
        // --- Filtros: una sola fila lógica (especie + raza + texto) ---
        String[] especiesFiltro = new String[ESPECIES.length + 1];
        especiesFiltro[0] = TODAS;
        System.arraycopy(ESPECIES, 0, especiesFiltro, 1, ESPECIES.length);
        comboEspecie.setModel(new DefaultComboBoxModel<>(especiesFiltro));

        String[] razasFiltro = new String[RAZAS.length + 1];
        razasFiltro[0] = TODAS;
        System.arraycopy(RAZAS, 0, razasFiltro, 1, RAZAS.length);
        comboRaza.setModel(new DefaultComboBoxModel<>(razasFiltro));

        txtBuscarMascota.setToolTipText("Buscar por nombre de mascota...");

        // El botón Buscar no tenía ActionListener: por eso no hacía nada
        btnBuscarMascota.addActionListener(this::btnBuscarMascotaActionPerformed);
        txtBuscarMascota.addActionListener(this::btnBuscarMascotaActionPerformed); // Enter
        comboEspecie.addActionListener(this::btnBuscarMascotaActionPerformed);     // filtra al cambiar
        comboRaza.addActionListener(this::btnBuscarMascotaActionPerformed);        // filtra al cambiar

        // La caja y el botón de la segunda fila eran redundantes
        txtBuscarRaza.setVisible(false);
        btnBuscarRaza.setVisible(false);

        // --- Tabla ---
        tablaMascotas.setDefaultEditor(Object.class, null); // celdas de solo lectura
        tablaMascotas.getTableHeader().setReorderingAllowed(false);
        tablaMascotas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablaMascotas.getColumnModel().getColumn(0).setPreferredWidth(50);  // Código
        tablaMascotas.getColumnModel().getColumn(3).setPreferredWidth(120); // Raza
        ((DefaultTableModel) tablaMascotas.getModel()).setColumnIdentifiers(
                new String[] { "Código", "Nombre", "Especie", "Raza", "Edad", "Dueño" });

        // --- Estilo ---
        btnNavMascotas.setForeground(java.awt.Color.WHITE); // contraste sobre el verde
        lblUsuarioSesion.setText("Sesión: -");
        btnVerHistorial.setText("Ver historial clínico");
    }

    private void aplicarPermisos() {
        Usuario usuarioActual = Sesion.getUsuarioActual();

        if (usuarioActual != null) {
            lblUsuarioSesion.setText("Sesión: " + usuarioActual.getNombre() + " (" + usuarioActual.getRol() + ")");
        }

        boolean esAdmin = usuarioActual != null && usuarioActual.esAdministrador();
        btnEliminarMascota.setEnabled(esAdmin);
        if (!esAdmin) {
            btnEliminarMascota.setToolTipText("Solo un Administrador puede eliminar mascotas");
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        pnlEncabezado = new javax.swing.JPanel();
        lblMarca = new javax.swing.JLabel();
        lblSubtituloPantalla = new javax.swing.JLabel();
        lblUsuarioSesion = new javax.swing.JLabel();
        pnlSidebar = new javax.swing.JPanel();
        btnNavClientes = new javax.swing.JButton();
        btnNavMascotas = new javax.swing.JButton();
        btnNavHistorial = new javax.swing.JButton();
        btnNavCitas = new javax.swing.JButton();
        btnNavInventario = new javax.swing.JButton();
        btnNavReportes = new javax.swing.JButton();
        btnNavUsuarios = new javax.swing.JButton();
        comboEspecie = new javax.swing.JComboBox<>();
        comboRaza = new javax.swing.JComboBox<>();
        txtBuscarMascota = new javax.swing.JTextField();
        btnBuscarMascota = new javax.swing.JButton();
        txtBuscarRaza = new javax.swing.JTextField();
        btnBuscarRaza = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaMascotas = new javax.swing.JTable();
        btnAgregarMascota = new javax.swing.JButton();
        btnEditarMascota = new javax.swing.JButton();
        btnEliminarMascota = new javax.swing.JButton();
        btnVerHistorial = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlEncabezado.setBackground(new java.awt.Color(27, 59, 59));
        pnlEncabezado.setForeground(new java.awt.Color(255, 255, 255));

        lblMarca.setFont(new java.awt.Font("Courier New", 1, 20));
        lblMarca.setForeground(new java.awt.Color(255, 255, 255));
        lblMarca.setText("Bigotes & Colas");

        lblSubtituloPantalla.setForeground(new java.awt.Color(207, 227, 223));
        lblSubtituloPantalla.setText(" | Mascotas");

        lblUsuarioSesion.setForeground(new java.awt.Color(255, 255, 255));
        lblUsuarioSesion.setText("Sesion: Administrador");

        javax.swing.GroupLayout pnlEncabezadoLayout = new javax.swing.GroupLayout(pnlEncabezado);
        pnlEncabezado.setLayout(pnlEncabezadoLayout);
        pnlEncabezadoLayout.setHorizontalGroup(
            pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEncabezadoLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(lblMarca)
                .addGap(60, 60, 60)
                .addComponent(lblSubtituloPantalla)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                .addComponent(lblUsuarioSesion)
                .addGap(33, 33, 33))
        );
        pnlEncabezadoLayout.setVerticalGroup(
            pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEncabezadoLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMarca)
                    .addComponent(lblSubtituloPantalla)
                    .addComponent(lblUsuarioSesion))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        pnlSidebar.setBackground(new java.awt.Color(233, 243, 241));

        btnNavClientes.setBackground(new java.awt.Color(233, 243, 241));
        btnNavClientes.setFont(new java.awt.Font("Arial", 1, 12));
        btnNavClientes.setForeground(new java.awt.Color(74, 74, 74));
        btnNavClientes.setText("Clientes");
        btnNavClientes.addActionListener(this::btnNavClientesActionPerformed);

        btnNavMascotas.setBackground(new java.awt.Color(46, 125, 107));
        btnNavMascotas.setFont(new java.awt.Font("Arial", 1, 12));
        btnNavMascotas.setForeground(new java.awt.Color(74, 74, 74));
        btnNavMascotas.setText("Mascotas");
        btnNavMascotas.addActionListener(this::btnNavMascotasActionPerformed);

        btnNavHistorial.setBackground(new java.awt.Color(233, 243, 241));
        btnNavHistorial.setFont(new java.awt.Font("Arial", 0, 12));
        btnNavHistorial.setForeground(new java.awt.Color(74, 74, 74));
        btnNavHistorial.setText("Historial");
        btnNavHistorial.addActionListener(this::btnNavHistorialActionPerformed);

        btnNavCitas.setBackground(new java.awt.Color(233, 243, 241));
        btnNavCitas.setFont(new java.awt.Font("Arial", 0, 12));
        btnNavCitas.setForeground(new java.awt.Color(74, 74, 74));
        btnNavCitas.setText("Citas");
        btnNavCitas.addActionListener(this::btnNavCitasActionPerformed);

        btnNavInventario.setBackground(new java.awt.Color(233, 243, 241));
        btnNavInventario.setFont(new java.awt.Font("Arial", 0, 12));
        btnNavInventario.setForeground(new java.awt.Color(74, 74, 74));
        btnNavInventario.setText("Inventario");
        btnNavInventario.addActionListener(this::btnNavInventarioActionPerformed);

        btnNavReportes.setBackground(new java.awt.Color(233, 243, 241));
        btnNavReportes.setFont(new java.awt.Font("Arial", 0, 12));
        btnNavReportes.setForeground(new java.awt.Color(74, 74, 74));
        btnNavReportes.setText("Reportes");
        btnNavReportes.addActionListener(this::btnNavReportesActionPerformed);

        btnNavUsuarios.setBackground(new java.awt.Color(233, 243, 241));
        btnNavUsuarios.setFont(new java.awt.Font("Arial", 0, 12));
        btnNavUsuarios.setForeground(new java.awt.Color(74, 74, 74));
        btnNavUsuarios.setText("Usuarios");
        btnNavUsuarios.addActionListener(this::btnNavUsuariosActionPerformed);

        javax.swing.GroupLayout pnlSidebarLayout = new javax.swing.GroupLayout(pnlSidebar);
        pnlSidebar.setLayout(pnlSidebarLayout);
        pnlSidebarLayout.setHorizontalGroup(
            pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidebarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnNavClientes)
                    .addComponent(btnNavMascotas)
                    .addComponent(btnNavHistorial)
                    .addComponent(btnNavCitas)
                    .addComponent(btnNavInventario)
                    .addComponent(btnNavReportes)
                    .addComponent(btnNavUsuarios))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        pnlSidebarLayout.setVerticalGroup(
            pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidebarLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(btnNavClientes)
                .addGap(18, 18, 18)
                .addComponent(btnNavMascotas)
                .addGap(18, 18, 18)
                .addComponent(btnNavHistorial)
                .addGap(18, 18, 18)
                .addComponent(btnNavCitas)
                .addGap(18, 18, 18)
                .addComponent(btnNavInventario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnNavReportes)
                .addGap(18, 18, 18)
                .addComponent(btnNavUsuarios)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        comboEspecie.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todas", "Perro", "Gato" }));

        comboRaza.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Labrador Retriever", "Pastor Aleman", "Golden Retriever", "Chihuahua", "Bulldog", "Poodle", "Beagle", "Husky Siberiano", "Rottweiler", "Dachshund", "Schnauzer", "Pug", "Cocker Spaniel", "Mestizo" }));

        txtBuscarMascota.setToolTipText("Buscar por nombre...");

        btnBuscarMascota.setText("Buscar");

        txtBuscarRaza.setToolTipText("Buscar por nombre...");

        btnBuscarRaza.setText("Buscar");

        tablaMascotas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"M-101", "Firulais", "Perro", " Labrador", "3 anios", "Maria Lopez"},
                {"M-102", "Michi", "Gato", "Siames", "2 anios", "Carlos Ruiz"},
                {"M-103", "Rocky", "Perro", "Pastor Aleman", "5 anios", "Ana Gomez"},
                {"M-104", "Luna", "Gato", "Comun europeo", "1 anio", null}
            },
            new String [] {
                "Codigo", "Nombre", "Especie", "Raza", "Edad", "Dueño"
            }
        ));
        jScrollPane1.setViewportView(tablaMascotas);

        btnAgregarMascota.setText("Agregar mascota");
        btnAgregarMascota.addActionListener(this::btnAgregarMascotaActionPerformed);

        btnEditarMascota.setText("Editar ");
        btnEditarMascota.addActionListener(this::btnEditarMascotaActionPerformed);

        btnEliminarMascota.setText("Eliminar ");
        btnEliminarMascota.addActionListener(this::btnEliminarMascotaActionPerformed);

        btnVerHistorial.setText("Ver historial clinico");
        btnVerHistorial.addActionListener(this::btnVerHistorialActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(comboRaza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtBuscarRaza, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnBuscarRaza)
                        .addGap(111, 111, 111))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(242, 242, 242)
                        .addComponent(comboEspecie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtBuscarMascota, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnBuscarMascota))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(178, 178, 178)
                        .addComponent(btnAgregarMascota)
                        .addGap(18, 18, 18)
                        .addComponent(btnEditarMascota)
                        .addGap(18, 18, 18)
                        .addComponent(btnEliminarMascota)
                        .addGap(18, 18, 18)
                        .addComponent(btnVerHistorial)))
                .addContainerGap(77, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(pnlSidebar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(29, 29, 29)
                    .addComponent(pnlEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(166, 166, 166)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboEspecie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBuscarMascota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarMascota))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboRaza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBuscarRaza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarRaza))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAgregarMascota)
                    .addComponent(btnEditarMascota)
                    .addComponent(btnEliminarMascota)
                    .addComponent(btnVerHistorial))
                .addContainerGap(42, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(32, 32, 32)
                            .addComponent(pnlEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(606, 606, 606))
                        .addComponent(pnlSidebar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap()))
        );

        pack();
    }

    // ===================== Navegación =====================

    private void btnNavMascotasActionPerformed(java.awt.event.ActionEvent evt) {
        // Ya estamos en Mascotas
    }

    private void btnNavClientesActionPerformed(java.awt.event.ActionEvent evt) {
        new Clientes().setVisible(true);
        this.dispose();
    }

    private void btnNavHistorialActionPerformed(java.awt.event.ActionEvent evt) {
        Mascota m = obtenerMascotaSeleccionada();
    if (m == null) return;
    new Historial(m).setVisible(true);
    this.dispose();;
    }

    private void btnNavCitasActionPerformed(java.awt.event.ActionEvent evt) {
        new Citas().setVisible(true);
        this.dispose();
    }

    private void btnNavInventarioActionPerformed(java.awt.event.ActionEvent evt) {
        new Inventario().setVisible(true);
        this.dispose();
    }

    private void btnNavReportesActionPerformed(java.awt.event.ActionEvent evt) {
        new Reportes().setVisible(true);
        this.dispose();
    }

    private void btnNavUsuariosActionPerformed(java.awt.event.ActionEvent evt) {
        new Usuarios().setVisible(true);
        this.dispose();
    }

    // ===================== Acciones CRUD =====================

    private void btnAgregarMascotaActionPerformed(java.awt.event.ActionEvent evt) {
        String nombre = JOptionPane.showInputDialog(this, "Nombre:");
        if (nombre == null) return;
        if (nombre.isBlank()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.");
            return;
        }

        // Especie desde lista cerrada, igual a la del filtro
        String especie = (String) JOptionPane.showInputDialog(this, "Especie:", "Agregar mascota",
                JOptionPane.QUESTION_MESSAGE, null, ESPECIES, ESPECIES[0]);
        if (especie == null) return;

        String raza = JOptionPane.showInputDialog(this, "Raza:");
        if (raza == null) return;

        String fechaNacTexto = JOptionPane.showInputDialog(this, "Fecha de nacimiento (aaaa-mm-dd):");
        if (fechaNacTexto == null) return;

        String buscarDueno = JOptionPane.showInputDialog(this, "Nombre o teléfono del dueño:");
        if (buscarDueno == null || buscarDueno.isBlank()) return;

        try {
            LocalDate fechaNac = fechaNacTexto.isBlank() ? null : LocalDate.parse(fechaNacTexto.trim());
            if (fechaNac != null && fechaNac.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "La fecha de nacimiento no puede ser futura.");
                return;
            }

            var clienteControlador = new com.mycompany.bigotes_colas.controlador.Cliente();
            List<Cliente> encontrados = clienteControlador.buscar(buscarDueno.trim());
            if (encontrados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontró un cliente con ese nombre/teléfono.");
                return;
            }

            // Si hay varios clientes que coinciden, el usuario elige
            Cliente dueno = encontrados.get(0);
            if (encontrados.size() > 1) {
                dueno = (Cliente) JOptionPane.showInputDialog(this, "Hay varios clientes, elige uno:",
                        "Seleccionar dueño", JOptionPane.QUESTION_MESSAGE, null,
                        encontrados.toArray(), encontrados.get(0));
                if (dueno == null) return;
            }

            var controlador = new com.mycompany.bigotes_colas.controlador.Mascota();
            controlador.agregar(nombre.trim(), especie, raza.trim(), fechaNac, dueno.getIdCliente());
            cargarMascotas();
            JOptionPane.showMessageDialog(this, "Mascota agregada.");
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Fecha inválida, usa el formato aaaa-mm-dd (ej. 2022-05-14).");
        } catch (IllegalArgumentException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void btnEditarMascotaActionPerformed(java.awt.event.ActionEvent evt) {
        Mascota m = obtenerMascotaSeleccionada();
        if (m == null) return;

        String nuevoNombre = JOptionPane.showInputDialog(this, "Nombre:", m.getNombre());
        if (nuevoNombre == null) return;
        if (nuevoNombre.isBlank()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.");
            return;
        }

        String nuevaRaza = JOptionPane.showInputDialog(this, "Raza:", m.getRaza());
        if (nuevaRaza == null) return; // antes, cancelar borraba la raza

        m.setNombre(nuevoNombre.trim());
        m.setRaza(nuevaRaza.trim());

        try {
            var controlador = new com.mycompany.bigotes_colas.controlador.Mascota();
            controlador.editar(m);
            JOptionPane.showMessageDialog(this, "Mascota actualizada.");
        } catch (IllegalArgumentException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        } finally {
            cargarMascotas(); // recarga desde BD aunque falle, para no mostrar datos a medias
        }
    }

    private void btnEliminarMascotaActionPerformed(java.awt.event.ActionEvent evt) {
        Usuario usuarioActual = Sesion.getUsuarioActual();
        if (usuarioActual == null || !usuarioActual.esAdministrador()) {
            JOptionPane.showMessageDialog(this, "Solo un Administrador puede eliminar mascotas.",
                    "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Mascota m = obtenerMascotaSeleccionada();
        if (m == null) return;

        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Seguro que quieres eliminar a " + m.getNombre() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmar != JOptionPane.YES_OPTION) return;

        try {
            var controlador = new com.mycompany.bigotes_colas.controlador.Mascota();
            controlador.eliminar(m.getIdMascota());
            cargarMascotas();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
        }
    }

    private void btnVerHistorialActionPerformed(java.awt.event.ActionEvent evt) {
      Mascota m = obtenerMascotaSeleccionada();
    if (m == null) return;

    // Abre el historial clínico filtrado por la mascota seleccionada
    new Historial(m).setVisible(true);
    this.dispose();
    }

    private void btnBuscarMascotaActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            var controlador = new com.mycompany.bigotes_colas.controlador.Mascota();
            String especie = (String) comboEspecie.getSelectedItem();
            String raza = (String) comboRaza.getSelectedItem();

            List<Mascota> resultado = controlador.filtrar(especie, txtBuscarMascota.getText().trim());

            if (raza != null && !TODAS.equals(raza)) {
                resultado = resultado.stream()
                        .filter(m -> m.getRaza() != null && m.getRaza().trim().equalsIgnoreCase(raza))
                        .toList();
            }

            mascotasActuales = resultado;
            llenarTabla(mascotasActuales);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar: " + ex.getMessage());
        }
    }

    // ===================== Utilidades =====================

    /**
     * Devuelve la mascota seleccionada en la tabla, o null (mostrando aviso)
     * si no hay selección. Convierte el índice de vista a modelo por si se
     * activa el ordenamiento de columnas.
     */
    private Mascota obtenerMascotaSeleccionada() {
        int filaVista = tablaMascotas.getSelectedRow();
        if (filaVista == -1 || mascotasActuales == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una mascota de la tabla primero.");
            return null;
        }
        int fila = tablaMascotas.convertRowIndexToModel(filaVista);
        if (fila >= mascotasActuales.size()) {
            JOptionPane.showMessageDialog(this, "Selecciona una mascota de la tabla primero.");
            return null;
        }
        return mascotasActuales.get(fila);
    }

    private void cargarMascotas() {
        try {
            var controlador = new com.mycompany.bigotes_colas.controlador.Mascota();
            mascotasActuales = controlador.listar();
            llenarTabla(mascotasActuales);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar mascotas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void llenarTabla(List<Mascota> lista) {
        DefaultTableModel modelo = (DefaultTableModel) tablaMascotas.getModel();
        modelo.setRowCount(0);

        Map<Integer, String> nombresClientes = new HashMap<>();
        try {
            var clienteControlador = new com.mycompany.bigotes_colas.controlador.Cliente();
            for (Cliente c : clienteControlador.listar()) {
                nombresClientes.put(c.getIdCliente(), c.getNombre());
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "No se pudieron cargar los clientes para la columna Dueño", ex);
        }

        for (Mascota m : lista) {
            modelo.addRow(new Object[]{
                m.getIdMascota(),
                m.getNombre(),
                m.getEspecie(),
                m.getRaza(),
                formatearEdad(m.getEdadAnios()),
                nombresClientes.getOrDefault(m.getIdCliente(), "(sin dueño)")
            });
        }
    }

    /** "1 año", "3 años", "Menos de 1 año"; vacío si no hay fecha (edad negativa). */
    private String formatearEdad(int anios) {
        if (anios < 0) return "";
        if (anios == 0) return "Menos de 1 año";
        return anios == 1 ? "1 año" : anios + " años";
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new Mascotas().setVisible(true));
    }

    private javax.swing.JButton btnAgregarMascota;
    private javax.swing.JButton btnBuscarMascota;
    private javax.swing.JButton btnBuscarRaza;
    private javax.swing.JButton btnEditarMascota;
    private javax.swing.JButton btnEliminarMascota;
    private javax.swing.JButton btnNavCitas;
    private javax.swing.JButton btnNavClientes;
    private javax.swing.JButton btnNavHistorial;
    private javax.swing.JButton btnNavInventario;
    private javax.swing.JButton btnNavMascotas;
    private javax.swing.JButton btnNavReportes;
    private javax.swing.JButton btnNavUsuarios;
    private javax.swing.JButton btnVerHistorial;
    private javax.swing.JComboBox<String> comboEspecie;
    private javax.swing.JComboBox<String> comboRaza;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMarca;
    private javax.swing.JLabel lblSubtituloPantalla;
    private javax.swing.JLabel lblUsuarioSesion;
    private javax.swing.JPanel pnlEncabezado;
    private javax.swing.JPanel pnlSidebar;
    private javax.swing.JTable tablaMascotas;
    private javax.swing.JTextField txtBuscarMascota;
    private javax.swing.JTextField txtBuscarRaza;
}