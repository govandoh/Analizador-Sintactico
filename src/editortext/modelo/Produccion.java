
package editortext.modelo;
import java.util.ArrayList;
import java.util.List; 

public class Produccion {
    private final String variable; 
    private final List<String> derivaciones; 
    
    public Produccion(String variable){
        this.variable = variable;
        this.derivaciones = new ArrayList<>(); 
    }
    
    public void agregarDerivacion(String derivacion){
        derivaciones.add(derivacion); 
    }
    
    public String getVariable(){
        return variable; 
    }
    
    public List<String> getDerivaciones(){
        return derivaciones; 
    }
    
    public String toString(){
        return variable + ":" + String.join("|", derivaciones); 
    }
    
}
