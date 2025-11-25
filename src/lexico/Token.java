package lexico;

public class Token {
    public String fuente;
    public String token;
    public int posicion;
    public int fin;
    public int linea;

    public Token(String fuente, String token, int posicion, int fin, int linea) {
        this.fuente = fuente;
        this.token = token;
        this.posicion = posicion;
        this.fin = fin;
        this.linea = linea;
    }

    @Override
    public String toString() {
        return "["+linea+", "+posicion+"] " + token + ": " + fuente;
    }
}
