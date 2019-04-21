/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collections;
import static org.omg.CORBA.AnySeqHelper.insert;
import com.google.api.services.drive.model.FileList;
import java.util.List;

/**
 *
 * @author Mario
 */
public class GoogleDrive {

    private static final String APPLICATION_NAME = "Use that font";

    private static final String UPLOAD_FILE_PATH = new File("drive").getAbsolutePath();
    private static final String DIR_FOR_DOWNLOADS = new File("drive").getAbsolutePath();
    private static final File UPLOAD_FILE = new File(UPLOAD_FILE_PATH);

    private static final File DATA_STORE_DIR = new File("drive");

    private static FileDataStoreFactory dataStoreFactory;

    private static HttpTransport httpTransport;

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static Drive drive;

    private static Credential authorize() throws Exception {

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(new FileInputStream(new File("client_secret.json"))));

        /*if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println(
                    "Enter Client ID and Secret from https://code.google.com/apis/console/?api=drive "
                    + "into drive-cmdline-sample/src/main/resources/client_secrets.json");
            System.exit(1);
        }*/
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets,
                Collections.singleton(DriveScopes.DRIVE_FILE)).setDataStoreFactory(dataStoreFactory)
                .build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
        // authorization
        Credential credential = authorize();

        System.out.println(credential.getAccessToken());

        drive = new Drive.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
                APPLICATION_NAME).build();

        
        //Subir
        /*com.google.api.services.drive.model.File algo = new com.google.api.services.drive.model.File();
        algo.setName("UTF-15_04_19.zip");
        java.io.File filePath = new java.io.File("backup/UTF-15_04_19.zip");
        FileContent mediaContent = new FileContent("file/.zip", filePath);
        com.google.api.services.drive.model.File file = drive.files().create(algo, mediaContent).setFields("id").execute();
        System.out.println("File ID: " + file.getId());*/
        
        //Descargar
        String fileId = "1TZD-yThtVrXhymWpsVz-QGdAHtZ3HLQ0";
        OutputStream outputStream = new ByteArrayOutputStream();
        drive.files().get(fileId)
                .executeMediaAndDownloadTo(outputStream);
        
        System.out.println(outputStream.toString());
        
        //Listar archivos
        FileList result = drive.files().list()
                .setPageSize(10)
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<com.google.api.services.drive.model.File> files = result.getFiles();
        if (files == null || files.size() == 0) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (com.google.api.services.drive.model.File movida : files) {
                System.out.printf("%s (%s)\n", movida.getName(), movida.getId());
            }
        }

    }

}

