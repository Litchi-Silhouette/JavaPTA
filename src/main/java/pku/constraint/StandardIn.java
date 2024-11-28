package pku.constraint;

import pku.abs.ConvertToField;

public class StandardIn {
    // for x in left, right in f(x)
    public int left;
    public int right;
    public ConvertToField f;

    public StandardIn(int left, int right, ConvertToField f) {
        this.left = left;
        this.right = right;
        this.f = f;
    }
}
