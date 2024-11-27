package pku;

import java.util.HashMap;
import java.util.List;
import pascal.taie.language.type.Type;

public class AbstractMallocDomain {
    public HashMap<Integer, AbstractMalloc> index2malloc;
    public HashMap<AbstractMalloc, Integer> malloc2index;
    public List<Integer> mallocs;

    public AbstractMallocDomain() {
        index2malloc = new HashMap<>();
        malloc2index = new HashMap<>();
    }

    public AbstractMalloc addMalloc(int value, Type type) {
        int count = malloc2index.size();
        AbstractMalloc malloc = new AbstractMalloc(count, value, type);
        malloc2index.put(malloc, count);
        index2malloc.put(count, malloc);
        if (value != 0) {
            mallocs.add(value);
        }
        return malloc;
    }
}
