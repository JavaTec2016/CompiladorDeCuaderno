package archivos;

public class FlujoDatos {
    public static int ejemplo(int a, int b){
        int c = a + 1;
        int d;
        if(c < b){
            d = c * 2;
            c = d - a;
        } else {
            d = b - 1;
        }
        return c + d;
    }

    public static void main(String[] args) {
        int resultado = ejemplo(3, 7);
        System.out.println("resultado: " + resultado);

        //java -cp soot-4.6.0-jar-with-dependencies.jar soot.Main -cp . -pp -process-dir . -f J -d salida
    }
}
