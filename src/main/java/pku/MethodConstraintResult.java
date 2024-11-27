package pku;

import java.util.List;
import java.util.ArrayList;
import pascal.taie.ir.stmt.Stmt;
import pascal.taie.ir.IR;

public class MethodConstraintResult {
    public StandardConstraintSet constraintSet;
    public AbstractVarDomain domain;
    public List<Stmt> leftStmts;

    public MethodConstraintResult() {
        this.constraintSet = new StandardConstraintSet();
        this.domain = new AbstractVarDomain();
        this.leftStmts = new ArrayList<>();
    }

    public void analysis(IR ir) {
        this.leftStmts.add(stmt);
    }
}
