
package editortext.util;
import javax.swing.table.AbstractTableModel; 
import java.util.List; 

public class GramaticaTableModel extends AbstractTableModel {
    private final List<String> datos; 
    private final String nombreColumna;
    
    public GramaticaTableModel(List<String> datos, String nombreColumna){
        this.datos = datos;
        this.nombreColumna = nombreColumna; 
    }
    
    @Override
    public int getRowCount() {
        return datos.size();
    }

   
    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return datos.get(row);
    }

    @Override
    public String getColumnName(int col) {
        return nombreColumna;
    }
    
}
