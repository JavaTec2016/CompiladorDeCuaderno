package lector;

import java.io.*;
import java.util.function.Consumer;

public class Reader {
    static String linea;
    static BufferedReader lec;

    public static void AbrirLector(String nombre) throws FileNotFoundException {
        String ruta = new File("").getAbsolutePath();
        String ruta2 = ruta.concat("/src/Archivos/").concat(nombre);
        System.out.println(ruta2);
        lec = new BufferedReader(new FileReader(ruta2));
    }
    public static void siguienteLinea() throws IOException {
        linea = lec.readLine();
    }
    public static void porCadaLinea(Consumer<String> call){
        lec.lines().forEach(call);
    }
}
