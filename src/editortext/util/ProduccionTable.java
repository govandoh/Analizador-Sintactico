package editortext.util;

import editortext.modelo.FilaProduccion; 
import javax.swing.table.AbstractTableModel; 
import java.util.List; 

public class ProduccionTable extends AbstractTableModel{
    
    private final List<FilaProduccion> filas; 
    
    public ProduccionTable(List<FilaProduccion> filas){
        this.filas = filas; 
    }

    @Override
    public int getRowCount() {
        return filas.size(); 
    }

    @Override
    public int getColumnCount() {
        return 2; 
    }

    @Override
    public Object getValueAt(int row, int column) {
       FilaProduccion fila = filas.get(row); 
       return column == 0 ? fila.getVariable() : fila.getTerminal(); 
    }
    
    @Override
    public String getColumnName(int col) {
        return col == 0 ? "Var" : "Produccion";
    }
    
}
