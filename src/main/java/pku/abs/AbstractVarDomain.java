package pku.abs;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeSet;

import pascal.taie.language.type.ClassType;
import pascal.taie.language.classes.JField;
import pascal.taie.language.type.ArrayType;
import pascal.taie.ir.exp.Var;

public class AbstractVarDomain {
    public HashMap<Integer, AbstractVar> index2name;
    public HashMap<AbstractVar, Integer> name2index;

    public HashMap<Integer, Integer> index2malloc;
    public TreeSet<Integer> newIndexes;
    public HashMap<Var, List<Integer>> var2indexs;

    public AbstractVarDomain() {
        index2name = new HashMap<>();
        name2index = new HashMap<>();
        index2malloc = new HashMap<>();
        newIndexes = new TreeSet<>();
        var2indexs = new HashMap<>();
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
        if (name.field != null) {
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
                    AbstractVar fieldVar = new AbstractVar(name.contextID, value, field);
                    addElement(fieldVar);
                }
            }
            return addElement(name);
        }
        return -1;
    }

    public Integer addElement(AbstractVar absVar) {

        // check if the element is already in the domain
        if (!name2index.containsKey(absVar)) {
            int index = name2index.size();
            name2index.put(absVar, index);
            index2name.put(index, absVar);

            // get all the ids corresponding to the value
            List<Integer> ids = var2indexs.containsKey(absVar.value) ? var2indexs.get(absVar.value) : new ArrayList<>();

            // check if the id is already in the list
            if (!ids.contains(name2index.get(absVar))) {
                ids.add(name2index.get(absVar));
                // update var2indexs with new id corresponding to the value
                var2indexs.put(absVar.value, ids);
            }
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

    public List<Integer> getIndexsByValue(Var value) {
        List<Integer> ids = var2indexs.containsKey(value) ? var2indexs.get(value) : null;
        if (ids == null) {
            System.err.println("No such value in domain: " + value.getName());
        }
        return ids;
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
            AbstractVar fieldVar = new AbstractVar(name.contextID, value, field);
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
