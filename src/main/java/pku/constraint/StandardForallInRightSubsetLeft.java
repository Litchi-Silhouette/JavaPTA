package pku.constraint;

import pku.abs.ConvertToField;

public class StandardForallInRightSubsetLeft {
    // for x in right, left contains f(x)
    public int left;
    public int right;
    public ConvertToField f;

    public StandardForallInRightSubsetLeft(int left, int right, ConvertToField f) {
        this.left = left;
        this.right = right;
        this.f = f;
    }

}
