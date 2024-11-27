package pku;

public class AllInConstraint {
    // for x in left, x contains in right;
    private int left;
    private int right;

    public AllInConstraint(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public int getLeft() {
        return this.left;
    }

    public int getRight() {
        return this.right;
    }

    @Override
    public String toString() {
        return "forall x in " + left + ", x contains in " + right;
    }
}
