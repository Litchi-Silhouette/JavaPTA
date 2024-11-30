package pku.constraint;

public class SimpleEConstraint {
    public int left;
    public int rightElement; // r is in left

    public SimpleEConstraint(int left, int rightElement) {
        this.left = left;
        this.rightElement = rightElement;
    }

    public void print() {
        System.out.println(left + " has " + rightElement);
    }
}
