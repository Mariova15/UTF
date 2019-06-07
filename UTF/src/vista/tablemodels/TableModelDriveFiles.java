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
public class TableModelDriveFiles extends AbstractTableModel {

    private final List<com.google.api.services.drive.model.File> listaDriveFiles;
    private final String[] columnas = {"Name"};

    public TableModelDriveFiles(List<com.google.api.services.drive.model.File> listaDriveFiles) {
        this.listaDriveFiles = listaDriveFiles;
    }

    @Override
    public int getRowCount() {
        return listaDriveFiles.size();
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
                return listaDriveFiles.get(rowIndex).getName();
        }
        return null;
    }

}
