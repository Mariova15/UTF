/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import controlador.OperacionesFicheros;
import utils.DescargaRecursos;
import utils.WinRegistry;

/**
 *
 * @author Mario
 */
public class PruebaReal {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        //String archivodescargar = "http://fonts.gstatic.com/s/lato/v14/S6uyw4BMUTPHvxk.ttf";

        //DescargaRecursos.descargarArchivo(archivodescargar, "lato.ttf", "");
        //File dir = new File("C:\\Windows\\Fonts\\lato.ttf");
        //Copia fichero en directorio Fonts
        File dir = new File("C:\\Windows\\Fonts\\lato.ttf");
        File font = new File("lato.ttf");
        //Files.copy(font.toPath(), dir.toPath());

        //Escribe clave de registro
        /*
        \[HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Fonts\] "Lato (TrueType)"="lato.ttf"
         */
        String name = "Lato (TrueType)";

        String valor = "lato.ttf";

        String key = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Fonts";

        String llaveRegistro = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Fonts";

        try {

            WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, key, name, valor);

            String readString = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE, key, name);
            System.out.println(readString);
            
           //WinRegistry.deleteKey(WinRegistry.HKEY_LOCAL_MACHINE, key);
            
            //WinRegistry.deleteValue(WinRegistry.HKEY_LOCAL_MACHINE, key + "\\" + name, valor);
            //https://stackoverflow.com/questions/62289/read-write-to-windows-registry-using-java
            //dir.delete();
            /* System.out.println(dir.delete());
            OperacionesFicheros algo = new OperacionesFicheros();
            for (File fuente : algo.listarArchivos(algo.listarDirectoriosNoVacios("C:\\Windows\\Fonts"))) {
            System.out.println(fuente.getName());
            }*/
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PruebaReal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(PruebaReal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(PruebaReal.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
