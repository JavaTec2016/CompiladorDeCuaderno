package sintactico;

public class ExpresionOperador extends NodoTipable {
    public ExpresionOperador(String fuente, String token, NodoBinario ambito, NodoBinario tipo, NodoBinario identificador, String alcance) {
        super(fuente, token, ambito, tipo, identificador, alcance);
    }

    public ExpresionOperador(String fuente, String token, NodoTipable heredar) {
        super(fuente, token, heredar);
    }

    public ExpresionOperador(String fuente, String token, String ambito, String tipo, String identificador, String alcance, int posicion, int linea) {
        super(fuente, token, ambito, tipo, identificador, alcance, posicion, linea);
    }
}
