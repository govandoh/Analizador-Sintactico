
package editortext.servicio;

import editortext.modelo.Gramatica; 
import editortext.modelo.Produccion; 
import java.util.*; 

public class CalcularFunciones {
    
   private List<String> separarSimbolos(String derivacion, List<String> variables, List<String> terminales) {
    List<String> simbolos = new ArrayList<>();
    String s = derivacion.trim();

    // Ordenar descendente por longitud para evitar prefijos incorrectos
    List<String> varsSorted = new ArrayList<>(variables);
    varsSorted.sort((a, b) -> Integer.compare(b.length(), a.length()));
    List<String> termsSorted = new ArrayList<>(terminales);
    termsSorted.sort((a, b) -> Integer.compare(b.length(), a.length()));

    while (!s.isEmpty()) {
        boolean match = false;

        // Primero, busca variables
        for (String v : varsSorted) {
            if (s.startsWith(v)) {
                // Validación especial: si v termina en prima pero lo siguiente NO es prima
                // Evita que 'S' (S-prima) haga match sobre 'S' seguida de ')'
                int vlen = v.length();
                if (v.endsWith("'") && s.length() > vlen) { //en este endWith tambien
                    char next = s.charAt(vlen);
                    if (next != '\'' && !Character.isLetterOrDigit(next)) {
                        // S' seguido de ) u otro caracter raro, NO debe matchear
                        // En este caso, sigue buscando variables más cortas
                        continue;
                    }
                }
                simbolos.add(v);
                s = s.substring(v.length());
                match = true;
                break;
            }
        }
        if (!match) {
            // Luego busca terminales
            for (String t : termsSorted) {
                if (s.startsWith(t)) {
                    simbolos.add(t);
                    s = s.substring(t.length());
                    match = true;
                    break;
                }
            }
        }
        if (!match) {
            // Si no encuentra, toma el primer caracter como símbolo desconocido
            simbolos.add(s.substring(0, 1));
            s = s.substring(1);
        }
        s = s.trim();
    }
    return simbolos;
}




        
    public Map<String, Set<String>> calcularPrimero(Gramatica g, List<String> variables, List<String> terminales) {
        Map<String, Set<String>> primeros = new LinkedHashMap<>();
        for (String var : variables) {
            primeros.put(var, new LinkedHashSet<>());
        }

        boolean cambio;
        do {
            cambio = false;
            for (Produccion p : g.getProducciones()) {
                String var = p.getVariable();
                for (String derivacion : p.getDerivaciones()) {
                    List<String> simbolos = separarSimbolos(derivacion, variables, terminales);
                    boolean nullable = true;
                    for (String s : simbolos) {
                        s = s.trim();
                        if (s.isEmpty()) continue;
                        if (terminales.contains(s)) {
                            // Es terminal
                            if (primeros.get(var).add(s)) cambio = true;
                            nullable = false;
                            break;
                        } else if (variables.contains(s)) {
                            // Es variable
                            Set<String> primerosVar = primeros.get(s);
                            for (String t : primerosVar) {
                                if (!t.equals("e")) {
                                    if (primeros.get(var).add(t)) cambio = true;
                                }
                            }
                            if (!primerosVar.contains("e")) {
                                nullable = false;
                                break;
                            }
                        } else if (s.equals("e")) {
                            // Epsilon explícito
                            if (primeros.get(var).add("e")) cambio = true;
                            nullable = true;
                            break;
                        }
                    }
                    if (nullable) {
                        if (primeros.get(var).add("e")) cambio = true;
                    }
                }
            }
        } while (cambio);
    return primeros;
}
    
    public Map<String, Set<String>> calcularSiguiente(
    Gramatica g,
    List<String> variables,
    List<String> terminales,
    Map<String, Set<String>> primeros,
    String simboloInicial
) {
    Map<String, Set<String>> siguientes = new LinkedHashMap<>();
    for (String v : variables) {
        siguientes.put(v, new LinkedHashSet<>());
    }
    // La variable inicial siempre lleva $
    siguientes.get(simboloInicial).add("$");

    boolean cambio;
    do {
        cambio = false;
        for (Produccion p : g.getProducciones()) {
            String A = p.getVariable();
            for (String derivacion : p.getDerivaciones()) {
                List<String> simbolos = separarSimbolos(derivacion, variables, terminales);
                System.err.println(separarSimbolos(derivacion, variables, terminales));
                for (int i = 0; i < simbolos.size(); i++) {
                    String B = simbolos.get(i);
                    if (!variables.contains(B)) continue;
                    boolean nullable = true;
                    // Recorre los símbolos a la derecha de B
                    for (int j = i + 1; j < simbolos.size(); j++) {
                        String beta = simbolos.get(j);
                        nullable = false;
                        if (terminales.contains(beta)) {
                            if (siguientes.get(B).add(beta)) cambio = true;
                            break; // Solo el primer terminal a la derecha importa aquí
                        } else if (variables.contains(beta)) {
                            // Añade PRIMERO(beta) excepto épsilon
                            for (String t : primeros.get(beta)) {
                                if (!t.equals("e")) {
                                    if (siguientes.get(B).add(t)) cambio = true;
                                }
                            }
                            if (!primeros.get(beta).contains("e")) {
                                break;
                            } else {
                                nullable = true;
                            }
                        }
                        // Si el siguiente símbolo puede ser épsilon, sigue buscando
                    }
                    // Si no hay nada o todo lo que sigue puede ser épsilon, añade Siguiente(A)
                    if (nullable || (i == simbolos.size() - 1)) {
                        for (String t : siguientes.get(A)) {
                            if (siguientes.get(B).add(t)) cambio = true;
                        }
                    }
                }
            }
        }
    } while (cambio);

    // Ordenar para la vista
    for (String v : variables) {
        Set<String> sigSet = siguientes.get(v);
        List<String> ordenada = new ArrayList<>();
        for (String t : terminales) {
            if (sigSet.contains(t)) ordenada.add(t);
        }
        if (sigSet.contains("$")) ordenada.add("$");
        siguientes.put(v, new LinkedHashSet<>(ordenada));
    }
    return siguientes;
}

}