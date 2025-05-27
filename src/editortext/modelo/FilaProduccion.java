package editortext.modelo;

public class FilaProduccion {
    private final String variable;
    private final String terminal; 

    public FilaProduccion(String variable, String terminal) {
        this.variable = variable;
        this.terminal = terminal;
    }

    public String getVariable() {
        return variable;
    }

    public String getTerminal() {
        return terminal;
    }
    
    
}
