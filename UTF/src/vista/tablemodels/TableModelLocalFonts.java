/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista.tablemodels;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import modelo.GoogleFont;
import modelo.LocalFont;

/**
 *
 * @author Mario
 */
public class TableModelLocalFonts extends AbstractTableModel {

    private final List<LocalFont> listaGoogleFonts;
    private final String[] columnas = {"Family", "Name"};

    public TableModelLocalFonts(List<LocalFont> listaGoogleFonts) {
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
                return listaGoogleFonts.get(rowIndex).getFamily();
            case 1:
                return listaGoogleFonts.get(rowIndex).getName();

        }
        return null;
    }

}
