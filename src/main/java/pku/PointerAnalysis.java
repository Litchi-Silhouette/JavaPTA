package pku;

import static pku.PointerAnalysisTrivial.logger;

import pascal.taie.World;
import pascal.taie.analysis.ProgramAnalysis;
import pascal.taie.config.AnalysisConfig;
import pku.abs.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import pku.Context;
import pascal.taie.ir.IR;
import pascal.taie.ir.stmt.Invoke;
import pascal.taie.language.classes.JMethod;

public class PointerAnalysis extends PointerAnalysisTrivial {
    public static final String ID = "pku-pta";

    public PointerAnalysis(AnalysisConfig config) {
        super(config);
    }

    @Override
    public PointerAnalysisResult analyze() {
        var PTAresult = new PointerAnalysisResult();
        var preprocess = new PreprocessResult();
        var interproceduralConstraintResult = new InterproceduralConstraintResult();
        var globalDomain = new AbstractVarDomain();
        var world = World.get();
        var main = world.getMainMethod();
        var jclass = main.getDeclaringClass();
        List<Context> workList = new ArrayList<>();
        List<Integer> visited = new ArrayList<>();
        

        world.getClassHierarchy().applicationClasses().forEach(tjclass -> {
            tjclass.getDeclaredFields().forEach(field -> {
                if (field.isStatic()) {
                    globalDomain.addField(new AbstractVar(0, null, field));
                }
            });

            tjclass.getDeclaredMethods().forEach(method -> {
                if (method.isAbstract())
                    return;
                Context context = new Context(null, method.getIR());
                int currentContextId = context.hashCode();

                // globalDomain and preprocess continually updated
                var mcr = new MethodConstraintResult(preprocess, globalDomain);
                System.out.println("Context: " + context.getName() + " " + currentContextId);
                mcr.analysis(context);

                interproceduralConstraintResult.updateInterprocedualConstraint(mcr, currentContextId, workList);
            });
        });
        
        // BFS, deal with 1-level context
        while (!workList.isEmpty()) {
            Context context = workList.remove(0);
            int currentContextId = context.hashCode();
            if (visited.contains(currentContextId)) {
                continue;
            } else {
                visited.add(currentContextId);
            }

            // domain and preprocess continually updated
            var mcr = new MethodConstraintResult(preprocess, globalDomain);
            System.out.println("Context: " + context.getName() + " " + currentContextId);
            mcr.analysis(context);

            interproceduralConstraintResult.updateInterprocedualConstraint(mcr, currentContextId, workList);
        }

        return super.analyze();
        // return result;
    }
}
