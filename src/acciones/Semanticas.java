package acciones;

import simbolo.FilaSimbolos;
import simbolo.TablaSimbolos;
import sintactico.Declaracion;
import sintactico.Nodo;
import sintactico.NodoBinario;
import sintactico.NodoTipable;

import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;

public class Semanticas {

    public static HashMap<String, Integer> tamanios = new HashMap<>();
    public static HashMap<String, String> enteros = new HashMap<>();
    public static HashMap<String, String> flotantes = new HashMap<>();

    public static String alcance = null;

    public static void cargarTipoDatos(){
        tamanios.put("char", 4);
        tamanios.put("byte", 8);
        tamanios.put("short", 16);
        tamanios.put("int", 32);
        tamanios.put("long", 64);
        tamanios.put("float", 32+64);
        tamanios.put("double", 64+64);

    }
    public static boolean revisarSimbolo(Nodo n){
        FilaSimbolos fila = TablaSimbolos.get(n.fuente);
        return fila != null;
    }
    public static boolean revisarSimbolo(String id){
        return TablaSimbolos.get(id) != null;
    }
    public static boolean insertarSimbolo(NodoTipable n){
        if(revisarSimbolo(n)) return false;
        TablaSimbolos.agregarNuevo(n);
        return true;
    }
    public static boolean insertarSimbolo(String id, NodoTipable n){
        if(revisarSimbolo(id)) return false;
        TablaSimbolos.agregarNuevo(id, n);
        return true;
    }
    public static void insertarSimbolo(String identificador, String tipo, String ambito, String alcance){
        TablaSimbolos.agregarSimbolo(new FilaSimbolos(identificador, tipo, ambito, alcance));
    }
    public static boolean probarAsignacion(Nodo tipoVariable, Nodo tipoValor){
        //enteros solo se les puede asignar otros enteros
        if(tipoVariable.fuente.equals(tipoValor.fuente)) return true;
        if(tamanios.containsKey(tipoVariable.fuente) && tamanios.containsKey(tipoValor.fuente)){
            return probarAlojamiento(tipoVariable.fuente, tipoValor.fuente);
        }
        return false;
    }

    /**
     * Comprueba si el primer tipo de dato puede alojar un valor del segundo tipo de dato
     * @param tipoVariable
     * @param tipoValor
     * @return
     */
    public static boolean probarAlojamiento(String tipoVariable, String tipoValor){
        if(tipoVariable.equals(tipoValor)) return true;
        if(!tamanios.containsKey(tipoVariable) || !tamanios.containsKey(tipoValor)) return false;
        return tamanios.get(tipoVariable) >= tamanios.get(tipoValor);
    }
    public static void setAlcance(String alcance){
        Semanticas.alcance = alcance;
    }
    public static String getAlcanceAnterior(NodoTipable n){
        NodoTipable nod = TablaSimbolos.getSimbolo(n.alcance);
        String out = n.alcance;
        while(nod != null && Objects.equals(n.alcance, nod.alcance)){
            nod = TablaSimbolos.getSimbolo(nod.alcance);
            out = nod.alcance;
        }
        return TablaSimbolos.getSimbolo(out).alcance;
    }
    public static boolean probarOperacion(String tipo1, Nodo operador, String tipo2){
        //todo: logica de revisino
        if(operador.fuente.equals("arm")){
            return tamanios.containsKey(tipo1) && tamanios.containsKey(tipo2);
        }
        else if(operador.fuente.equals("log")){
            return !tipo1.equals("string") || !tipo2.equals("string");
        }
        return false;
    }
    public static String obtenerTipoOperacion(String tipo1, Nodo op, String tipo2){
        if(tipo1.equals("string") || tipo2.equals("string")) return "string";
        if(op.fuente.equals("log")) return "boolean";
        if(tamanios.get(tipo1) < tamanios.get(tipo2)) return tipo2;
        return tipo1;
    }
    public static boolean revisarJerarquia(String alcanceVariable, String alcanceActual){
        String actual = alcanceActual;
        //if(actual.equals("for2")) System.out.println("ALCANCES >> "+ alcanceVariable);
        //incrementar el alcanceActual hasta encontrar el alcance de la variable
        //si el alcance incrementado llega a nulo sin coincidir, sale flase
        System.out.println("MATCHEAR: " + alcanceVariable + " con " + alcanceActual);
        while (actual != null && !Objects.equals(actual, alcanceVariable)){
            actual = TablaSimbolos.get(actual).alcance;
        }
        System.out.println("encontrado: " + actual);
        //if(alcanceVariable.equals("for2") && alcanceActual.equals("for1")) new Stack<>().pop();
        return Objects.equals(actual, alcanceVariable);
    }

}
