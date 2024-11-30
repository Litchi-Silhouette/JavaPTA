package pku;

import java.util.TreeSet;

import pascal.taie.World;
import pascal.taie.config.AnalysisConfig;
import pku.abs.*;
import pku.constraint.*;
import java.util.HashMap;

public class PointerAnalysis extends PointerAnalysisTrivial {
    public static final String ID = "pku-pta";

    public PointerAnalysis(AnalysisConfig config) {
        super(config);
    }

    @Override
    public PointerAnalysisResult analyze() {
        var result = new PointerAnalysisResult();
        var preprocess = new PreprocessResult();
        var domain = new AbstractVarDomain();
        var world = World.get();
        var main = world.getMainMethod();
        var jclass = main.getDeclaringClass();

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
                if (!method.isAbstract())
                    preprocess.analysis(method.getIR());
            });
        });

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
