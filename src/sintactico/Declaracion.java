package sintactico;

import lexico.Token;

public class Declaracion extends NodoTipable{
    public Declaracion(String fuente, String token, NodoBinario ambito,  NodoBinario tipo, NodoBinario variable, String alcance){
        super(fuente, token, ambito, tipo, variable, alcance);
        this.ambito = ambito;
        if(this.ambito == null) this.ambito = new NodoBinario("public", "ambito", tipo.posicion, tipo.posicion+1, tipo.linea);
        this.tipo = tipo;
        identificador = variable;
        this.alcance = alcance;

        agregar();
    }
}
