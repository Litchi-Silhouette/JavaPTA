package pku;

import java.util.TreeSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import pascal.taie.World;
import pascal.taie.config.AnalysisConfig;
import pku.abs.*;
import pku.constraint.*;

public class PointerAnalysis extends PointerAnalysisTrivial {
    public static final String ID = "pku-pta";

    public PointerAnalysis(AnalysisConfig config) {
        super(config);
    }

    @Override
    public PointerAnalysisResult analyze() {
        var result = new PointerAnalysisResult();
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


        interproceduralConstraintResult.constraintSet.print();

        var set = new StandardConstraintSet();
        interproceduralConstraintResult.constraintSet.allhas_constraint.forEach(constraint -> {
            set.addStandardForallInLeftContainsRightConstraint(
                    new StandardForallInLeftContainsRight(constraint.left,constraint.right,
                            new ConvertToField(globalDomain,constraint.field)));
        });
        interproceduralConstraintResult.constraintSet.allin_constraint.forEach(constraint -> {
            set.addStandardForallInRightSubsetLeftConstraint(
                    new StandardForallInRightSubsetLeft(constraint.left,constraint.right,
                            new ConvertToField(globalDomain,constraint.field)));
        });
        HashMap<Integer, StandardSimple> simple = new HashMap<>();
        interproceduralConstraintResult.constraintSet.sime_constraint.forEach(constraint -> {
            if (!simple.containsKey(constraint.left)) {
                simple.put(constraint.left, new StandardSimple(constraint.left));
            }
            simple.get(constraint.left).addElement(constraint.rightElement);
        });
        interproceduralConstraintResult.constraintSet.sims_constraint.forEach(constraint -> {
            if (!simple.containsKey(constraint.left)) {
                simple.put(constraint.left, new StandardSimple(constraint.left));
            }
            simple.get(constraint.left).addSubset(constraint.right);
        });
        simple.forEach((key, value) -> {
            set.addSimpleConstraint(value);
        });
        set.print();
        set.solve();

        preprocess.test_pts.forEach((test_id, pt) -> {
            var objs = new TreeSet<Integer>();
            List<Integer> ids = globalDomain.getIndexsByValue(pt);
            System.out.println("testid: " + test_id);
            for (int id : ids) {
                System.out.println("id: " + id);
                set.printInfo(id);
                set.getInfo(id).forEach(index -> {
                    objs.add(preprocess.mallocDomain.index2malloc.get(index).value);
                });
            }
            System.out.println("objs: " + objs);
            result.put(test_id, objs);
        });
        dump(result);

        return result;
    }
}
