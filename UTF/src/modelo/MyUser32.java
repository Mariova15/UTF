/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

/**
 *
 * @author Mario
 */
public interface MyUser32 extends StdCallLibrary {

    MyUser32 INSTANCE = (MyUser32) Native.loadLibrary("user32", MyUser32.class);

    public long SendMessageA(
            int hWnd,
            int WM_FONTCHANGE,
            int wParam,
            int lParam
    );

}
