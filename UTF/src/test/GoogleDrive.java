/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import java.io.File;

/**
 *
 * @author Mario
 */
public class GoogleDrive {

    private static final String APPLICATION_NAME = "Use that font";

    private static final String UPLOAD_FILE_PATH = "Enter File Path";
    private static final String DIR_FOR_DOWNLOADS = "Enter Download Directory";
    private static final File UPLOAD_FILE = new java.io.File(UPLOAD_FILE_PATH);

    private static final File DATA_STORE_DIR
            = new File(System.getProperty("user.home"), ".store/drive_sample");

    private static FileDataStoreFactory dataStoreFactory;

    private static HttpTransport httpTransport;

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    
    private static Drive drive;
    
    private static Credential authorize() throws Exception {
    // load client secrets
    /*GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
        new InputStreamReader(DriveSample.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println(
          "Enter Client ID and Secret from https://code.google.com/apis/console/?api=drive "
          + "into drive-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }*/
    // set up authorization code flow
    /*GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JSON_FACTORY, clientSecrets,
        Collections.singleton(DriveScopes.DRIVE_FILE)).setDataStoreFactory(dataStoreFactory)
        .build();*/
    // authorize
    //return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    return null;
  }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }

}
