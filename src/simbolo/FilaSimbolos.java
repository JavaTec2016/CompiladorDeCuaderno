package simbolo;

import sintactico.NodoBinario;
import sintactico.NodoTipable;

public class FilaSimbolos {
    public String identificador;
    public String tipo;
    public String ambito;
    public String alcance;
    public NodoTipable nodo;
    public Object valor;
    public FilaSimbolos(String identificador, String tipo, String ambito, String alcance) {
        this.identificador = identificador;
        this.tipo = tipo;
        this.ambito = ambito;
        this.alcance = alcance;
    }
    public FilaSimbolos(String identificador, String tipo, String ambito, String alcance, Object valor, NodoTipable nodo){
        this(identificador, tipo, ambito, alcance);
        this.valor = valor;
        this.nodo = nodo;
    }

    @Override
    public String toString() {
        return identificador+" | "+tipo + " | " + ambito + " | " + alcance + " | fuente: " + nodo;
    }
}
