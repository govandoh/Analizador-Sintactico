package editortext.servicio;

import editortext.modelo.Gramatica; 
import editortext.modelo.Produccion; 
import java.util.*; 

public class TablaSimbolos {
    public Map<String, Map<String, List<String>>> construirTabla(
            Gramatica g,
            List<String> variables,
            List<String> terminales,
            Map<String, Set<String>> primero,
            Map<String, Set<String>> siguiente
    ) {
        // Terminales + $
        List<String> columnas = new ArrayList<>(terminales);
        if (!columnas.contains("$")) columnas.add("$");

        Map<String, Map<String, List<String>>> tabla = new LinkedHashMap<>();

        for (String var : variables) {
            Map<String, List<String>> fila = new LinkedHashMap<>();
            for (String term : columnas) {
                fila.put(term, new ArrayList<>());
            }
            tabla.put(var, fila);
        }

        // Llenar la tabla LL(1)
        for (Produccion p : g.getProducciones()) {
            String A = p.getVariable();
            for (String prod : p.getDerivaciones()) {
                Set<String> primerosAlpha = calcularPrimeroDeCadena(prod, primero, variables, terminales);

                for (String term : primerosAlpha) {
                    if (!term.equals("e")) {
                        tabla.get(A).get(term).add(limpiarTerminalesSoloTerminales(prod, terminales));
                    }
                }
                // Si puede derivar epsilon
                if (primerosAlpha.contains("e")) {
                    for (String b : siguiente.get(A)) {
                        tabla.get(A).get(b).add(limpiarTerminalesSoloTerminales(prod, terminales));
                    }
                }
            }
        }
        return tabla;
    }

    // Calcula PRIMERO para una cadena de símbolos
    public Set<String> calcularPrimeroDeCadena(String cadena, Map<String, Set<String>> primero, List<String> variables, List<String> terminales) {
        Set<String> resultado = new LinkedHashSet<>();
        List<String> simbolos = separarSimbolos(cadena, variables, terminales);
        boolean allEpsilon = true;
        for (String s : simbolos) {
            if (s.isEmpty()) continue;
            if (terminales.contains(s)) {
                resultado.add(s);
                allEpsilon = false;
                break;
            } else if (variables.contains(s)) {
                Set<String> primeroVar = primero.get(s);
                resultado.addAll(sinEpsilon(primeroVar));
                if (!primeroVar.contains("e")) {
                    allEpsilon = false;
                    break;
                }
            } else if (s.equals("e")) {
                resultado.add("e");
                break;
            }
        }
        if (allEpsilon) resultado.add("e");
        return resultado;
    }

    private Set<String> sinEpsilon(Set<String> s) {
        Set<String> r = new LinkedHashSet<>(s);
        r.remove("e");
        return r;
    }

    private List<String> separarSimbolos(String derivacion, List<String> variables, List<String> terminales) {
        List<String> simbolos = new ArrayList<>();
        String s = derivacion.trim();
        List<String> varsSorted = new ArrayList<>(variables);
        varsSorted.sort((a, b) -> Integer.compare(b.length(), a.length()));
        List<String> termsSorted = new ArrayList<>(terminales);
        termsSorted.sort((a, b) -> Integer.compare(b.length(), a.length()));

        while (!s.isEmpty()) {
            boolean match = false;
            for (String v : varsSorted) {
                if (s.startsWith(v)) {
                    simbolos.add(v);
                    s = s.substring(v.length());
                    match = true;
                    break;
                }
            }
            if (!match) {
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
                simbolos.add(s.substring(0, 1));
                s = s.substring(1);
            }
            s = s.trim();
        }
        return simbolos;
    }

    // **NUEVA función** para limpiar comillas simples/dobles de terminales en las producciones:
        private String limpiarTerminalesSoloTerminales(String prod, List<String> terminales) {
        // Separa la producción en símbolos
        String resultado = "";
        int i = 0;
        while (i < prod.length()) {
            boolean esTerminal = false;
            // Busca terminales (pueden ser varios caracteres)
            for (String t : terminales) {
                // Busca terminales con comillas simples o dobles
                if (prod.startsWith("'" + t + "'", i)) {
                    resultado += t;
                    i += t.length() + 2;
                    esTerminal = true;
                    break;
                }
                if (prod.startsWith("\"" + t + "\"", i)) {
                    resultado += t;
                    i += t.length() + 2;
                    esTerminal = true;
                    break;
                }
            }
            if (!esTerminal) {
                // Si no es terminal rodeado de comillas, añade el caracter tal cual
                resultado += prod.charAt(i);
                i++;
            }
        }
        return resultado;
    }

}
