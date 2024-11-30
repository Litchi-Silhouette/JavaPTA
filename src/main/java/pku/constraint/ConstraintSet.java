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

    public void print() {
        System.out.println("Constraint Set:");
        System.out.println("Simple E Constraints:");
        for (var sec : sime_constraint) {
            sec.print();
        }
        System.out.println("Simple S Constraints:");
        for (var ssc : sims_constraint) {
            ssc.print();
        }
        System.out.println("All In Constraints:");
        for (var aic : allin_constraint) {
            aic.print();
        }
        System.out.println("All Has Constraints:");
        for (var ahc : allhas_constraint) {
            ahc.print();
        }
        System.out.println();
    }
}
