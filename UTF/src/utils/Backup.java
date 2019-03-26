/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Mario
 */
public class Backup {

    /**
     * Método que copia todos los archivos y directorios de una ruta.
     *
     * @param rutaCopiar String con el origen de los directorios y archivos a
     * copiar.
     * @param rutaCopiado String con la ruta donde copiar los directorios y
     * archivos.
     */
    public static void copyAll(String rutaCopiar, String rutaCopiado) {
        File fileCopiar = new File(rutaCopiar);
        System.out.println(fileCopiar.getAbsolutePath());
        File fileCopiado = new File(rutaCopiado);

        File[] listFiles = fileCopiar.listFiles();
        File dir;
        for (File listFile : listFiles) {
            if (listFile.isDirectory()) {
                dir = new File(fileCopiado.getAbsolutePath() + File.separator + listFile.getName());
                dir.mkdir();
                copyAll(listFile.getAbsolutePath(), dir.getAbsolutePath());
            } else {
                dir = new File(fileCopiado.getAbsolutePath() + File.separator + listFile.getName());
                try {
                    Files.copy(listFile.toPath(), dir.toPath());
                } catch (IOException ex) {
                    Logger.getLogger(Backup.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Método que comprime los archivos en un zip repetando los directorios.
     * Sacado de https://www.journaldev.com/957/java-zip-file-folder-example#java-zip-folder
     * 
     * @param dir Directorio a comprimir.
     * @param zipDirName Ruta y nombre dle archivo comprimido.
     * @param filesListInDir Lista de archivos a comprimir
     */
    public static void zipDirectory(File dir, String zipDirName, List<String> filesListInDir) {
        try {
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for (String filePath : filesListInDir) {
                //System.out.println("Zipping " + filePath);
                //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
                zos.putNextEntry(ze);
                //read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que lista todos los archivos del directorio proporcionado.
     * Sacado de https://www.journaldev.com/957/java-zip-file-folder-example#java-zip-folder
     * 
     * @param dir Directorio a listar.
     * @param filesListInDir lista a la que añadir directorios.
     * @return List String con todos los archivos de los directorios.
     */
    public static List<String> populateFilesList(File dir, List<String> filesListInDir) {
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    filesListInDir.add(file.getAbsolutePath());
                } else {
                    populateFilesList(file, filesListInDir);
                }
            }
        }
        return filesListInDir;
    }

    /**
     * Método que calcula el tamaño de los archivos de una lista.
     * 
     * @param filesListInDir Lista de archivos de la que calcular el tamaño.
     * @return tamanno Long con el tamaño d ela lista de archivos en BITS.
     */    
    public static Long calcularTamanno(List<String> filesListInDir) {
        Long tamanno = 0L;
        for (String string : filesListInDir) {
            File file = new File(string);
            tamanno += file.length();
        }
        return tamanno;
    }

}
