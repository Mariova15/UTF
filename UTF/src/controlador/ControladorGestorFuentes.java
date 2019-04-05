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
import utils.DescargaRecursos;

/**
 *
 * @author Mario
 */
public class ControladorGestorFuentes {

    private static String JSON_GOOGLE_FONTS
            = "https://www.googleapis.com/webfonts/v1/webfonts?key=AIzaSyB6PLrsPXC9TteULArPKMtaBlirw60pqZ0";
    private List<GoogleFont> listaFuentes;

    public ControladorGestorFuentes() {
        listaFuentes = new ArrayList<>();
    }

    public List<GoogleFont> getListaFuentes() {
        return listaFuentes;
    }

    public void descargaJsonFuentes() {
        //APPDATA
        File destino = new File("");
        //System.out.println(destino.getAbsolutePath());
        DescargaRecursos.descargarArchivo(JSON_GOOGLE_FONTS, "Fonts.json", destino.getAbsolutePath());
        lecturaJson();
    }

    private void lecturaJson() {
        JsonReader jsonReader;
        JsonObject jsonFuentes = null;
        try {
            InputStream fis = new FileInputStream("Fonts.json");
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

    public List<File> generarListaFuentesLocales(File raiz) {
        List<File> listaFuenteLocales = new ArrayList<>();

        buscarArchivos(listaFuenteLocales, raiz);

        return listaFuenteLocales;

    }

    private void buscarArchivos(List<File> listaFuenteLocales, File raiz) {

        if (raiz.isDirectory()) {
            for (File archivo : raiz.listFiles()) {
                if (archivo.isDirectory()) {
                    buscarArchivos(listaFuenteLocales, archivo);
                } else {
                    listaFuenteLocales.add(archivo);
                }
            }
        }

    }

}
