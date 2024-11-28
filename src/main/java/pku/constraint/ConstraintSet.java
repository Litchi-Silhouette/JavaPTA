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
}
