package pku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StandardConstraintSet {
    // 
    private HashMap<Integer, List<Integer>> constraint;

    public StandardConstraintSet() {
        constraint = new HashMap<>();
    }

    public void addConstraint(int index, int value) {
        if (!constraint.containsKey(index)) {
            constraint.put(index, new ArrayList<>());
        }
        constraint.get(index).add(value);
    }

    public void calculate() {
        // do something
    }
}
