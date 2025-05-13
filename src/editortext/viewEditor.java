package editortext;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatLightOwlIJTheme;
import editortext.modelo.FilaProduccion;
import editortext.modelo.Gramatica;
import editortext.modelo.Produccion;
import editortext.servicio.GramaticaParser;
import editortext.servicio.AnalizadorComponentes; 
import editortext.util.GramaticaTableModel; 
import editortext.util.ProduccionTable;

import java.awt.*; 
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
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), " Error al analizar ", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String limpiarTerminal(String derivacion) {
    if (derivacion.equals("e")) {
        return "e";
    }
    return derivacion.replace("'", ""); // elimina todas las comillas simples
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
        jSeparator2 = new javax.swing.JSeparator();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1915, 1009));

        jSeparator1.setForeground(new java.awt.Color(0, 153, 204));
        jSeparator1.setDoubleBuffered(true);

        jSeparator2.setForeground(new java.awt.Color(0, 153, 204));
        jSeparator2.setDoubleBuffered(true);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("ANALIZADOR SINTÁCTICO - DESPLIEGUE DE GRAMÁTICAS");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel2.setText("GERARDO ANTONIO OVANDO HERNANDEZ | 9490-21-7");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 3, 16)); // NOI18N
        jLabel3.setText("COMPILADORES - SECCIÓN B - LABORATORIO");

        txtEditor.setColumns(15);
        txtEditor.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
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

        tablaVar.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
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

        tablaTerm.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
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

        tablaMatriz.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_Analizar, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(10, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane4))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3))
                    .addComponent(jScrollPane1))
                .addGap(18, 18, 18)
                .addComponent(btn_Analizar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        jTabbedPane1.addTab("Gramatica Ambigua", jPanel1);

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
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1)
                                .addGap(288, 288, 288)))
                        .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1872, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 1872, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(19, 19, 19)
                                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 1872, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 45, Short.MAX_VALUE)))
                .addGap(29, 29, 29))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGap(108, 108, 108)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(15, 15, 15)
                            .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JTable tablaMatriz;
    private javax.swing.JTable tablaTerm;
    private javax.swing.JTable tablaVar;
    private javax.swing.JTextArea txtEditor;
    // End of variables declaration//GEN-END:variables
}
