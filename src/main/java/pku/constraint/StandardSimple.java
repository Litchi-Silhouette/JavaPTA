package pku.constraint;

import java.util.ArrayList;
import java.util.List;

public class StandardSimple {
    public int left;
    public List<Integer> subsets;
    public List<Integer> elements;

    public StandardSimple(int left) {
        this.left = left;
        this.elements = new ArrayList<>();
        this.subsets = new ArrayList<>();
    }

    public void addElement(int element) {
        this.elements.add(element);
    }

    public void addSubset(int subset) {
        this.subsets.add(subset);
    }
}
