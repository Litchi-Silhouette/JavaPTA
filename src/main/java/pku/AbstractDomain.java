package pku;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class AbstractDomain {
    // 变量命名：obj.method#x#var x为上下文克隆的次数
    private HashMap<Integer, BitSet> domain;
    private HashMap<String, Integer> name2index;
    private HashMap<Integer, String> index2name;

    public AbstractDomain() {
        domain = new HashMap<>();
        name2index = new HashMap<>();
        index2name = new HashMap<>();
    }

    // add a new element to the domain
    public void add(String name) {
        if (!name2index.containsKey(name)) {
            int index = name2index.size();
            name2index.put(name, index);
            index2name.put(index, name);
            domain.put(index, new BitSet());
        }
    }

    // set the value of an element in the domain
    public void set(int index, BitSet value) {
        domain.put(index, value);
    }

    // get the value of an element in the domain
    public BitSet get(int index) {
        return domain.get(index);
    }

    public void merge(int index, BitSet value) {
        BitSet oldValue = domain.get(index);
        oldValue.or(value);
        domain.put(index, oldValue);
    }

    public boolean check(int index, int index2) {
        BitSet oldValue = domain.get(index);
        return oldValue.get(index2);
    }

    public List<String> getElements(int index) {
        BitSet value = domain.get(index);
        List<String> elements = new ArrayList<>();
        for (int i = value.nextSetBit(0); i >= 0; i = value.nextSetBit(i + 1)) {
            elements.add(index2name.get(i));
        }
        return elements;
    }

    public List<Integer> getElementIndex(int index) {
        BitSet value = domain.get(index);
        List<Integer> elements = new ArrayList<>();
        for (int i = value.nextSetBit(0); i >= 0; i = value.nextSetBit(i + 1)) {
            elements.add(i);
        }
        return elements;
    }
}
