/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import logica.OperacionesFicheros;
import utils.DescargaRecursos;

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
        
        //https://stackoverflow.com/questions/62289/read-write-to-windows-registry-using-java
        
        //dir.delete();
        
       /* System.out.println(dir.delete());
       
        OperacionesFicheros algo = new OperacionesFicheros();                        
        for (File fuente : algo.listarArchivos(algo.listarDirectoriosNoVacios("C:\\Windows\\Fonts"))) {
            System.out.println(fuente.getName());
        }*/
       
        
        
    }
    
}
