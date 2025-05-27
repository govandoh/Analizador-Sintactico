package editortext.servicio;

import editortext.modelo.Gramatica; 
import editortext.modelo.Produccion; 

public class DetectorAmbiguedad {
    
    public boolean esAmbigua(Gramatica g){
        for(Produccion p: g.getProducciones()){
            String var = p.getVariable(); 
            int countRecIzq = 0; 
            for(String der : p.getDerivaciones()){
                if(der.trim().startsWith(var)){
                    countRecIzq++;
                }
            }
            if(countRecIzq > 1){
                return true; 
            }
        }
        return false; 
    }
}
