package Scan;

import acciones.Semanticas;
import simbolo.TablaSimbolos;
import sintactico.NodoBinario;
import sintactico.NodoTipable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Aritmo {
//apostate
    static HashMap<String, Integer> jerarquia = new HashMap<>();
    static HashMap<String, String> expresiones = new HashMap<>();
    static HashMap<String, Object> valores = new HashMap<>();
    static int contador = 0;
    static {
        jerarquia.put("+", 1);
        jerarquia.put("-", 1);
        jerarquia.put("*", 2);
        jerarquia.put("/", 2);
        jerarquia.put("%", 2);
        jerarquia.put("^", 3);
        jerarquia.put("(", 5);
        jerarquia.put(")", 6);
    }
    /**
     * convierte una secuencia aritmetica infija de nodos a orden postfijo
     * @param nodosInfija secuencia infija de nodos
     * @return secuencia postfija
     */
    public static ArrayList<NodoBinario> nodosAPostFija(ArrayList<NodoBinario> nodosInfija){
        System.out.println("ORGANIZANDO ARITMO>>>>>>");
        ArrayList<NodoBinario> post = new ArrayList<>();
        Stack<NodoBinario> pila = new Stack<>();

        for (NodoBinario nodoBinario : nodosInfija) {
            System.out.println("PARA: " + nodoBinario.fuente);
            int jer/*sa*/ = getJerarquia(nodoBinario);
            if(jer == jerarquia.get("(")){
                System.out.println("APILA PARENTESIS: " + nodoBinario.fuente);
                pila.push(nodoBinario);
            }else if(jer == -1) {
                System.out.println("AGREGA OPERANDO/PARENTEIS: " + nodoBinario.fuente);
                post.add(nodoBinario);
            } else {//switch piraton pa no anidar ifs
                if(pila.isEmpty()) {
                    System.out.println("APILA PRIMER OPERADOR: " + nodoBinario.fuente);
                    pila.push(nodoBinario);
                    continue;
                }
                if(jer <= getJerarquia(pila.peek())) {
                    System.out.println("APILA OPERADOR: " + nodoBinario.fuente);
                    pila.push(nodoBinario);
                    continue;
                }
                /*
                 * Ultimo caso: operacion de mayor jerarquia
                 * si es parentesis cerrado: desapilar hasta el primer parentesis abierto, no agregar parentesis
                 *  si es operador: agregar y desapilar todos los operadores de menor jerarquia
                 */
                if(jer == jerarquia.get(")")){
                    while (getJerarquia(pila.peek()) != jerarquia.get("(")){
                        System.out.println("AGREGA OPERADOR PARENTESIS: " + pila.peek().fuente);
                        post.add(pila.pop());
                    }
                    pila.pop();//queda el parentesis, no deberia necesitar el chequeo
                    continue;
                }
                post.add(nodoBinario);
                while (!pila.isEmpty() && getJerarquia(pila.peek()) < jer){
                    System.out.println("AGREGA OPERADOR: " + pila.peek().fuente);
                    post.add(pila.pop());
                }
            }
        }
        while(!pila.isEmpty()){
            System.out.println("AGREGA OPERADOR FINAL: " + pila.peek().fuente);
            post.add(pila.pop());
        }
        printNodos(post);
        return post;
    }
    public static int getJerarquia(NodoBinario nodo){
        //no es operador
        if(nodo.token.equals("termino")) return -1;
        if(!jerarquia.containsKey(nodo.fuente)){
            System.out.println("Jerarquia no encontrada para '"+nodo.token+"': " + nodo.fuente);
            return -1;
        }
        return jerarquia.get(nodo.fuente);
    }
    public static String printNodos(ArrayList<NodoBinario> nds){
        StringBuilder out = new StringBuilder();
        for (NodoBinario nd : nds) {
            out.append(nd.fuente).append(" ");
        }
        System.out.println(out);
        return out.toString();
    }

    /**
     * Extrae los valores de cada nodo y los inserta en nuevos nodos para facilitar su acceso
     * @param post expresion en orden postfijo
     */
    public static ArrayList<NodoTipable> reemplazarSimbolos(ArrayList<NodoBinario> post){
        ArrayList<NodoTipable> out = new ArrayList<>();

        post.forEach(nodo->{
            NodoTipable value = new NodoTipable(null, nodo.token, nodo);

            if(nodo.fuente.equals("factor")) {
                value.fuente = nodo.get(0).get(0).fuente;
            }
            if(nodo.fuente.equals("simbolo")){
                String id = nodo.get(0).identificador;
                Object valor = TablaSimbolos.get(id).valor;
                value.fuente = valor.toString();
            }
            if(nodo.token.equals("arm") || nodo.token.equals("log")){
                value.fuente = nodo.fuente;
            }
            out.add(value);
        });
        return out;
    }
    public static void parse(ArrayList<NodoBinario> post){
        ArrayList<NodoTipable> values = reemplazarSimbolos(post);
        Stack<NodoBinario> pila = new Stack<>();
        //triplo
        NodoBinario op;
        NodoTipable t1;
        NodoTipable t2;

        for (NodoTipable nodoBinario : values) {
            System.out.print(nodoBinario.fuente + " ");
            int jer = getJerarquia(nodoBinario);
            if(jer == -1){
                pila.push(nodoBinario);
            }else {
                op = pila.pop();
                t2 = (NodoTipable) pila.pop();
                t1 = (NodoTipable) pila.pop();
                //tratar de resumir la operacion
                NodoTipable nod = crearExpresion(t1, op, t2);
                nod.setChild(t1);
                nod.setChild(op);
                nod.setChild(t2);
                pila.push(nod);
            }
        }
        System.out.println("Expresiones: ");
        expresiones.forEach((id, exp)->{
            System.out.println(id + ": " + exp + " = " + valores.get(id).toString());
        });
    }

    /**
     * Evalua un triplo y, si es posible, asigna el resultado a un nodo expresion y guarda el valor
     * @param t1 operando
     * @param op operador
     * @param t2 operando
     * @return nodo expresion con el resultado del triplo, o el triplo en texto si no se puede evaluar
     */
    public static NodoTipable crearExpresion(NodoTipable t1, NodoBinario op, NodoTipable t2){
        String id = generarId();
        //expresion
        String exp = t1.fuente + " " + op.fuente + " " + t2.fuente;
        //evaluada
        Double res = evaluar(t1.fuente, op.fuente, t2.fuente);
        if(res != null) {
            exp = res.toString();
        }
        //expresion evaluada existe?
        if(!expresiones.containsKey(exp)){
            //no, agregarla y retornar nodo resultante
            expresiones.put(exp, id);
            valores.put(id, res);
            return new NodoTipable(id, "expresion", t1);
        }else {
            //si, reemplazar por la id de la original
            exp = expresiones.get(exp);
            expresiones.put(exp, id);
            valores.put(id, exp);
            return new NodoTipable(id, "expresion", t1);
        }

    }
    public static Double evaluar(String n1, String op, String n2){
        if(n1.equals("null") || n2.equals("null")) return null;
        return switch (op) {
            case "+" -> Double.parseDouble(n1) + Double.parseDouble(n2);
            case "-" -> Double.parseDouble(n1) - Double.parseDouble(n2);
            case "*" -> Double.parseDouble(n1) * Double.parseDouble(n2);
            case "/" -> Double.parseDouble(n1) / Double.parseDouble(n2);
            case "^" -> Math.pow(Double.parseDouble(n1), Double.parseDouble(n2));
            default -> {
                System.out.println("Operador desconicido: " + op);
                yield null;
            }
        };
    }
    private static String generarId(){
        contador++;
        return "e"+contador;
    }
}
