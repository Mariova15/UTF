/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mario
 */
public class GestorGoogleDrive implements Serializable {

    private static final String APPLICATION_NAME = "Use that font";
    private String gDBackupDirID;

    private static FileDataStoreFactory dataStoreFactory;

    private static HttpTransport httpTransport;

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static Drive drive;

    private File clientSecret, dirGdrive;

    public GestorGoogleDrive(File dirGdrive, File clientSecret) {

        try {
            this.clientSecret = clientSecret;
            this.dirGdrive = dirGdrive;

            dataStoreFactory = new FileDataStoreFactory(dirGdrive);
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            //drive = new Drive.Builder(httpTransport, JSON_FACTORY, authorize()).setApplicationName(APPLICATION_NAME).build();
            generarObjetoDrive();

            if (listarArchivosDrive().size() > 0) {
                for (com.google.api.services.drive.model.File dirBackup : listarArchivosDrive()) {
                    if (dirBackup.getName().equals("Backup")) {
                        gDBackupDirID = dirBackup.getId();
                    }
                }
            } else {
                crearDirectorio("Backup");
            }

        } catch (GeneralSecurityException ex) {
            Logger.getLogger(GestorGoogleDrive.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GestorGoogleDrive.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GestorGoogleDrive.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setgDBackupDirID(String gDBackupDirID) {
        this.gDBackupDirID = gDBackupDirID;
    }

    private void generarObjetoDrive() {
        try {
            dataStoreFactory = new FileDataStoreFactory(dirGdrive);
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            drive = new Drive.Builder(httpTransport, JSON_FACTORY, authorize()).setApplicationName(APPLICATION_NAME).build();
        } catch (Exception ex) {
            Logger.getLogger(GestorGoogleDrive.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Método que genera una credencial de google.
     *
     * @return
     * @throws Exception
     */
    private Credential authorize() throws Exception {

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(new FileInputStream(clientSecret)));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, Collections.singleton(DriveScopes.DRIVE_FILE)).setDataStoreFactory(dataStoreFactory).build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

    }

    /**
     * Método que sube un archivo a google drive.
     *
     * @param archivoLocal
     * @return
     */
    public com.google.api.services.drive.model.File subidaArchivo(File archivoLocal) {

        com.google.api.services.drive.model.File execute = null;
        if (drive == null) {
            generarObjetoDrive();
        }
        try {
            com.google.api.services.drive.model.File archivoSubir = new com.google.api.services.drive.model.File();

            archivoSubir.setName(archivoLocal.getName());

            //archivoSubir.setParents(Collections.singletonList("1s93eDS_QKIIiplMcgv0RtZGnN4aZT5S4"));
            archivoSubir.setParents(Collections.singletonList(gDBackupDirID));

            FileContent mediaContent = new FileContent("file/.zip", archivoLocal);
            //FileContent mediaContent = new FileContent("image/.png", archivoLocal);

            execute = drive.files().create(archivoSubir, mediaContent).setFields("id").execute();
        } catch (IOException ex) {
            Logger.getLogger(GestorGoogleDrive.class.getName()).log(Level.SEVERE, null, ex);
        }

        return execute;

    }

    /**
     * Método que descarga un archivo desde google drive.
     *
     * @param rutaDestino
     * @param fileId
     */
    public void descargaArchivo(String rutaDestino, String fileId) {

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (drive == null) {
                generarObjetoDrive();
            }
            drive.files().get(fileId).executeMediaAndDownloadTo(byteArrayOutputStream);

            FileOutputStream fos = new FileOutputStream(
                    rutaDestino + File.separator + drive.files().get(fileId).execute().getName());

            fos.write(byteArrayOutputStream.toByteArray());
            fos.close();
        } catch (IOException ex) {
            Logger.getLogger(GestorGoogleDrive.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Método que crea un direcotio en el raiz de google drive.
     *
     * @param nombreDir nombre del directorio.
     */
    public void crearDirectorio(String nombreDir) {
        try {
            if (drive == null) {
                generarObjetoDrive();
            }
            com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
            fileMetadata.setName(nombreDir);
            fileMetadata.setMimeType("application/vnd.google-apps.folder");

            com.google.api.services.drive.model.File execute = drive.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
            gDBackupDirID = execute.getId();
        } catch (IOException ex) {
            Logger.getLogger(GestorGoogleDrive.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Método que lista los archivos de google drive.
     *
     * @return
     */
    public List<com.google.api.services.drive.model.File> listarArchivosDrive() {

        //Falta hacer método recursivo        
        List<com.google.api.services.drive.model.File> result = new ArrayList<>();
        if (drive == null) {
            generarObjetoDrive();
        }
        try {
            Files.List request = drive.files().list();
            do {
                try {
                    FileList files = request.execute();
                    //System.out.println(files.getFiles().size());
                    result.addAll(files.getFiles());
                    request.setPageToken(files.getNextPageToken());
                } catch (IOException e) {
                    request.setPageToken(null);
                }
            } while (request.getPageToken() != null && request.getPageToken().length() > 0);

        } catch (IOException ex) {
            Logger.getLogger(GestorGoogleDrive.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

}
