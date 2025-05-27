package editortext.servicio;

import editortext.modelo.Gramatica; 
import editortext.modelo.Produccion;
import java.util.*; 

public class TransformadorRecursividad {
    public Gramatica transformarGramatica(Gramatica original){
        Gramatica nueva = new Gramatica(); 
        List<Produccion> producciones = original.getProducciones();
        
        for(Produccion prod: producciones){
            String var = prod.getVariable(); 
            
            List<String> recursivas = new ArrayList<>(); 
            List<String> noRecursivas = new ArrayList<>(); 
            
            for(String der: prod.getDerivaciones()){
                if(der.startsWith(var)){
                    recursivas.add(der.substring(var.length()));
                }else{
                    noRecursivas.add(der); 
                }
            }
            
            if(recursivas.isEmpty()){
                nueva.agregarProduccion(prod);
            }else{
                String nuevaVar = var + "'"; 
                Produccion prodNoRec = new Produccion(var); 
                Produccion prodRec = new Produccion(nuevaVar);
                
                for(String beta: noRecursivas){
                    prodNoRec.agregarDerivacion(beta.trim() + nuevaVar);
                }
                
                for(String alpha: recursivas){
                    prodRec.agregarDerivacion(alpha + nuevaVar);
                }
                
                prodRec.agregarDerivacion("e");
                
                nueva.agregarProduccion(prodNoRec);
                nueva.agregarProduccion(prodRec);
            }
            
        }
        return nueva; 
    }
}
