package pku;

import java.util.HashMap;
import pascal.taie.language.type.Type;

public class AbstractMallocDomain {
    public HashMap<Integer, AbstractMalloc> index2malloc;
    public HashMap<AbstractMalloc, Integer> malloc2index;

    public AbstractMallocDomain() {
        index2malloc = new HashMap<>();
        malloc2index = new HashMap<>();
    }

    public Integer addMalloc(int value, Type type) {
        int count = malloc2index.size();
        AbstractMalloc malloc = new AbstractMalloc(count, value, type);
        malloc2index.put(malloc, count);
        index2malloc.put(count, malloc);
        return count;
    }
}
