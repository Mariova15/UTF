/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import controlador.ControladorGestorFuentes;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mario
 */
public class TestControlador {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ControladorGestorFuentes cgf = new ControladorGestorFuentes();
        
        cgf.descargaJsonFuentes();
        
        cgf.verFuentes();
        
        Font createFont = null;
        try {
            createFont = Font.createFont(Font.TRUETYPE_FONT, new File("AGaramondPro-Regular.otf"));
        } catch (FontFormatException ex) {
            Logger.getLogger(TestControlador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println(createFont.toString());
    }
    
}
