package pku.constraint;

import java.util.List;
import java.util.ArrayList;

public class ConstraintSet {
    public List<SimpleEConstraint> sime_constraint;
    public List<SimpleSConstraint> sims_constraint;
    public List<AllInConstraint> allin_constraint;
    public List<AllHasConstraint> allhas_constraint;

    public ConstraintSet() {
        sime_constraint = new ArrayList<>();
        sims_constraint = new ArrayList<>();
        allin_constraint = new ArrayList<>();
        allhas_constraint = new ArrayList<>();
    }

    public void addSimpleEConstraint(SimpleEConstraint sec) {
        sime_constraint.add(sec);
    }

    public void addSimpleSConstraint(SimpleSConstraint ssc) {
        sims_constraint.add(ssc);
    }

    public void addAllInConstraint(AllInConstraint aic) {
        allin_constraint.add(aic);
    }

    public void addAllHasConstraint(AllHasConstraint ahc) {
        allhas_constraint.add(ahc);
    }

    public void addConstraintSet(ConstraintSet cs) {
        sime_constraint.addAll(cs.sime_constraint);
        sims_constraint.addAll(cs.sims_constraint);
        allin_constraint.addAll(cs.allin_constraint);
        allhas_constraint.addAll(cs.allhas_constraint);
    }
}