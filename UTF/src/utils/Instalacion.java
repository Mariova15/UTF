/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.FuenteInstalada;
import utils.WinRegistry;

/**
 *
 * @author Mario
 */
public class Instalacion {

    private static String KEY = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Fonts";

    public static FuenteInstalada instalarFuente(File installDir, File font, String nombreFuente) {

        File dirDestino = new File(installDir.getAbsolutePath() + File.separator + font.getName());

        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {

            try {
                Files.copy(font.toPath(), dirDestino.toPath());
                WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, KEY, nombreFuente, font.getName());
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (System.getProperty("os.name").toLowerCase().startsWith("lin")) {

            try {
                if (font.getName().endsWith(".ttf")) {
                    dirDestino = new File(installDir.getAbsolutePath() + File.separator + "truetype");
                } else if (font.getName().endsWith(".otf")) {
                    dirDestino = new File(installDir.getAbsolutePath() + File.separator + "opentype");
                }
                if (dirDestino.exists()) {
                    dirDestino.mkdir();
                }
                Files.copy(font.toPath(), dirDestino.toPath());
                limpiarCacheFuentesLinux();
            } catch (IOException ex) {
                Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return new FuenteInstalada(dirDestino, nombreFuente);

    }

    public static void desinstalarFuente(FuenteInstalada font) {

        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {

            try {
                if (font.getDirInstalacion().isDirectory()) {
                    for (File fuenteBorrar : font.getDirInstalacion().listFiles()) {
                        fuenteBorrar.delete();
                    }
                }
                font.getDirInstalacion().delete();
                WinRegistry.deleteValue(WinRegistry.HKEY_LOCAL_MACHINE, KEY, font.getValorRegistro());
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (System.getProperty("os.name").toLowerCase().startsWith("lin")) {

            font.getDirInstalacion().delete();
            limpiarCacheFuentesLinux();
        }

    }

    private static void limpiarCacheFuentesLinux() {
        try {
            Runtime.getRuntime().exec("sudo fc-cache -fv").exitValue();
        } catch (IOException ex) {
            Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
