package pku;

import pascal.taie.language.type.Type;

public class AbstractMalloc {
    public int count;
    public int value; // 0 for unknown
    public Type type;

    public AbstractMalloc(int count, int value, Type type) {
        this.count = count;
        this.value = value;
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractMalloc) {
            AbstractMalloc other = (AbstractMalloc) obj;
            return this.count == other.count;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.count;
    }
}
