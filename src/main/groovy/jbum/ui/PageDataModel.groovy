package jbum.ui

import jbum.core.DPage
import jbum.core.Prefs

import javax.swing.table.AbstractTableModel

class PageDataModel extends AbstractTableModel {

    ArrayList<String[]> data;

    PageDataModel() {
        data = jbum.core.Prefs.getLastModified();
    }

    void update() {
        data = Prefs.getLastModified();
        fireTableDataChanged();
    }

    int getColumnCount() {
        return 5;
    }

    int getRowCount() {
        return data.size();
    }

    @Override
    Class<?> getColumnClass(int columnIndex) {
        if(columnIndex==2){
            return Integer.class
        }
        return String.class
    }

    Object getValueAt(int row, int col) {
        if(data.size()<row)
            return ""
        if( col >= data.get(row).size())
            return null //?
        def val = data.get(row)[col]
        if(col == 2){
            return Integer.parseInt(val)
        }
        return val
    }

    String getColumnName(int col) {
        return ["Modified", "Oldest", "Photos", "Title", "Path"][col];
    }

    private int PATH_COLUMN = 4

    void validate() {
        // if data is in old format, reject it.
        if(data.size()>0 && data.get(0).size()!=getColumnCount()){
            data = new ArrayList<>()
        }
        for (int i = data.size() - 1; i >= 0; i--) {
            if (!new File(getValueAt(i, PATH_COLUMN).toString()).exists()) {
                data.remove(i);
            }
            // Update title and modified date.
            DPage dPage = new DPage(new File(getValueAt(i, PATH_COLUMN)))
            data.get(i)[3] = dPage.title
        }
        fireTableDataChanged();
    }

    void clear() {
        data.clear();
        fireTableDataChanged();
    }
}
