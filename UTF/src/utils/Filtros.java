/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Mario
 */
public class Filtros {

    /**
     * Método que devuelve un FilenameFilter con extensiones de archivos de
     * imágen.
     *
     * @return imgFilter un FilenameFilter con extensiones de archivos de imágen
     */
    public static FilenameFilter fontFilter() {
        FilenameFilter imgFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".ttf") || name.endsWith(".otf");
            }
        };
        return imgFilter;
    }

    /**
     * Método que devuelve una lista de FileFilter para usar en un JFileChooser.
     * 
     * @return listaFiltros
     */
    public static List<FileFilter> fontFilterFileChooser() {

        List<FileFilter> listaFiltros = new ArrayList<>();
        
        listaFiltros.add(crearFiltro("TrueType Fonts (*.ttf)", ".ttf"));
        listaFiltros.add(crearFiltro("OpenType Fonts (*.otf)", ".otf"));
                
        return listaFiltros;

    }

    /**
     * Método que crea un filtro para usar en JFileChooser.
     * 
     * @param descripcion del filtro.
     * @param extension del archivo.
     * @return 
     */
    public static FileFilter crearFiltro(String descripcion, String extension) {

        FileFilter filtro = new FileFilter() {
            public String getDescription() {
                return descripcion;
            }

            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return f.getName().toLowerCase().endsWith(extension);
                }
            }
        };

        return filtro;

    }

}
