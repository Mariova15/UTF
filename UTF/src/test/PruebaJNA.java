/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import modelo.MyGdi32;
import modelo.MyUser32;

/**
 *
 * @author Mario
 */
public class PruebaJNA {

    private final static int HWND_BROADCAST = 0xffff;
    private final static int WM_FONTCHANGE = 0x001D;

    private static int installFont(File file) {
        int result = MyGdi32.INSTANCE.AddFontResourceA(
                file.getAbsolutePath());

        return result;
    }

    public static boolean removeFont(File file) {
        boolean result = MyGdi32.INSTANCE.RemoveFontResourceA(
                file.getAbsolutePath());

        return result;
    }

    public static long sentMessage() {
        long result = MyUser32.INSTANCE.SendMessageA(
                HWND_BROADCAST,
                WM_FONTCHANGE,
                0, 0);

        return result;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        installFont(new File("AJensonPro-Regular.otf"));
        sentMessage();
        removeFont(new File("AJensonPro-Regular.otf"));
        sentMessage();
    }

}
