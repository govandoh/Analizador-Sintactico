package editortext;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatLightOwlIJTheme;
import editortext.modelo.FilaFuncion;
import editortext.modelo.FilaProduccion;
import editortext.modelo.Gramatica;
import editortext.modelo.Produccion;
import editortext.servicio.GramaticaParser;
import editortext.servicio.AnalizadorComponentes; 
import editortext.servicio.TransformadorRecursividad; 
import editortext.servicio.DetectorAmbiguedad;
import editortext.servicio.CalcularFunciones;
import editortext.util.FuncionTableModel;
import editortext.util.GramaticaTableModel; 
import editortext.util.ProduccionTable;

import java.awt.*; 
import java.util.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*; 
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;


public class viewEditor extends javax.swing.JFrame {

    /**
     * Creates new form viewEditor
     */
    public viewEditor() {
        try{
            UIManager.setLookAndFeel(new FlatLightOwlIJTheme());
        }
        catch(Exception ex){
            System.out.println("Failing to initilize Laf");
        }
        initComponents();
        setTitle("Editor de Texto - Analizador Sintáctico");
        this.getContentPane().setBackground(Color.decode("#fffae6"));
        
        ImageIcon logoIcon = new ImageIcon("src/imgs/logo.png");
        Image scaledImage = logoIcon.getImage().getScaledInstance(180, 115, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        setLocationRelativeTo(null);
        logoLabel.setIcon(scaledIcon);
        
        Image icon = Toolkit.getDefaultToolkit().getImage(("src/imgs/icon.png"));
        setIconImage(icon);
        
        btn_Analizar.addActionListener(e -> analizar());
        configuraF5();
    }
    
    private void configuraF5(){
        KeyStroke f5 = KeyStroke.getKeyStroke("F5"); 
        InputMap inputmap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW); 
        ActionMap actionmap = getRootPane().getActionMap(); 
        
        inputmap.put(f5, "analizadorGramatica");
        actionmap.put("analizadorGramatica", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analizar(); 
            }
        });
    }
    
    public void analizar(){
        try{
            GramaticaParser parser = new GramaticaParser(); 
            Gramatica g = parser.parsear(txtEditor.getText());
            
            DetectorAmbiguedad detector = new DetectorAmbiguedad(); 
            if(detector.esAmbigua(g)){
               JOptionPane.showMessageDialog(this, "La gramática es ambigua, La gramática de \n debe ser recursiva por la izquierda .");
            }
            
            AnalizadorComponentes analizaC = new AnalizadorComponentes(); 
            List<String> vars = new ArrayList<>(analizaC.extraerVariables(g));
            List<String> terms = new ArrayList<>(analizaC.extraerTerminales(g));
            
            List<FilaProduccion> filas = new ArrayList<>(); 
            for(Produccion p : g.getProducciones()){
                for(String term: p.getDerivaciones()){
                    String terminal = limpiarTerminal(term);
                    filas.add(new FilaProduccion(p.getVariable(), terminal));    
                }
            }
            tablaVar.setModel((TableModel) new GramaticaTableModel(vars, "Variables"));
            tablaTerm.setModel((TableModel) new GramaticaTableModel(terms, "Terminales"));
            tablaMatriz.setModel(new ProduccionTable(filas));
            
            centrarContenidoTablas(tablaVar);
            centrarContenidoTablas(tablaTerm);
            centrarContenidoTablas(tablaMatriz);
            
            //Codigon para segunda tab
            TransformadorRecursividad transformar = new TransformadorRecursividad(); 
            Gramatica gramaticaSinR = transformar.transformarGramatica(g);
            
            AnalizadorComponentes analizadorNA = new AnalizadorComponentes(); 
            
            List<String> vars_NA = new ArrayList<>(analizadorNA.extraerVariables(gramaticaSinR)); 
            List<String> terms_NA = new ArrayList<>(analizadorNA.extraerTerminales(gramaticaSinR)); 
            
            List<FilaProduccion> filas_NA = new ArrayList<>();
            for(Produccion p: gramaticaSinR.getProducciones()){
                for(String term: p.getDerivaciones()){
                    String terminal = limpiarTerminal(term); 
                    filas_NA.add(new FilaProduccion(p.getVariable(), terminal)); 
                }
            }
            
            tablaVar_NA.setModel((TableModel) new GramaticaTableModel(vars_NA, "Variables"));
            tablaTerm_NA.setModel((TableModel) new GramaticaTableModel(terms_NA, "Terminales"));
            tablaMatriz_NA.setModel(new ProduccionTable(filas_NA));
            
            centrarContenidoTablas(tablaVar_NA);
            centrarContenidoTablas(tablaTerm_NA);
            centrarContenidoTablas(tablaMatriz_NA);
            
            
            StringBuilder sb = new StringBuilder();
            for (Produccion p : gramaticaSinR.getProducciones()) {
                sb.append(p.getVariable()).append(":");
                boolean primero = true;
                for (String term : p.getDerivaciones()) {
                        if (!primero) sb.append("|");
                        sb.append(limpiarTerminal(term));
                        primero = false;
                }
                sb.append("\n");
            }
            txtEditor_NA.setText(sb.toString());
            
           //Calcular Primero
            CalcularFunciones calcular = new CalcularFunciones(); 
            Map<String, Set<String>> primeros = calcular.calcularPrimero(gramaticaSinR, vars_NA, terms_NA);
            
            //Construyo filas para la tabla de funcion primera
            List<FilaFuncion> filasPrimero = new ArrayList<>();
            for (String var : vars_NA) {
                String ter = String.join(",", primeros.get(var)); //aqui ya escribo en la tabla
                filasPrimero.add(new FilaFuncion(var, ter));
            }
            FuncionTableModel modelPrimero = new FuncionTableModel(filasPrimero);
            tabla_FPrimero.setModel(modelPrimero);
            centrarContenidoTablas(tabla_FPrimero);
            
            //Calcular siguiente
            Map<String, Set<String>> siguientes = calcular.calcularSiguiente(gramaticaSinR, vars_NA, terms_NA, primeros, vars_NA.get(0)); 
            //Construyo tabla Siguiente
            List<FilaFuncion> filasSiguiente = new ArrayList<>();
            for (String var : vars_NA) {
                Set<String> sigs = siguientes.get(var);
                String ter = String.join(",", sigs); //aqui ya escribo en la tabla
                filasSiguiente.add(new FilaFuncion(var, ter));
            }
            FuncionTableModel modelSiguiente = new FuncionTableModel(filasSiguiente);
            tabla_FSiguiente.setModel(modelSiguiente);
            centrarContenidoTablas(tabla_FSiguiente);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), " Error al analizar ", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    private String limpiarTerminal(String derivacion) {
        if (derivacion.equals("e")) {
            return "e";
        }
        return derivacion.replaceAll("'([^']+)'", "$1"); // Quito las comillas simples para la salida. 
    }

    
    public void centrarContenidoTablas(JTable table){
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        for(int i=0; i<table.getColumnCount(); i++){
            TableColumn column = table.getColumnModel().getColumn(i); 
            column.setCellRenderer(centerRenderer);
        }
        
        tablaVar.getTableHeader().setReorderingAllowed(false);
        tablaTerm.getTableHeader().setReorderingAllowed(false);
        tablaMatriz.getTableHeader().setReorderingAllowed(false);
    }
    
    
    
    public class NonEditableTableModel extends DefaultTableModel {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Todas las celdas no son editables
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        logoLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtEditor = new javax.swing.JTextArea();
        btn_Analizar = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaVar = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaTerm = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tablaMatriz = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtEditor_NA = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tablaVar_NA = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tablaTerm_NA = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tablaMatriz_NA = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jScrollPane9 = new javax.swing.JScrollPane();
        tabla_FPrimero = new javax.swing.JTable();
        jScrollPane10 = new javax.swing.JScrollPane();
        tabla_FSiguiente = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jSeparator1.setForeground(new java.awt.Color(0, 153, 204));
        jSeparator1.setDoubleBuffered(true);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("ANALIZADOR SINTÁCTICO - TABLA DE SÍMBOLOS");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel2.setText("GERARDO ANTONIO OVANDO HERNANDEZ | 9490-21-7");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 3, 16)); // NOI18N
        jLabel3.setText("COMPILADORES - SECCIÓN B - PROYECTO");

        txtEditor.setColumns(15);
        txtEditor.setFont(new java.awt.Font("Segoe UI", 1, 45)); // NOI18N
        txtEditor.setForeground(new java.awt.Color(0, 0, 0));
        txtEditor.setRows(5);
        txtEditor.setTabSize(5);
        txtEditor.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jScrollPane1.setViewportView(txtEditor);

        btn_Analizar.setBackground(new java.awt.Color(204, 204, 204));
        btn_Analizar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btn_Analizar.setForeground(new java.awt.Color(0, 0, 0));
        btn_Analizar.setText("ANALIZAR");
        btn_Analizar.setOpaque(true);

        tablaVar.setFont(new java.awt.Font("Segoe UI", 0, 30)); // NOI18N
        tablaVar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaVar.setToolTipText("");
        tablaVar.setRowHeight(30);
        jScrollPane3.setViewportView(tablaVar);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("VARIABLES");
        jLabel4.setToolTipText("");

        tablaTerm.setFont(new java.awt.Font("Segoe UI", 0, 30)); // NOI18N
        tablaTerm.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaTerm.setToolTipText("");
        tablaTerm.setRowHeight(30);
        jScrollPane2.setViewportView(tablaTerm);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("TERMINALES");
        jLabel5.setToolTipText("");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("MATRIZ DE PRODUCCIONES");
        jLabel6.setToolTipText("");

        tablaMatriz.setFont(new java.awt.Font("Segoe UI", 0, 30)); // NOI18N
        tablaMatriz.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tablaMatriz.setRowHeight(30);
        tablaMatriz.setShowGrid(false);
        jScrollPane4.setViewportView(tablaMatriz);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("GRÁMATICA SIN TRANSFORMAR");
        jLabel7.setToolTipText("");

        jSeparator2.setForeground(new java.awt.Color(0, 153, 204));
        jSeparator2.setDoubleBuffered(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btn_Analizar, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(134, 134, 134)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(52, 52, 52)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(56, 56, 56)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 1872, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane4))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 612, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 615, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(btn_Analizar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Gramatica Ambigua", jPanel1);

        txtEditor_NA.setEditable(false);
        txtEditor_NA.setColumns(15);
        txtEditor_NA.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        txtEditor_NA.setForeground(new java.awt.Color(0, 0, 0));
        txtEditor_NA.setRows(5);
        txtEditor_NA.setTabSize(5);
        txtEditor_NA.setToolTipText("");
        txtEditor_NA.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtEditor_NA.setDisabledTextColor(new java.awt.Color(204, 255, 204));
        jScrollPane5.setViewportView(txtEditor_NA);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("GRÁMATICA TRANSFORMADA");
        jLabel8.setToolTipText("");

        tablaVar_NA.setFont(new java.awt.Font("Segoe UI", 0, 32)); // NOI18N
        tablaVar_NA.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaVar_NA.setToolTipText("");
        tablaVar_NA.setRowHeight(30);
        jScrollPane6.setViewportView(tablaVar_NA);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("VARIABLES");
        jLabel9.setToolTipText("");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("TERMINALES");
        jLabel10.setToolTipText("");

        tablaTerm_NA.setFont(new java.awt.Font("Segoe UI", 0, 32)); // NOI18N
        tablaTerm_NA.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaTerm_NA.setToolTipText("");
        tablaTerm_NA.setRowHeight(30);
        jScrollPane7.setViewportView(tablaTerm_NA);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("MATRIZ DE PRODUCCIONES");
        jLabel11.setToolTipText("");

        tablaMatriz_NA.setFont(new java.awt.Font("Segoe UI", 0, 32)); // NOI18N
        tablaMatriz_NA.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tablaMatriz_NA.setRowHeight(30);
        tablaMatriz_NA.setShowGrid(false);
        jScrollPane8.setViewportView(tablaMatriz_NA);

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("TABLA DE SÍMBOLOS");
        jLabel12.setToolTipText("");

        jSeparator3.setForeground(new java.awt.Color(0, 153, 204));
        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator3.setDoubleBuffered(true);

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("F. Primera");
        jLabel13.setToolTipText("");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("F. Siguiente");
        jLabel14.setToolTipText("");

        jSeparator4.setForeground(new java.awt.Color(0, 153, 204));
        jSeparator4.setDoubleBuffered(true);

        tabla_FPrimero.setFont(new java.awt.Font("Segoe UI", 0, 25)); // NOI18N
        tabla_FPrimero.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tabla_FPrimero.setRowHeight(30);
        jScrollPane9.setViewportView(tabla_FPrimero);

        tabla_FSiguiente.setFont(new java.awt.Font("Segoe UI", 0, 25)); // NOI18N
        tabla_FSiguiente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tabla_FSiguiente.setRowHeight(30);
        jScrollPane10.setViewportView(tabla_FSiguiente);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 977, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(26, 26, 26))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 466, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(275, 275, 275)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jSeparator4)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(136, 136, 136)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(111, 111, 111))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(355, 355, 355)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                            .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addGap(0, 18, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 760, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Transformación Gramática", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 511, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 511, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1)
                                .addGap(340, 340, 340)))
                        .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1872, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 1872, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(29, 29, 29))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(viewEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(viewEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(viewEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(viewEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new viewEditor().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Analizar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JTable tablaMatriz;
    private javax.swing.JTable tablaMatriz_NA;
    private javax.swing.JTable tablaTerm;
    private javax.swing.JTable tablaTerm_NA;
    private javax.swing.JTable tablaVar;
    private javax.swing.JTable tablaVar_NA;
    private javax.swing.JTable tabla_FPrimero;
    private javax.swing.JTable tabla_FSiguiente;
    private javax.swing.JTextArea txtEditor;
    private javax.swing.JTextArea txtEditor_NA;
    // End of variables declaration//GEN-END:variables
}
