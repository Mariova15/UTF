/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import modelo.FuenteInstalada;
import modelo.MyGdi32;
import modelo.MyUser32;
import utils.WinRegistry;

/**
 *
 * @author Mario
 */
public class Instalacion {

    private static String KEY = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Fonts";
    
    private final static int HWND_BROADCAST = 0xffff;
    private final static int WM_FONTCHANGE = 0x001D;
    
    private static int installFont(File file) {
        int result = MyGdi32.INSTANCE.AddFontResourceA(
                file.getAbsolutePath());

        return result;
    }

    public static boolean removeFont(File file) {
        boolean result = MyGdi32.INSTANCE.RemoveFontResourceA(
                file.getAbsolutePath());

        return result;
    }

    public static long sentMessage() {
        long result = MyUser32.INSTANCE.SendMessageA(
                HWND_BROADCAST,
                WM_FONTCHANGE,
                0, 0);

        return result;
    }

    public static FuenteInstalada instalarFuente(File installDir, File font, String nombreFuente) {

        File dirDestino = new File(installDir.getAbsolutePath() + File.separator + font.getName());

        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {

            try {
                /*Files.copy(font.toPath(), dirDestino.toPath());
                WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, KEY, nombreFuente, font.getName());*/
                WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, KEY, nombreFuente, font.getAbsolutePath());
                installFont(font);
                sentMessage();
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
            }
            /*catch (IOException ex) {
                Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
            }*/

        } else if (System.getProperty("os.name").toLowerCase().startsWith("lin")) {

            try {
                if (font.getName().endsWith(".ttf")) {
                    dirDestino = new File(installDir.getAbsolutePath() + File.separator + "truetype");
                } else if (font.getName().endsWith(".otf")) {
                    dirDestino = new File(installDir.getAbsolutePath() + File.separator + "opentype");
                }
                if (!dirDestino.exists()) {
                    dirDestino.mkdir();
                }
                dirDestino = new File(dirDestino.getAbsolutePath() + File.separator + font.getName());
                Files.copy(font.toPath(), dirDestino.toPath());
                //limpiarCacheFuentesLinux();
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
                /*font.getDirInstalacion().delete();
                WinRegistry.deleteValue(WinRegistry.HKEY_LOCAL_MACHINE, KEY, font.getValorRegistro());*/
                WinRegistry.deleteValue(WinRegistry.HKEY_LOCAL_MACHINE, KEY, font.getValorRegistro());
                removeFont(font.getDirInstalacion());
                sentMessage();
                //limpiarCacheFuentesWindows();
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
            String pass = JOptionPane.showInputDialog("Escriba su contraseña de administrador");
            if (pass != null) {
                String[] cmd = {"/bin/bash", "-c", "echo " + pass + "| sudo -S fc-cache -fv"};
                Runtime.getRuntime().exec(cmd);
            }
            //Runtime.getRuntime().exec("sudo fc-cache -fv");
        } catch (IOException ex) {
            Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void limpiarCacheFuentesWindows() {
        try {
            Process p;
            p = Runtime.getRuntime().exec("cmd /c net stop FontCache");
            pintarCMD(p);
            p = Runtime.getRuntime().exec("cmd /c del /A /F /Q /S %WinDir%\\ServiceProfiles\\LocalService\\AppData\\Local\\FontCache\\*Font*");
            pintarCMD(p);
            p = Runtime.getRuntime().exec("cmd /c del /A /F /Q %WinDir%\\System32\\FNTCACHE.DAT");
            pintarCMD(p);
            p = Runtime.getRuntime().exec("cmd /c net start FontCache");
            pintarCMD(p);
        } catch (IOException ex) {
            Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void pintarCMD(Process p) {
        try {
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
