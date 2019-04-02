/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    public void descargaJsonFuentes() {
        DescargaRecursos.descargarArchivo(JSON_GOOGLE_FONTS, "Fonts.json", "nada");
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
            listaFuentes.add(new GoogleFont(fuente.getJsonString("kind").getString(),
                    fuente.getJsonString("family").getString(), fuente.getJsonString("category").getString()));
        }
    }
    
    public void verFuentes(){
        for (GoogleFont fuente : listaFuentes) {
            System.out.println(fuente.toString());
        }
    }

}
