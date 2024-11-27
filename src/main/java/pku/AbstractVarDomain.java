package pku;

import java.util.HashMap;

public class AbstractVarDomain {
    public HashMap<Integer, AbstractVar> index2name;
    public HashMap<AbstractVar, Integer> name2index;

    public AbstractVarDomain() {
        index2name = new HashMap<>();
        name2index = new HashMap<>();
    }

    public Integer addElement(AbstractVar name) {
        if (!name2index.containsKey(name)) {
            int index = name2index.size();
            name2index.put(name, index);
            index2name.put(index, name);
            return index;
        }
        return -1;
    }
}
