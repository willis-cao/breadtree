package ui;

import model.*;
import javax.swing.table.AbstractTableModel;
import java.util.List;

// Represents a table model for a notebook
class NotebookTableModel extends AbstractTableModel {
    private String[] columnNames = {"Word",
                                    "Definition",
                                    "Tags"};

    private Object[][] data;

    // MODIFIES: this
    // EFFECTS: reads data from the given notebook and updates the table display
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
        fireTableDataChanged();
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

}
