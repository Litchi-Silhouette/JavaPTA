package pku;

import java.util.HashMap;

import pascal.taie.ir.IR;
import pascal.taie.ir.exp.IntLiteral;
import pascal.taie.ir.exp.InvokeStatic;
import pascal.taie.ir.exp.Var;
import pascal.taie.ir.stmt.Invoke;
import pascal.taie.ir.stmt.New;

import java.util.Map;
import pku.abs.*;

public class PreprocessResult {

    public final Map<New, AbstractMalloc> objs;
    public final Map<Integer, Var> test_pts;
    public final AbstractMallocDomain mallocDomain;

    public PreprocessResult() {
        objs = new HashMap<New, AbstractMalloc>();
        test_pts = new HashMap<Integer, Var>();
        mallocDomain = new AbstractMallocDomain();
    }

    /**
     * Benchmark.alloc(id);
     * X x = new X;// stmt
     * 
     * @param stmt statement that allocates a new object
     * @param id   id of the object allocated, -1 if not labeled
     */
    public void alloc(New stmt, int id) {
        var malloc = mallocDomain.addMalloc(id, stmt.getLValue().getType());
        objs.put(stmt, malloc);
    }

    /**
     * Benchmark.test(id, var)
     * 
     * @param id id of the testing
     * @param v  the pointer/variable
     */
    public void test(int id, Var v) {
        test_pts.put(id, v);
    }

    /**
     * @param id
     * @return the pointer/variable in Benchmark.test(id, var);
     */
    public Var getTestPt(int id) {
        return test_pts.get(id);
    }

    /**
     * analysis of a JMethod, the result storing in this
     * 
     * @param ir ir of a JMethod
     */
    public void analysis(IR ir) {
        var stmts = ir.getStmts();
        Integer id = 0;
        for (var stmt : stmts) {

            if (stmt instanceof Invoke) {
                var exp = ((Invoke) stmt).getInvokeExp();
                if (exp instanceof InvokeStatic) {
                    var methodRef = ((InvokeStatic) exp).getMethodRef();
                    var className = methodRef.getDeclaringClass().getName();
                    var methodName = methodRef.getName();
                    if (className.equals("benchmark.internal.Benchmark")
                            || className.equals("benchmark.internal.BenchmarkN")) {
                        if (methodName.equals("alloc")) {
                            var lit = exp.getArg(0).getConstValue();
                            assert lit instanceof IntLiteral;
                            id = ((IntLiteral) lit).getNumber();
                        } else if (methodName.equals("test")) {
                            var lit = exp.getArg(0).getConstValue();
                            assert lit instanceof IntLiteral;
                            var test_id = ((IntLiteral) lit).getNumber();
                            var pt = exp.getArg(1);
                            this.test(test_id, pt);
                        }
                    }

                }
            } else if (stmt instanceof New) {
                this.alloc((New) stmt, id);
                id = 0;
            }
        }
    }
}
