package pku.constraint;

import pascal.taie.language.classes.JField;

public class AllHasConstraint {
    // for x in left, right in x.field
    // left.field = right
    public int left;
    public int right;
    public JField field;

    public AllHasConstraint(int left, int right, JField field) {
        this.left = left;
        this.right = right;
        this.field = field;
    }

    public void print() {
        System.out.println(left + "." + field.getName() + " = " + right);
    }
}
