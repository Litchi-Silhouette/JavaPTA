package pku;

import java.util.List;
import java.util.ArrayList;
import pascal.taie.ir.stmt.Stmt;
import pascal.taie.ir.IR;

public class MethodConstraintResult {
    public StandardConstraintSet constraintSet;
    public AbstractVarDomain domain;
    public AbstractMallocDomain mallocDomain;
    public List<Stmt> leftStmts;

    public MethodConstraintResult(AbstractMallocDomain mallocDomain) {
        this.constraintSet = new StandardConstraintSet();
        this.domain = new AbstractVarDomain();
        this.leftStmts = new ArrayList<>();
        this.mallocDomain = mallocDomain;
    }

    public void analysis(IR ir) {

    }
}
