
package editortext.servicio;

import editortext.modelo.Gramatica; 
import editortext.modelo.Produccion; 
import java.util.LinkedHashSet; 
import java.util.Set; 

public class AnalizadorComponentes {
    
    public Set<String> extraerVariables(Gramatica g){
        Set<String> vars = new LinkedHashSet<>(); 
        for(Produccion p: g.getProducciones()){
            vars.add(p.getVariable());
        }
        return vars;
    }
    
    
    // extrar terminales que vengan sin comillas para las uniones
    /*public Set<String> extraerTerminales(Gramatica g) {
        Set<String> terminales = new HashSet<>();
        Set<String> variables = extraerVariables(g); // Usamos los lados izquierdos

        for (Produccion p : g.getProducciones()) {
            for (String der : p.getDerivaciones()) {
                if (der.equals("e")) {
                    terminales.add("e"); // Epsilon visual
                } else {
                    boolean enComillas = false;
                    StringBuilder temp = new StringBuilder();

                    for (int i = 0; i < der.length(); i++) {
                        char c = der.charAt(i);

                        // Si encontramos comilla simple → modo terminal explícito
                        if (c == '\'') {
                            enComillas = !enComillas;
                            if (!enComillas && temp.length() > 0) {
                                terminales.add(temp.toString());
                                temp.setLength(0);
                            }
                        } else if (enComillas) {
                            temp.append(c);
                        } else {
                            // Fuera de comillas: revisar si no es variable declarada
                            String simbolo = String.valueOf(c);
                                if (!variables.contains(simbolo) && !Character.isWhitespace(c)) {
                                    terminales.add(simbolo); // Asumimos que es terminal implícito
                                }
                            }
                        }
                    }
                }
            }
        return terminales;
    }*/

    public Set<String> extraerTerminales(Gramatica g){
        Set<String> terminals = new LinkedHashSet<>(); 
        for(Produccion p: g.getProducciones()){
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
