package pku.constraint;

import pku.abs.ConvertToField;

public class StandardForallInLeftContainsRight {
    // for x in left, right in f(x)
    public int left;
    public int right;
    public ConvertToField f;

    public StandardForallInLeftContainsRight(int left, int right, ConvertToField f) {
        this.left = left;
        this.right = right;
        this.f = f;
    }
}
