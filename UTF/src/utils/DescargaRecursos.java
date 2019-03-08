/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mario
 */
public class DescargaRecursos {

    /**
     * Método que a traves de una url descarga el recurso y lo almacena en
     * disco.
     *
     * @param url del recurso a descargar.
     * @param nombreArchivo nombre del archivo final.
     * @param rutaDestino ruta del archivo final.
     */
    public static void descargarArchivo(String url, String nombreArchivo, String rutaDestino) {
        try {
            byte[] response = null;
            InputStream in = new BufferedInputStream(
                    new URL(url).openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            byte[] buf = new byte[1024];
            int n = 0;

            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }

            out.close();
            in.close();
            response = out.toByteArray();

            FileOutputStream fos = new FileOutputStream(nombreArchivo);
            fos.write(response);
        } catch (IOException ex) {
            Logger.getLogger(DescargaRecursos.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
