/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import modelo.FuenteInstalada;
import modelo.GoogleFont;
import modelo.LocalFont;
import utils.Backup;
import utils.DescargaRecursos;
import utils.Fecha;
import utils.Instalacion;

/**
 *
 * @author Mario
 */
public class ControladorGestorFuentes implements Serializable {

    /*private static String JSON_GOOGLE_FONTS
            = "https://www.googleapis.com/webfonts/v1/webfonts?key=AIzaSyB6PLrsPXC9TteULArPKMtaBlirw60pqZ0";*/
    private static String JSON_GOOGLE_FONTS
            = "https://www.googleapis.com/webfonts/v1/webfonts?key=AIzaSyBCg_A6NZYHLU-bSlpS92dOIBmurELni_Q";
    private List<GoogleFont> listaFuentes;
    private List<FuenteInstalada> listafuenteInstaladas;

    private File misFuentes, backup, datosApp, dirInstalacion;

    private File[] systemFonts;

    private ContoladorGoogleDrive cgd;

    public ControladorGestorFuentes(File misFuentes, File backup, File datosApp) {
        this.misFuentes = misFuentes;
        if (!misFuentes.exists()) {
            misFuentes.mkdir();
        }
        this.datosApp = datosApp;
        this.backup = backup;
        if (File.separator.equals("\\")) {
            dirInstalacion = new File("C:\\Windows\\Fonts");
        }
        listaFuentes = new ArrayList<>();
        listafuenteInstaladas = new ArrayList<>();

        systemFonts = dirInstalacion.listFiles();

    }

    public List<GoogleFont> getListaFuentes() {
        return listaFuentes;
    }

    public File getMisFuentes() {
        return misFuentes;
    }

    public File getBackup() {
        return backup;
    }

    public File getDatosApp() {
        return datosApp;
    }

    public File getDirInstalacion() {
        return dirInstalacion;
    }

    /**
     * Método que descarga que hace la petición a google para descargar el
     * archivo json con las fuentes y genera una lista con ellas.
     */
    public void descargaJsonFuentes() {
        //APPDATA                
        DescargaRecursos.descargarArchivo(JSON_GOOGLE_FONTS, "GoogleFonts.json", datosApp.getAbsolutePath());
        lecturaJson();
    }

    /**
     * Método que lee el archivo json con las fuentes y las convierte y añade a
     * una lista en forma de objetos GoogleFont.
     */
    private void lecturaJson() {
        JsonReader jsonReader;
        JsonObject jsonFuentes = null;
        try {
            InputStream fis = new FileInputStream(datosApp.getAbsolutePath() + File.separator + "GoogleFonts.json");
            jsonReader = Json.createReader(fis);
            jsonFuentes = jsonReader.readObject();
            jsonReader.close();
            fis.close();
        } catch (IOException ex) {
            Logger.getLogger(ControladorGestorFuentes.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (Iterator<JsonValue> it = jsonFuentes.getJsonArray("items").iterator(); it.hasNext();) {
            JsonObject fuente = (JsonObject) it.next();

            Map<String, String> mapaFuentes = new HashMap<>();
            JsonArray variants = fuente.getJsonArray("variants");
            JsonObject files = fuente.getJsonObject("files");

            for (int i = 0; i < variants.size(); i++) {
                mapaFuentes.put(variants.getString(i), files.getString(variants.getString(i)));
            }

            listaFuentes.add(new GoogleFont(fuente.getJsonString("kind").getString(),
                    fuente.getJsonString("family").getString(), fuente.getJsonString("category").getString(),
                    fuente.getJsonString("version").getString(), mapaFuentes));
        }
    }

    public void verFuentes() {
        for (GoogleFont fuente : listaFuentes) {
            System.out.println(fuente.toString());
        }
    }

    /**
     * Método que genera una lista de fuentes en base al directorio por
     * parámetro.
     *
     * @param raiz directorio en base al que se genera la lista.
     * @return listaFuentesLocales con objetos LocalFont.
     */
    public List<LocalFont> generarListaFuentesLocales(File raiz) {
        List<LocalFont> listaFuentesLocales = new ArrayList<>();

        buscarArchivos(listaFuentesLocales, raiz);

        return listaFuentesLocales;

    }

    /**
     * Método que recorre un directorio y si contiene archivos .ttf u .otf los
     * añade a la lista pasada por parámetro.
     *
     * @param listaFuentesLocales a la que añadir los objetos
     * @param raiz directorio en el que buscar.
     */
    private void buscarArchivos(List<LocalFont> listaFuentesLocales, File raiz) {

        if (raiz.isDirectory()) {
            for (File archivo : raiz.listFiles()) {
                if (archivo.isDirectory()) {
                    buscarArchivos(listaFuentesLocales, archivo);
                } else {
                    if (archivo.getName().endsWith(".ttf") || archivo.getName().endsWith(".otf")) {
                        listaFuentesLocales.add(new LocalFont(archivo));
                    }
                }
            }
        }

    }

    /**
     * Método que crea un archivo zip en el directorio backup con el contenido
     * del directorio misfuentes.
     */
    public void crearBackup() {
        Backup.zipDirectory(misFuentes,
                backup.getAbsolutePath() + File.separator + "UTF-" + Fecha.formatearFecha(new Date().getTime()) + ".zip",
                Backup.populateFilesList(misFuentes, new ArrayList<>()));
    }

    /**
     * Método que descoprime archivo zip pasado por parámetro
     *
     * @param backupCarga
     */
    public void cargarBackup(File backupCarga) {
        //Borrar antes todos los archivos de la carpeta mis fuentes.
        Backup.unzip(backupCarga.getAbsolutePath(), misFuentes.getAbsolutePath());
    }

    public void iniciarGoogleDrive() {
        cgd = new ContoladorGoogleDrive(datosApp, new File(datosApp.getAbsolutePath() + File.separator + "client_secret.json"));
    }

    public void cambiarCuentaGoogleDrive() {
        for (File fileToDelete : datosApp.listFiles()) {
            if (fileToDelete.getName().equals("StoredCredential")) {
                fileToDelete.delete();
            }
        }
        iniciarGoogleDrive();
    }

    public void subirBackupGoogleDrive() {

        List<String> nombresArchivosDrive = new ArrayList<>();
        for (com.google.api.services.drive.model.File fileDrive : cgd.listarArchivosDrive()) {
            nombresArchivosDrive.add(fileDrive.getName());
        }
        for (File backupFile : backup.listFiles()) {
            if (!nombresArchivosDrive.contains(backupFile.getName())) {
                cgd.subidaArchivo(backupFile);
            }
        }
    }

    public void descargaBackupGoogleDrive() {

        List<String> nombresArchivosLocal = new ArrayList<>();

        for (File archivoLocal : backup.listFiles()) {
            nombresArchivosLocal.add(archivoLocal.getName());
        }

        for (com.google.api.services.drive.model.File backupDescargar : cgd.listarArchivosDrive()) {
            if (backupDescargar.getMimeType().equals("file/.zip") && !nombresArchivosLocal.contains(backupDescargar.getName())) {
                cgd.descargaArchivo(backup.getAbsolutePath(), backupDescargar.getId());
            }
        }
    }

    public void instalarFuente(File fuenteInstalar, String nombreFuente) {
        listafuenteInstaladas.add(Instalacion.instalarFuente(dirInstalacion, fuenteInstalar, nombreFuente));
    }

}
