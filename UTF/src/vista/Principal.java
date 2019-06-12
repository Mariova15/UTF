/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import controlador.ControladorGestorFuentes;
import controlador.GestorFicherosObjetos;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import static java.awt.image.ImageObserver.ERROR;
import static java.awt.image.ImageObserver.WIDTH;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import mdlaf.MaterialLookAndFeel;
import mdlaf.animation.MaterialUIMovement;
import mdlaf.utils.MaterialColors;
import modelo.GoogleFont;
import modelo.LocalFont;
import utils.DescargaRecursos;
import utils.Filtros;
import vista.tablemodels.TableModelGoogleFonts;
import vista.tablemodels.TableModelLocalFonts;

/**
 *
 * @author Mario
 */
public class Principal extends javax.swing.JFrame {

    private static final String RUTA_LOGO = "/img/logo.png";

    private GestorFicherosObjetos gfo;

    private ControladorGestorFuentes cgf;
    private Font createFont = null;

    private File misFuentes;
    private DefaultMutableTreeNode root;
    private DefaultTreeModel treeModel;

    private File dirDestino;

    private List<LocalFont> listaFuentesLocales;
    private List<GoogleFont> listaFuentesGoogle;

    /**
     * Creates new form PruebaVista
     */
    public Principal() {
        initComponents();
        this.setLocationRelativeTo(null);
        setIconImage(new ImageIcon(getClass().getResource(RUTA_LOGO)).getImage());
        setTitle("Use that font");
        //cargarLook();
        gfo = new GestorFicherosObjetos();
        //Cambiar String file por System.getProperty("user.home")+File.separator+"AppData"+File.separator+"UTF"+File.separator+"Datos"
        if (new File("Datos" + File.separator + "configuracion.conf").exists()) {
            try {
                gfo.abrirFicheroLecturaObjetos(new File("Datos" + File.separator + "configuracion.conf").getAbsolutePath());
                cgf = gfo.leerUnRegistroFicheroObjetos();
                gfo.cerrarFicherosLecturaObjetos();
            } catch (IOException ex) {
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            cgf = new ControladorGestorFuentes(new File("Mis fuentes"), new File("Backup"), new File("Datos"));
            gfo.abrirFicheroEscrituraObjetos(new File("Datos" + File.separator + "configuracion.conf").getAbsolutePath());
            gfo.grabarObjetoFicheroObjetos(cgf);
            gfo.cerrarFicherosEscrituraObjetos();
        }

        cgf.descargaJsonFuentes();
        jButtonActivar.setVisible(false);
        jComboBoxStyles.setVisible(false);
        jComboBoxFiltro.setModel(new DefaultComboBoxModel<>(cgf.getListaTiposGoogle().toArray(new String[cgf.getListaTiposGoogle().size()])));

        jComboBoxFiltro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jComboBoxFiltro.getSelectedItem().toString().equals("Todos los estilos")) {
                    listaFuentesGoogle = cgf.getListaFuentesGoogle();
                } else {
                    listaFuentesGoogle = cgf.filtrarFuentesGoogles(jComboBoxFiltro.getSelectedItem().toString());
                }
                rellenarTablaGoogleFonts();
            }
        });

        listaFuentesGoogle = cgf.getListaFuentesGoogle();
        rellenarTablaGoogleFonts();
        dragDrop();

        jTableGoogleFonts.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                jComboBoxStyles.setModel(new DefaultComboBoxModel(listaFuentesGoogle.get(jTableGoogleFonts.getSelectedRow()).getFiles().keySet().toArray()));
                if (listaFuentesLocales != null) {
                    if (cgf.comprobarFuenteInstalada(listaFuentesLocales.get(jTableGoogleFonts.getSelectedRow()).getFontFile(), false)) {
                        jButtonDescargar.setText("Desinstalar fuente");
                    } else {
                        jButtonDescargar.setText("Instalar fuente");
                    }
                    if (cgf.comprobarFuenteActivada(listaFuentesLocales.get(jTableGoogleFonts.getSelectedRow()).getFontFile(), false)) {
                        jButtonActivar.setText("Desactivar");
                    } else {
                        jButtonActivar.setText("Activar");
                    }
                } else {
                    jComboBoxStyles.setVisible(true);
                }
            }
        });

        misFuentes = cgf.getMisFuentes();
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
                }
                if (SwingUtilities.isRightMouseButton(e) && listaFuentesLocales == null) {
                    jPopupMenuBotonDerecho.show(e.getComponent(), e.getX(), e.getY());
                }

            }
        });

        if (!new File("Datos" + File.separator + "StoredCredential").exists()) {
            visibilidadAccionesGDrive(false);
        } else {
            visibilidadAccionesGDrive(true);
        }

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                super.windowClosing(we);
                gfo.abrirFicheroEscrituraObjetos(new File("Datos" + File.separator + "configuracion.conf").getAbsolutePath());
                gfo.grabarObjetoFicheroObjetos(cgf);
                gfo.cerrarFicherosEscrituraObjetos();

            }

        });

    }

    private void cargarLook() {
        try {
            UIManager.setLookAndFeel(new MaterialLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
        SwingUtilities.updateComponentTreeUI(this);
        pack();

        jButtonPreview.setForeground(MaterialColors.LIGHT_BLUE_A200);
        jButtonPreview.setBackground(MaterialColors.WHITE);

        /*buttonSingup.setForeground(MaterialColors.LIGHT_BLUE_A200);
        buttonSingup.setBackground(MaterialColors.WHITE);
        buttonLogin.setBackground(MaterialColors.LIGHT_BLUE_A200);

        MaterialUIMovement.add(buttonSingup, MaterialColors.LIGHT_BLUE_100, WIDTH, ERROR);
        MaterialUIMovement.add(buttonLogin, MaterialColors.BLUE_200, 5, 100 / 30);*/
    }

    private void rellenarTablaGoogleFonts() {
        jTableGoogleFonts.setModel(new TableModelGoogleFonts(listaFuentesGoogle));
    }

    private void rellenarTablaLocalFonts() {
        jTableGoogleFonts.setModel(new TableModelLocalFonts(listaFuentesLocales));
    }

    private void crearFuente(String dirGoogleFont) {
        try {
            createFont = Font.createFont(Font.TRUETYPE_FONT, new URL(dirGoogleFont).openStream());
        } catch (FontFormatException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void visibilidadAccionesGDrive(boolean visible) {
        if (visible) {
            jMenuItemLogin.setVisible(false);
        } else {
            jMenuItemLogin.setVisible(true);
        }
        jMenuItemBuackupGDSubir.setVisible(visible);
        jMenuItemBuackupGDDescargar.setVisible(visible);
        jMenuItemCerrarSesion.setVisible(visible);
        jMenuItemCambiar.setVisible(visible);
        jMenuItemGestionar.setVisible(visible);
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
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
                List<File> archivosCopiar = new ArrayList<>();
                for (File file : transferData) {
                    if (file.getName().endsWith(".ttf") || file.getName().endsWith(".otf")) {
                        archivosCopiar.add(file);
                    }
                }
                Principal.this.coiparArchivos(archivosCopiar.toArray(new File[archivosCopiar.size()]));

                return true;
            }
        };
        jTreeUserDir.setTransferHandler(th);
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
            if (jc.getSelectedFile().exists()) {
                file = jc.getSelectedFile();
            } else {
                file = jc.getCurrentDirectory();
            }
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
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (listaFuentesLocales != null) {
            cargarListaFuentesLocales(true);
        }
    }

    public void cargarListaFuentesLocales(boolean importar) {
        if (importar) {
            listaFuentesLocales = cgf.generarListaFuentesLocales(dirDestino);
        } else {
            listaFuentesLocales = cgf.generarListaFuentesLocales(dirDestino.getParentFile());
        }
        if (cgf.comprobarLimiteFuentes(listaFuentesLocales)) {
            rellenarTablaLocalFonts();
        } else {
            JOptionPane.showMessageDialog(this, "Limite es" + cgf.getLimiteFuentes() + " y estas intentando cargar " + listaFuentesLocales.size());
            listaFuentesLocales = null;
        }
        dirDestino = null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenuBotonDerecho = new javax.swing.JPopupMenu();
        jMenuItemCrear = new javax.swing.JMenuItem();
        jMenuItemBorrar = new javax.swing.JMenuItem();
        jMenuItemMover = new javax.swing.JMenuItem();
        jMenuItemRenombrar = new javax.swing.JMenuItem();
        jMenuItemImportar = new javax.swing.JMenuItem();
        jPanelFondo = new javax.swing.JPanel();
        jScrollPaneTable = new javax.swing.JScrollPane();
        jTableGoogleFonts = new javax.swing.JTable();
        jLabelTituloPrueba = new javax.swing.JLabel();
        jScrollPaneTextArea = new javax.swing.JScrollPane();
        jTextAreaLorem = new javax.swing.JTextArea();
        jButtonPreview = new javax.swing.JButton();
        jComboBoxStyles = new javax.swing.JComboBox<>();
        jScrollPaneTree = new javax.swing.JScrollPane();
        jTreeUserDir = new javax.swing.JTree();
        jButtonDescargar = new javax.swing.JButton();
        jButtonVerGoogleFonts = new javax.swing.JButton();
        jButtonVerFuentesLocales = new javax.swing.JButton();
        jComboBoxFiltro = new javax.swing.JComboBox<>();
        jButtonActivar = new javax.swing.JButton();
        jMenuBar = new javax.swing.JMenuBar();
        jMenuConf = new javax.swing.JMenu();
        jMenuGD = new javax.swing.JMenu();
        jMenuItemLogin = new javax.swing.JMenuItem();
        jMenuItemGestionar = new javax.swing.JMenuItem();
        jMenuItemCerrarSesion = new javax.swing.JMenuItem();
        jMenuItemCambiar = new javax.swing.JMenuItem();
        jMenuItemConfApp = new javax.swing.JMenuItem();
        jMenuBackup = new javax.swing.JMenu();
        jMenuItemGestionBackup = new javax.swing.JMenuItem();
        jMenuItemBackupCrear = new javax.swing.JMenuItem();
        jMenuItemBackupCargar = new javax.swing.JMenuItem();
        jMenuItemBuackupGDSubir = new javax.swing.JMenuItem();
        jMenuItemBuackupGDDescargar = new javax.swing.JMenuItem();

        jMenuItemCrear.setText("Crear");
        jMenuItemCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCrearActionPerformed(evt);
            }
        });
        jPopupMenuBotonDerecho.add(jMenuItemCrear);

        jMenuItemBorrar.setText("Borrar");
        jMenuItemBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBorrarActionPerformed(evt);
            }
        });
        jPopupMenuBotonDerecho.add(jMenuItemBorrar);

        jMenuItemMover.setText("Mover");
        jMenuItemMover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMoverActionPerformed(evt);
            }
        });
        jPopupMenuBotonDerecho.add(jMenuItemMover);

        jMenuItemRenombrar.setText("Renombrar");
        jMenuItemRenombrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRenombrarActionPerformed(evt);
            }
        });
        jPopupMenuBotonDerecho.add(jMenuItemRenombrar);

        jMenuItemImportar.setText("Importar");
        jMenuItemImportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemImportarActionPerformed(evt);
            }
        });
        jPopupMenuBotonDerecho.add(jMenuItemImportar);

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

        jLabelTituloPrueba.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabelTituloPrueba.setText("ASDFGHJKL");

        jTextAreaLorem.setColumns(20);
        jTextAreaLorem.setLineWrap(true);
        jTextAreaLorem.setRows(5);
        jTextAreaLorem.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam porta congue tortor sit amet suscipit. Sed lobortis ipsum non nisl consequat tempor. Donec cursus fringilla euismod. Mauris convallis tincidunt mauris, sed tristique nisi efficitur vitae. Proin luctus, purus nec ultrices feugiat, mi risus consequat sapien, nec maximus urna turpis et dolor. Proin fermentum magna vitae finibus convallis. Curabitur rhoncus massa libero.");
        jTextAreaLorem.setToolTipText("");
        jTextAreaLorem.setWrapStyleWord(true);
        jScrollPaneTextArea.setViewportView(jTextAreaLorem);

        jButtonPreview.setText("Vista previa");
        jButtonPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPreviewActionPerformed(evt);
            }
        });

        jScrollPaneTree.setViewportView(jTreeUserDir);

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

        jButtonActivar.setText("Activar");
        jButtonActivar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonActivarActionPerformed(evt);
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
                        .addGroup(jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelTituloPrueba, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPaneTree, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelFondoLayout.createSequentialGroup()
                                .addComponent(jButtonPreview)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBoxStyles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonActivar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonDescargar))
                            .addGroup(jPanelFondoLayout.createSequentialGroup()
                                .addComponent(jButtonVerGoogleFonts)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonVerFuentesLocales)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBoxFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPaneTable, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanelFondoLayout.setVerticalGroup(
            jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneTree, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPaneTable, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonVerGoogleFonts)
                    .addComponent(jButtonVerFuentesLocales)
                    .addComponent(jComboBoxFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTituloPrueba, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPreview)
                    .addComponent(jComboBoxStyles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDescargar)
                    .addComponent(jButtonActivar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPaneTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addContainerGap())
        );

        jMenuConf.setText("Configuración");

        jMenuGD.setText("Google Drive");

        jMenuItemLogin.setText("Iniciar sesión");
        jMenuItemLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemLoginActionPerformed(evt);
            }
        });
        jMenuGD.add(jMenuItemLogin);

        jMenuItemGestionar.setText("Gestionar");
        jMenuItemGestionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGestionarActionPerformed(evt);
            }
        });
        jMenuGD.add(jMenuItemGestionar);

        jMenuItemCerrarSesion.setText("Cerrar sesion");
        jMenuItemCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCerrarSesionActionPerformed(evt);
            }
        });
        jMenuGD.add(jMenuItemCerrarSesion);

        jMenuItemCambiar.setText("Cambiar cuenta");
        jMenuItemCambiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCambiarActionPerformed(evt);
            }
        });
        jMenuGD.add(jMenuItemCambiar);

        jMenuConf.add(jMenuGD);

        jMenuItemConfApp.setText("Configuración aplicación");
        jMenuItemConfApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemConfAppActionPerformed(evt);
            }
        });
        jMenuConf.add(jMenuItemConfApp);

        jMenuBar.add(jMenuConf);

        jMenuBackup.setText("Backup");

        jMenuItemGestionBackup.setText("Gestionar");
        jMenuItemGestionBackup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGestionBackupActionPerformed(evt);
            }
        });
        jMenuBackup.add(jMenuItemGestionBackup);

        jMenuItemBackupCrear.setText("Crear");
        jMenuItemBackupCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBackupCrearActionPerformed(evt);
            }
        });
        jMenuBackup.add(jMenuItemBackupCrear);

        jMenuItemBackupCargar.setText("Cargar");
        jMenuItemBackupCargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBackupCargarActionPerformed(evt);
            }
        });
        jMenuBackup.add(jMenuItemBackupCargar);

        jMenuItemBuackupGDSubir.setText("Subir a Google Drive");
        jMenuItemBuackupGDSubir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBuackupGDSubirActionPerformed(evt);
            }
        });
        jMenuBackup.add(jMenuItemBuackupGDSubir);

        jMenuItemBuackupGDDescargar.setText("Descarga desde Google Drive");
        jMenuItemBuackupGDDescargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBuackupGDDescargarActionPerformed(evt);
            }
        });
        jMenuBackup.add(jMenuItemBuackupGDDescargar);

        jMenuBar.add(jMenuBackup);

        setJMenuBar(jMenuBar);

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
            /*crearFuente(listaFuentesGoogle.get(jTableGoogleFonts.getSelectedRow()).getFiles().get(jComboBoxStyles.getSelectedItem().toString()));
            jLabelTituloPrueba.setText(createFont.getFamily());
            jLabelTituloPrueba.setFont(createFont.deriveFont(24F));
            jTextAreaLorem.setFont(createFont.deriveFont(14F));*/            
            Loading loading = new Loading(this, true, "Descarga");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    crearFuente(listaFuentesGoogle.get(jTableGoogleFonts.getSelectedRow()).getFiles().get(jComboBoxStyles.getSelectedItem().toString()));
                    jLabelTituloPrueba.setText(createFont.getFamily());
                    jLabelTituloPrueba.setFont(createFont.deriveFont(24F));
                    jTextAreaLorem.setFont(createFont.deriveFont(14F));

                    loading.dispose();
                    //JOptionPane.showMessageDialog(loading.getParent(), "Operación completa");
                }
            }).start();
            loading.setVisible(true);
        } else {
            jLabelTituloPrueba.setText(listaFuentesLocales.get(jTableGoogleFonts.getSelectedRow()).getFont().getFamily());
            jLabelTituloPrueba.setFont(listaFuentesLocales.get(jTableGoogleFonts.getSelectedRow()).getFont().deriveFont(24F));
            jTextAreaLorem.setFont(listaFuentesLocales.get(jTableGoogleFonts.getSelectedRow()).getFont().deriveFont(14F));
        }
    }//GEN-LAST:event_jButtonPreviewActionPerformed

    private void jButtonDescargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDescargarActionPerformed
        if (listaFuentesLocales != null) {
            if (jButtonDescargar.getText().equals("Desinstalar fuente")) {
                cgf.desinstalarFuente(listaFuentesLocales.get(jTableGoogleFonts.getSelectedRow()).getFontFile(), false);
                JOptionPane.showMessageDialog(this, "Fuente desinstalada");
                jButtonDescargar.setText("Instalar fuente");
            } else {
                String mensaje = cgf.instalarFuente(listaFuentesLocales.get(jTableGoogleFonts.getSelectedRow()).getFontFile(),
                        listaFuentesLocales.get(jTableGoogleFonts.getSelectedRow()).getFont().getFontName(), false);

                JOptionPane.showMessageDialog(this, mensaje);
                if (mensaje.equals("Fuente instalada")) {
                    jButtonDescargar.setText("Desinstalar fuente");
                }
            }
        } else {
            if (dirDestino == null) {
                JOptionPane.showMessageDialog(this, "Elija el directorio donde descargar las fuentes");
                String seleccionarDirectorio = seleccionarDirectorio(this);
                dirDestino = new File(seleccionarDirectorio);
            }

            Loading loading = new Loading(this, true, "Descarga");
            new Thread(new Runnable() {
                @Override
                public void run() {

                    String url = listaFuentesGoogle.get(jTableGoogleFonts.getSelectedRow()).getFiles().get(jComboBoxStyles.getSelectedItem().toString());

                    GoogleFont fontDescargar = listaFuentesGoogle.get(jTableGoogleFonts.getSelectedRow());

                    DescargaRecursos.descargarArchivo(url, fontDescargar.getFamily() + "-" + jComboBoxStyles.getSelectedItem().toString() + ".ttf", dirDestino.getAbsolutePath());

                    dirDestino = null;
                    jComboBoxStyles.setVisible(false);
                    actualizarNodos();

                    loading.dispose();
                    JOptionPane.showMessageDialog(loading.getParent(), "Operación completa");
                }
            }).start();
            loading.setVisible(true);
        }

    }//GEN-LAST:event_jButtonDescargarActionPerformed

    private void jButtonVerGoogleFontsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVerGoogleFontsActionPerformed
        listaFuentesLocales = null;
        jComboBoxFiltro.setVisible(true);
        jButtonActivar.setVisible(false);
        jLabelTituloPrueba.setText("ASDFGHJKL");
        jButtonDescargar.setText("Descargar fuente");
        jComboBoxStyles.setVisible(true);
        jButtonDescargar.setVisible(true);
        rellenarTablaGoogleFonts();
    }//GEN-LAST:event_jButtonVerGoogleFontsActionPerformed

    private void jButtonVerFuentesLocalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVerFuentesLocalesActionPerformed

        if (dirDestino == null) {
            JOptionPane.showMessageDialog(this, "Elija el directorio a partir del que buscar las fuentes");
            String seleccionarDirectorio = seleccionarDirectorio(this);
            dirDestino = new File(seleccionarDirectorio);
        }
        if (dirDestino.isDirectory()) {
            listaFuentesLocales = cgf.generarListaFuentesLocales(dirDestino);
            if (cgf.comprobarLimiteFuentes(listaFuentesLocales)) {
                jComboBoxFiltro.setVisible(false);
                jComboBoxStyles.setVisible(false);
                if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
                    jButtonActivar.setText("Activar");
                    jButtonActivar.setVisible(true);
                }
                jLabelTituloPrueba.setText("ASDFGHJKL");
                jButtonDescargar.setText("Instalar fuente");
                rellenarTablaLocalFonts();
            } else {
                JOptionPane.showMessageDialog(this, "Limite es" + cgf.getLimiteFuentes() + " y estas intentando cargar " + listaFuentesLocales.size());
                listaFuentesLocales = null;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Tiene que escoger un proyecto");
        }
        dirDestino = null;

    }//GEN-LAST:event_jButtonVerFuentesLocalesActionPerformed

    private void jMenuItemConfAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConfAppActionPerformed
        Configuracion configuracion = new Configuracion(this, true, cgf);
        configuracion.setVisible(true);
        misFuentes = cgf.getMisFuentes();
        actualizarNodos();
    }//GEN-LAST:event_jMenuItemConfAppActionPerformed

    private void jMenuItemBackupCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBackupCrearActionPerformed
        if (misFuentes.listFiles().length > 0) {
            cgf.crearBackup();
            JOptionPane.showMessageDialog(this, "Backup creado");
        } else {
            JOptionPane.showMessageDialog(this, "No tiene proyectos creados");
        }
    }//GEN-LAST:event_jMenuItemBackupCrearActionPerformed

    private void jMenuItemBackupCargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBackupCargarActionPerformed
        JOptionPane.showMessageDialog(this, "Elija la copia");
        File selectedFiles = null;
        JFileChooser jc = new JFileChooser();
        jc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jc.setCurrentDirectory(cgf.getBackup());
        jc.setFileFilter(Filtros.crearFiltro("Archivos zip", ".zip"));

        int seleccion = jc.showOpenDialog(this);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            selectedFiles = jc.getSelectedFile();
        }

        cgf.cargarBackup(selectedFiles);
        actualizarNodos();
        JOptionPane.showMessageDialog(this, "Backup cargado");
    }//GEN-LAST:event_jMenuItemBackupCargarActionPerformed

    private void jMenuItemLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLoginActionPerformed
        cgf.iniciarGoogleDrive();
        visibilidadAccionesGDrive(true);
        JOptionPane.showMessageDialog(this, "Sesión iniciada");
    }//GEN-LAST:event_jMenuItemLoginActionPerformed

    private void jMenuItemCambiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCambiarActionPerformed
        cgf.cambiarCuentaGoogleDrive();
        JOptionPane.showMessageDialog(this, "Cuenta cambiada");
    }//GEN-LAST:event_jMenuItemCambiarActionPerformed

    private void jMenuItemBuackupGDSubirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBuackupGDSubirActionPerformed
        Loading loading = new Loading(this, true, "Subida");
        new Thread(new Runnable() {
            @Override
            public void run() {
                cgf.subirBackupGoogleDrive();
                loading.dispose();
                JOptionPane.showMessageDialog(loading.getParent(), "Operación completa");
            }
        }).start();
        loading.setVisible(true);
    }//GEN-LAST:event_jMenuItemBuackupGDSubirActionPerformed

    private void jMenuItemBuackupGDDescargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBuackupGDDescargarActionPerformed
        Loading loading = new Loading(this, true, "Descarga");
        new Thread(new Runnable() {
            @Override
            public void run() {
                cgf.descargaBackupGoogleDrive();
                loading.dispose();
                JOptionPane.showMessageDialog(loading.getParent(), "Operación completa");
            }
        }).start();
        loading.setVisible(true);
    }//GEN-LAST:event_jMenuItemBuackupGDDescargarActionPerformed

    private void jMenuItemBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBorrarActionPerformed
        if (dirDestino != null && !dirDestino.equals(misFuentes)) {
            if (cgf.comprobarAccion(dirDestino)) {
                if (dirDestino.isDirectory() && dirDestino.listFiles().length == 0) {
                    //dirDestino.delete();
                    if (!dirDestino.delete()) {
                        JOptionPane.showMessageDialog(this, "Archivo en uso");
                    }
                } else {
                    if (dirDestino.listFiles() != null) {
                        int showConfirmDialog = JOptionPane.showConfirmDialog(this, "El directorio no esta vacio ¿quieres borrarlo al completo?");
                        if (showConfirmDialog == 0) {
                            cgf.borrarDirectorio(dirDestino);
                        }
                    } else {
                        //dirDestino.delete();
                        if (!dirDestino.delete()) {
                            JOptionPane.showMessageDialog(this, "Archivo en uso");
                        }
                    }
                }
                if (listaFuentesLocales != null) {
                    cargarListaFuentesLocales(false);
                }
                dirDestino = null;
                actualizarNodos();
            } else {
                JOptionPane.showMessageDialog(this, "No se puede borrar por fuente instalada");
            }
        } else if (dirDestino.equals(misFuentes)) {
            JOptionPane.showMessageDialog(this, "Seleccione un directorio que no sea mis fuentes");
        } else {
            JOptionPane.showMessageDialog(this, "Antes seleccione un directorio");
        }
    }//GEN-LAST:event_jMenuItemBorrarActionPerformed

    private void jMenuItemCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCrearActionPerformed
        String nombreProyecto = JOptionPane.showInputDialog("Escribe el nombre del nuevo directorio");
        if (nombreProyecto != null) {
            File nuevoDirectorio = new File(cgf.getMisFuentes().getAbsolutePath() + File.separator + nombreProyecto);
            nuevoDirectorio.mkdir();
            actualizarNodos();
        }
    }//GEN-LAST:event_jMenuItemCrearActionPerformed

    private void jMenuItemMoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMoverActionPerformed
        if (dirDestino != null && !dirDestino.equals(misFuentes)) {
            if (cgf.comprobarAccion(dirDestino)) {
                try {
                    Files.move(dirDestino.toPath(), new File(seleccionarDirectorio(this) + File.separator + dirDestino.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (listaFuentesLocales != null) {
                    cargarListaFuentesLocales(false);
                }
                dirDestino = null;
                actualizarNodos();
            } else {
                JOptionPane.showMessageDialog(this, "No se puede mover por fuente instalada");
            }
        } else if (dirDestino.equals(misFuentes)) {
            JOptionPane.showMessageDialog(this, "Seleccione un directorio que no sea mis fuentes");
        } else {
            JOptionPane.showMessageDialog(this, "Antes seleccione un directorio");
        }
    }//GEN-LAST:event_jMenuItemMoverActionPerformed

    private void jMenuItemRenombrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRenombrarActionPerformed
        if (dirDestino != null && !dirDestino.equals(misFuentes)) {
            if (cgf.comprobarAccion(dirDestino)) {
                if (dirDestino.isDirectory()) {
                    String nuevoNombre = JOptionPane.showInputDialog("Escribe el nombre del nuevo directorio");
                    if (nuevoNombre != null) {
                        dirDestino.renameTo(new File(dirDestino.getParent() + File.separator + nuevoNombre));
                    }
                    if (listaFuentesLocales != null) {
                        cargarListaFuentesLocales(false);
                    }
                    dirDestino = null;
                    actualizarNodos();
                } else {
                    JOptionPane.showMessageDialog(this, "Seleccione un proyecto");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se puede renombrar por fuente instalada");
            }
        } else if (dirDestino.equals(misFuentes)) {
            JOptionPane.showMessageDialog(this, "Seleccione un directorio que no sea mis fuentes");
        } else {
            JOptionPane.showMessageDialog(this, "Antes seleccione un directorio");
        }
    }//GEN-LAST:event_jMenuItemRenombrarActionPerformed

    private void jMenuItemImportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemImportarActionPerformed
        coiparArchivos(seleccionarArchivos(this));
        JOptionPane.showMessageDialog(this, "Fuentesimportada");
    }//GEN-LAST:event_jMenuItemImportarActionPerformed

    private void jMenuItemCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCerrarSesionActionPerformed
        cgf.cerrarGoogleDrive();
        visibilidadAccionesGDrive(false);
        JOptionPane.showMessageDialog(this, "Sesión cerrada");
    }//GEN-LAST:event_jMenuItemCerrarSesionActionPerformed

    private void jMenuItemGestionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGestionarActionPerformed
        GestionGD gestionGD = new GestionGD(this, true, cgf, false);
        gestionGD.setVisible(true);
    }//GEN-LAST:event_jMenuItemGestionarActionPerformed

    private void jMenuItemGestionBackupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGestionBackupActionPerformed
        GestionGD gestionGD = new GestionGD(this, true, cgf, true);
        gestionGD.setVisible(true);
    }//GEN-LAST:event_jMenuItemGestionBackupActionPerformed

    private void jButtonActivarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonActivarActionPerformed
        if (jButtonActivar.getText().equals("Desactivar")) {
            cgf.desinstalarFuente(listaFuentesLocales.get(jTableGoogleFonts.getSelectedRow()).getFontFile(), true);
            JOptionPane.showMessageDialog(this, "Fuente desactivada");
            jButtonActivar.setText("Activar");
        } else {
            String mensaje = cgf.instalarFuente(listaFuentesLocales.get(jTableGoogleFonts.getSelectedRow()).getFontFile(),
                    listaFuentesLocales.get(jTableGoogleFonts.getSelectedRow()).getFont().getFontName(), true);
            JOptionPane.showMessageDialog(this, mensaje);
            if (mensaje.equals("Fuente activada")) {
                jButtonActivar.setText("Desactivar");
            }
        }
    }//GEN-LAST:event_jButtonActivarActionPerformed

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
            java.util.logging.Logger.getLogger(Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonActivar;
    private javax.swing.JButton jButtonDescargar;
    private javax.swing.JButton jButtonPreview;
    private javax.swing.JButton jButtonVerFuentesLocales;
    private javax.swing.JButton jButtonVerGoogleFonts;
    private javax.swing.JComboBox<String> jComboBoxFiltro;
    private javax.swing.JComboBox<String> jComboBoxStyles;
    private javax.swing.JLabel jLabelTituloPrueba;
    private javax.swing.JMenu jMenuBackup;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuConf;
    private javax.swing.JMenu jMenuGD;
    private javax.swing.JMenuItem jMenuItemBackupCargar;
    private javax.swing.JMenuItem jMenuItemBackupCrear;
    private javax.swing.JMenuItem jMenuItemBorrar;
    private javax.swing.JMenuItem jMenuItemBuackupGDDescargar;
    private javax.swing.JMenuItem jMenuItemBuackupGDSubir;
    private javax.swing.JMenuItem jMenuItemCambiar;
    private javax.swing.JMenuItem jMenuItemCerrarSesion;
    private javax.swing.JMenuItem jMenuItemConfApp;
    private javax.swing.JMenuItem jMenuItemCrear;
    private javax.swing.JMenuItem jMenuItemGestionBackup;
    private javax.swing.JMenuItem jMenuItemGestionar;
    private javax.swing.JMenuItem jMenuItemImportar;
    private javax.swing.JMenuItem jMenuItemLogin;
    private javax.swing.JMenuItem jMenuItemMover;
    private javax.swing.JMenuItem jMenuItemRenombrar;
    private javax.swing.JPanel jPanelFondo;
    private javax.swing.JPopupMenu jPopupMenuBotonDerecho;
    private javax.swing.JScrollPane jScrollPaneTable;
    private javax.swing.JScrollPane jScrollPaneTextArea;
    private javax.swing.JScrollPane jScrollPaneTree;
    private javax.swing.JTable jTableGoogleFonts;
    private javax.swing.JTextArea jTextAreaLorem;
    private javax.swing.JTree jTreeUserDir;
    // End of variables declaration//GEN-END:variables
}
