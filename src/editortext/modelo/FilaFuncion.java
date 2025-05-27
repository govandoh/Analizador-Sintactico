
package editortext.modelo;

public class FilaFuncion {
    private String variable; private String terminales; 
    
    public FilaFuncion(String variable, String terminales){
        this.variable = variable; 
        this.terminales = terminales;
    }
    
    public String getVariable() { return variable;}
    public String getTerminales() { return terminales;}
}
