/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logica;

import java.util.List;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mario
 */
public class OperacionesFicheros {

    private List<File> listaFicheros;

    /**
     * Constructor que inicializa la lista de ficheros.
     */
    public OperacionesFicheros() {
        listaFicheros = new ArrayList<File>();
    }

    /**
     * Método que lista las unidades del S.O.
     *
     * @return File[] listaUnidades con las unidades del S.O.
     */
    public File[] listarUnidades() {
        List<File> raizLinux = new ArrayList<File>();
        File[] listaUnidades = null;
        //LINUX
        if (File.listRoots()[0].getAbsolutePath().equals("/")) {
            //Listando directorio media 
            File unidadesLinux = new File("/media");
            //Listando directorio de usuario
            File[] user = unidadesLinux.listFiles();
            //Listando unidades montadas
            File[] mount = null;
            for (File file : user) {
                if ((mount = file.listFiles()) != null) {
                    for (File file1 : mount) {
                        if (file1.listFiles() != null) {
                            raizLinux.add(file1);
                        }
                    }
                }
            }
            //Listando RAIZ LINUX
            listaUnidades = File.listRoots();
            //Añadiendo RAIZ a coleccion
            raizLinux.addAll(Arrays.asList(listaUnidades));
            Collections.reverse(raizLinux);
            //Conviertiendo colección a array
            listaUnidades = raizLinux.toArray(new File[raizLinux.size()]);
        } else {
            //WINDOWS
            listaUnidades = File.listRoots();
        }
        return listaUnidades;
    }

    /**
     * Método que lista los directorios vacios.
     *
     * @param ruta en la que analizar si existen directorios vacios
     * @return List File listadoDirVacio listado de directorios vacios
     */
    public List<File> listarDirectoriosVacios(String ruta) {
        File[] listadoDirVacio = null;
        File[] listaDirAnalizar = null;
        File dirAnalizar = new File(ruta);

        listaDirAnalizar = dirAnalizar.listFiles();

        if (listaDirAnalizar != null) {
            for (int i = 0; i < listaDirAnalizar.length; i++) {
                if (listaDirAnalizar[i].isDirectory() && listaDirAnalizar[i].length() == 0
                        || listaDirAnalizar[i].isDirectory() && listaDirAnalizar[i].length() == 4096) {
                    listarDirectoriosVacios(listaDirAnalizar[i].getAbsolutePath());
                    //Añade el directorio vacio a la colección
                    listaFicheros.add(listaDirAnalizar[i]);
                }
            }
        }
        return listaFicheros;
    }

    /**
     * Método que lista los directorios que contienen archivos.
     *
     * @param ruta a analizar
     * @return File[] listadoDirNoVacio con los directorios que contienen
     * archivos
     */
    public File[] listarDirectoriosNoVacios(String ruta) {
        File[] listadoDirNoVacio = null;
        File[] listaDirAnalizar = null;
        File dirAnalizar = new File(ruta);

        listaDirAnalizar = dirAnalizar.listFiles();

        if (listaDirAnalizar != null) {
            for (int i = 0; i < listaDirAnalizar.length; i++) {
                if (listaDirAnalizar[i].isDirectory()) {
                    listarDirectoriosNoVacios(listaDirAnalizar[i].getAbsolutePath());
                }
                if (listaDirAnalizar[i].isFile()) {
                    if (!listaFicheros.contains(listaDirAnalizar[i].getParent())) {
                        if (!listaFicheros.contains(listaDirAnalizar[i].getParentFile())) {
                            listaFicheros.add(listaDirAnalizar[i].getParentFile());
                        }
                    }
                }
            }
        }
        listadoDirNoVacio = listaFicheros.toArray(new File[listaFicheros.size()]);
        return listadoDirNoVacio;
    }

    /**
     * Método que lista todos los archivos del sistema.
     *
     * @param listadoDirectorios con directorios llenos.
     * @return File[] Con todos los archivos del sistema.
     */
    public File[] listarArchivos(File[] listadoDirectorios) {
        File[] archivosAFiltrar = listadoDirectorios;
        File dirFiltrar = null;
        List<File> listaArchivosFiltrados = new ArrayList<File>();
        for (int i = 0; i < archivosAFiltrar.length; i++) {
            dirFiltrar = new File(archivosAFiltrar[i].getAbsolutePath());
            for (File file : dirFiltrar.listFiles()) {
                if (file.isFile()) {
                    listaArchivosFiltrados.add(file);
                }
            }
        }
        File[] archivosfiltrados = listaArchivosFiltrados.toArray(new File[listaArchivosFiltrados.size()]);
        return archivosfiltrados;
    }


    /**
     * Método que borra una lista de archivos o directorios.
     *
     * @param listaBorrar Conjunto de archivos o directorios a borrar.
     * @return boolean borrado true en caso de ser borradoo false en caso
     * contrario.
     */
    public boolean BorrarTodos(List<File> listaBorrar) {
        boolean borrado = false;
        List<File> listaAborrar = new ArrayList<File>();
        listaAborrar = listaBorrar;

        for (File file : listaAborrar) {
            if (file.exists()) {
                file.delete();
                borrado = true;
            } else {
                borrado = false;
            }
        }
        return borrado;
    }

    /**
     * Método que borrar un archivo o directorio.
     *
     * @param file archivo o directorio a borrar
     * @return boolean borrado true si ha sido borrado y false si no se ha
     * podido borrar.
     */
    public boolean BorrarUno(File file) {
        boolean borrado;
        if (file.exists()) {
            file.delete();
            borrado = true;
        } else {
            borrado = false;
        }
        return borrado;
    }


    /**
     * Método que lista los archivos por tamaño.
     *
     * @param listaFiltrar Lista con archivos a filtrar.
     * @param tamannoFiltrar Tamaño con el que filtrar los archivos.
     * @return Lista de Files listaTamaño con los archivos que cumplen las
     * condicienes de tamaño.
     */
    public List<File> ListarTamanno(File[] listaFiltrar, long tamannoFiltrar) {
        File[] listaAnalizar = listaFiltrar;
        List<File> listaTamanno = new ArrayList<File>();
        
        for (int i = 0; i < listaAnalizar.length; i++) {
            File[] listFiles = listaAnalizar[i].listFiles();
            for (File listFile : listFiles) {
                if (listFile.length() >= tamannoFiltrar) {
                    listaTamanno.add(listFile);
                }
            }
        }
        return listaTamanno;
    }

    /**
     * Método que filtra archivos a partir de directorios con archivos.
     *
     * @param listadoDirectorios listado de directorios con archivos a filtrar
     * @param filtro criterio para filtrar los archivos
     * @return File[] archivosfiltrados segun el criterio
     */
    public List<File> FiltrarArchivos(File[] listadoDirectorios, FilenameFilter filtro) {
        File dirFiltrar = null;
        File[] archivosAFiltrar = listadoDirectorios;
        List<File> listaArchivosFiltrados = new ArrayList<File>();

        for (int i = 0; i < archivosAFiltrar.length; i++) {
            dirFiltrar = new File(archivosAFiltrar[i].getAbsolutePath());
            for (File file : dirFiltrar.listFiles(filtro)) {
                listaArchivosFiltrados.add(file);
            }
        }
        return listaArchivosFiltrados;
    }

    /**
     * Método que copia archivos a un directorio determinado.
     *
     * @param listaRespaldo Lista de Files de archivos a respaldar.
     * @param rutaDestino String con ruta en la que se copiaran los archivos.
     */
    public void respaldoVarios(List<File> listaRespaldo, String rutaDestino) {
        List<File> listaAborrar = new ArrayList<File>();
        listaAborrar = listaRespaldo;
        FileSystem system = FileSystems.getDefault();
        Path original = null;
        Path pathDestino = null;
        
        for (File file : listaAborrar) {
            if (file.exists()) {
                original = system.getPath(file.getAbsolutePath());
                try {
                    if (File.listRoots()[0].getAbsolutePath().equals("C:\\")) {
                        pathDestino = system.getPath(rutaDestino + "\\" + file.getName());
                    } else {
                        pathDestino = system.getPath(rutaDestino + "/" + file.getName());
                    }
                    File comprobar = pathDestino.toFile();
                    if (!comprobar.exists()) {
                        Files.copy(original, pathDestino);
                    }                    
                } catch (IOException ex) {
                    Logger.getLogger(OperacionesFicheros.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Método que copia el archivo original en la rita de destino.
     * 
     * @param archivoOrigen File con archivo a copiar.
     * @param rutaDestino  String con ruta en la que se copia el archivo.
     */
    public void respaldoUno(File archivoOrigen, String rutaDestino) {
        FileSystem system = FileSystems.getDefault();
        Path pathDestino = null;
        
        if (File.listRoots()[0].getAbsolutePath().equals("C:\\")) {
            pathDestino = system.getPath(rutaDestino + "\\" + archivoOrigen.getName());
        } else {
            pathDestino = system.getPath(rutaDestino + "/" + archivoOrigen.getName());
        }
        if (archivoOrigen.exists()) {
            Path original = system.getPath(archivoOrigen.toString());
            try {
                Files.copy(original, pathDestino);
            } catch (IOException ex) {
                Logger.getLogger(OperacionesFicheros.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * Método que ordena los archivos por su fecha de mod.
     *
     * @param listadoDirectorios Con archivos a filtrar.
     * @return Lista de Files Con directorios ordenados por su fecha de mod.
     */
    public List<File> ordenarFecha(File[] listadoDirectorios) {
        List<File> listaArchivosFiltrados = new ArrayList<File>();
        File[] archivosfiltrados = listarArchivos(listadoDirectorios);

        Arrays.sort(archivosfiltrados, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.compare(f1.lastModified(), f2.lastModified());
            }
        });
        listaArchivosFiltrados.addAll(Arrays.asList(archivosfiltrados));
        return listaArchivosFiltrados;
    }

}
