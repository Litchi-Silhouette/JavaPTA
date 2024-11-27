package pku;

import java.util.ArrayList;

public class StandardConstraintSet {
    private List<SimpleConstraint> sim_constraint;
    private List<AllInConstraint> allin_constraint;
    private List<AllHasConstraint> allhas_constraint;

    public StandardConstraintSet() {
        sim_constraint = new ArrayList<>();
        allin_constraint = new ArrayList<>();
        allhas_constraint = new ArrayList<>();
    }

    public void addSimpleConstraint(SimpleConstraint sc) {
        sim_constraint.add(sc);
    }

    public void addAllInConstraint(AllInConstraint aic) {
        allin_constraint.add(aic);
    }

    public void addAllHasConstraint(AllHasConstraint ahc) {
        allhas_constraint.add(ahc);
    }
}
