package sintactico;

public class Asignacion extends NodoTipable {
    NodoTipable declaracion;
    Sentencia valor;
    public Asignacion(String fuente, String token, NodoTipable declaracion, Sentencia valor) {
        super(fuente, token, declaracion.ambito, declaracion.tipo, declaracion.identificador, declaracion.alcance);
        this.declaracion = declaracion;
        this.valor = valor;

        agregar();
        setDimensiones(declaracion);
    }
    public Asignacion(String fuente, String token, Asignacion pendiente, Sentencia valor){
        this(fuente, token, pendiente.declaracion, valor);

    }
    @Override
    public void agregar() {
        super.agregar();
        setChild(declaracion);
        setChild(valor);
    }
}
