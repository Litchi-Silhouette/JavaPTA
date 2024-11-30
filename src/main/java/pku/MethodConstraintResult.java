package pku;

import java.util.List;
import java.util.ArrayList;
import pascal.taie.ir.stmt.*;
import pascal.taie.language.classes.JField;
import pascal.taie.language.classes.JMethod;
import pascal.taie.language.type.*;
import pascal.taie.ir.IR;
import pascal.taie.ir.exp.InstanceFieldAccess;
import pascal.taie.ir.exp.IntLiteral;
import pascal.taie.ir.exp.InvokeSpecial;
import pascal.taie.ir.exp.InvokeStatic;
import pascal.taie.ir.exp.IntLiteral;
import pascal.taie.ir.exp.InvokeStatic;
import pascal.taie.ir.exp.StaticFieldAccess;
import pku.abs.*;
import pku.constraint.*;

public class MethodConstraintResult {
    public ConstraintSet constraintSet;
    public AbstractVarDomain domain;
    public final PreprocessResult preprocess;
    public List<Invoke> invokeStmts; // all invoke statements in the method

    public MethodConstraintResult(PreprocessResult preprocess, AbstractVarDomain domain) {
        this.constraintSet = new ConstraintSet();
        // this.domain = domain.clone();
        this.domain = domain;
        this.preprocess = preprocess;
        this.invokeStmts = new ArrayList<Invoke>();
    }

    public Boolean checkRef(Type src, Type dst) {
        if (src instanceof ReferenceType && dst instanceof ReferenceType) {
            return true;
        }
        return false;
    }

    public void analysis(Context context) {
        IR ir = context.getIR();
        preprocess.analysis(ir);
        var stmts = ir.getStmts();
        int currentContextId = context.hashCode();
        for (var stmt : stmts) {
            System.out.println(stmt);
            if (stmt instanceof New) {
                System.out.println("New");
                int malloc = preprocess.objs.get(stmt).count;
                var value = ((New) stmt).getLValue();
                AbstractVar var = new AbstractVar(currentContextId, value, null);
                var id = domain.checkAndAdd(var);
                domain.addMallocMapping(id, malloc);
                var constraint = new SimpleEConstraint(id, id);
                constraintSet.addSimpleEConstraint(constraint);
            } else if (stmt instanceof AssignLiteral) {
                continue;
            } else if (stmt instanceof Copy) {
                System.out.println("Copy");
                var src = ((Copy) stmt).getRValue();
                var dst = ((Copy) stmt).getLValue();
                if (!checkRef(src.getType(), dst.getType())) {
                    continue;
                }
                var srcVar = new AbstractVar(currentContextId, src, null);
                var dstVar = new AbstractVar(currentContextId, dst, null);
                var dstId = domain.checkAndAdd(dstVar);
                var srcId = domain.getVarIndex(srcVar);
                if (srcId == -1) {
                    System.err.println("srcVar not defined: " + srcVar);
                    srcId = domain.addVar(srcVar);
                }
                var constraint = new SimpleSConstraint(dstId, srcId);
                constraintSet.addSimpleSConstraint(constraint);
            } else if (stmt instanceof LoadField) {
                System.out.println("LoadField");
                var dst = ((LoadField) stmt).getLValue();
                var fieldaccess = ((LoadField) stmt).getFieldAccess();
                AbstractVar fieldVar;
                JField field = null;
                if (fieldaccess instanceof StaticFieldAccess) {
                    field = ((StaticFieldAccess) fieldaccess).getFieldRef().resolve();
                    if (!checkRef(field.getType(), dst.getType())) {
                        continue;
                    }
                    fieldVar = new AbstractVar(currentContextId, null, field);
                } else {
                    var base = ((InstanceFieldAccess) fieldaccess).getBase();
                    field = ((InstanceFieldAccess) fieldaccess).getFieldRef().resolve();
                    if (!checkRef(field.getType(), dst.getType())) {
                        continue;
                    }
                    fieldVar = new AbstractVar(currentContextId, base, null);
                }
                AbstractVar dstvar = new AbstractVar(currentContextId, dst, null);
                int dstId = domain.checkAndAdd(dstvar);
                int fieldId = domain.getVarIndex(fieldVar);
                if (fieldId == -1) {
                    fieldId = domain.addField(fieldVar);
                    System.err.println("field not defined: " + field);
                }
                if (fieldaccess instanceof StaticFieldAccess)
                    constraintSet.addSimpleSConstraint(new SimpleSConstraint(dstId, fieldId));
                else
                    constraintSet.addAllInConstraint(new AllInConstraint(dstId, fieldId, field));
            } else if (stmt instanceof StoreField) {
                System.out.println("StoreField");
                var src = ((StoreField) stmt).getRValue();
                var fieldaccess = ((StoreField) stmt).getFieldAccess();
                AbstractVar fieldVar;
                JField field = null;
                if (fieldaccess instanceof StaticFieldAccess) {
                    field = ((StaticFieldAccess) fieldaccess).getFieldRef().resolve();
                    if (!checkRef(field.getType(), src.getType())) {
                        continue;
                    }
                    fieldVar = new AbstractVar(currentContextId, null, field);
                } else {
                    var base = ((InstanceFieldAccess) fieldaccess).getBase();
                    field = ((InstanceFieldAccess) fieldaccess).getFieldRef().resolve();
                    if (!checkRef(field.getType(), src.getType())) {
                        continue;
                    }
                    fieldVar = new AbstractVar(currentContextId, base, null);
                }
                AbstractVar srcvar = new AbstractVar(currentContextId, src, null);
                int srcId = domain.checkAndAdd(srcvar);
                int fieldId = domain.getVarIndex(fieldVar);
                if (fieldId == -1) {
                    fieldId = domain.addField(fieldVar);
                    System.err.println("field not defined: " + field);
                }
                if (fieldaccess instanceof StaticFieldAccess)
                    constraintSet.addSimpleSConstraint(new SimpleSConstraint(fieldId, srcId));
                else
                    constraintSet.addAllHasConstraint(new AllHasConstraint(fieldId, srcId, field));
            } else if (stmt instanceof LoadArray) {
                var dst = ((LoadArray) stmt).getLValue();
                var array = ((LoadArray) stmt).getArrayAccess();
                var base = array.getBase();
                var index = array.getIndex();
                if (!checkRef(base.getType(), dst.getType())) {
                    continue;
                }
                AbstractVar dstvar = new AbstractVar(currentContextId, dst, null);
                AbstractVar basevar = new AbstractVar(currentContextId, base, null);
                int dstId = domain.checkAndAdd(dstvar);
                int baseId = domain.getVarIndex(basevar);
                if (baseId == -1) {
                    baseId = domain.addVar(basevar);
                    System.err.println("base not defined: " + base);
                }
                constraintSet.addSimpleSConstraint(new SimpleSConstraint(dstId, baseId));
            } else if (stmt instanceof StoreArray) {
                var src = ((StoreArray) stmt).getRValue();
                var array = ((StoreArray) stmt).getArrayAccess();
                var base = array.getBase();
                var index = array.getIndex();
                if (!checkRef(base.getType(), src.getType())) {
                    continue;
                }
                AbstractVar srcvar = new AbstractVar(currentContextId, src, null);
                AbstractVar basevar = new AbstractVar(currentContextId, base, null);
                int baseId = domain.checkAndAdd(basevar);
                int srcId = domain.getVarIndex(srcvar);
                if (srcId == -1) {
                    srcId = domain.addVar(srcvar);
                    System.err.println("src not defined: " + src);
                }
                constraintSet.addSimpleSConstraint(new SimpleSConstraint(baseId, srcId));
            } else if (stmt instanceof Binary) {
                continue;
            } else if (stmt instanceof Unary) {
                continue;
            } else if (stmt instanceof InstanceOf) {
                continue;
            } else if (stmt instanceof Cast) {
                continue;
            } else if (stmt instanceof Invoke) {
                var exp = ((Invoke) stmt).getInvokeExp();
                if (exp instanceof InvokeStatic) {
                    var methodRef = ((InvokeStatic) exp).getMethodRef();
                    var className = methodRef.getDeclaringClass().getName();
                    if (className.equals("benchmark.internal.Benchmark")
                            || className.equals("benchmark.internal.BenchmarkN")) {
                        continue; // ignore alloc and test when analyzing
                    }
                }
                invokeStmts.add((Invoke) stmt);
            } else if (stmt instanceof Return) {
                continue;
            } else if (stmt instanceof If) {
                continue;
            } else if (stmt instanceof Goto) {
                continue;
            } else if (stmt instanceof SwitchStmt) {
                continue;
            } else if (stmt instanceof Monitor) {
                continue;
            } else if (stmt instanceof Throw) {
                continue;
            } else if (stmt instanceof Catch) {
                continue;
            } else if (stmt instanceof Nop) {
                continue;
            } else {
                System.err.println("Unknown stmt: " + stmt);
            }
        }
    }
}
