package pku;

import java.util.List;
import java.util.ArrayList;
import pascal.taie.ir.stmt.*;
import pascal.taie.language.classes.JField;
import pascal.taie.language.type.*;
import polyglot.ast.Switch;
import pascal.taie.ir.IR;
import pascal.taie.ir.exp.InstanceFieldAccess;
import pascal.taie.ir.exp.StaticFieldAccess;

public class MethodConstraintResult {
    public StandardConstraintSet constraintSet;
    public AbstractVarDomain domain;
    public List<Stmt> leftStmts;
    public final PreprocessResult preprocess;

    public MethodConstraintResult(PreprocessResult preprocess, AbstractVarDomain domain) {
        this.constraintSet = new StandardConstraintSet();
        this.domain = domain.clone();
        this.leftStmts = new ArrayList<>();
        this.preprocess = preprocess;
    }

    public Boolean checkRef(Type src, Type dst) {
        if (src instanceof ReferenceType && dst instanceof ReferenceType) {
            return true;
        }
        return false;
    }

    public void analysis(IR ir) {
        var stmts = ir.getStmts();
        for (var stmt : stmts) {
            if (stmt instanceof New) {
                int malloc = preprocess.objs.get(stmt).count;
                var value = ((New) stmt).getLValue();
                AbstractVar var = new AbstractVar(0, value, null);
                var id = domain.checkAndAdd(var);
                constraintSet.addSimpleEConstraint(new SimpleEConstraint(id, malloc));
            } else if (stmt instanceof AssignLiteral) {
                continue;
            } else if (stmt instanceof Copy) {
                var src = ((Copy) stmt).getRValue();
                var dst = ((Copy) stmt).getLValue();
                if (!checkRef(src.getType(), dst.getType())) {
                    continue;
                }
                var srcVar = new AbstractVar(0, src, null);
                var dstVar = new AbstractVar(0, dst, null);
                var dstId = domain.checkAndAdd(dstVar);
                var srcId = domain.getVarIndex(srcVar);
                if (srcId == -1) {
                    System.err.println("srcVar not defined: " + srcVar);
                    srcId = domain.addVar(srcVar);
                }
                constraintSet.addSimpleSConstraint(new SimpleSConstraint(dstId, srcId));
            } else if (stmt instanceof LoadField) {
                var dst = ((LoadField) stmt).getLValue();
                var fieldaccess = ((LoadField) stmt).getFieldAccess();
                AbstractVar fieldVar;
                JField field = null;
                if (fieldaccess instanceof StaticFieldAccess) {
                    field = ((StaticFieldAccess) fieldaccess).getFieldRef().resolve();
                    if (!checkRef(field.getType(), dst.getType())) {
                        continue;
                    }
                    fieldVar = new AbstractVar(0, null, field);
                } else {
                    var base = ((InstanceFieldAccess) fieldaccess).getBase();
                    field = ((InstanceFieldAccess) fieldaccess).getFieldRef().resolve();
                    if (!checkRef(field.getType(), dst.getType())) {
                        continue;
                    }
                    fieldVar = new AbstractVar(0, base, null);
                }
                AbstractVar dstvar = new AbstractVar(0, dst, null);
                int dstId = domain.checkAndAdd(dstvar);
                int fieldId = domain.getVarIndex(fieldVar);
                if (fieldId == -1) {
                    fieldId = domain.addField(fieldVar);
                    System.err.println("field not defined: " + field);
                }
                if (fieldaccess instanceof StaticFieldAccess)
                    constraintSet.addSimpleSConstraint(new SimpleSConstraint(dstId, fieldId));
                else
                    constraintSet.addAllHasConstraint(new AllHasConstraint(dstId, fieldId, field));
            } else if (stmt instanceof StoreField) {
                var src = ((StoreField) stmt).getRValue();
                var fieldaccess = ((StoreField) stmt).getFieldAccess();
                AbstractVar fieldVar;
                JField field = null;
                if (fieldaccess instanceof StaticFieldAccess) {
                    field = ((StaticFieldAccess) fieldaccess).getFieldRef().resolve();
                    if (!checkRef(field.getType(), src.getType())) {
                        continue;
                    }
                    fieldVar = new AbstractVar(0, null, field);
                } else {
                    var base = ((InstanceFieldAccess) fieldaccess).getBase();
                    field = ((InstanceFieldAccess) fieldaccess).getFieldRef().resolve();
                    if (!checkRef(field.getType(), src.getType())) {
                        continue;
                    }
                    fieldVar = new AbstractVar(0, base, null);
                }
                AbstractVar srcvar = new AbstractVar(0, src, null);
                int srcId = domain.checkAndAdd(srcvar);
                int fieldId = domain.getVarIndex(fieldVar);
                if (fieldId == -1) {
                    fieldId = domain.addField(fieldVar);
                    System.err.println("field not defined: " + field);
                }
                if (fieldaccess instanceof StaticFieldAccess)
                    constraintSet.addSimpleSConstraint(new SimpleSConstraint(fieldId, srcId));
                else
                    constraintSet.addAllHasConstraint(new AllHasConstraint(srcId, fieldId, field));
            } else if (stmt instanceof LoadArray) {
                var dst = ((LoadArray) stmt).getLValue();
                var array = ((LoadArray) stmt).getArrayAccess();
                var base = array.getBase();
                var index = array.getIndex();
                if (!checkRef(base.getType(), dst.getType())) {
                    continue;
                }
                AbstractVar dstvar = new AbstractVar(0, dst, null);
                AbstractVar basevar = new AbstractVar(0, base, null);
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
                AbstractVar srcvar = new AbstractVar(0, src, null);
                AbstractVar basevar = new AbstractVar(0, base, null);
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
                leftStmts.add(stmt);
            } else if (stmt instanceof Return) {
                leftStmts.add(stmt);
            } else if (stmt instanceof If) {
                continue;
            } else if (stmt instanceof Goto) {
                continue;
            } else if (stmt instanceof Switch) {
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
