
package editortext.servicio;

import editortext.modelo.Gramatica; 
import editortext.modelo.Produccion; 

import javax.swing.JOptionPane;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher; 
import java.util.regex.Pattern; 

public class GramaticaParser {
    private static final Pattern PATRON_PRODUCCION = Pattern.compile("^(\\w+)\\s*:\\s*(.+)$"); 
    
    public Gramatica parsear(String texto) throws Exception{
        Gramatica gramatica = new Gramatica();
        Set<String> variablesExistentes = new HashSet<>();
        String[] lineas = texto.split("\\R"); 
        
        for(String linea: lineas){
            Matcher matcher = PATRON_PRODUCCION.matcher(linea.trim());
            if(matcher.matches()){
                String variable = matcher.group(1).trim(); 
                String[] derivaciones = matcher.group(2).split("\\|"); 
                
                if(variable.equals("e")){
                    String nuevoVar = generarNuevoVariable("e", variablesExistentes);
                    JOptionPane.showMessageDialog(null,
                    " Caracter 'e' es reservada para epsilon.\nSe ha renombrado automáticamente a: '" + nuevoVar + "'",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
                variable = nuevoVar;
                }
                variablesExistentes.add(variable);
                
                Produccion produccion = new Produccion(variable); 
                for(String derivacion: derivaciones){
                    produccion.agregarDerivacion(derivacion.trim());
                }
                
                gramatica.agregarProduccion(produccion);
            }else if(!linea.trim().isEmpty()){
                throw new Exception("Linea invalida: " + linea); 
            }
            
        }
        return gramatica;
    }
    
    public String generarNuevoVariable(String base, Set<String> existentes){
        int cont = 1;
        String nuevoVar; 
        do{
            nuevoVar = base + cont; 
            cont++;
        }while(existentes.contains(nuevoVar));
        return nuevoVar;
    }
    
}
