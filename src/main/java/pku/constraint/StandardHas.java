package pku.constraint;

import pku.abs.ConvertToField;

public class StandardHas {
    // for x in right, f(x) in left
    public int left;
    public int right;
    public ConvertToField f;

    public StandardHas(int left, int right, ConvertToField f) {
        this.left = left;
        this.right = right;
        this.f = f;
    }

}
