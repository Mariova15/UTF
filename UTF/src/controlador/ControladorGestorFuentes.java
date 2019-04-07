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
import modelo.GoogleFont;
import modelo.LocalFont;
import utils.DescargaRecursos;

/**
 *
 * @author Mario
 */
public class ControladorGestorFuentes implements Serializable {

    private static String JSON_GOOGLE_FONTS
            = "https://www.googleapis.com/webfonts/v1/webfonts?key=AIzaSyB6PLrsPXC9TteULArPKMtaBlirw60pqZ0";
    private List<GoogleFont> listaFuentes;

    private File misFuentes, datosApp;

    private File[] systemFonts;

    public ControladorGestorFuentes(File misFuentes, File datosApp) {
        this.misFuentes = misFuentes;
        if (!misFuentes.exists()) {
            misFuentes.mkdir();
        }
        this.datosApp = datosApp;
        listaFuentes = new ArrayList<>();

        //Buscar directorio fuentes linux y diferenciar los casos con File.separator
        //usar a la hora de instalar las fuente para no instalar una fuente ya existente.
        File dirSystemFonts = new File("C:\\Windows\\Fonts");
        systemFonts = dirSystemFonts.listFiles();

    }

    public List<GoogleFont> getListaFuentes() {
        return listaFuentes;
    }

    public File getMisFuentes() {
        return misFuentes;
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
        
}
