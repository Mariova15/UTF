/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Mario
 */
public class GoogleFont {

    private String kind, family, category, version;
    private Date lastModified;
    private Map<String, String> files;

    public GoogleFont(String kind, String family, String category, String version, Map<String, String> files) {
        this.kind = kind;
        this.family = family;
        this.category = category;
        this.version = version;
        this.files = files;
    }

    public String getKind() {
        return kind;
    }

    public String getFamily() {
        return family;
    }

    public String getCategory() {
        return category;
    }

    public String getVersion() {
        return version;
    }
        
    @Override
    public String toString() {
        return "GoogleFont{" + "kind=" + kind + ", family=" + family + ", category=" + category + ", version=" + version + ", lastModified=" + lastModified + ", files=" + files + '}';
    }

   

}
