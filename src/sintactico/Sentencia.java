package sintactico;

import lexico.Token;

public class Sentencia extends NodoTipable {

    public Sentencia(String fuente, String token, NodoBinario ambito, NodoBinario tipo, NodoBinario identificador, String alcance) {
        super(fuente, token, ambito, tipo, identificador, alcance);
        linea = identificador.linea;
        posicion = identificador.posicion;
    }
    public Sentencia(String fuente, String token, NodoTipable expresion){
        this(fuente, token, expresion.ambito, expresion.tipo, expresion.identificador, expresion.alcance);
    }
}
