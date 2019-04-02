/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.util.List;

/**
 *
 * @author Mario
 */
public class GoogleFont {

    private String kind, family, category;
    private List<String> urls;

    public GoogleFont(String kind, String family, String category) {
        this.kind = kind;
        this.family = family;
        this.category = category;
    }

    @Override
    public String toString() {
        return "GoogleFont{" + "kind=" + kind + ", family=" + family + ", category=" + category + '}';
    }

}
