package acciones;

import sintactico.Nodo;
import sintactico.NodoBinario;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Declaracion de accion semantica, el metodo {@link #realizar(String, String, ArrayList)} se ejecuta durante la accion semantica
 * @param <T> tipo de dato del retorno
 */
public interface Accionable<T> {
    /**
     * Accion semantica que se ejecuta durante el analisis sintactico
     * @param fuente combinacion de tokens
     * @param token gramatica que reemplaza
     * @param nodos nodos desapilados de la combinacion de tokens
     * @return {@link T} se define el tipo de dato a retornar cuando se declara la accion semantica
     */
    T realizar(String fuente, String token, ArrayList<NodoBinario> nodos) throws Exception;

}
