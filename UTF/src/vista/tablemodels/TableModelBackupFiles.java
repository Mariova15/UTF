/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista.tablemodels;

import java.io.File;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import modelo.GoogleFont;
import modelo.LocalFont;

/**
 *
 * @author Mario
 */
public class TableModelBackupFiles extends AbstractTableModel {

    private final List<File> listaBackupFiles;
    private final String[] columnas = {"Name"};

    public TableModelBackupFiles(List<File> listaBackupFiles) {
        this.listaBackupFiles = listaBackupFiles;
    }

    @Override
    public int getRowCount() {
        return listaBackupFiles.size();
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
                return listaBackupFiles.get(rowIndex).getName();
        }
        return null;
    }

}
