package pku;

import java.util.List;
import java.util.ArrayList;

public class ConstraintSet {
    private List<StandardConstraint> constraints;

    public ConstraintSet() {
        this.constraints = new ArrayList<>();
    }

    public void add(StandardConstraint constraint) {
        this.constraints.add(constraint);
    }

    public void solve(AbstractDomain domain) {

    }
}
