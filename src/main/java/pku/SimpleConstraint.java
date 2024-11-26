package pku;

import java.util.List;
import java.util.ArrayList;

public class SimpleConstraint {
    private int left;
    private List<Integer> subsets; // left + subset1 + subset2 + ...
    private List<Integer> elements;// left + {element1, element2}

    public SimpleConstraint(int left) {
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
}
