/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mario
 */
public class LocalFont implements Serializable {

    private String family, name;
    private File fontFile;
    private Font font;

    public LocalFont(File fontFile) {
        try {
            //cambiar temp por carpeta de datos de la app
            File file = new File("temp"+File.separator+fontFile.getName());
            
            Path copy = file.toPath();
            
            if (!file.exists()) {
                copy = Files.copy(fontFile.toPath(), file.toPath(),REPLACE_EXISTING);
            }

            //copy.toFile().deleteOnExit();
            //System.out.println(file.getAbsolutePath());
            file.deleteOnExit();
      
            font = Font.createFont(Font.TRUETYPE_FONT,new File(copy.toString()));           
        } catch (FontFormatException ex) {
            Logger.getLogger(LocalFont.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LocalFont.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.family = font.getFamily();
        this.name = font.getName();
        this.fontFile = fontFile;
    }

    public String getFamily() {
        return family;
    }

    public String getName() {
        return name;
    }

    public File getFontFile() {
        return fontFile;
    }

    public Font getFont() {
        return font;
    }

}
