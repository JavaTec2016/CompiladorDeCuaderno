package archivos;

import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.options.Options;

public class GenerarJimple {
    public static void main(String[] args) {
        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_soot_classpath("./src"); // Ruta donde está compilado Ejemplo
        Options.v().set_output_dir("jimple_output");
        Options.v().set_output_format(Options.output_format_jimple);
        String classname = "FlujoDatos";
        SootClass sclass = Scene.v().loadClassAndSupport(classname);
        sclass.setApplicationClass();
        Scene.v().loadNecessaryClasses();

        PackManager.v().writeOutput();
        System.out.println("Jalo");
    }
}
