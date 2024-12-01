package pku.constraint;

public class SimpleSConstraint {
    public int left;
    public int right; // left contains right

    public SimpleSConstraint(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public void print() {
        System.out.println(left + " contains " + right);
    }
}
