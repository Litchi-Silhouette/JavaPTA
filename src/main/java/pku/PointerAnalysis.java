package pku;

import java.util.TreeSet;

import pascal.taie.World;
import pascal.taie.config.AnalysisConfig;
import pku.abs.*;

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

        var methodResult = new MethodConstraintResult(preprocess, domain);
        methodResult.analysis(main.getIR());

        if (!methodResult.leftStmts.isEmpty()) {
            logger.info("Left stmts: {}", methodResult.leftStmts);
            var objs = new TreeSet<>(preprocess.mallocDomain.mallocs);
            preprocess.test_pts.forEach((test_id, pt) -> {
                result.put(test_id, objs);
            });

            dump(result);
        } else {
            methodResult.constraintSet.print();
            var set = new StandardConstraintSet();
            methodResult.constraintSet.allhas_constraint.forEach(constraint -> {
                set.addStandardForallInLeftContainsRightConstraint(
                        new StandardForallInLeftContainsRight(constraint.left, constraint.right,
                                new ConvertToField(methodResult.domain, constraint.field)));
            });
            methodResult.constraintSet.allin_constraint.forEach(constraint -> {
                set.addStandardForallInRightSubsetLeftConstraint(
                        new StandardForallInRightSubsetLeft(constraint.left, constraint.right,
                                new ConvertToField(methodResult.domain, constraint.field)));
            });
            HashMap<Integer, StandardSimple> simple = new HashMap<>();
            methodResult.constraintSet.sime_constraint.forEach(constraint -> {
                if (!simple.containsKey(constraint.left)) {
                    simple.put(constraint.left, new StandardSimple(constraint.left));
                }
                simple.get(constraint.left).addElement(constraint.rightElement);
            });
            methodResult.constraintSet.sims_constraint.forEach(constraint -> {
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
                var pt_obj = new AbstractVar(0, pt, null);
                Integer id = methodResult.domain.getVarIndex(pt_obj);
                System.out.println("testid: " + test_id + " id: " + id);
                set.printInfo(id);
                set.getInfo(id).forEach(index -> {
                    objs.add(methodResult.preprocess.mallocDomain.index2malloc.get(index).value);
                });
                System.out.println("objs: " + objs);
                result.put(test_id, objs);
            });

            dump(result);
        }
        // return result;
        return result;
    }
}
