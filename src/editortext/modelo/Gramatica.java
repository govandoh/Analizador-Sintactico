
package editortext.modelo;

import java.util.ArrayList; 
import java.util.List; 

public class Gramatica {
    private final List<Produccion> producciones; 
    
    public Gramatica(){ 
       this.producciones = new ArrayList<>(); 
    }
    
    public void agregarProduccion(Produccion produccion){
        producciones.add(produccion); 
    }
    
    public List<Produccion> getProducciones(){
        return producciones; 
    }
}
