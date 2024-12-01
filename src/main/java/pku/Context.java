package pku;

import pascal.taie.ir.IR;
import pascal.taie.ir.stmt.*;
import pascal.taie.ir.exp.*;

public class Context {
    
    private final int lineNumber;
    private final int index;
    private final String methodName;
    private final String className;
    private final String name;
    private final IR bodyIR;
    
    /**
     * Constructor of Context
     * @param stmt: the statement that this context is invoked, null if it is not invoked
     * @param ir: IR of the callee method
     * 
     */
    public Context(Stmt stmt, IR ir, int callerContextId) {
        if (stmt == null) {
            this.lineNumber = -1;
            this.index = -1;
        } else {
            this.lineNumber = stmt.getLineNumber();
            this.index = stmt.getIndex();
        }
        this.bodyIR = ir;
        this.methodName = ir.getMethod().getName();
        this.className = ir.getMethod().getDeclaringClass().getName();
        
        
        // for super init, generate a unique context
        if (stmt!=null && isSuper((Invoke)stmt)) {
            this.name = className + "." + methodName + "[" + index + '@' + lineNumber + ']' + "called in <" + callerContextId + ">";
        } else {
            this.name = className + "." + methodName + "[" + index + '@' + lineNumber + ']';
        }
    }

    public IR getIR() {
        return bodyIR;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Context)) {
            return false;
        }else return this.hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String getName() {
        return name;
    }

    /**
     * Check if the invoke is a super init
     * E.g. invokespecial %this.<test.A: void <init>()>();
     * @param invoke
     * @return
     */
    public static boolean isSuper(Invoke invoke) {

        InvokeExp invokeExp = invoke.getInvokeExp();
        if (invokeExp instanceof InvokeInstanceExp) {
            Var base = ((InvokeInstanceExp) invokeExp).getBase();
            try {
                return base.getName().equals("%this") && invoke.getMethodRef().getName().equals("<init>");
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
