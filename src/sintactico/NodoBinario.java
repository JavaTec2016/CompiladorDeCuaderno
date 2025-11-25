package sintactico;

import lexico.Token;

import java.util.function.Consumer;

public class NodoBinario extends Nodo{
    public NodoBinario izquierdo;
    public NodoBinario derecho;
    private Exception exception;
    public NodoBinario(String fuente, String token, int posicion, int fin, int linea) {
        super(fuente, token, posicion, fin, linea);
    }
    public NodoBinario(String fuente, String token, NodoBinario izq, NodoBinario der){
        super(fuente, token, izq.posicion, izq.fin, izq.linea);
        izquierdo = izq;
        derecho = der;
    }
    public NodoBinario(String fuente, String token) {
        super(fuente, token);
    }

    public NodoBinario(Token t) {
        super(t);
    }

    public void setExceptionMensaje(String msj) {
        this.exception = new Exception(getPosiciones()+" >> " + msj);
    }
    protected void error() throws Exception {
        if(exception == null) return;

        throw exception;
    }

    public void accionPreorden(Consumer<NodoBinario> c){
        for (int i = 0; i < partes.size(); i++) {
            get(i).accionPreorden(c);
        }
        c.accept(this);
    }
}
