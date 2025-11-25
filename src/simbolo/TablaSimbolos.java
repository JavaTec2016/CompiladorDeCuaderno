package simbolo;

import lexico.Token;
import sintactico.Nodo;
import sintactico.NodoTipable;

import java.util.HashMap;

public class TablaSimbolos {
    static HashMap<String, FilaSimbolos> tabla = new HashMap<>();
    static int anonimoContador = 0;
    public static void agregarSimbolo(FilaSimbolos fila){
        tabla.put(fila.identificador, fila);
    }
    public static FilaSimbolos get(String identificador){
        return tabla.get(identificador);
    }
    public static void agregarNuevo(NodoTipable n){
        agregarSimbolo(new FilaSimbolos(n.fuente, n.tipo.fuente, n.ambito.fuente, n.alcance, null, n));
    }
    public static void agregarNuevo(String id, NodoTipable n){
        agregarSimbolo(new FilaSimbolos(id, n.tipo.fuente, n.ambito.fuente, n.alcance, null, n));
    }
    public static String generarIdentificador(String fijo){
        anonimoContador++;
        return fijo+anonimoContador;
    }
    public static FilaSimbolos obtenerAlcanceDe(String id){
        String idAlcance = tabla.get(id).alcance;
        if(!tabla.containsKey(idAlcance)) return null;
        return tabla.get(idAlcance);
    }
    public static NodoTipable getSimbolo(String id){
        return tabla.get(id).nodo;
    }
    public static void print(){
        tabla.forEach((id, fila)->{
            System.out.println(id+":"+fila);
        });
    }
}
