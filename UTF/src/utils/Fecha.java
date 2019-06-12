/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Mario
 */
public class Fecha {

    /**
     * Método que da formato dd/MM/yy a fechas en formato long.
     * 
     * @param fechaFormatear Long del que se crea un objeto date.
     * @return String con la fecha en formato dd/MM/yy.
     */
    public static String formatearFecha(Long fechaFormatear) {
        //SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yy");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        Date fecha = new Date(fechaFormatear);
        String fechaFormateada = sdf.format(fecha);
        return fechaFormateada;
    }

}
