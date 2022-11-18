package ui;

import model.*;
import javax.swing.table.AbstractTableModel;
import java.util.List;

class NotebookTableModel extends AbstractTableModel {
    private String[] columnNames = {"Word",
                                    "Definition",
                                    "Tags"};

    private Object[][] data;

    public void updateDataFromNotebook(Notebook notebook) {
        List<Entry> entries = notebook.getEntries();
        Object[][] updatedData = new Object[entries.size()][3];

        for (int i = 0; i < entries.size(); i++) {
            Entry currentEntry = entries.get(i);
            updatedData[i][0] = currentEntry.getWord();
            updatedData[i][1] = currentEntry.getDefinition();
            updatedData[i][2] = currentEntry.tagsAsString();
        }

        data = updatedData;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col < 2) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }

}
