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
import java.util.Arrays;
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

    private static String JSON_GOOGLE_FONTS
            = "https://www.googleapis.com/webfonts/v1/webfonts?key=AIzaSyBCg_A6NZYHLU-bSlpS92dOIBmurELni_Q";
    private List<GoogleFont> listaFuentesGoogle;
    private List<FuenteInstalada> listaFuentesInstaladas;
    private List<FuenteInstalada> listaFuentesActivadas;
    private List<String> listaTiposGoogle;

    private File misFuentes, backup, datosApp, dirInstalacion;

    private File[] systemFonts;

    private GestorGoogleDrive cgd;

    private int limiteFuentes;

    public ControladorGestorFuentes(File misFuentes, File backup, File datosApp) {
        this.misFuentes = misFuentes;
        if (!misFuentes.exists()) {
            misFuentes.mkdir();
        }
        this.datosApp = datosApp;
        if (!datosApp.exists()) {
            datosApp.mkdir();
        }
        this.backup = backup;
        if (!backup.exists()) {
            backup.mkdir();
        }

        limiteFuentes = 10;

        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            dirInstalacion = new File(System.getenv("WINDIR") + File.separator + "Fonts");
            systemFonts = dirInstalacion.listFiles();
        } else if (System.getProperty("os.name").toLowerCase().startsWith("lin")) {

            dirInstalacion = new File(System.getProperty("user.home") + File.separator + ".fonts");

            File dirOpentype = new File(dirInstalacion.getAbsolutePath() + File.separator + "opentype");
            File dirTruetype = new File(dirInstalacion.getAbsolutePath() + File.separator + "truetype");

            if (!dirInstalacion.exists()) {
                dirInstalacion.mkdir();
                dirOpentype.mkdir();
                dirTruetype.mkdir();
            } else {
                if (!dirOpentype.exists()) {
                    dirOpentype.mkdir();
                }
                if (!dirTruetype.exists()) {
                    dirTruetype.mkdir();
                }
            }

            List<File> listaTemp = new ArrayList<>();
            listaTemp = Backup.buscarArchivos(dirInstalacion, new ArrayList<>());
            listaTemp.addAll(Backup.buscarArchivos(new File("/usr/share/fonts/truetype"), new ArrayList<>()));
            listaTemp.addAll(Backup.buscarArchivos(new File("/usr/share/fonts/opentype"), new ArrayList<>()));
            systemFonts = listaTemp.toArray(new File[listaTemp.size()]);
        }

        listaFuentesInstaladas = new ArrayList<>();
        listaFuentesActivadas = new ArrayList<>();

    }

    public List<GoogleFont> getListaFuentesGoogle() {
        return listaFuentesGoogle;
    }

    public List<String> getListaTiposGoogle() {
        return listaTiposGoogle;
    }

    public void setMisFuentes(File misFuentes) {
        this.misFuentes = misFuentes;
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

    public int getLimiteFuentes() {
        return limiteFuentes;
    }

    public void setLimiteFuentes(int limiteFuentes) {
        this.limiteFuentes = limiteFuentes;
    }

    /**
     * Método que hace la petición a Google fonts para descargar el archivo json
     * con las fuentes y genera una lista de las mismas.
     */
    public void descargaJsonFuentes() {
        DescargaRecursos.descargarArchivo(JSON_GOOGLE_FONTS, "GoogleFonts.json", datosApp.getAbsolutePath());
        listaFuentesGoogle = new ArrayList<>();
        listaTiposGoogle = new ArrayList<>();
        lecturaJson();
    }

    /**
     * Método que lee el archivo json con las fuentes convirtiendolas y
     * añadiendolas una lista en forma de objetos GoogleFont.
     */
    private void lecturaJson() {
        JsonReader jsonReader;
        JsonObject jsonFuentes = null;
        listaTiposGoogle.add("Todos los estilos");
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

            listaFuentesGoogle.add(new GoogleFont(fuente.getJsonString("kind").getString(),
                    fuente.getJsonString("family").getString(), fuente.getJsonString("category").getString(),
                    fuente.getJsonString("version").getString(), mapaFuentes));

            if (!listaTiposGoogle.contains(fuente.getJsonString("category").getString())) {
                listaTiposGoogle.add(fuente.getJsonString("category").getString());
            }
        }
    }

    /**
     * Método que genera una lista de LocalFont en base al directorio por
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
     * Método que descoprime archivo zip pasado por parámetro borrando antes el
     * contenido del directorio Mis fuentes.
     *
     * @param backupCarga archivo zip a cargar.
     */
    public void cargarBackup(File backupCarga) {
        if (misFuentes.listFiles().length > 0) {
            for (File listFile : misFuentes.listFiles()) {
                if (listFile.isDirectory()) {
                    borrarDirectorio(listFile);
                } else {
                    listFile.delete();
                }
            }
        }
        Backup.unzip(backupCarga.getAbsolutePath(), misFuentes.getAbsolutePath());
    }

    /**
     * Método que inicia sesión en google drive abriendo el navegador y
     * generando un archivo con las credenciales.
     */
    public void iniciarGoogleDrive() {
        cgd = new GestorGoogleDrive(datosApp);
    }

    /**
     * Método que borra el archivo StoredCredential para poder hacer un nuevo
     * login.
     */
    public void cerrarGoogleDrive() {
        for (File fileToDelete : datosApp.listFiles()) {
            if (fileToDelete.getName().equals("StoredCredential")) {
                fileToDelete.delete();
            }
        }
    }

    /**
     * Método que borra el archivo de credenciales y vuelve a llamar al login
     * del usuario de google drive.
     */
    public void cambiarCuentaGoogleDrive() {
        cerrarGoogleDrive();
        iniciarGoogleDrive();
    }

    /**
     * Método que sube los zip dentro del direcotio backup a google drive
     * comprobando si ya existen en Drive.
     */
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

    /**
     * Método que descarga los archivos zip alojados en google drive a la
     * carpeta backup.
     */
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

    /**
     * Método que genera una lista con los archivos de backup en Drive.
     *
     * @return lista con los archivos en drive.
     */
    public List<com.google.api.services.drive.model.File> listarArchivosGoogleDrive() {
        List<com.google.api.services.drive.model.File> listarArchivosDrive = cgd.listarArchivosDrive();
        for (Iterator<com.google.api.services.drive.model.File> iterator = listarArchivosDrive.iterator(); iterator.hasNext();) {
            com.google.api.services.drive.model.File next = iterator.next();
            if (!next.getMimeType().equals("file/.zip")) {
                iterator.remove();
            }
        }
        return listarArchivosDrive;
    }

    /**
     * Método que borra un archivo de Drive por su id.
     *
     * @param fileId id del archivo a borrar.
     */
    public void borrarArchivoDrive(String fileId) {
        cgd.borrarArchivoDrive(fileId);
    }

    /**
     * Método que devuelve una lista de los archivos existentes en el directorio
     * backup.
     *
     * @return lista con los archivos en el directorio backup.
     */
    public List<File> listaArchivosBackup() {
        List<File> listaBackupFiles = new ArrayList<>(Arrays.asList(backup.listFiles()));
        return listaBackupFiles;
    }

    /**
     * Método que instala una fuente en el sistema si no es parte de el.
     *
     * @param fuenteInstalar archivo de fuente.
     * @param nombreFuente
     * @return String con el resultado de la operación.
     */
    public String instalarFuente(File fuenteInstalar, String nombreFuente, boolean activar) {
        if (!comprobarFuenteInstalada(fuenteInstalar, Boolean.TRUE)) {
            if (activar) {
                listaFuentesActivadas.add(Instalacion.instalarFuente(dirInstalacion, fuenteInstalar, nombreFuente, activar));
                return "Fuente activada";
            } else {
                listaFuentesInstaladas.add(Instalacion.instalarFuente(dirInstalacion, fuenteInstalar, nombreFuente, activar));
                return "Fuente instalada";
            }
        } else {
            return "La fuente ya esta instalada en el sistema";
        }
    }

    /**
     * Método que desinstala una fuente.
     *
     * @param fuenteDesinstalar
     */
    public void desinstalarFuente(File fuenteDesinstalar, boolean activar) {
        if (activar) {
            for (Iterator<FuenteInstalada> iterator = listaFuentesActivadas.iterator(); iterator.hasNext();) {
                FuenteInstalada next = iterator.next();
                if (next.getDirInstalacion().getName().equals(fuenteDesinstalar.getName())) {
                    Instalacion.desinstalarFuente(next, activar);
                    iterator.remove();
                }
            }
        } else {
            for (Iterator<FuenteInstalada> iterator = listaFuentesInstaladas.iterator(); iterator.hasNext();) {
                FuenteInstalada next = iterator.next();
                if (next.getDirInstalacion().getName().equals(fuenteDesinstalar.getName())) {
                    Instalacion.desinstalarFuente(next, activar);
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Método que comprueba si se intenta instalar una fuente ya instalada.
     *
     * @param fuenteComprobar
     * @param sistema true para comprobar si es una fuente del sistema.
     * @return True si la fuente esta instalada o false en caso contrario.
     */
    public boolean comprobarFuenteInstalada(File fuenteComprobar, Boolean sistema) {
        boolean instalada = false;

        if (sistema) {
            for (File systemFont : systemFonts) {
                if (systemFont.getName().equals(fuenteComprobar.getName())) {
                    instalada = true;
                }
            }
        } else {
            for (FuenteInstalada listafuenteInstalada : listaFuentesInstaladas) {
                if (listafuenteInstalada.getDirInstalacion().getName().equals(fuenteComprobar.getName())) {
                    instalada = true;
                }
            }
        }
        return instalada;
    }

    /**
     * Método que comprueba si se intenta instalar una fuente ya instalada.
     *
     * @param fuenteComprobar
     * @param sistema true para comprobar si es una fuente del sistema.
     * @return True si la fuente esta instalada o false en caso contrario.
     */
    public boolean comprobarFuenteActivada(File fuenteComprobar, Boolean sistema) {
        boolean instalada = false;

        if (sistema) {
            for (File systemFont : systemFonts) {
                if (systemFont.getName().equals(fuenteComprobar.getName())) {
                    instalada = true;
                }
            }
        } else {
            for (FuenteInstalada listafuenteActivada : listaFuentesActivadas) {
                if (listafuenteActivada.getDirInstalacion().getName().equals(fuenteComprobar.getName())) {
                    instalada = true;
                }
            }
        }
        return instalada;
    }

    /**
     * Método que comprueba si la fuente esta instalada o el directorio contiene
     * fuentes instaladas.
     *
     * @param comprobar directorio a comprobar.
     * @return
     */
    public boolean comprobarAccion(File comprobar) {
        boolean mover = false;
        if (comprobar.isFile() && !comprobarFuenteInstalada(comprobar, false)) {
            mover = true;
        } else if (comprobar.isDirectory()) {
            if (comprobar.listFiles().length < 1) {
                mover = true;
            } else {
                for (File listFile : comprobar.listFiles()) {
                    mover = true;
                    if (listFile.isFile() && comprobarFuenteInstalada(listFile, false)) {
                        mover = false;
                        break;
                    }
                }
            }
        }
        return mover;
    }

    /**
     * Método que filtra la lista de google fonts por estilo.
     *
     * @param estilo
     * @return lista de las fuentes que coninciden con el estilo.
     */
    public List<GoogleFont> filtrarFuentesGoogles(String estilo) {
        List<GoogleFont> listaFiltrada = new ArrayList<>();

        for (GoogleFont fuenteFiltrar : listaFuentesGoogle) {
            if (fuenteFiltrar.getCategory().equals(estilo)) {
                listaFiltrada.add(fuenteFiltrar);
            }
        }
        return listaFiltrada;
    }

    /**
     * Método que borra un directorio y su contenido.
     *
     * @param dirBorrar
     */
    public void borrarDirectorio(File dirBorrar) {
        for (File listFile : dirBorrar.listFiles()) {
            listFile.delete();
        }
        dirBorrar.delete();
    }

    /**
     * Método que comprueba si las fuentes a cargar superan el limite.
     *
     * @param listaFuentes
     * @return boolean true en caso de no superar el limite y false en caso
     * contrario.
     */
    public boolean comprobarLimiteFuentes(List<LocalFont> listaFuentes) {
        if (listaFuentes.size() <= limiteFuentes) {
            return true;
        } else {
            return false;
        }
    }

}
