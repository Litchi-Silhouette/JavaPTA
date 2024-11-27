package pku;

import java.util.List;
import java.util.ArrayList;

public class AllHasConstraint {
    // for x in left, x contains e;
    private int left;
    private List<Integer> subsets;
    private List<Integer> elements;

    public AllHasConstraint(int left) {
        this.left = left;
        this.subsets = new ArrayList<>();
        this.elements = new ArrayList<>();
    }

    public void addSubset(int subset) {
        this.subsets.add(subset);
    }

    public void addElement(int element) {
        this.elements.add(element);
    }

    public int getLeft() {
        return this.left;
    }

    public List<Integer> getSubsets() {
        return this.subsets;
    }

    public List<Integer> getElements() {
        return this.elements;
    }

    @Override
    public String toString() {
        return "forall x in " + left + ", x contains " + elements;
    }
}
