package pku.constraint;

import pascal.taie.language.classes.JField;

public class AllInConstraint {
    // for x in right, x field in left
    public int left;
    public int right;
    public JField field;

    public AllInConstraint(int left, int right, JField field) {
        this.left = left;
        this.right = right;
        this.field = field;
    }
}
