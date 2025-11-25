package sintactico; /**
 * Materia: Lenguajes y Autómatas I
 * Tema: Análisis sintáctico
 * Nombre del programa: ANALIZADOR SINTÁCTICO
 * Alumno: Santiago Dominik Bañuelos de la Torre
 * Fecha: Miércoles 14/05/25
 */

import acciones.Accionable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class AnalizadorSintactico {
    public static Stack<String> tokens = new Stack<>();
    public static Stack<NodoBinario> nodos = new Stack<>();
    public static HashMap<String, String> incompletos = new HashMap<>();
    //desapilar token por token y agregar al principio, obteniendo todas las gramaticas que coinciden
    //obtener la gramatica resumible más extensa
    public static NodoBinario desapilar(NodoBinario nodo) throws Exception {
        StringBuilder combinacion = new StringBuilder();
        String combinacionLimpia = "";
        String coincidenciaFuente = "";

        Stack<NodoBinario> copia = new Stack<>();
        String coincidencia = null;
        nodos.push(nodo);
        System.out.println("PUSH " + nodo.token);
        //tokens.push(nodo.token);
        for (NodoBinario n : nodos){
            copia.push(n);
        }
        int i = 0;
        int desapilables = 0;
        while(!copia.isEmpty()){
            combinacion.insert(0, copia.pop().token+" ");
            combinacionLimpia = combinacion.substring(0, combinacion.length()-1).toString();

            String gramatica = Gramatica.probarGramatica(combinacionLimpia);
            i++;
            if(gramatica != null){
                coincidencia = gramatica;
                coincidenciaFuente = combinacionLimpia;
                desapilables = i;
            }
        }
        tokens = getTokens();
        System.out.println(coincidencia + " , " + coincidenciaFuente);
        if(coincidencia != null) return convertir(coincidencia, coincidenciaFuente, desapilables);
        return null;
    }
    public static Stack<String> getTokens(){
        Stack<String> n = new Stack<>();
        nodos.forEach(nodo->{
            n.push(nodo.token);
        });
        return n;
    }
    public static NodoBinario convertir(String coincidencia, String combinacion, int cantidad) throws Exception {
        NodoBinario nodoGeneral = null;
        ArrayList<NodoBinario> elementos = new ArrayList<>();
        Accionable accionSemantica = Gramatica.getAccion(combinacion);

        //System.out.println("AAAAAA" + Gramatica.acciones.get("FOR ("));


        if(coincidencia != null){
            System.out.println("DESAPILANDO TOKENS> " + tokens.toString());
            //nodoGeneral = new Nodo(combinacion, coincidencia);
            while (cantidad > 0){
                //tokens.pop();
                elementos.add(0, nodos.pop());
                cantidad--;
            }
            nodoGeneral = (NodoBinario)accionSemantica.realizar(combinacion, coincidencia, elementos);
            if(nodoGeneral == null) {
                throw new Exception("NODO NULO: " + combinacion + " > " + elementos);
            }
            nodos.push(nodoGeneral);
            //tokens.push(nodoGeneral.token);
            System.out.println("nodos: " + nodos.size());
        }
        return nodoGeneral;
    }
    /**
     * todo:
     * llevar seguimiento de en qué se está convirtiendo cada token usando Nodo, ya esta
     * llevar seguimiento de a qué gramática general se unen los tokens usando Nodo, ya esta
     * @param token el siguiente token a comprobar con la pila actual
     * @return true si el token fue unido a una gramática general, false si no
     */
    public static boolean procesarToken(String token){
        //reemplazado por desapilar
        StringBuilder combinacion = new StringBuilder();
        //se extraen los tokens actuales de la pila en una combinacion con el token actual
        System.out.println("Actual: " + tokens.toString() + " >> " + token);
        for (String s : tokens) {
            combinacion.append(" "+s);
        }
        combinacion.append(" "+token);
        combinacion.delete(0, 1);

        System.out.println("Procesando>>>>> '" + combinacion+"'");
            //revisar si la pila + el token cumplen una gramatica
        String gramatica = Gramatica.probarGramatica(combinacion.toString());
        if(gramatica == null){  //si no entonces se apila el token y se retorna fols
            System.out.println("push: " + token);
            tokens.push(token);
            return false;
        } else {    //si si entonces se vacia la pila, se apila la izquierda de la gramática y se retorna tru
            System.out.println("identificado: " + token + " >>> " + gramatica);
            while (!tokens.isEmpty()) tokens.pop();
            tokens.push(gramatica);
            return true;
        }
    }
    public static void desapilarTodo(ArrayList<NodoBinario> nodosLista) throws Exception {

        ArrayList<NodoBinario> nodoCopia = nodosLista;
        boolean coincidencia;
        int vuelta = 1;
        do {
            coincidencia = false;
            System.out.println("////////////VUELTA NUMERO: " + vuelta);
            for(NodoBinario n : nodoCopia){
                System.out.println("--------");
                System.out.println("token >>> " + n.token);
                if(desapilar(n) != null) coincidencia = true;
                System.out.println(nodos.toString());
            }
            nodoCopia = new ArrayList<>(nodos);

            nodos.clear();
            vuelta++;

            System.out.println(nodoCopia.size() + ", modificacion: " + coincidencia);
        }while(nodoCopia.size() > 1 && coincidencia);
        nodos.addAll(nodoCopia);
    }
    public static void iniciar(){
        Gramatica.iniciarReglas();
        Gramatica.compilarReglas();
    }
    public static void main(String[] args) {
        Gramatica.iniciarReglas();
        Gramatica.compilarReglas();
        ///recibe los tokens de la linea "public class Ball {"
        String[] pruebanteCrudo = new String[]{"reservada", "reservada", "variable", "{"};

        //public static void main(String[] args){
        //  double _Lamamba23=a*k
        // }
        String[] pruebanteMain = new String[]{
                "reservada", "reservada", "reservada", "variable", "(", "reservada", "[", "]", "variable", ")", "{",
                    "reservada", "variable", "=", "variable", "operador", "variable",
                "}"};
        //desapilarTodo(pruebanteMain);

        System.out.println("////////ARBOL SINTACTICO//////");
        for (Nodo nodo : nodos) {
            System.out.println("NODO: " + nodo.fuente);
            nodo.printDescendencia();

        }
    }
    public static void marcarIncompleto(String gramatica, String razon){
        incompletos.put(gramatica, razon);
    }
    public static void removerIncompleto(String gramatica){
        incompletos.remove(gramatica);
    }
    public static void setErrores(){
        marcarIncompleto("asignacionPendiente", "Asignacion incompleta");
        marcarIncompleto("variable", "Variable flotando");
        marcarIncompleto("operador", "Operador flotando");
        marcarIncompleto("expresion", "sentencia incompleta, se esperaba ';'");
    }
    public static void detectarErrores() throws Exception {
        if(nodos.size() < 2) return;
        for (NodoBinario nodo : nodos) {
            nodo.error();
        }
    }
}
