package sintactico;

import lexico.Token;

import java.util.ArrayList;
import java.util.Stack;

public class Nodo extends Token {
    public ArrayList<NodoBinario> partes;
    public Nodo parent;

    public String identificador;


    public Nodo (String fuente, String token, int posicion, int fin, int linea){
        super(fuente, token, posicion, fin, linea);
        partes = new ArrayList<>();
    }
    public Nodo(String fuente, String token){
        super(fuente, token, -1, -1, -1);
        partes = new ArrayList<>();
    }
    public Nodo(Token t){
        super(t.fuente, t.token, t.posicion, t.fin, t.linea);
        partes = new ArrayList<>();
    }
    public void setChild(NodoBinario nodo){
        if(nodo != null && nodo.parent == null) nodo.setParent(this);
        partes.add(nodo);
    }
    public void setChildren(ArrayList<NodoBinario> nodos){
        for (NodoBinario nodo : nodos) {
            setChild(nodo);
        }
    }
    public NodoBinario get(int i){
        return partes.get(i);
    }
    public void setParent(Nodo nodo){
        parent = nodo;
    }
    public static ArrayList<Nodo> arreglar(ArrayList<Token> tokens){
        ArrayList<Nodo> output = new ArrayList<>();
        for (Token token : tokens){
            output.add(new Nodo(token));
        }
        return output;
    }

    public void printDescendencia(){
        for (Nodo parte : partes) {
            System.out.println(fuente+ " >> HIJO: " + parte.fuente);
            System.out.println();
            parte.printDescendencia();
        }
    }
    public Nodo getPrimero(){
        if(partes.isEmpty()) return this;
        return partes.get(0).getPrimero();
    }
    public Nodo getUltimo(){
        System.out.println("ultimo en: " + partes);
        if(partes.isEmpty()) return this;
        return partes.get(partes.size()-1).getUltimo();
    }
    public String getPosiciones(){
        return "["+(linea+1)+","+posicion+"]";
    }
    public void setDimensiones(int posicion, int fin, int linea){
        this.posicion = posicion;
        this.linea = linea;
        this.fin = fin;
    }
    public void setDimensiones(Nodo n){
        setDimensiones(n.posicion, n.fin, n.linea);
    }
}
