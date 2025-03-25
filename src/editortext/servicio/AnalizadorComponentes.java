
package editortext.servicio;

import editortext.modelo.Gramatica; 
import editortext.modelo.Produccion; 

import java.util.HashSet; 
import java.util.Set; 

public class AnalizadorComponentes {
    
    public Set<String> extraerVariables(Gramatica g){
        Set<String> vars = new HashSet<>(); 
        for(Produccion p: g.getProdcciones()){
            vars.add(p.getVariable());
        }
        return vars;
    }
    
    
    public Set<String> extraerTerminales(Gramatica g){
        Set<String> terminals = new HashSet<>(); 
        for(Produccion p: g.getProdcciones()){
            for(String der: p.getDerivaciones()){
                if(der.equals("e")){
                    terminals.add("e"); 
                }else{
                    StringBuilder temp = new StringBuilder(); 
                    boolean enComillas = false;
                    for(char c: der.toCharArray()){
                        if(c == '\''){
                            enComillas = !enComillas; 
                            if(!enComillas && temp.length() > 0){
                                terminals.add(temp.toString());
                                temp.setLength(0);
                            }
                        }else if(enComillas){
                            temp.append(c); 
                        }
                    }
                }
            }
        }
        return terminals; 
    }
}
