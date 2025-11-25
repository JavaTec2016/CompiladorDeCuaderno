package lexico;

import sintactico.Nodo;

public class Tipable extends Nodo {

    public Tipable(String fuente, String token, int posicion, int fin, int linea) {
        super(fuente, token, posicion, fin, linea);
    }

    public Tipable(String fuente, String token) {
        super(fuente, token);
    }

    public Tipable(Token t) {
        super(t);
    }
}