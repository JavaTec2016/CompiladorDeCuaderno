package lexico;

import com.sun.source.tree.Tree;
import simbolo.TablaSimbolos;
import sintactico.Nodo;
import sintactico.NodoBinario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TablaTokens {
    static LinkedHashMap<String, String> tokens = new LinkedHashMap<>();
    static LinkedHashMap<String, String> residual = new LinkedHashMap<>();
    static String tokenActual = null;
    static boolean tokenValido = false;

    public static void registrarToken(String nom, String exp){
        tokens.put(nom, exp);
    }
    public static void probar(String parte){

        tokens.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String token, String exp) {
                if(tokenValido) return;

                Pattern p = Pattern.compile(exp);
                if(p.matcher(parte).matches()) {
                    tokenActual = token;
                    tokenValido = true;
                }
            }
        });
    }

    public static void precargar(){
        registrarToken("reservada", "\\bclass\\b|\\bpublic\\b|\\bstatic\\b|\\bif\\b|\\bfor\\b|\\bwhile\\b|\\breturn\\b|\\bint\\b|\\bdouble\\b|" +
                "\\bchar\\b|\\bstring\\b|\\bswitch\\b|\\bbreak\\b|\\bcontinue\\b|\\bvoid\\b|\\bnew\\b");
        registrarToken("variable", "(_|[a-zA-Z])\\w*");
        registrarToken("operador", "\\+|-|\\*|\\/|\\^|%|\\|\\||&&|<|>|!|!=|=");
        registrarToken("parentesis", "\\(|\\)");
        registrarToken("corchete", "\\[|\\]");
        registrarToken("llave", "\\{|\\}");
        registrarToken("comentario", "\\/\\/+.*");
        registrarToken("espacio", "(^ *)|(  +)");
        registrarToken("desconocido", "[^ ]+");
    }
    public static void precargarLimpiables(){
        residual.put("comentario", "\\/\\/+.*");
        residual.put("espacio", "(^ *)|(  +)");
    }
    public static void precargarUnicos(){
        registrarToken("static", "\\bstatic\\b");
        registrarToken("return", "\\breturn\\b");
        registrarToken("new", "\\bnew\\b");
        registrarToken("if", "\\bif\\b");
        registrarToken("for", "\\bfor\\b");
        registrarToken("while", "\\bwhile\\b");
        registrarToken("switch", "\\bswitch\\b");
        registrarToken(",", ",");
        registrarToken(";", ";");
    }
    public static void precargarAlcance(){
        registrarToken("{", "\\{");
        registrarToken("}", "\\}");
        registrarToken("[", "\\[");
        registrarToken("]", "\\]");
        registrarToken("(", "\\(");
        registrarToken(")", "\\)");
    }
    public static void precargarTipos(){
        registrarToken("tipo", "\\bclass\\b|\\bchar\\b|\\bbyte\\b|\\bshort\\b|\\bint\\b|\\blong\\b|\\bfloat\\b|\\bdouble\\b|\\bvoid\\b|\\bstring\\b|\\bboolean\\b");
    }
    public static void precargarAmbitos(){
        registrarToken("ambito", "\\bpublic\\b|\\bprotected\\b|\\bprivate\\b");
    }
    public static void precargarFlujos(){
        registrarToken("flujo", "\\bbreak\\b|\\bcontinue\\b");
    }
    public static void precargarOperadores(){
        registrarToken("arm", "\\+|-|\\*|\\/|\\^|%");
        registrarToken("log","\\|\\||&&|!=|==|<=|>=|<|>|!");
        registrarToken("=", "=");
    }
    public static void precargarLexema(){

        registrarToken("cadena", "\\b\"(.)*\"\\b");
        registrarToken("flotante", "\\b((0\\.\\d+)|([1-9]+\\.\\d+))f\\b");
        registrarToken("doble", "\\b((0\\.\\d+)|([1-9]+\\.\\d+))\\b");
        registrarToken("entero", "\\b((\\d{1})|([1-9]+\\d))\\b");
        registrarToken("caracter", "'.'");

        //el mas general es el ultimo a probar
        registrarToken("variable", "\\b(_|[a-zA-Z])\\w*\\b");
    }
    public static void precargar2(){
        precargarLimpiables();
        precargarAmbitos();
        precargarUnicos();
        precargarTipos();
        precargarFlujos();
        precargarOperadores();
        precargarAlcance();
        precargarLexema();

        registrarToken(".", "\\.");
        registrarToken("desconocido", "[^ ]+");

    }
    public static String limpiarToken(String t, String tk){

        return t.replaceAll(tokens.get(tk), "");
    }
    public static String limpiarResidual(String t){
        t = t.replaceAll(residual.get("comentario"), "");
        return t.replaceAll(residual.get("espacio"), "");
    }
    public static String ocultarToken2(String t, String tk, String carac){

        Pattern p = Pattern.compile(tokens.get(tk));
        String result = t;
        Matcher m = p.matcher(t);
        while(m.find()){//cirugia de strings acrobacia
            char[] found = m.group().toCharArray();
            String oculto = "";
            for (int i = 0; i < found.length; i++) {
                oculto += carac;
            }

            //mocha el result para quitarle el caracter coincidencia
            String antes = result.substring(0, m.start());
            System.out.println("--------------");
            System.out.println("TODO "+ result);
            System.out.println("ENCONTRAO '" + m.group()+"' " + tk);
            System.out.println("START '"+ antes+"'");
            String depsues = result.substring(m.end());
            System.out.println("DESP '" + depsues+"'");


            //pegalo otra vez pero con el oculto
            result = antes + oculto + depsues;
        }
        return result;
    }

    /**
     * escanea una linea de texto e identifica las secuencias del token especificado
     * @param linea linea de texto a escanear
     * @param token token a identificar
     * @param y numero de linea del codigo
     * @return {@link TreeMap} con los tokens organizados por su {@link Token#posicion}
     */
    public static TreeMap<Integer, NodoBinario> probarPara(String linea, String token, int y){
        TreeMap<Integer, NodoBinario> results = new TreeMap<>();
        Pattern p = Pattern.compile(tokens.get(token));
        Matcher m = p.matcher(linea);
        if(token.equals(";")) System.out.println(linea);
        while(m.find()){
            NodoBinario t = new NodoBinario(m.group(), token, m.start(), m.end(), y);
            results.put(t.posicion, t);
        }
        return results;
    }
    public static TreeMap<Integer, NodoBinario> mapearTokensLinea(String linea, int y) {
        TreeMap<Integer, NodoBinario> results = new TreeMap<>();
        final String[] lineaFinal = {linea};
        //ponerlos todos todotes
        tokens.forEach((token, fuentes) -> {

            results.putAll(probarPara(lineaFinal[0], token, y));
            lineaFinal[0] = ocultarToken2(lineaFinal[0], token, " ");
        });
        return results;
    }
    public static ArrayList<NodoBinario> buscarDesconocidos(TreeMap<Integer, NodoBinario> linea){
        ArrayList<NodoBinario> results = new ArrayList<>();
        linea.forEach((posicion, token)-> {
            if(token.token.equals("desconocido")) results.add(token);
        });
        return results;
    }
}
