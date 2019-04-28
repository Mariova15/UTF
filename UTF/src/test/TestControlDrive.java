/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import controlador.ContoladorGoogleDrive;
import java.io.File;

/**
 *
 * @author Mario
 */
public class TestControlDrive {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ContoladorGoogleDrive cgd = new ContoladorGoogleDrive(new File("drive"), new File(""));

        for (com.google.api.services.drive.model.File file : cgd.listarArchivosDrive()) {
            /*System.out.println(file.getId());
            System.out.println(file.getName());
            System.out.println(file.getKind());
            System.out.println("-----------------------");*/

            System.out.println(file);
            
            if(file.getMimeType().equals("file/.zip")){
                System.out.println("BUENA");
            }
        }

        //cgd.crearDirectorio("backup");
        //1s93eDS_QKIIiplMcgv0RtZGnN4aZT5S4 dirID
        //1dpEV77HyLdgyoThIGqKPiCAmcgCByPqt ALGO        
        //cgd.descargaArchivo(new File("drive").getAbsolutePath(), "1pKRLJV-q58-ZjQRW2gLN8-IZPCDH7Egp");
        //cgd.descargaArchivo(new File("temp").getAbsolutePath(), "1s93eDS_QKIIiplMcgv0RtZGnN4aZT5S4");
        
        
        //cgd.setgDBackupDirID("1s93eDS_QKIIiplMcgv0RtZGnN4aZT5S4");
        /*for (File listFile : new File("backup").listFiles()) {
            cgd.subidaArchivo(listFile);
        }*/

    }

}
