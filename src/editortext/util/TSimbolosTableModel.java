
package editortext.util;

import javax.swing.table.AbstractTableModel;
import java.util.*;
public class TSimbolosTableModel extends AbstractTableModel {
    
    private final List<String> variables; private final List<String> terminales; 
    private final Map<String,Map<String, List<String>>> tabla; 
    
    public TSimbolosTableModel(List<String> variables, List<String> terminales, Map<String, Map<String, List<String>>> tabla){
        this.variables = variables; 
        this.terminales = new ArrayList<>();
        for(String t: terminales){
            if(!t.equals("e")) this.terminales.add(t);
        }
        if(!this.terminales.contains("$")) this.terminales.add("$");
        this.tabla = tabla; 
        
        
        
    }
    

    @Override
    public int getRowCount() {
        return variables.size();
    }

    @Override
    public int getColumnCount() {
        return 1 + terminales.size();
    }
    
    @Override
    public String getColumnName(int col){
        if(col == 0 ) return "Variables"; 
        return terminales.get(col -1); 
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex == 0)return variables.get(rowIndex); 
        String var = variables.get(rowIndex);
        String term = terminales.get(columnIndex - 1); 
        List<String> producciones = tabla.get(var).get(term); 
        if(producciones == null || producciones.isEmpty()) return ""; 
        return String.join("|", producciones); 
    }
    
}
