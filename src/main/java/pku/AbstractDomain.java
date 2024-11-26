package pku;

import java.util.BitSet;
import java.util.HashMap;
import pascal.taie.language.type.Type;

public class AbstractDomain {
    // 变量命名：obj.method#x#var x为上下文克隆的次数
    private HashMap<Integer, BitSet> domain;
    private HashMap<AbstractVar, Integer> name2index;
    private HashMap<Integer, AbstractVar> index2name;

    private HashMap<Integer, Integer> malloc2index;
    private HashMap<Integer, Integer> index2malloc;
    private HashMap<Integer, Type> mallocType;

    public AbstractDomain() {
        domain = new HashMap<>();
        name2index = new HashMap<>();
        index2name = new HashMap<>();
        malloc2index = new HashMap<>();
        index2malloc = new HashMap<>();

        mallocType = new HashMap<>();
    }

    // add a new malloc to the domain
    public Integer addMalloc(int malloc, Type type) {
        if (!malloc2index.containsKey(malloc)) {
            int index = malloc2index.size();
            malloc2index.put(malloc, index);
            index2malloc.put(index, malloc);
            mallocType.put(index, type);
            return index;
        }
        return -1;
    }

    // add a new element to the domain
    public Integer addElement(AbstractVar name, Type type) {
        if (!name2index.containsKey(name)) {
            int index = name2index.size();
            name2index.put(name, index);
            index2name.put(index, name);
            domain.put(index, new BitSet());
            return index;
        }
        return -1;
    }
}
