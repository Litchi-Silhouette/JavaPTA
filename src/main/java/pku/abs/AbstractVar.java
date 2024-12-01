package pku.abs;

import pascal.taie.ir.exp.Var;
import pascal.taie.language.classes.JClass;
import pascal.taie.language.classes.JField;
import pascal.taie.language.classes.JMethod;

public class AbstractVar {
    public int contextID;
    public Var value;
    public JField field;
    public JClass clazz;
    public JMethod method;

    public AbstractVar(int ctx, Var value, JField field) {
        this.contextID = ctx;
        this.value = value;
        this.field = field;
        if (value != null) {
            this.method = value.getMethod();
            this.clazz = value.getMethod().getDeclaringClass();
        } else if (field != null) {
            this.method = null;
            this.clazz = field.getDeclaringClass();
        }
    }

    @Override
    public String toString() {
        String name = contextID + clazz.getName() + ": ";
        if (this.value != null) {
            name += this.value.getName() + (this.field != null ? "." + this.field.getName() : "");
        } else if (this.field != null) {
            name += this.field.getName();
        }
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractVar) {
            AbstractVar other = (AbstractVar) obj;
            return this.contextID == other.contextID
                    && (this.value == null && other.value == null || this.value.equals(other.value)) &&
                    (this.field == null && other.field == null || this.field.equals(other.field));
        }
        return false;
    }

    public boolean equalsWithoutContext(Object obj) {
        if (obj instanceof AbstractVar) {
            AbstractVar other = (AbstractVar) obj;
            return (this.value == null && other.value == null || this.value.equals(other.value)) &&
                    (this.field == null && other.field == null || this.field.equals(other.field));
        }
        return false;
    }

    @Override
    public int hashCode() {
        String name = null;
        if (this.value != null) {
            var className = this.clazz.getName();
            var methodName = this.method.getName();
            name = className + "." + methodName +
                    "#" + this.contextID + "#" + this.value.getName();
            if (this.field != null) {
                name += "." + this.field.getName();
            }
        } else if (this.field != null) {
            var className = this.clazz.getName();
            name = className + "." + this.field.getName();
        }
        return name.hashCode();
    }

}
