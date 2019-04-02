/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import controlador.ControladorGestorFuentes;

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
    }
    
}
