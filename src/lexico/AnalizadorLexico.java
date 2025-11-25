/**
 * Materia: Lenguajes y Autómatas I
 * Tema: Análisis léxico
 * Nombre del programa: ANALIZADOR LÉXICO
 * Alumno: Santiago Dominik Bañuelos de la Torre
 * Fecha: Martes 05/05/25
 */
package lexico;

import lector.Reader;
import sintactico.Nodo;
import sintactico.NodoBinario;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class AnalizadorLexico {
    static HashMap<Integer, TreeMap<Integer, String[]>> lineasProcesadas = new HashMap<>();
    static HashMap<Integer, TreeMap<Integer, NodoBinario>> tokensProcesados = new HashMap<>();

    public static void tokenizar(String archivo) throws Exception {
        TablaTokens.precargar2();
        HashMap<Integer, TreeMap<Integer, NodoBinario>> mapeados = new HashMap<>();

            final int[] i = {0};
            Reader.AbrirLector(archivo);
            Reader.porCadaLinea(lineaFuente -> {
                //limpiar la linea
                String linea = TablaTokens.limpiarResidual(lineaFuente);
                mapeados.put(i[0], TablaTokens.mapearTokensLinea(linea, i[0]));

                i[0]++;
            });

            tokensProcesados = mapeados;
            if(detectarErrores()) throw new Exception("Se detuvo la compilacion");
            System.out.println("TABLA DE TOKENS:::::::::::::::::");
            tokensProcesados.forEach((linea, tks) ->{
                tks.forEach((posicion, token) -> {
                    //System.out.println(token);
                    System.out.print(token.token+" ");
                });
                System.out.println(" ");
            });
    }
    public static boolean detectarErrores(){
        AtomicBoolean fail = new AtomicBoolean(false);
        tokensProcesados.forEach((linea, tokens)-> {
            TablaTokens.buscarDesconocidos(tokens).forEach(token -> {
                fail.set(true);
                System.err.println("["+linea+", "+token.posicion+"]" +" ERROR: Simbolo desconocido \""+token.fuente+"\"");
            });
        });
        return fail.get();
    }
    public static ArrayList<NodoBinario> serializar(){
        ArrayList<NodoBinario> result = new ArrayList<>();
        tokensProcesados.forEach((linea, tokens) -> result.addAll(tokens.values()));
        return result;
    }
}
