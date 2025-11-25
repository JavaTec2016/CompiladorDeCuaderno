package verificar;

import java.util.HashMap;

public class Compat {
    static HashMap<String, Integer> operadores;
    static HashMap<String, Integer> tipos;

    static void addOperador(String token, int valor){
        operadores.put(token, valor);
    }
    static void addTipo(String token, int valor){
        tipos.put(token, valor);
    }
    static boolean probarTipos(String t1, String t2){
        return false;
    }
}
