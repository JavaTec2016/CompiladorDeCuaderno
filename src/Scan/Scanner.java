package Scan;

import sintactico.NodoBinario;
import sintactico.NodoTipable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scanner {

    /**
     * Busca un nodo hijo con el token especificado
     * @param n nodo padre
     * @param target token a buscar
     * @return primera coincidencia del token, o null si no se encuentra
     */
    public static NodoTipable buscar(NodoTipable n, String target){
        NodoTipable nodo = n;
        while(nodo.parent != null){
            nodo = nodo.parent;
            if(nodo.token.equals(target)) return nodo;
        }
        return null;
    }

    /**
     * Recopila de izquierda a derecha los nodos de términos, operadores y parentesis de una expresión
     * @param nodo nodo a escanear
     * @return lista de nodos encontrados en orden
     */
    public static ArrayList<NodoBinario> scanAritmetica(NodoTipable nodo){
        System.out.println("ESCANEO ARITM>>>>");
        ArrayList<NodoBinario> nodos = new ArrayList<>();
        String[] coins = {"termino", "arm", "log", "\\(", "\\)"};
        String pattern = String.join("||", coins);
        Pattern p = Pattern.compile(pattern);
        nodo.accionPreorden(nodoBinario -> {
            //System.out.println("nodo: " + nodoBinario.parent + ", actual: " + nodoBinario);
            if(p.matcher(nodoBinario.token).matches()){
                System.out.println("ENCONTRADO: " + nodoBinario.fuente);
                nodos.add(nodoBinario);
            }
        });
        return nodos;
    }
}
