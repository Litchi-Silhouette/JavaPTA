package pku;

public class JudgeConstraint {
    private int index1;
    private int index2;
    private int choice1;
    private int choice2;

    // -1 represents null
    // index1 in index2's domain ? choice1 : choice2
    public JudgeConstraint(int index1, int index2, int choice1, int choice2) {
        this.index1 = index1;
        this.index2 = index2;
        this.choice1 = choice1;
        this.choice2 = choice2;
    }

    public int calculate(AbstractDomain domain) {
        if (domain.check(index2, index1)) {
            return choice1;
        } else {
            return choice2;
        }
    }
}
