package pku.constraint;

import pascal.taie.language.classes.JField;

public class AllHasConstraint {
    // for x in right, left in x.field
    public int left;
    public int right;
    public JField field;

    public AllHasConstraint(int left, int right, JField field) {
        this.left = left;
        this.right = right;
        this.field = field;
    }
}
