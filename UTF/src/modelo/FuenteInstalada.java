/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author Mario
 */
public class FuenteInstalada implements Serializable {

    private File dirInstalacion;

    private String claveRegistro;

    public FuenteInstalada(File dirInstalacion, String claveRegistro) {
        this.dirInstalacion = dirInstalacion;
        this.claveRegistro = claveRegistro;
    }

    public File getDirInstalacion() {
        return dirInstalacion;
    }

    public String getClaveRegistro() {
        return claveRegistro;
    }

}
