package archivos;
import soot.*;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.util.cfgcmd.CFGToDotGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.dot.DotGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class GenerarCFG {

    public static void main(String[] args) {

        // Configurar Soot
        Options.v().set_prepend_classpath(true);
        Options.v().set_soot_classpath("./src"); // Ruta donde está compilado Ejemplo
        Options.v().set_keep_line_number(true);
        Options.v().set_whole_program(true);

        // Cargar clase objetivo
        SootClass sc = Scene.v().loadClassAndSupport("FlujoDatos");
        sc.setApplicationClass();
        Scene.v().loadNecessaryClasses();

        // Seleccionamos el método a analizar
        SootMethod method = sc.getMethodByName("ejemplo");

        // Obtenemos el cuerpo del método
        Body body = method.retrieveActiveBody();

        // Crear el grafo de flujo de control
        UnitGraph graph = new ExceptionalUnitGraph(body);

        // Convertir a formato DOT
        CFGToDotGraph dotGraph = new CFGToDotGraph();
        String outputFilename = "cfg_ejemplo.dot";
        DotGraph dotOutput = dotGraph.drawCFG(graph, body);
        dotOutput.plot(outputFilename);



        System.out.println("CFG generado correctamente en: " + outputFilename);
        System.out.println("Puedes visualizarlo con Graphviz:");
        System.out.println("dot -Tpng cfg_ejemplo.dot -o cfg_ejemplo.png");
    }
}
