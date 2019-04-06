/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import controlador.ControladorGestorFuentes;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import modelo.GoogleFont;
import modelo.LocalFont;
import test.TestControlador;
import utils.DescargaRecursos;
import utils.Filtros;
import vista.tablemodels.TableModelGoogleFonts;
import vista.tablemodels.TableModelLocalFonts;

/**
 *
 * @author Mario
 */
public class PruebaVista extends javax.swing.JFrame {

    private ControladorGestorFuentes cgf;
    private Font createFont = null;

    private File misFuentes;
    private DefaultMutableTreeNode root;
    private DefaultTreeModel treeModel;

    private File dirDestino;

    private List<LocalFont> listaFuentesLocales;

    /**
     * Creates new form PruebaVista
     */
    public PruebaVista() {
        initComponents();
        this.setLocationRelativeTo(null);
        //Guardar cfg en fichero de datos y gestionar que el usuario elija el lugar del dir Mis fuentes
        //Hacer que el archivo Json se guarde en el directorio appdata o en documentos
        //Mirar como hacer ficheros ocultos en windows desde java
        cgf = new ControladorGestorFuentes(new File("Mis fuentes"), new File(""));
        cgf.descargaJsonFuentes();
        rellenarTablaGoogleFonts();
        dragDrop();

        jTableGoogleFonts.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                jComboBoxStyles.setModel(new DefaultComboBoxModel(cgf.getListaFuentes().get(jTableGoogleFonts.getSelectedRow()).getFiles().keySet().toArray()));
            }
        });

        misFuentes = cgf.getMisFuentes();
        root = new DefaultMutableTreeNode(misFuentes);

        actualizarNodos();

        jTreeUserDir.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (jTreeUserDir.getLeadSelectionPath() != null) {
                    //crear ruta
                    String ruta = jTreeUserDir.getLeadSelectionPath().toString();
                    ruta = ruta.replace("[/", "");
                    ruta = ruta.replace(", ", File.separator);
                    ruta = ruta.substring(1, ruta.length() - 1);
                    dirDestino = new File(ruta);
                    //System.out.println(dirDestino.getAbsolutePath());
                }

            }
        });

    }

    private void rellenarTablaGoogleFonts() {
        jTableGoogleFonts.setModel(new TableModelGoogleFonts(cgf.getListaFuentes()));
    }

    private void rellenarTablaLocalFonts() {
        jTableGoogleFonts.setModel(new TableModelLocalFonts(listaFuentesLocales));
    }

    private void crearFuente(String dirGoogleFont) {
        try {
            //createFont = Font.createFont(Font.TRUETYPE_FONT, new File("AGaramondPro-Regular.otf"));
            //createFont = Font.createFont(Font.TRUETYPE_FONT, new URL("https://fonts.gstatic.com/s/roboto/v18/KFOmCnqEu92Fr1Mu4mxP.ttf").openStream());
            createFont = Font.createFont(Font.TRUETYPE_FONT, new URL(dirGoogleFont).openStream());
        } catch (FontFormatException ex) {
            Logger.getLogger(TestControlador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createChildNodes(File fileRoot,
            DefaultMutableTreeNode node) {
        File[] files = fileRoot.listFiles();

        //comprueba que files tenga archivos o directorios
        if (files == null) {
            return;
        }

        for (File file : files) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file.getName());

            //añade el directorio o archivo al nodo raiz
            node.add(childNode);

            if (file.isDirectory()) {
                this.createChildNodes(file, childNode);
            }
        }
    }

    private void actualizarNodos() {
        //dirDestino = null;
        root = new DefaultMutableTreeNode(misFuentes);

        createChildNodes(misFuentes, root);
        treeModel = new DefaultTreeModel(root);
        jTreeUserDir.setModel(treeModel);
    }

    private void dragDrop() {
        TransferHandler th = new TransferHandler(null) {
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                return true;
            }

            @Override
            public boolean importData(JComponent comp, Transferable t) {

                List<File> transferData = null;

                try {
                    transferData = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(PruebaVista.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(PruebaVista.class.getName()).log(Level.SEVERE, null, ex);
                }

                List<File> archivosCopiar = new ArrayList<>();

                for (File file : transferData) {
                    if (file.getName().endsWith(".ttf") || file.getName().endsWith(".otf")) {
                        archivosCopiar.add(file);
                    }
                }
                PruebaVista.this.coiparArchivos(archivosCopiar.toArray(new File[archivosCopiar.size()]));
                
                /*for (Iterator<File> iterator = transferData.iterator(); iterator.hasNext();) {
                    File next = iterator.next();
                    if (!next.getName().endsWith(".ttf") || !next.getName().endsWith(".otf")) {
                        iterator.remove();
                    }
                }
                File[] toArray = transferData.toArray(new File[transferData.size()]);
                PruebaVista.this.coiparArchivos(toArray);*/

                return true;
            }

        };
        jLabelDragDrop.setTransferHandler(th);
    }

    /**
     * Método que devuelve una ruta escogida por un JFileChooser.
     *
     * @param pantalla Componente padre.
     * @return String con una ruta.
     */
    public String seleccionarDirectorio(Component pantalla) {
        File file = null;
        JFileChooser jc = new JFileChooser();
        jc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        jc.setCurrentDirectory(misFuentes);
        int seleccion = jc.showOpenDialog(pantalla);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            file = jc.getSelectedFile();
        }
        return file.getAbsolutePath();
    }

    /**
     * Método que devuelve una o varias rutas escogida por un JFileChooser.
     *
     * @param pantalla Componente padre.
     * @return String con una ruta.
     */
    public File[] seleccionarArchivos(Component pantalla) {
        JOptionPane.showMessageDialog(this, "Elija las fuentes a importar");
        File[] selectedFiles = null;
        JFileChooser jc = new JFileChooser();
        jc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jc.setMultiSelectionEnabled(true);

        /*for (FileFilter fileFilter : Filtros.fontFilterFileChooser()) {
            jc.addChoosableFileFilter(fileFilter);
        }*/
        for (int i = 1; i > -1; i--) {
            jc.setFileFilter(Filtros.fontFilterFileChooser().get(i));
        }

        int seleccion = jc.showOpenDialog(pantalla);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            selectedFiles = jc.getSelectedFiles();
        }
        return selectedFiles;
    }

    public void coiparArchivos(File[] archivosCopiar) {
        if (dirDestino == null) {
            JOptionPane.showMessageDialog(this, "Elija el directorio donde importar las fuentes");
            String seleccionarDirectorio = seleccionarDirectorio(this);
            dirDestino = new File(seleccionarDirectorio);
        }

        for (File selectedFile : archivosCopiar) {
            try {
                String dirDestinoCadena = dirDestino.getAbsolutePath();
                dirDestinoCadena = dirDestinoCadena + File.separator + selectedFile.getName();
                File dirTemp = new File(dirDestinoCadena);

                Files.copy(selectedFile.toPath(), dirTemp.toPath());

                dirTemp = null;
                actualizarNodos();
            } catch (IOException ex) {
                Logger.getLogger(PruebaVista.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        dirDestino = null;
    }

    public void buscarFuentesLocales(File dirBusqueda) {

        File[] files = dirBusqueda.listFiles();

        //comprueba que files tenga archivos o directorios
        if (files != null) {
            for (File file : files) {

                if (file.isDirectory()) {
                    this.buscarFuentesLocales(file);
                } else {
                    if (file.getName().endsWith(".ttf") || file.getName().endsWith(".otf")) {
                        listaFuentesLocales.add(new LocalFont(file));
                    }                    
                }
            }
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelFondo = new javax.swing.JPanel();
        jScrollPaneTable = new javax.swing.JScrollPane();
        jTableGoogleFonts = new javax.swing.JTable();
        jLabel1TituloPrueba = new javax.swing.JLabel();
        jScrollPaneTextArea = new javax.swing.JScrollPane();
        jTextAreaLorem = new javax.swing.JTextArea();
        jButtonPreview = new javax.swing.JButton();
        jComboBoxStyles = new javax.swing.JComboBox<>();
        jScrollPaneTree = new javax.swing.JScrollPane();
        jTreeUserDir = new javax.swing.JTree();
        jLabelDragDrop = new javax.swing.JLabel();
        jButtonImportar = new javax.swing.JButton();
        jButtonBorrar = new javax.swing.JButton();
        jButtonCrear = new javax.swing.JButton();
        jButtonDescargar = new javax.swing.JButton();
        jButtonVerGoogleFonts = new javax.swing.JButton();
        jButtonVerFuentesLocales = new javax.swing.JButton();
        jButtonMover = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanelFondo.setBackground(new java.awt.Color(255, 255, 255));

        jTableGoogleFonts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        jTableGoogleFonts.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPaneTable.setViewportView(jTableGoogleFonts);

        jLabel1TituloPrueba.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1TituloPrueba.setText("ASDFGHJKL");

        jTextAreaLorem.setColumns(20);
        jTextAreaLorem.setLineWrap(true);
        jTextAreaLorem.setRows(5);
        jTextAreaLorem.setText(" Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam porta congue tortor sit amet suscipit. Sed lobortis ipsum non nisl consequat tempor. Donec cursus fringilla euismod. Mauris convallis tincidunt mauris, sed tristique nisi efficitur vitae. Proin luctus, purus nec ultrices feugiat, mi risus consequat sapien, nec maximus urna turpis et dolor. Proin fermentum magna vitae finibus convallis. Curabitur rhoncus massa libero.");
        jTextAreaLorem.setToolTipText("");
        jTextAreaLorem.setWrapStyleWord(true);
        jScrollPaneTextArea.setViewportView(jTextAreaLorem);

        jButtonPreview.setText("Vista previa");
        jButtonPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPreviewActionPerformed(evt);
            }
        });

        jComboBoxStyles.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jScrollPaneTree.setViewportView(jTreeUserDir);

        jLabelDragDrop.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelDragDrop.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDragDrop.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButtonImportar.setText("Importar");
        jButtonImportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonImportarActionPerformed(evt);
            }
        });

        jButtonBorrar.setText("Borrar");
        jButtonBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBorrarActionPerformed(evt);
            }
        });

        jButtonCrear.setText("Crear");
        jButtonCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCrearActionPerformed(evt);
            }
        });

        jButtonDescargar.setText("Descargar fuente");
        jButtonDescargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDescargarActionPerformed(evt);
            }
        });

        jButtonVerGoogleFonts.setText("Ver google fonts");
        jButtonVerGoogleFonts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVerGoogleFontsActionPerformed(evt);
            }
        });

        jButtonVerFuentesLocales.setText("Ver fuentes locales");
        jButtonVerFuentesLocales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVerFuentesLocalesActionPerformed(evt);
            }
        });

        jButtonMover.setText("Mover");
        jButtonMover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMoverActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFondoLayout = new javax.swing.GroupLayout(jPanelFondo);
        jPanelFondo.setLayout(jPanelFondoLayout);
        jPanelFondoLayout.setHorizontalGroup(
            jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneTextArea)
                    .addGroup(jPanelFondoLayout.createSequentialGroup()
                        .addGroup(jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPaneTree, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1TituloPrueba, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelDragDrop, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonImportar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonBorrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonCrear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonMover, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelFondoLayout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(jButtonPreview)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBoxStyles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonDescargar))
                            .addGroup(jPanelFondoLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelFondoLayout.createSequentialGroup()
                                        .addComponent(jButtonVerGoogleFonts)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButtonVerFuentesLocales))
                                    .addComponent(jScrollPaneTable, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanelFondoLayout.setVerticalGroup(
            jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelFondoLayout.createSequentialGroup()
                        .addComponent(jScrollPaneTable, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonVerFuentesLocales, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButtonVerGoogleFonts, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jPanelFondoLayout.createSequentialGroup()
                        .addComponent(jScrollPaneTree, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonCrear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonMover)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonBorrar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonImportar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelDragDrop, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1TituloPrueba, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPreview)
                    .addComponent(jComboBoxStyles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDescargar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPaneTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelFondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelFondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPreviewActionPerformed

        if (listaFuentesLocales == null) {
            crearFuente(cgf.getListaFuentes().get(jTableGoogleFonts.getSelectedRow()).getFiles().get(jComboBoxStyles.getSelectedItem().toString()));

            jLabel1TituloPrueba.setFont(createFont.deriveFont(24F));
            jTextAreaLorem.setFont(createFont.deriveFont(14F));
        } else {
            jLabel1TituloPrueba.setFont(listaFuentesLocales.get(jTableGoogleFonts.getSelectedRow()).getFont().deriveFont(24F));
            jTextAreaLorem.setFont(listaFuentesLocales.get(jTableGoogleFonts.getSelectedRow()).getFont().deriveFont(14F));
        }


    }//GEN-LAST:event_jButtonPreviewActionPerformed

    private void jButtonBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBorrarActionPerformed
        System.out.println(dirDestino.getAbsolutePath());
        //dirDestino.delete();
        System.out.println(dirDestino.delete());

        dirDestino = null;
        actualizarNodos();
    }//GEN-LAST:event_jButtonBorrarActionPerformed

    private void jButtonCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCrearActionPerformed
        String showInputDialog = JOptionPane.showInputDialog("Escribe el nombre del nuevo directorio");

        File nuevoDirectorio = new File(dirDestino.getAbsolutePath() + File.separator + showInputDialog);
        nuevoDirectorio.mkdir();
        actualizarNodos();
    }//GEN-LAST:event_jButtonCrearActionPerformed

    private void jButtonImportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonImportarActionPerformed

        coiparArchivos(seleccionarArchivos(this));

    }//GEN-LAST:event_jButtonImportarActionPerformed

    private void jButtonDescargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDescargarActionPerformed
        if (dirDestino == null) {
            JOptionPane.showMessageDialog(this, "Elija el directorio donde importar las fuentes");
            String seleccionarDirectorio = seleccionarDirectorio(this);
            dirDestino = new File(seleccionarDirectorio);
        }

        String url = cgf.getListaFuentes().get(jTableGoogleFonts.getSelectedRow()).getFiles().get(jComboBoxStyles.getSelectedItem().toString());

        GoogleFont fontDescargar = cgf.getListaFuentes().get(jTableGoogleFonts.getSelectedRow());

        DescargaRecursos.descargarArchivo(url, fontDescargar.getFamily() + "-" + jComboBoxStyles.getSelectedItem().toString() + ".ttf", dirDestino.getAbsolutePath());

        dirDestino = null;
        actualizarNodos();

    }//GEN-LAST:event_jButtonDescargarActionPerformed

    private void jButtonVerGoogleFontsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVerGoogleFontsActionPerformed
        listaFuentesLocales = null;
        jComboBoxStyles.setVisible(true);
        jButtonDescargar.setVisible(true);
        rellenarTablaGoogleFonts();
    }//GEN-LAST:event_jButtonVerGoogleFontsActionPerformed

    private void jButtonVerFuentesLocalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVerFuentesLocalesActionPerformed

        jComboBoxStyles.setVisible(false);
        jButtonDescargar.setVisible(false);

        listaFuentesLocales = new ArrayList<>();

        if (dirDestino == null) {
            JOptionPane.showMessageDialog(this, "Elija el directorio donde importar las fuentes");
            String seleccionarDirectorio = seleccionarDirectorio(this);
            dirDestino = new File(seleccionarDirectorio);
        }

        buscarFuentesLocales(dirDestino);

        rellenarTablaLocalFonts();

    }//GEN-LAST:event_jButtonVerFuentesLocalesActionPerformed

    private void jButtonMoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMoverActionPerformed

        if (dirDestino == null) {
            JOptionPane.showMessageDialog(this, "Elija el directorio a mover");
            String seleccionarDirectorio = seleccionarDirectorio(this);
            dirDestino = new File(seleccionarDirectorio);
        }

        try {
            Files.move(dirDestino.toPath(), new File(seleccionarDirectorio(this) + File.separator + dirDestino.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(PruebaVista.class.getName()).log(Level.SEVERE, null, ex);
        }

        dirDestino = null;
        actualizarNodos();

    }//GEN-LAST:event_jButtonMoverActionPerformed

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
            java.util.logging.Logger.getLogger(PruebaVista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PruebaVista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PruebaVista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PruebaVista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PruebaVista().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBorrar;
    private javax.swing.JButton jButtonCrear;
    private javax.swing.JButton jButtonDescargar;
    private javax.swing.JButton jButtonImportar;
    private javax.swing.JButton jButtonMover;
    private javax.swing.JButton jButtonPreview;
    private javax.swing.JButton jButtonVerFuentesLocales;
    private javax.swing.JButton jButtonVerGoogleFonts;
    private javax.swing.JComboBox<String> jComboBoxStyles;
    private javax.swing.JLabel jLabel1TituloPrueba;
    private javax.swing.JLabel jLabelDragDrop;
    private javax.swing.JPanel jPanelFondo;
    private javax.swing.JScrollPane jScrollPaneTable;
    private javax.swing.JScrollPane jScrollPaneTextArea;
    private javax.swing.JScrollPane jScrollPaneTree;
    private javax.swing.JTable jTableGoogleFonts;
    private javax.swing.JTextArea jTextAreaLorem;
    private javax.swing.JTree jTreeUserDir;
    // End of variables declaration//GEN-END:variables
}
