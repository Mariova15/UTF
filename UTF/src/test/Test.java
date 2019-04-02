/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import utils.DescargaRecursos;

/**
 *
 * @author Mario
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        /*OperacionesFicheros op = new OperacionesFicheros();
        
        File[] listarDirectoriosNoVacios = op.listarDirectoriosNoVacios("C:\\Windows\\Fonts");
        File[] listarArchivos = op.listarArchivos(listarDirectoriosNoVacios);
        
        for (File listarArchivo : listarArchivos) {
        System.out.println(listarArchivo.getAbsolutePath());
        }
        
        FileSystem system = FileSystems.getDefault();
        
        File dir = new File("C:\\Windows\\Fonts\\AGaramondPro-Bold.otf");
        
        File font = new File("D:\\Fonts\\Font folio\\Western Fonts\\Adobe Garamond Pro\\AGaramondPro-Bold.otf");
        
        Path original = system.getPath(font.getAbsolutePath());
        
        Path pathDestino =  system.getPath(dir.getAbsolutePath() + File.pathSeparator + font.getName());
        
        System.out.println(font.toPath().toString());
        System.out.println(dir.toPath().toString());
        System.out.println(FileSystems.getDefault().getSeparator());
        System.out.println(File.separator);
        System.out.println(System.getProperty("file.separator"));
        
        Files.copy(font.toPath(), dir.toPath());
        
        File fonts = new File("C:\\Windows\\Fonts");
        
        for (File listFile : fonts.listFiles()) {
        System.out.println(listFile.toString());
        }*/

        //COPIAR
        /*File original = new File("D:\\info.txt");
        File destino = new File("C:\\Users\\info.txt");
        
        original.setExecutable(true, false);
        original.setWritable(true, false);
        destino.setExecutable(true, false);
        destino.setWritable(true, false);
        
        Files.copy(original.toPath(), destino.toPath());*/
        System.out.println(isAdmin());

        /*BufferedInputStream in = new BufferedInputStream(
                new URL("https://fonts.googleapis.com/css?family=Roboto").openStream());*/
        //DESCARGA FUENTE
        /*BufferedInputStream in = new BufferedInputStream(
        new URL("https://fonts.gstatic.com/s/roboto/v18/KFOmCnqEu92Fr1Mu4mxP.ttf").openStream());
        new URL("http://themes.googleusercontent.com/static/fonts/antic/v4/hEa8XCNM7tXGzD0Uk0AipA.ttf").openStream());
        
        FileOutputStream fileOutputStream = new FileOutputStream("roboto.ttf");
        
        byte dataBuffer[] = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
        fileOutputStream.write(dataBuffer, 0, bytesRead);
        }*/
        //WebfontsRequestInitializer init = new WebfontsRequestInitializer("AIzaSyB6PLrsPXC9TteULArPKMtaBlirw60pqZ0");
        byte[] response = null;

        String archivodescargar = "http://fonts.gstatic.com/s/abeezee/v12/esDR31xSG-6AGleN6tKukbcHCpE.ttf";
        String googlefontsJson = "https://www.googleapis.com/webfonts/v1/webfonts?key=AIzaSyB6PLrsPXC9TteULArPKMtaBlirw60pqZ0";

        /*InputStream in = new BufferedInputStream(
        new URL(archivodescargar).openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        byte[] buf = new byte[1024];
        int n = 0;
        
        while (-1 != (n = in.read(buf))) {
        out.write(buf, 0, n);
        }
        
        out.close();
        in.close();
        response = out.toByteArray();
        
        FileOutputStream fos = new FileOutputStream("ABeeZee-Regular.ttf");
        fos.write(response);*/
        //DescargaRecursos.descargarArchivo(googlefontsJson, "pruebaMetodo.json", "nada");
        
        //DescargaRecursos.descargarArchivo(googlefontsJson, "pruebaMetodo.json", "nada");
        
        JsonReader jsonReader;
        JsonObject pruebaFonts = null;
        try {
            InputStream fis = new FileInputStream("googleFonts.json");
            jsonReader = Json.createReader(fis);
            pruebaFonts = jsonReader.readObject();
            jsonReader.close();
            fis.close();
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(pruebaFonts.getJsonString("kind").getString());

        JsonArray fuentes = pruebaFonts.getJsonArray("items");

        JsonObject fuente = (JsonObject) fuentes.get(0);

        System.out.println(fuente.getJsonString("kind").getString());
        System.out.println(fuente.getJsonString("family").getString());
        System.out.println(fuente.getJsonString("category").getString());

        JsonArray variants = fuente.getJsonArray("variants");
        for (JsonValue variant : variants) {
            System.out.println(variant.toString());
        }

        JsonArray subsets = fuente.getJsonArray("subsets");
        for (JsonValue subset : subsets) {
            System.out.println(subset.toString());
        }

        System.out.println(fuente.getJsonString("version"));
        System.out.println(fuente.getJsonString("lastModified"));

        JsonObject files = fuente.getJsonObject("files");
        for (int i = 0; i < variants.size(); i++) {
            System.out.println(files.getString(variants.getString(i)));
            
        }

    }

    public static boolean isAdmin() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe");
            Process process = processBuilder.start();
            PrintStream printStream = new PrintStream(process.getOutputStream(), true);
            Scanner scanner = new Scanner(process.getInputStream());
            printStream.println("@echo off");
            printStream.println(">nul 2>&1 \"%SYSTEMROOT%\\system32\\cacls.exe\" \"%SYSTEMROOT%\\system32\\config\\system\"");
            printStream.println("echo %errorlevel%");

            boolean printedErrorlevel = false;
            while (true) {
                String nextLine = scanner.nextLine();
                if (printedErrorlevel) {
                    int errorlevel = Integer.parseInt(nextLine);
                    return errorlevel == 0;
                } else if (nextLine.equals("echo %errorlevel%")) {
                    printedErrorlevel = true;
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

}
