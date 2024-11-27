package pku.dataStructures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class UnionFind <T> {
    private final HashMap<T, T> parent;
    private HashSet<T> roots;
    public UnionFind() {
        parent = new HashMap<>();
        roots = new HashSet<>();
    }
    public void add(T element) {
        if (!parent.containsKey(element)) {
            parent.put(element, element);
            roots.add(element);
        }
    }
    public T find(T element) {
        if (parent.get(element) == element) {
            return element;
        }
        T root = find(parent.get(element));
        parent.put(element, root);
        return root;
    }
    // 会将 element1 的根节点指向 element2 的根节点
    public void union(T element1, T element2) {
        T root1 = find(element1);
        T root2 = find(element2);
        parent.put(root1, root2);
        roots.remove(root1);
    }
    public Iterator<T> getRootsIterator() {
        return roots.iterator();
    }
}
