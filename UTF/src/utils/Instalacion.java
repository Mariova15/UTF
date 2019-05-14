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

    public static FuenteInstalada instalarFuente(File installDir, File font, String nombreFuente) {

        String valorClave = null;

        String key = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Fonts";
        String name = nombreFuente;
        String valor = font.getName();

        try {

            Files.copy(font.toPath(), installDir.toPath());

            WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, key, name, valor);
            valorClave = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE, key, name);

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new FuenteInstalada(installDir, valorClave);

    }

    public static void desinstalarFuente(FuenteInstalada font) {
        String key = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Fonts";        
        try {
            font.getDirInstalacion().delete();
            //WinRegistry.deleteKey(WinRegistry.HKEY_LOCAL_MACHINE, font.getValorRegistro());
            WinRegistry.deleteValue(WinRegistry.HKEY_LOCAL_MACHINE, key, font.getValorRegistro());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
