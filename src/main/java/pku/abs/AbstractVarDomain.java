package pku.abs;

import java.util.HashMap;
import java.util.TreeSet;

import pascal.taie.language.type.ClassType;
import pascal.taie.language.classes.JField;
import pascal.taie.language.type.ArrayType;

public class AbstractVarDomain {
    public HashMap<Integer, AbstractVar> index2name;
    public HashMap<AbstractVar, Integer> name2index;

    public HashMap<Integer, Integer> index2malloc;
    public TreeSet<Integer> newIndexes;

    public AbstractVarDomain() {
        index2name = new HashMap<>();
        name2index = new HashMap<>();
        index2malloc = new HashMap<>();
        newIndexes = new TreeSet<>();
    }

    public Integer addMallocMapping(Integer varIndex, Integer mallocIndex) {
        if (!index2malloc.containsKey(varIndex) && !newIndexes.contains(varIndex)) {
            index2malloc.put(varIndex, mallocIndex);
            newIndexes.add(varIndex);
            return mallocIndex;
        }
        return -1;
    }

    public Integer addField(AbstractVar name) {
        if (!name2index.containsKey(name) && name.field != null) {
            return addElement(name);
        }
        return -1;
    }

    public Integer addVar(AbstractVar name) {
        if (!name2index.containsKey(name) && name.field == null) {
            var value = name.value;
            var type = value.getType();
            if (type instanceof ArrayType) {
                type = ((ArrayType) type).elementType();
            }
            if (type instanceof ClassType) {
                var fields = ((ClassType) type).getJClass().getDeclaredFields();
                for (var field : fields) {
                    if (field.isStatic())
                        continue;
                    AbstractVar fieldVar = new AbstractVar(0, value, field);
                    addElement(fieldVar);
                }
            }
            return addElement(name);
        }
        return -1;
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

    public Integer getVarIndex(AbstractVar name) {
        var id = name2index.get(name);
        if (id == null)
            return -1;
        return id;
    }

    public Integer checkAndAdd(AbstractVar name) {
        var id = getVarIndex(name);
        if (id == -1) {
            return addVar(name);
        }
        return id;
    }

    public Integer getVarField(Integer varIndex, JField field) {
        var name = index2name.get(varIndex);
        if (name == null || name.field != null || name.value == null)
            return -1;
        var value = name.value;
        var type = value.getType();
        if (type instanceof ArrayType) {
            type = ((ArrayType) type).elementType();
        }
        if (type instanceof ClassType) {
            AbstractVar fieldVar = new AbstractVar(0, value, field);
            return getVarIndex(fieldVar);
        } else
            return -1;
    }

    @Override
    public AbstractVarDomain clone() {
        var newDomain = new AbstractVarDomain();
        newDomain.index2name = new HashMap<>(index2name);
        newDomain.name2index = new HashMap<>(name2index);
        return newDomain;
    }

}
