/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista.tablemodels;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import modelo.GoogleFont;

/**
 *
 * @author Mario
 */
public class TableModelGoogleFonts extends AbstractTableModel {

    private final List<GoogleFont> listaGoogleFonts;
    private final String[] columnas = {"Kind", "Family", "Category", "Version"};

    public TableModelGoogleFonts(List<GoogleFont> listaGoogleFonts) {
        this.listaGoogleFonts = listaGoogleFonts;
    }

    @Override
    public int getRowCount() {
        return listaGoogleFonts.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnas[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return listaGoogleFonts.get(rowIndex).getKind();
            case 1:
                return listaGoogleFonts.get(rowIndex).getFamily();
            case 2:
                return listaGoogleFonts.get(rowIndex).getCategory();
            case 3:
                return listaGoogleFonts.get(rowIndex).getVersion();
        }
        return null;
    }

}
