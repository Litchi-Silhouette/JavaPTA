package pku;

import pascal.taie.ir.exp.Var;
import pascal.taie.ir.proginfo.MethodRef;
import pascal.taie.language.classes.JClass;

public class AbstractVar {
    public int count;
    public Var value;

    public AbstractVar(int count, Var value) {
        this.count = count;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractVar) {
            AbstractVar other = (AbstractVar) obj;
            return this.count == other.count && this.value.equals(other.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        var method = this.value.getMethod();
        var methodName = method.getName();
        var methodClassName = method.getDeclaringClass().getName();
        String name = methodClassName + "." + methodName +
                "#" + this.count + "#" + this.value.getName();
        return name.hashCode();
    }

}
