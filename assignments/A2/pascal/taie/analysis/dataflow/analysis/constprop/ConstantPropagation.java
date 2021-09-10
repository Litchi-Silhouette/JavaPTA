/*
 * Tai-e: A Static Analysis Framework for Java
 *
 * Copyright (C) 2020-- Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2020-- Yue Li <yueli@nju.edu.cn>
 * All rights reserved.
 *
 * Tai-e is only for educational and academic purposes,
 * and any form of commercial use is disallowed.
 * Distribution of Tai-e is disallowed without the approval.
 */

package pascal.taie.analysis.dataflow.analysis.constprop;

import pascal.taie.analysis.dataflow.analysis.AbstractDataflowAnalysis;
import pascal.taie.analysis.graph.cfg.CFG;
import pascal.taie.analysis.graph.cfg.Edge;
import pascal.taie.config.AnalysisConfig;
import pascal.taie.ir.IR;
import pascal.taie.ir.exp.ArithmeticExp;
import pascal.taie.ir.exp.BinaryExp;
import pascal.taie.ir.exp.BitwiseExp;
import pascal.taie.ir.exp.ConditionExp;
import pascal.taie.ir.exp.Exp;
import pascal.taie.ir.exp.IntLiteral;
import pascal.taie.ir.exp.ShiftExp;
import pascal.taie.ir.exp.Var;
import pascal.taie.ir.stmt.DefinitionStmt;
import pascal.taie.ir.stmt.If;
import pascal.taie.ir.stmt.Stmt;
import pascal.taie.ir.stmt.SwitchStmt;
import pascal.taie.language.type.PrimitiveType;
import pascal.taie.language.type.Type;
import pascal.taie.util.AnalysisException;

public class ConstantPropagation extends
        AbstractDataflowAnalysis<Stmt, CPFact> {

    public static final String ID = "constprop";

    public ConstantPropagation(AnalysisConfig config) {
        super(config);
    }

    @Override
    public boolean isForward() {
        return true;
    }

    @Override
    public CPFact newBoundaryFact(CFG<Stmt> cfg) {
        return newBoundaryFact(cfg.getIR());
    }

    public CPFact newBoundaryFact(IR ir) {
        // make conservative assumption about parameters: assign NAC to them
        CPFact entryFact = new CPFact();
        ir.getParams()
                .stream()
                .filter(this::canHoldInt)
                .forEach(p -> entryFact.update(p, Value.getNAC()));
        return entryFact;
    }

    @Override
    public CPFact newInitialFact() {
        return new CPFact();
    }

    @Override
    public void meetInto(CPFact fact, CPFact target) {
        fact.forEach((var, value) ->
                target.update(var, meetValue(value, target.get(var))));
    }

    /**
     * Meets two Values.
     */
    public Value meetValue(Value v1, Value v2) {
        if (v1.isUndef() && v2.isConstant()) {
            return v2;
        } else if (v1.isConstant() && v2.isUndef()) {
            return v1;
        } else if (v1.isNAC() || v2.isNAC()) {
            return Value.getNAC();
        } else if (v1.equals(v2)) {
            return v1;
        } else {
            return Value.getNAC();
        }
    }

    @Override
    public boolean transferNode(Stmt stmt, CPFact in, CPFact out) {
        if (stmt instanceof DefinitionStmt) {
            Exp lvalue = ((DefinitionStmt<?, ?>) stmt).getLValue();
            if (lvalue instanceof Var) {
                Var lhs = (Var) lvalue;
                Exp rhs = ((DefinitionStmt<?, ?>) stmt).getRValue();
                boolean changed = false;
                for (Var inVar : in.keySet()) {
                    if (!inVar.equals(lhs)) {
                        changed |= out.update(inVar, in.get(inVar));
                    }
                }
                return canHoldInt(lhs) ?
                        out.update(lhs, evaluate(rhs, in)) || changed :
                        changed;
            }
        }
        return out.copyFrom(in);
    }

    /**
     * @return true if the given variable can hold integer value, otherwise false.
     */
    public boolean canHoldInt(Var var) {
        Type type = var.getType();
        if (type instanceof PrimitiveType) {
            switch ((PrimitiveType) type) {
                case BYTE:
                case SHORT:
                case INT:
                case CHAR:
                case BOOLEAN:
                    return true;
            }
        }
        return false;
    }

    /**
     * Evaluates the {@link Value} of given expression.
     *
     * @param exp the expression to be evaluated
     * @param in  IN fact of the statement
     * @return the resulting {@link Value}
     */
    public static Value evaluate(Exp exp, CPFact in) {
        if (exp instanceof IntLiteral) {
            return Value.makeConstant(((IntLiteral) exp).getValue());
        } else if (exp instanceof Var) {
            return in.get((Var) exp);
        } else if (exp instanceof BinaryExp) {
            BinaryExp binary = (BinaryExp) exp;
            BinaryExp.Op op = binary.getOperator();
            Value v1 = evaluate(binary.getOperand1(), in);
            Value v2 = evaluate(binary.getOperand2(), in);
            // handle division-by-zero by returning UNDEF
            if ((op == ArithmeticExp.Op.DIV || op == ArithmeticExp.Op.REM) &&
                    v2.isConstant() && v2.getConstant() == 0) {
                return Value.getUndef();
            }
            if (v1.isConstant() && v2.isConstant()) {
                int i1 = v1.getConstant();
                int i2 = v2.getConstant();
                return Value.makeConstant(evaluate(op, i1, i2));
            } else if (v1.isNAC() || v2.isNAC()) {
                return Value.getNAC();
            }
            return Value.getUndef();
        }
        // return NAC for other cases
        return Value.getNAC();
    }

    private static int evaluate(BinaryExp.Op op, int i1, int i2) {
        if (op instanceof ArithmeticExp.Op) {
            switch ((ArithmeticExp.Op) op) {
                case ADD:
                    return i1 + i2;
                case SUB:
                    return i1 - i2;
                case MUL:
                    return i1 * i2;
                case DIV:
                    return i1 / i2;
                case REM:
                    return i1 % i2;
            }
        } else if (op instanceof BitwiseExp.Op) {
            switch ((BitwiseExp.Op) op) {
                case OR:
                    return i1 | i2;
                case AND:
                    return i1 & i2;
                case XOR:
                    return i1 ^ i2;
            }
        } else if (op instanceof ConditionExp.Op) {
            switch ((ConditionExp.Op) op) {
                case EQ:
                    return i1 == i2 ? 1 : 0;
                case NE:
                    return i1 != i2 ? 1 : 0;
                case LT:
                    return i1 < i2 ? 1 : 0;
                case GT:
                    return i1 > i2 ? 1 : 0;
                case LE:
                    return i1 <= i2 ? 1 : 0;
                case GE:
                    return i1 >= i2 ? 1 : 0;
            }
        } else if (op instanceof ShiftExp.Op) {
            switch ((ShiftExp.Op) op) {
                case SHL:
                    return i1 << i2;
                case SHR:
                    return i2 >> i2;
                case USHR:
                    return i1 >>> i2;
            }
        }
        throw new AnalysisException("Unexpected op: " + op);
    }
}