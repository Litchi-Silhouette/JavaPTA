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
        var result = new InterproceduralConstraintResult();
        var domain = new AbstractVarDomain();
        var world = World.get();
        var main = world.getMainMethod();
        var jclass = main.getDeclaringClass();
        List<Context> workList = new ArrayList<>();
        List<Integer> visited = new ArrayList<>();
        

        // TODO
        // You need to use `preprocess` like in PointerAnalysisTrivial
        // when you enter one method to collect infomation given by
        // Benchmark.alloc(id) and Benchmark.test(id, var)
        //
        // As for when and how you enter one method,
        // it's your analysis assignment to accomplish

        world.getClassHierarchy().applicationClasses().forEach(tjclass -> {
            tjclass.getDeclaredFields().forEach(field -> {
                if (field.isStatic()) {
                    domain.addField(new AbstractVar(0, null, field));
                }
            });

            tjclass.getDeclaredMethods().forEach(method -> {
                if (method.isAbstract())
                    return;
                Context context = new Context(null, method.getIR());
                int currentContextId = context.hashCode();

                // domain and preprocess continually updated
                var mcr = new MethodConstraintResult(preprocess, domain);
                System.out.println("Context: " + context.getName() + " " + currentContextId);
                mcr.analysis(context);

                // add all invoke contexts to worklist and add interprocedual constraints
                result.addInterprocedualConstraint(mcr, currentContextId, workList);
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
            var mcr = new MethodConstraintResult(preprocess, domain);
            System.out.println("Context: " + context.getName() + " " + currentContextId);
            mcr.analysis(context);

            // add all invoke contexts to worklist and add interprocedual constraints
            result.addInterprocedualConstraint(mcr, currentContextId, workList);
        }

        return super.analyze();
        // return result;
    }
}
