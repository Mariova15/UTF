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

    public static List<FileFilter> fontFilterFileChooser() {

        List<FileFilter> listaFiltros = new ArrayList<>();

        FileFilter ttf = new FileFilter() {
            public String getDescription() {
                return "TrueType Fonts (*.ttf)";
            }

            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return f.getName().toLowerCase().endsWith(".ttf");
                }
            }
        };

        FileFilter otf = new FileFilter() {
            public String getDescription() {
                return "OpenType Fonts (*.otf)";
            }

            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return f.getName().toLowerCase().endsWith(".otf");
                }
            }
        };

        listaFiltros.add(ttf);
        listaFiltros.add(otf);
        
        return listaFiltros;

    }

}
