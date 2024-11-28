package pku;

import java.util.List;
import java.util.ArrayList;

import pku.constraint.*;

public class StandardConstraintSet {
    public List<StandardSimple> sim_constraint;
    public List<StandardHas> has_constraint;
    public List<StandardIn> in_constraint;

    public StandardConstraintSet() {
        sim_constraint = new ArrayList<>();
        has_constraint = new ArrayList<>();
        in_constraint = new ArrayList<>();
    }

    public void addSimpleConstraint(StandardSimple sec) {
        sim_constraint.add(sec);
    }

    public void addHasConstraint(StandardHas shc) {
        has_constraint.add(shc);
    }

    public void addInConstraint(StandardIn sic) {
        in_constraint.add(sic);
    }
}
