package sintactico;

import acciones.Semanticas;
import lexico.Token;

import java.util.ArrayList;
import java.util.function.Consumer;

public class NodoTipable extends NodoBinario {
    public NodoBinario tipo;
    public String alcance;
    public NodoBinario identificador;
    public NodoBinario ambito;
    public NodoBinario nodoFuente;
    public NodoTipable parent;
    public NodoTipable(String fuente, String token, NodoBinario ambito, NodoBinario tipo, NodoBinario identificador, String alcance) {
        super(fuente, token);
        this.tipo = tipo;
        this.alcance = alcance;
        this.identificador = identificador;
        this.ambito = ambito;
        if(this.ambito == null) this.ambito = new NodoBinario("public", "ambito");
        agregar();
    }
    public int getPartesOffset(){
        return 3;
    }
    public ArrayList<NodoBinario> getPartes(){
        ArrayList<NodoBinario> res = new ArrayList<>();
        for (int i = 0; i < partes.size()-getPartesOffset(); i++) {
            res.add(get(i));
        }
        return  res;
    }
    public NodoTipable(String fuente, String token, NodoTipable heredar){
        this(fuente, token, heredar.ambito, heredar.tipo, heredar.identificador, heredar.alcance);
        setDimensiones(heredar);
    }
    public NodoTipable(String fuente, String token, NodoBinario heredar){
        this(fuente, token, "private", "null", heredar.identificador, Semanticas.alcance, heredar.posicion, heredar.linea);
        setDimensiones(heredar);
    }
    public NodoTipable(String fuente, String token, String ambito, String tipo, String identificador, String alcance, int posicion, int linea){
        this(fuente, token,
                new NodoBinario(new Token(ambito, "ambito", -1, -1, -1)),
                new NodoBinario(new Token(tipo, "tipo", -1, -1, -1)),
                new NodoBinario(new Token(identificador, "variable", posicion, -1, linea)), alcance
        );
    }


    public void agregar(){
        setChild(ambito);
        setChild(tipo);
        setChild(identificador);

    }
    @Override
    public NodoBinario get(int i){
        return super.get(i+ getPartesOffset());
    }

    public String getParentAlcance(){
        return parent.alcance;
    }
    @Override
    public void accionPreorden(Consumer<NodoBinario> c){
        for (int i = 0; i < partes.size()-getPartesOffset(); i++) {
            get(i).accionPreorden(c);
        }
        c.accept(this);
    }

    @Override
    public String toString() {
        return token;
        //return token+":"+fuente + ", tipo: " + tipo.fuente + " ambito " + ambito.fuente;
    }
}
