/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Mario
 */
public class AtributosFont {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FontFormatException, IOException {
        Font createFont = Font.createFont(Font.TRUETYPE_FONT, new File("AGaramondPro-Regular.otf"));
        
        System.out.println(createFont.getName());
        System.out.println(createFont.getFontName());
        System.out.println(createFont.getFamily());
        System.out.println(createFont.getStyle());
        
    }
    
}
