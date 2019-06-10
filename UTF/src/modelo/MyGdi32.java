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
public interface MyGdi32 extends StdCallLibrary {

    MyGdi32 INSTANCE = (MyGdi32) Native.loadLibrary("gdi32", MyGdi32.class);

    public int AddFontResourceA(String Arg1);

    public boolean RemoveFontResourceA(String lpFileName);

}
