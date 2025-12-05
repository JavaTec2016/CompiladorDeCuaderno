package sintactico;

public class Asignacion extends NodoTipable {
    NodoTipable declaracion;
    Sentencia sentencia;
    public Asignacion(String fuente, String token, NodoTipable declaracion, Sentencia sentencia) {
        super(fuente, token, declaracion.ambito, declaracion.tipo, declaracion.identificador, declaracion.alcance);
        this.declaracion = declaracion;
        this.sentencia = sentencia;

        agregar();
        setDimensiones(declaracion);
    }
    public Asignacion(String fuente, String token, Asignacion pendiente, Sentencia sentencia){
        this(fuente, token, pendiente.declaracion, sentencia);

    }
    @Override
    public void agregar() {
        super.agregar();
        setChild(declaracion);
        setChild(sentencia);
    }
}
