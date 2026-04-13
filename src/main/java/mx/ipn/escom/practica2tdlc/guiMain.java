/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package mx.ipn.escom.practica2tdlc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author rafael-marquez
 */
public class guiMain extends javax.swing.JFrame {
    private Automata automataActual = null;
    private int indicePaso = 0;
    private String cadenaPasoAPaso = "";
    private Set<String> estadoActualSimulacion = new HashSet<>();
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(guiMain.class.getName());
    private void actualizarTablaVista() {
        if(automataActual != null) {
            jScrollPane3.setViewportView(GraphViewer.createGraphComponent(automataActual, estadoActualSimulacion));
        }
        
        if (automataActual != null) {
            if (automataActual.determinarTipo() == Automata.Tipo.AFNL) {
                lblTipoAutomata.setText("Tipo: AFN-λ");
            } else if (automataActual.determinarTipo() == Automata.Tipo.AFND) {
                lblTipoAutomata.setText("Tipo: AFND"); 
            } else {
                lblTipoAutomata.setText("Tipo: AFD"); 
            }
        }
    }

    /**
     * Creates new form guiMain
     */
    public guiMain() {
        initComponents();
        jTabbedPane1.setEnabledAt(0, false);
        jTabbedPane1.setEnabledAt(1, false);

        jPanel8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
        jPanel8.removeAll();
        jPanel8.add(jButton5);

        javax.swing.JButton btnConvertirAFN = new javax.swing.JButton("Convertir a AFND");
        btnConvertirAFN.addActionListener(e -> {
            if(automataActual != null) {
                StringBuilder log = new StringBuilder();
                automataActual = OperacionesAutomata.afnlAfn(automataActual, log);
                actualizarTablaVista();
                txtConsolaTraza.setText("Autómata convertido a AFND (sin transiciones λ).\n");
                txtConsolaTraza.append(log.toString());
            }
        });
        jPanel8.add(btnConvertirAFN);

        javax.swing.JButton btnConvertirAFD = new javax.swing.JButton("Convertir a AFD");
        btnConvertirAFD.addActionListener(e -> {
            if(automataActual != null) {
                StringBuilder log = new StringBuilder();
                automataActual = OperacionesAutomata.afnAfd(automataActual, log);
                actualizarTablaVista();
                txtConsolaTraza.setText("Autómata convertido a AFD (por subconjuntos).\n");
                txtConsolaTraza.append(log.toString());
            }
        });
        jPanel8.add(btnConvertirAFD);

        javax.swing.JButton btnMinimizar = new javax.swing.JButton("Minimizar AFD");
        btnMinimizar.addActionListener(e -> {
            if(automataActual != null) {
                try {
                   Automata minimizado = OperacionesAutomata.minimizarAfd(automataActual);
                   javax.swing.JFrame frameMin = new javax.swing.JFrame("Comparativa Minimización");
                   frameMin.setSize(800, 400);
                   frameMin.setLayout(new java.awt.GridLayout(1, 2));
                   
                   javax.swing.JPanel p1 = new javax.swing.JPanel(new java.awt.BorderLayout());
                   p1.setBorder(javax.swing.BorderFactory.createTitledBorder("Original (" + automataActual.obtenerTodosLosEstados().size() + " estados)"));
                   p1.add(GraphViewer.createGraphComponent(automataActual, null), java.awt.BorderLayout.CENTER);
                   
                   javax.swing.JPanel p2 = new javax.swing.JPanel(new java.awt.BorderLayout());
                   p2.setBorder(javax.swing.BorderFactory.createTitledBorder("Minimizado (" + minimizado.obtenerTodosLosEstados().size() + " estados)"));
                   p2.add(GraphViewer.createGraphComponent(minimizado, null), java.awt.BorderLayout.CENTER);
                   
                   frameMin.add(p1);
                   frameMin.add(p2);
                   frameMin.setVisible(true);

                   automataActual = minimizado;
                   actualizarTablaVista();
                   txtConsolaTraza.setText("Autómata AFD minimizado exitosamente.\n");
                } catch (Exception ex) {
                   txtConsolaTraza.setText("Error en minimización: " + ex.getMessage());
                }
            }
        });
        jPanel8.add(btnMinimizar);
        
        javax.swing.JButton btnMultiple = new javax.swing.JButton("Pruebas por Lote");
        btnMultiple.addActionListener(e -> {
            if(automataActual != null) {
                javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
                int seleccion = fileChooser.showOpenDialog(this);
                if (seleccion == javax.swing.JFileChooser.APPROVE_OPTION) {
                     try {
                         java.util.List<String> cadenas = java.nio.file.Files.readAllLines(fileChooser.getSelectedFile().toPath());
                         txtConsolaTraza.setText("--- RESULTADOS PRUEBAS LOTE ---\n");
                         for(String c : cadenas) {
                              String res = automataActual.validarCadena(c);
                              if(res.contains("CADENA ACEPTADA")) {
                                  txtConsolaTraza.append("Cadena '" + c + "': ACEPTADA\n");
                              } else {
                                  txtConsolaTraza.append("Cadena '" + c + "': RECHAZADA\n");
                              }
                         }
                     } catch (Exception ex) {
                         txtConsolaTraza.setText("Error leyendo pruebas múltiples: " + ex.getMessage());
                     }
                }
            }
        });
        jPanel8.add(btnMultiple);
        javax.swing.JButton btnVerLambda = new javax.swing.JButton("Ver λ-Clausura");
        btnVerLambda.addActionListener(e -> {
            if(automataActual != null) {
                String q = javax.swing.JOptionPane.showInputDialog(this, "Ingresa el estado:");
                if(q != null && automataActual.obtenerTodosLosEstados().contains(q.trim())) {
                    Set<String> clausura = automataActual.calcularClausuraLambda(new HashSet<>(java.util.Arrays.asList(q.trim())));
                    txtConsolaTraza.setText("λ-Clausura (" + q.trim() + ") = " + clausura.toString() + "\n");
                    jScrollPane3.setViewportView(GraphViewer.createGraphComponent(automataActual, clausura));
                }
            }
        });
        jPanel8.add(btnVerLambda);
        
        javax.swing.JButton btnExportar = new javax.swing.JButton("Exportar (JFF)");
        btnExportar.addActionListener(e -> {
            if(automataActual != null) {
                javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
                int seleccion = fileChooser.showSaveDialog(this);
                if (seleccion == javax.swing.JFileChooser.APPROVE_OPTION) {
                    try {
                        String rute = fileChooser.getSelectedFile().getAbsolutePath();
                        if(!rute.endsWith(".jff")) rute += ".jff";
                        ExportadorJFLAP.exportar(automataActual, rute);
                        txtConsolaTraza.setText("Exportado a:\n" + rute + "\n");
                    } catch (Exception ex) {
                        txtConsolaTraza.setText("Error al exportar: " + ex.getMessage());
                    }
                }
            }
        });
        jPanel8.add(btnExportar);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        rbtnManual = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        txtAlfabeto = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtEstados = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTransicionesCrear = new javax.swing.JTable();
        txtEstadosAceptacion = new javax.swing.JTextField();
        txtEstadoInicial = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        rbtnImportar = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        lblEstadoArchivo = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        lblMensajeError = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTransicionesVista = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtConsolaTraza = new javax.swing.JTextArea();
        lblEstadoActual = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblTipoAutomata = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        txtIngresarCadena = new javax.swing.JTextField();
        btnValidarDeGolpe = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        lblResultadoValidacion = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PRACTICA 2 TDLC");
        setMinimumSize(new java.awt.Dimension(800, 500));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Insertar parámetros", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        rbtnManual.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rbtnManual);
        rbtnManual.addActionListener(this::rbtnManualActionPerformed);

        jLabel2.setText("Alfabeto (Σ):");
        jLabel2.setEnabled(false);

        txtAlfabeto.setText("{0,1}");
        txtAlfabeto.setEnabled(false);
        txtAlfabeto.addActionListener(this::txtAlfabetoActionPerformed);

        jLabel3.setText("Estados (Q):");
        jLabel3.setEnabled(false);

        txtEstados.setText("{q0,q1,q2,q3}");
        txtEstados.setEnabled(false);

        jLabel4.setText("Estado inicial:");
        jLabel4.setEnabled(false);

        jLabel5.setText("Estados de aceptación:");
        jLabel5.setEnabled(false);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        tblTransicionesCrear.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"q0", "λ", "q1"},
                {"q1", "λ", "q2"},
                {"q1", "1", "q3"},
                {"q2", "0", "q1"},
                {"q2", "1", "q2"},
                {"q3", "λ", "q0"},
                {"q3", "1", "q3"}
            },
            new String [] {
                "Estado", "Símbolo", "Siguiente Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblTransicionesCrear.setColumnSelectionAllowed(true);
        tblTransicionesCrear.setEnabled(false);
        tblTransicionesCrear.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblTransicionesCrear);
        tblTransicionesCrear.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tblTransicionesCrear.getColumnModel().getColumnCount() > 0) {
            tblTransicionesCrear.getColumnModel().getColumn(0).setResizable(false);
            tblTransicionesCrear.getColumnModel().getColumn(2).setResizable(false);
        }

        txtEstadosAceptacion.setText("{q0}");
        txtEstadosAceptacion.setEnabled(false);

        txtEstadoInicial.setText("q0");
        txtEstadoInicial.setEnabled(false);
        txtEstadoInicial.addActionListener(this::txtEstadoInicialActionPerformed);

        jButton3.setText("Agregar transición");
        jButton3.addActionListener(this::jButton3ActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(rbtnManual)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap(111, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtEstadosAceptacion))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(1, 1, 1)
                                .addComponent(txtEstados))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtAlfabeto, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtEstadoInicial)))
                        .addGap(28, 28, 28)))
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addGap(132, 132, 132))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(rbtnManual)
                        .addGap(40, 40, 40)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtAlfabeto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtEstados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtEstadoInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtEstadosAceptacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Importar desde archivo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        rbtnImportar.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rbtnImportar);
        rbtnImportar.setSelected(true);
        rbtnImportar.setText("(.jff, .json, .xml)");
        rbtnImportar.addActionListener(this::rbtnImportarActionPerformed);

        jButton1.setText("Browse");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        lblEstadoArchivo.setText("Ningún archivo seleccionado");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(rbtnImportar)
                .addGap(33, 33, 33)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(109, 109, 109)
                .addComponent(lblEstadoArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 45, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(rbtnImportar)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEstadoArchivo))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3, java.awt.BorderLayout.PAGE_START);

        jButton2.setText("Simular");
        jButton2.addActionListener(this::jButton2ActionPerformed);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblMensajeError, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 308, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(lblMensajeError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1.add(jPanel4, java.awt.BorderLayout.PAGE_END);

        jTabbedPane1.addTab("Create", jPanel1);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Simulación"));

        tblTransicionesVista.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Estado", "Símbolo", "Siguiente"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTransicionesVista.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tblTransicionesVista);
        tblTransicionesVista.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tblTransicionesVista.getColumnModel().getColumnCount() > 0) {
            tblTransicionesVista.getColumnModel().getColumn(0).setResizable(false);
            tblTransicionesVista.getColumnModel().getColumn(2).setResizable(false);
        }

        txtConsolaTraza.setColumns(20);
        txtConsolaTraza.setRows(5);
        jScrollPane1.setViewportView(txtConsolaTraza);

        lblEstadoActual.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N

        jLabel10.setText("Función de transición completa:");

        jLabel1.setText("Estado:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblTipoAutomata, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(102, 102, 102)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(lblEstadoActual, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 547, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(lblTipoAutomata)
                    .addComponent(jLabel1)
                    .addComponent(lblEstadoActual, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)))
        );

        jPanel5.add(jPanel6, java.awt.BorderLayout.CENTER);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Ingresar cadena"));

        btnValidarDeGolpe.setText("Validar de golpe");
        btnValidarDeGolpe.addActionListener(this::btnValidarDeGolpeActionPerformed);

        jButton4.setText("Paso a paso");
        jButton4.addActionListener(this::jButton4ActionPerformed);

        lblResultadoValidacion.setText("Cadena aceptada");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(txtIngresarCadena, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnValidarDeGolpe)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4)
                .addGap(27, 27, 27)
                .addComponent(lblResultadoValidacion, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIngresarCadena, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnValidarDeGolpe)
                    .addComponent(jButton4)
                    .addComponent(lblResultadoValidacion))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel7, java.awt.BorderLayout.PAGE_START);

        jButton5.setText("Atrás");
        jButton5.addActionListener(this::jButton5ActionPerformed);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(679, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel8, java.awt.BorderLayout.PAGE_END);

        jTabbedPane1.addTab("Automaton", jPanel5);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
// CONDICIÓN 1: Si seleccionó cargar desde archivo
    if (rbtnImportar.isSelected()) {
        // Verificamos que el archivo realmente se haya cargado previamente con el botón "Browse"
        if (automataActual == null) {
            lblMensajeError.setText("ERROR: Primero debes buscar y cargar un archivo.");
            lblMensajeError.setForeground(java.awt.Color.RED);
        } else {
            lblMensajeError.setText("ÉXITO: Autómata de archivo listo. Pasando a simulación...");
            lblMensajeError.setForeground(java.awt.Color.BLUE);
            // Cambiamos a la pestaña 2 automáticamente
            jTabbedPane1.setSelectedIndex(1); 
            actualizarTablaVista();
        }
    } 
    // CONDICIÓN 2: Si seleccionó crearlo manualmente
    else if (rbtnManual.isSelected()) {
        try {
            // === AQUÍ PEGAS TODO EL CÓDIGO QUE TE DI ANTES PARA LEER LA TABLA ===
            String strAlfabeto = txtAlfabeto.getText().replaceAll("[{}\\s]", "");
            String strEstados = txtEstados.getText().replaceAll("[{}\\s]", "");
            String estadoInicial = txtEstadoInicial.getText().trim();
            String strEstadosAceptacion = txtEstadosAceptacion.getText().replaceAll("[{}\\s]", "");

            Set<String> alfabeto = new HashSet<>(Arrays.asList(strAlfabeto.split(",")));
            Set<String> estados = new HashSet<>(Arrays.asList(strEstados.split(",")));
            Set<String> estadosAceptacion = new HashSet<>(Arrays.asList(strEstadosAceptacion.split(",")));

            Map<String, Map<String, Set<String>>> transiciones = new HashMap<>();
            if (tblTransicionesCrear.isEditing()) {
            tblTransicionesCrear.getCellEditor().stopCellEditing();
            }
            javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) tblTransicionesCrear.getModel();

            for (int i = 0; i < modelo.getRowCount(); i++) {
                if (modelo.getValueAt(i, 0) == null || modelo.getValueAt(i, 1) == null || modelo.getValueAt(i, 2) == null) continue;

                String origen = modelo.getValueAt(i, 0).toString().trim();
                String simbolo = modelo.getValueAt(i, 1).toString().trim();
                String destino = modelo.getValueAt(i, 2).toString().trim();

                if (origen.isEmpty() || simbolo.isEmpty() || destino.isEmpty()) continue;

                // === INICIO DE LA VALIDACIÓN ESTRICTA ===
                // Si el símbolo de la tabla NO está en el conjunto del alfabeto que escribimos arriba
                if (!alfabeto.contains(simbolo) && !simbolo.equals("λ") && !simbolo.equals("ε")) {
                    // Mostramos el error en tu etiqueta roja
                    lblMensajeError.setText("ERROR: El símbolo '" + simbolo + "' no está en el alfabeto Σ.");
                    lblMensajeError.setForeground(java.awt.Color.RED);
                    return; // ¡ESTO ES CLAVE! Detiene la ejecución del botón y evita que avance de pestaña
                }
                if (!estados.contains(origen)) {
                    lblMensajeError.setText("ERROR: El estado origen '" + origen + "' no fue definido en Q.");
                    lblMensajeError.setForeground(java.awt.Color.RED);
                    return;
                }

                if (!estados.contains(destino)) {
                    lblMensajeError.setText("ERROR: El estado destino '" + destino + "' no fue definido en Q.");
                    lblMensajeError.setForeground(java.awt.Color.RED);
                    return;
                }
                // === FIN DE LA VALIDACIÓN ===

                transiciones.putIfAbsent(origen, new HashMap<>());
                transiciones.get(origen).putIfAbsent(simbolo, new HashSet<>());
                transiciones.get(origen).get(simbolo).add(destino);
            }

            automataActual = new Automata(alfabeto, estadoInicial, estadosAceptacion, transiciones);
            // ===================================================================

            lblMensajeError.setText("ÉXITO: Autómata manual creado. Pasando a simulación...");
            lblMensajeError.setForeground(java.awt.Color.BLUE);
            actualizarTablaVista();
            jTabbedPane1.setSelectedIndex(1); // Cambiamos a la pestaña 2
            
        } catch (Exception e) {
            lblMensajeError.setText("ERROR: Revisa que todos los campos manuales estén llenos correctamente.");
            lblMensajeError.setForeground(java.awt.Color.RED);
        }
    } 
    // CONDICIÓN 3: No seleccionó nada
    else {
        lblMensajeError.setText("ERROR: Selecciona si deseas importar o insertar parámetros.");
        lblMensajeError.setForeground(java.awt.Color.RED);
    }
    if (automataActual != null) {
        if (automataActual.determinarTipo() == Automata.Tipo.AFNL) {
            lblTipoAutomata.setText("Tipo: AFN-λ");
        } else if (automataActual.determinarTipo() == Automata.Tipo.AFND) {
            lblTipoAutomata.setText("Tipo: AFND"); 
        } else {
            lblTipoAutomata.setText("Tipo: AFD"); 
        }
    } else {
        lblTipoAutomata.setText("Tipo: AFD"); // Default fallback
    }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
// 1. Destruir el autómata de la memoria (Eliminar la quíntupla)
    automataActual = null;

    // 4. Limpiar los elementos del Tab 2 (Simulación)
    txtIngresarCadena.setText("");
    txtConsolaTraza.setText(""); // Limpiar la consola grande
    lblEstadoActual.setText(""); // Limpiar tu etiqueta 'q1'
    lblResultadoValidacion.setText("");
    
    // Limpiar la tabla de solo lectura del Tab 2
    jScrollPane3.setViewportView(tblTransicionesVista); // Volvemos a colocar la tabla vacía original si se desea

    // 5. Regresar al usuario al Tab 1
    jTabbedPane1.setSelectedIndex(0);
    indicePaso = 0; // Reiniciamos el contador del paso a paso por si acaso
    txtIngresarCadena.setEnabled(true);
    btnValidarDeGolpe.setEnabled(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void txtEstadoInicialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEstadoInicialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEstadoInicialActionPerformed

    private void btnValidarDeGolpeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnValidarDeGolpeActionPerformed
    String cadena = txtIngresarCadena.getText().trim();
    String resultadoTraza = automataActual.validarCadena(cadena);
    txtConsolaTraza.setText(resultadoTraza);
    if (resultadoTraza.contains("ACEPTADA")) {
        lblResultadoValidacion.setText("CADENA ACEPTADA");
        lblResultadoValidacion.setForeground(new java.awt.Color(0, 153, 0)); // Verde oscuro
    } else {
        lblResultadoValidacion.setText("CADENA RECHAZADA");
        lblResultadoValidacion.setForeground(java.awt.Color.RED);
    }
    }//GEN-LAST:event_btnValidarDeGolpeActionPerformed

    private void rbtnImportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnImportarActionPerformed
        jButton1.setEnabled(true);
        lblEstadoArchivo.setEnabled(true);
        tblTransicionesCrear.setEnabled(false);
        txtAlfabeto.setEnabled(false);
        txtEstados.setEnabled(false);
        txtEstadoInicial.setEnabled(false);
        txtEstadosAceptacion.setEnabled(false);
        jLabel2.setEnabled(false);
        jLabel3.setEnabled(false);
        jLabel4.setEnabled(false);
        jLabel5.setEnabled(false);
    }//GEN-LAST:event_rbtnImportarActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
    int seleccion = fileChooser.showOpenDialog(this);
    
    if (seleccion == javax.swing.JFileChooser.APPROVE_OPTION) {
        java.io.File archivo = fileChooser.getSelectedFile();
        try {
            // Llama a la clase que acabamos de crear
            automataActual = LectorJFLAP.cargarDesdeXML(archivo.getAbsolutePath());
            
            lblEstadoArchivo.setText("Cargado: " + archivo.getName());
            lblMensajeError.setText("ÉXITO: Autómata importado. Ve a Simulación.");
            lblMensajeError.setForeground(java.awt.Color.BLUE);
            
        } catch (Exception e) {
            lblMensajeError.setText("ERROR: No se pudo leer el archivo JFLAP/XML.");
            lblMensajeError.setForeground(java.awt.Color.RED);
            e.printStackTrace();
        }
    }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void rbtnManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnManualActionPerformed
        jButton1.setEnabled(false);
        lblEstadoArchivo.setEnabled(false);
        tblTransicionesCrear.setEnabled(true);
        txtAlfabeto.setEnabled(true);
        txtEstados.setEnabled(true);
        txtEstadoInicial.setEnabled(true);
        txtEstadosAceptacion.setEnabled(true);
        jLabel2.setEnabled(true);
        jLabel3.setEnabled(true);
        jLabel4.setEnabled(true);
        jLabel5.setEnabled(true);
        jButton2.setEnabled(true);
    }//GEN-LAST:event_rbtnManualActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
if (automataActual == null) return;

    // Si es el primer clic (inicio de la simulación)
    if (indicePaso == 0) {
        txtIngresarCadena.setEnabled(false);
        btnValidarDeGolpe.setEnabled(false);
        cadenaPasoAPaso = txtIngresarCadena.getText().trim();
        
        Set<String> initSet = new HashSet<>();
        initSet.add(automataActual.getEstadoInicial());
        estadoActualSimulacion.clear();
        estadoActualSimulacion.addAll(automataActual.calcularClausuraLambda(initSet));
        
        txtConsolaTraza.setText("--- INICIO SIMULACIÓN PASO A PASO ---\n");
        txtConsolaTraza.append("Cadena: " + cadenaPasoAPaso + "\n");
        lblEstadoActual.setText(estadoActualSimulacion.toString()); // Tu etiqueta "q1"
        actualizarTablaVista();
        lblResultadoValidacion.setText("EN PROCESO...");
        lblResultadoValidacion.setForeground(java.awt.Color.ORANGE);
        
        if (cadenaPasoAPaso.isEmpty()) {
            txtConsolaTraza.append("Advertencia: Cadena vacía.\n");
        }
    }

    // Si aún hay caracteres por leer en la cadena
    if (indicePaso < cadenaPasoAPaso.length()) {
        String simbolo = String.valueOf(cadenaPasoAPaso.charAt(indicePaso));
        
        txtConsolaTraza.append("\nEvaluando símbolo: '" + simbolo + "' desde estados " + estadoActualSimulacion + "\n");
        
        Set<String> siguientes = new HashSet<>();
        for(String q : estadoActualSimulacion) {
            Map<String, Set<String>> transiciones = automataActual.getTransiciones().get(q);
            if (transiciones != null && transiciones.containsKey(simbolo)) {
                siguientes.addAll(transiciones.get(simbolo));
            }
        }
        
        if (!siguientes.isEmpty()) {
            Set<String> clLambda = automataActual.calcularClausuraLambda(siguientes);
            txtConsolaTraza.append(estadoActualSimulacion + " --(" + simbolo + ")--> " + clLambda + "\n");
            
            estadoActualSimulacion.clear();
            estadoActualSimulacion.addAll(clLambda);
            lblEstadoActual.setText(estadoActualSimulacion.toString()); // Actualiza la etiqueta visual
            actualizarTablaVista();
            indicePaso++;
        } else {
            txtConsolaTraza.append("ERROR: No hay caminos válidos para '" + simbolo + "'. El autómata se detiene.\n");
            lblResultadoValidacion.setText("CADENA RECHAZADA");
            lblResultadoValidacion.setForeground(java.awt.Color.RED);
            indicePaso = 0; // Reiniciar
            txtIngresarCadena.setEnabled(true);
            btnValidarDeGolpe.setEnabled(true);
        }
    } 
    // Si ya terminamos de leer toda la cadena
    else {
        txtConsolaTraza.append("\n--- FIN DE LA CADENA ---\n");
        
        boolean isAceptado = false;
        for(String q : estadoActualSimulacion) {
            if(automataActual.getEstadosAceptacion().contains(q)) isAceptado = true;
        }

        if (isAceptado) {
            txtConsolaTraza.append("Al menos un estado en " + estadoActualSimulacion + " ES de aceptación.\n");
            lblResultadoValidacion.setText("CADENA ACEPTADA");
            lblResultadoValidacion.setForeground(new java.awt.Color(0, 153, 0));
        } else {
            txtConsolaTraza.append("Ninguno de los estados en " + estadoActualSimulacion + " es de aceptación.\n");
            lblResultadoValidacion.setText("CADENA RECHAZADA");
            lblResultadoValidacion.setForeground(java.awt.Color.RED);
        }
        indicePaso = 0; // Reiniciar para la siguiente simulación
        estadoActualSimulacion.clear();
        actualizarTablaVista();
        txtIngresarCadena.setEnabled(true);
        btnValidarDeGolpe.setEnabled(true);
    }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) tblTransicionesCrear.getModel();
        modelo.addRow(new Object[]{"", "", ""});
    }//GEN-LAST:event_jButton3ActionPerformed

    private void txtAlfabetoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAlfabetoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAlfabetoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
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
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new guiMain().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnValidarDeGolpe;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblEstadoActual;
    private javax.swing.JLabel lblEstadoArchivo;
    private javax.swing.JLabel lblMensajeError;
    private javax.swing.JLabel lblResultadoValidacion;
    private javax.swing.JLabel lblTipoAutomata;
    private javax.swing.JRadioButton rbtnImportar;
    private javax.swing.JRadioButton rbtnManual;
    private javax.swing.JTable tblTransicionesCrear;
    private javax.swing.JTable tblTransicionesVista;
    private javax.swing.JTextField txtAlfabeto;
    private javax.swing.JTextArea txtConsolaTraza;
    private javax.swing.JTextField txtEstadoInicial;
    private javax.swing.JTextField txtEstados;
    private javax.swing.JTextField txtEstadosAceptacion;
    private javax.swing.JTextField txtIngresarCadena;
    // End of variables declaration//GEN-END:variables
}
