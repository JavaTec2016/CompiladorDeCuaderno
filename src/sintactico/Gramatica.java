package sintactico;

import Scan.Aritmo;
import Scan.Scanner;
import acciones.Accionable;
import acciones.Semanticas;
import lexico.Tipable;
import simbolo.TablaSimbolos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Stack;
import java.util.regex.Pattern;

public class Gramatica {
    public static LinkedHashMap<String, String> reglas = new LinkedHashMap<>();
    public static LinkedHashMap<String, Accionable> acciones = new LinkedHashMap<>();
    public static LinkedHashMap<Pattern, String> reglasCompiladas = new LinkedHashMap<>();

    public static void agregarRegla(String regla, String gramatica){
        reglas.put(regla, gramatica);
    }
    public static void lanzar(Nodo nodo, String msj) throws Exception {

        throw new Exception(nodo.getPosiciones() + " >> " + msj);
    }
    /**
     * Agrega una nueva regla gramatical y su respectiva accion semantica
     * @param regla combinacion de tokens
     * @param gramatica gramatica que reemplaza
     * @param semantica accion semantica, puede ser nulo
     */
    public static void agregarRegla(String regla, String gramatica, Accionable<?> semantica){
        reglas.put(regla, gramatica);
        acciones.put(regla, semantica);
    }
    public static void agregarRegla(String regla, String gramatica, String fuente, Accionable<?> semantica){
        reglas.put(regla, gramatica);
        acciones.put(fuente, semantica);
    }
    public static String getGramatica(String regla){
        return reglas.get(regla);
    }
    public static Accionable getAccion(String regla){
        return acciones.get(regla);
    }
    public static void iniciarReglasAlcance(){
        agregarRegla("\\[", "Corchete1");
        agregarRegla("\\]", "Corchete2");
        agregarRegla("\\(", "parentesis1");

    }
    public static void iniciarReglasLlaves(){
        agregarRegla("\\{", "llave1", "{", new Accionable<NodoTipable>() {

            @Override
            public NodoTipable realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                String id = TablaSimbolos.generarIdentificador("bloqueAnonimo");
                NodoTipable n = new NodoTipable(fuente, token, "private", "bloqueAnonimo", id, Semanticas.alcance, -1, -1);
                n.setDimensiones(nodos.get(0));
                n.setChild(nodos.get(0));
                Semanticas.insertarSimbolo(n);
                return n;
            }
        });
        agregarRegla("\\}", "llave2", "}", new Accionable<NodoBinario>() {

            @Override
            public NodoBinario realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                NodoTipable n = new NodoTipable(fuente, token, "private", "bloqueAnonimo", fuente, Semanticas.alcance, -1, -1);
                n.setDimensiones(nodos.get(0));
                n.setChild(nodos.get(0));
                if(Semanticas.alcance == null){
                    lanzar(n, "La llave '}' no cierra ningun bloque.");
                }
                Semanticas.setAlcance(Semanticas.getAlcanceAnterior(n));
                return n;
            }
        });
    }
    /**
     * Inicia reglas y acciones semanticas para declaraciones de variables
     */
    public static void iniciarReglasDeclaracion(){
        agregarRegla("tipo variable", "declaracion", (Accionable<NodoBinario>) (fuente, token, nodos) -> {
            Declaracion out = new Declaracion(fuente, token, null, nodos.get(0), nodos.get(1), Semanticas.alcance);
            out.linea = nodos.get(0).linea;
            out.posicion = nodos.get(0).posicion;
            if(Semanticas.revisarSimbolo(out.identificador)) {
                //error
                lanzar(out, "Variable duplicada '"+out.identificador.fuente+"'");
            }
            Semanticas.insertarSimbolo(out.identificador.fuente, out);
            return out;
        });
        agregarRegla("ambito tipo variable", "declaracion", (Accionable<Declaracion>) (fuente, token, nodos) -> {
            Declaracion out = new Declaracion(fuente, token, nodos.get(0), nodos.get(1), nodos.get(1), Semanticas.alcance);
            out.linea = nodos.get(0).linea;
            out.posicion = nodos.get(0).posicion;
            if(Semanticas.revisarSimbolo(out.identificador)) {
                //error
                lanzar(out, "Variable duplicada '"+out.identificador.fuente+"'");
            }
            Semanticas.insertarSimbolo(out.identificador.fuente, out);
            return out;
        });

    }
    public static void iniciarReglasVariable(){
        agregarRegla("variable", "simbolo", new Accionable<NodoTipable>() {
            @Override
            public NodoTipable realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                NodoBinario varr = nodos.get(0);
                if(!Semanticas.revisarSimbolo(varr.fuente)){
                    //error
                    lanzar(varr, "Variable no declarada '"+varr.fuente+"'");
                };
                NodoTipable out = new NodoTipable(fuente, token, TablaSimbolos.getSimbolo(varr.fuente));
                if(!Semanticas.revisarJerarquia(out.alcance, Semanticas.alcance)){
                    lanzar(varr,"La variable '"+out.identificador.fuente+"' esta fuera del alcance en el que fue definida ("+out.alcance+")");
                };
                out.setExceptionMensaje("No es una sentencia.");
                out.setChildren(nodos);
                return out;
            }
        });
    }
    public static void iniciarReglasAsignacion(){
        agregarRegla("declaracion =", "asignacionPendiente", new Accionable<Asignacion>() {
            @Override
            public Asignacion realizar(String fuente, String token, ArrayList<NodoBinario> nodos) {
                Asignacion out = new Asignacion(fuente, token, (NodoTipable)nodos.get(0),null);
                out.setExceptionMensaje("Asignacion incompleta a '"+out.identificador.fuente+"'");
                out.setChildren(nodos);
                return out;
            }
        });
        agregarRegla("simbolo =", "asignacionPendiente", new Accionable<Asignacion>() {
            @Override
            public Asignacion realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                NodoTipable varr = (NodoTipable)nodos.get(0);
                if(!Semanticas.revisarSimbolo(varr.identificador.fuente)){
                    //error
                    lanzar(varr, "Variable no declarada '"+varr.fuente+"'");
                };

                TablaSimbolos.print();
                Asignacion out = new Asignacion(fuente, token, TablaSimbolos.getSimbolo(varr.identificador.fuente),null);
                out.setExceptionMensaje("Asignacion incompleta a '"+out.identificador.fuente+"'");
                out.setChildren(nodos);
                return out;
            }
        });
        agregarRegla("asignacionPendiente sentenciaAsignable", "asignacion", new Accionable<Asignacion>() {
            //aqui se pueden hacer las cosas de deteccion y revision de operaciones
            @Override
            public Asignacion realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                Asignacion asg = (Asignacion)nodos.get(0);
                Sentencia valor = (Sentencia)nodos.get(1);
                if(!Semanticas.probarAsignacion(asg.tipo, valor.tipo)){
                    //error
                    lanzar(asg, "Tipos incompatibles para '"+asg.identificador.fuente+"' ("+asg.tipo.fuente+"): << " + valor.tipo.fuente);
                }
                Asignacion out = new Asignacion(fuente, token, asg, valor);
                out.setChildren(nodos);
                return out;
            }
        });

    }
    public static void iniciarReglasTermino(){
        agregarRegla("factor", "termino", (Accionable<NodoTipable>) (fuente, token, nodos) ->{
            NodoTipable factor = (NodoTipable)nodos.get(0);
            NodoTipable out = new NodoTipable(fuente, token, factor);
            out.setChildren(nodos);
            return out;
        });
        agregarRegla("cadena", "termino", (Accionable<NodoTipable>) (fuente, token, nodos) ->{
            NodoTipable out = new NodoTipable(fuente, token, "private", "string", fuente, Semanticas.alcance, nodos.get(0).posicion, nodos.get(0).linea);
            out.setChildren(nodos);
            return out;
        });
        agregarRegla("simbolo", "termino", (Accionable<NodoTipable>) (fuente, token, nodos) ->{
            NodoTipable varr = (NodoTipable)nodos.get(0);
            if(!Semanticas.revisarSimbolo(varr.identificador.fuente)){
                lanzar(nodos.get(0), "Variable no declarada: '"+varr.identificador.fuente+"'");
            }
            NodoTipable simbolo = TablaSimbolos.getSimbolo(varr.identificador.fuente);
            NodoTipable out = new NodoTipable(fuente, token, simbolo);
            out.setChildren(nodos);
            return out;
        });
    }
    public static void iniciarReglasFactor(){
        agregarRegla("entero", "factor", (Accionable<NodoTipable>) (fuente, token, nodos) ->{
            //todo: checar el tamanio del numero pa asignarle tipo
            NodoTipable out = new NodoTipable(fuente, token, "private", "int", fuente, Semanticas.alcance, nodos.get(0).posicion, nodos.get(0).linea);
            out.nodoFuente = nodos.get(0);
            System.out.println(out);
            out.setChildren(nodos);
            return out;
        });
        agregarRegla("doble", "factor", (Accionable<NodoTipable>) (fuente, token, nodos) ->{
            //todo: checar el tamanio del numero pa asignarle tipo
            NodoTipable out = new NodoTipable(fuente, token, "private", "double", fuente, Semanticas.alcance, nodos.get(0).posicion, nodos.get(0).linea);
            out.setChildren(nodos);
            return out;
        });
        agregarRegla("flotante", "factor", (Accionable<NodoTipable>) (fuente, token, nodos) ->{
            NodoTipable out = new NodoTipable(fuente, token, "private", "float", fuente, Semanticas.alcance, nodos.get(0).posicion, nodos.get(0).linea);
            out.setChildren(nodos);
            return out;
        });
    }
    public static void iniciarReglasExpresion(){

        agregarRegla("expresionOperador termino", "expresion", new Accionable<NodoTipable>() {
            @Override
            public NodoTipable realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                NodoTipable exp = (NodoTipable) nodos.get(0);
                NodoBinario op =  exp.get(1);
                NodoTipable term = (NodoTipable) nodos.get(1);
                if(!Semanticas.probarOperacion(exp.tipo.fuente, op, term.tipo.fuente)){
                    //error
                    lanzar(op, "Operacion incompatible: '"+op.get(0).fuente+"' para '"+exp.tipo.fuente+", '"+term.tipo.fuente+"'");
                }
                String tipoResult = Semanticas.obtenerTipoOperacion(exp.tipo.fuente, op, term.tipo.fuente);
                NodoBinario tipo = new NodoBinario(tipoResult, "tipo", exp.posicion, -1, exp.linea);
                NodoTipable out = new NodoTipable(fuente, token, exp.ambito, tipo, exp.identificador, exp.alcance);
                out.linea = exp.linea;
                out.posicion = exp.posicion;
                out.setChild(exp);
                out.setChild(term);
                out.setExceptionMensaje("No es una sentencia");
                return out;
            }
        });
        agregarRegla("expresion operador", "expresionOperador", new Accionable<NodoTipable>() {
            @Override
            public NodoTipable realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                NodoTipable exp = (NodoTipable) nodos.get(0);
                NodoTipable out = new NodoTipable(fuente, token, exp);
                System.out.println(exp.partes);
                out.setChild(exp);
                out.setChild(nodos.get(1));
                out.setExceptionMensaje("Operacion incompleta");
                return out;
            }
        });
        agregarRegla("termino", "expresion", new Accionable<NodoTipable>() {
            @Override
            public NodoTipable realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                NodoTipable term = (NodoTipable) nodos.get(0);
                NodoTipable out = new NodoTipable(fuente, token, term);
                out.setChild(term);
                out.setExceptionMensaje("No es una sentencia");
                return out;
            }
        });
        agregarRegla("\\( expresion \\)", "expresion", "( expresion )", new Accionable<NodoBinario>() {
            @Override
            public NodoBinario realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                NodoTipable exp = (NodoTipable) nodos.get(1);
                NodoTipable out = new NodoTipable(fuente, token, exp);
                out.setChildren(nodos);
                out.setExceptionMensaje("No es una sentencia");
                return out;
            }
        });
    }
    public static void iniciarReglasOperador(){
        agregarRegla("arm", "operador", new Accionable<NodoBinario>() {
            @Override
            public NodoBinario realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                NodoBinario out = new NodoBinario(nodos.get(0));
                out.setChild(nodos.get(0));
                out.fuente = fuente;
                out.token = token;
                out.setExceptionMensaje("Token inesperado '"+out.getPrimero().fuente+"'");
                return out;
            }
        });
        agregarRegla("log", "operador", new Accionable<NodoBinario>() {
            @Override
            public NodoBinario realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                NodoBinario out = new NodoBinario(nodos.get(0));
                out.setChild(nodos.get(0));
                out.fuente = fuente;
                out.token = token;
                out.setExceptionMensaje("Token inesperado '"+out.getPrimero().fuente+"'");
                return out;
            }
        });
    }
    public static void iniciarReglasSentencia(){
        agregarRegla("expresion ;", "sentenciaAsignable", new Accionable<Sentencia>() {
            @Override
            public Sentencia realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                Sentencia out = new Sentencia(fuente, token, (NodoTipable) nodos.get(0));
                out.setChild(nodos.get(0));
                out.setChild(nodos.get(1));
                out.setExceptionMensaje("Sentencia fuera de bloque");


                Aritmo.parse(Aritmo.nodosAPostFija(Scanner.scanAritmetica(out)));
                //System.out.println();
                return out;
            }
        });
        agregarRegla("asignacion", "sentencia", new Accionable<Sentencia>() {
            @Override
            public Sentencia realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                Sentencia out = new Sentencia(fuente, token, (NodoTipable) nodos.get(0));
                out.setChild(nodos.get(0));
                return out;
            }
        });
        agregarRegla("bloqueCiclo", "sentencia", new Accionable<Sentencia>() {
            @Override
            public Sentencia realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                Sentencia out = new Sentencia(fuente, token, (NodoTipable) nodos.get(0));
                out.setChild(nodos.get(0));
                return out;
            }
        });
    }
    public static void iniciarReglasFor(){
        agregarRegla("for", "FOR", new Accionable<NodoTipable>() {
            @Override
            public NodoTipable realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                Nodo forNodo = nodos.get(0);
                String id = TablaSimbolos.generarIdentificador("for");
                System.out.println(forNodo.posicion);
                NodoTipable out = new NodoTipable(fuente, token, "private", "for", id, Semanticas.alcance, forNodo.posicion, forNodo.linea);
                out.setDimensiones(forNodo);

                out.setExceptionMensaje("ciclo for incompleto.");
                Semanticas.insertarSimbolo(id, out);
                return out;
            }
        });
        agregarRegla("FOR \\(", "forInicio", "FOR (", new Accionable<NodoTipable>() {
            @Override
            public NodoTipable realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                NodoTipable nodoFor = (NodoTipable)nodos.get(0);
                System.out.println(nodoFor);
                //new Stack<>().pop();
                NodoTipable out = new NodoTipable(fuente, token, nodoFor);
                out.setChild(nodos.get(0));
                out.setChild(nodos.get(1));
                out.identificador = nodoFor.identificador;
                Semanticas.setAlcance(nodoFor.identificador.fuente);
                return out;
            }
        });
        agregarRegla("forInicio sentencia sentenciaAsignable sentencia", "forConfig", new Accionable<NodoTipable>() {
            @Override
            public NodoTipable realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                NodoTipable nodoFor = (NodoTipable) nodos.get(0);
                Sentencia declaraciones = (Sentencia) nodos.get(1);
                Sentencia condicion = (Sentencia) nodos.get(2);
                NodoTipable incremento = (NodoTipable) nodos.get(3);

                NodoTipable out = new NodoTipable(fuente, token, nodoFor);
                out.setChild(nodoFor);
                out.setChild(declaraciones);
                out.setChild(condicion);
                out.setChild(incremento);
                if(!Semanticas.probarAlojamiento("boolean", condicion.tipo.fuente)){

                    lanzar(condicion, "la sentencia condicional no es booleana");
                }
                return out;
            }
        });
        agregarRegla("forInicio sentencia sentencia sentencia", "forNoCondicion", new Accionable<Object>() {
            @Override
            public Object realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                lanzar(nodos.get(2), "No es una condicion");
                return null;
            }
        });
        agregarRegla("forConfig \\)", "forSentencia", "forConfig )", new Accionable<NodoTipable>() {
            @Override
            public NodoTipable realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                NodoTipable forConfig = (NodoTipable) nodos.get(0);
                NodoTipable out = new NodoTipable(fuente, token, forConfig);
                out.setChild(forConfig);
                out.setChild(nodos.get(1));
                out.setExceptionMensaje("ciclo for sin cuerpo");
                return out;
            }
        });
    }
    public static void iniciarReglasBloqueCiclo(){
        agregarRegla("forSentencia llave1", "bloqueCicloInicio", new Accionable<NodoTipable>() {
            @Override
            public NodoTipable realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                NodoTipable forSentencia = (NodoTipable) nodos.get(0);
                NodoTipable out = new NodoTipable(fuente, token, forSentencia.ambito.fuente, "bloque", forSentencia.identificador.fuente, Semanticas.alcance, -1, -1);
                out.linea = forSentencia.linea;
                out.posicion = forSentencia.posicion;
                out.setChild(forSentencia);
                out.setChild(nodos.get(1));
                out.setExceptionMensaje("Bloque sin cerrar (Se esperaba '}')");
                return out;
            }
        });

        agregarRegla("bloqueCicloInicio llave2", "bloqueCiclo", new Accionable<NodoTipable>() {

            @Override
            public NodoTipable realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                NodoTipable bloq = (NodoTipable)nodos.get(0);
                NodoTipable out = new NodoTipable(fuente, token, bloq);
                out.setChild(bloq);
                out.setChild(nodos.get(1));
                return out;
            }
        });
        agregarRegla("bloqueCicloInicio sentencia", "bloqueCicloCuerpo", new Accionable<NodoTipable>() {

            @Override
            public NodoTipable realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                NodoTipable bloq = (NodoTipable)nodos.get(0);
                NodoTipable out = new NodoTipable(fuente, token, bloq);
                out.setChild(bloq);
                out.setChild(nodos.get(1));
                out.setExceptionMensaje("Bloque sin cerrar (Se esperaba '}')");
                return out;
            }
        });
        agregarRegla("bloqueCicloCuerpo sentencia", "bloqueCicloCuerpo", new Accionable<NodoTipable>() {
            @Override
            public NodoTipable realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                NodoTipable out = (NodoTipable)nodos.get(0);
                out.setChild(nodos.get(1));
                out.setExceptionMensaje("Bloque sin cerrar (Se esperaba '}')");
                return out;
            }
        });
        agregarRegla("bloqueCicloCuerpo llave2", "bloqueCiclo", new Accionable<NodoTipable>() {
            @Override
            public NodoTipable realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception {
                NodoTipable bloq = (NodoTipable)nodos.get(0);
                NodoTipable out = new NodoTipable(fuente, token, bloq);
                out.setChild(bloq);
                out.setChild(nodos.get(1));
                return out;
            }
        });
    }

    public static void iniciarReglas(){
        //iniciarReglasTokens();
        iniciarReglasLlaves();
        iniciarReglasVariable();
        iniciarReglasDeclaracion();
        iniciarReglasAsignacion();
        iniciarReglasOperador();
        iniciarReglasFactor();
        iniciarReglasTermino();
        iniciarReglasExpresion();
        iniciarReglasSentencia();
        iniciarReglasFor();
        iniciarReglasBloqueCiclo();
    }
    public static void compilarReglas(){
        reglas.forEach((regla, gramatica) -> {
            reglasCompiladas.put(Pattern.compile(regla), gramatica);

        });
    }

    static public String probarGramatica(String combinacion){
        final String[] tipo = new String[]{null};
        reglasCompiladas.forEach((regla, gramatica) -> {
            if(tipo[0]!= null) return;
            //System.out.println("PROBANDO: '" + combinacion + "' << (" + regla +", "+ gramatica+")");
            if(regla.matcher(combinacion).matches()){
                tipo[0] = gramatica;
                System.out.println("ENCONTRADO: " + gramatica);
            }
        });
        return tipo[0];
    }

}
