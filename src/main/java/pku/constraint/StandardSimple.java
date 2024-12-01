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

    @Override
    public String toString() {
        String result = left + " contains elements {";
        for (int i = 0; i < elements.size(); i++) {
            result += elements.get(i);
            if (i != elements.size() - 1) {
                result += ", ";
            }
        }
        result += "} and subsets {";
        for (int i = 0; i < subsets.size(); i++) {
            result += subsets.get(i);
            if (i != subsets.size() - 1) {
                result += ", ";
            }
        }
        result += "}";
        return result;
    }
}
