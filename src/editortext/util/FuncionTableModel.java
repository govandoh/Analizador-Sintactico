
package editortext.util;
import editortext.modelo.FilaFuncion; 
import javax.swing.table.AbstractTableModel; 
import java.util.List; 

public class FuncionTableModel extends AbstractTableModel {
    private final List<FilaFuncion> filas; 
    private final String[] columnas = {"v", "Terminales"}; 
    
    public FuncionTableModel(List<FilaFuncion> filas){
        this.filas = filas;
    }
    
    @Override
    public int getRowCount() {
        return filas.size(); 
    }

    @Override
    public int getColumnCount() {
        return columnas.length; 
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        FilaFuncion fila = filas.get(rowIndex);
        return columnIndex == 0  ? fila.getVariable() : fila.getTerminales(); 
    }
    
    @Override
    public String getColumnName(int col){
        return columnas[col]; 
    }

    
}
