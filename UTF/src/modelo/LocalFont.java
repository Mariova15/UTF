/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
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
            font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
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
