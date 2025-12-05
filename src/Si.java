import Scan.Aritmo;
import acciones.Semanticas;
import lexico.AnalizadorLexico;
import simbolo.TablaSimbolos;
import sintactico.AnalizadorSintactico;

import java.io.FileNotFoundException;

public class Si {
    public static void main(String[] args) throws Exception {
        Semanticas.cargarTipoDatos();
        try {
            AnalizadorLexico.tokenizar("asignacion.txt");
        } catch (FileNotFoundException e) {
            System.err.println("El archivo no se encuentra");
            return;
        } catch (Exception e){
            System.err.println(e.getMessage());
            return;
        }
        AnalizadorSintactico.iniciar();
        AnalizadorSintactico.desapilarTodo(AnalizadorLexico.serializar());
        AnalizadorSintactico.detectarErrores();
        System.out.println(AnalizadorSintactico.nodos);
        System.out.println("=======TABLA DE SIMBOLOS======");
        TablaSimbolos.print();
        Aritmo.printExps();
    }
}
