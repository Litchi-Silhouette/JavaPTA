package pku.dataStructures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Graph <V> {
    // 以 V 为节点类型，Integer 为边标签的有向图
    private HashMap<V, HashMap<V, Integer>> graph_map; // 图的邻接表
    private UnionFind<V> union_find; // 并查集，用于快速合并
    private static final boolean DEBUG = true;
    public Graph() {
        graph_map = new HashMap<>();
        union_find = new UnionFind<>();
    }
    public void addVertex(V vertex) {
        if (!graph_map.containsKey(vertex)) {
            graph_map.put(vertex, new HashMap<>());
            union_find.add(vertex);
            if (DEBUG) {
                System.out.println("Add vertex " + vertex);
            }
        }
    }
    // 这里我们假设用 0 表示边活跃，用非零表示边不活跃
    public void addEdge(V from, V to, Integer Integer) {
        V from1 = union_find.find(from);
        V to1 = union_find.find(to);
        if (!graph_map.containsKey(from1)) {
            graph_map.put(from1, new HashMap<>());
        }
        graph_map.get(from1).put(to1, Integer);
        if (DEBUG) {
            System.out.println("Add edge from " + from + " to " + to + " with label " + Integer);
        }
    }
    public Integer getEdge(V from, V to) {
        V from1 = union_find.find(from);
        V to1 = union_find.find(to);
        return graph_map.get(from1).get(to1);
    }
    public HashMap<V, Integer> getEdges(V vertex) {
        V vertex1 = union_find.find(vertex);
        return graph_map.get(vertex1);
    }
    public void setEdges(V vertex, HashMap<V, Integer> edges) {
        V vertex1 = union_find.find(vertex);
        graph_map.put(vertex1, edges);
    }
    public Iterator<V> getVerticesIterator() {
        return union_find.getRootsIterator();
    }
    // 只有两个边都不活跃时，合并后的边才不活跃
    private Integer mergeLabel(Integer a, Integer b) {
        if (a == 0 && b == 0) {
            return 0;
        }
        else {
            return 1;
        }
    }   
    public void merge(V vertex1, V vertex2) {
        // 并查集合并前，先对两个节点对应的邻接表进行合并
        if (DEBUG) {
            System.out.println("Merge vertex " + vertex1 + " and vertex " + vertex2);
            System.out.println("Before merge:");
            System.out.println("Vertex " + vertex1 + ": " + getEdges(vertex1));
            System.out.println("Vertex " + vertex2 + ": " + getEdges(vertex2));
        }
        HashMap<V, Integer> egdes_1 = getEdges(vertex1);
        HashMap<V, Integer> egdes_2 = getEdges(vertex2);
        for (V vertex : egdes_2.keySet()) {
            if (!egdes_1.containsKey(vertex)) {
                egdes_1.put(vertex, egdes_2.get(vertex));
            }
            else {
                // 如果边出现两次，那么合并后的边的标签为两者的标签合并
                egdes_1.put(vertex, mergeLabel(egdes_1.get(vertex), egdes_2.get(vertex)));
            }
        }
        union_find.union(vertex1, vertex2);
        setEdges(vertex2, egdes_1); // 将 vertex2 对应的邻接表（也是 vertex1 对应的邻接表）设置为合并后的邻接表
        if (DEBUG) {
            System.out.println("After merge:");
            System.out.println("Vertex " + vertex1 + ": " + getEdges(vertex1));
            System.out.println("Vertex " + vertex2 + ": " + getEdges(vertex2));
        }
    }
    public void mergeStrongConnectedComponent(V current, HashSet<V> path, HashSet<V> visited) {
        if (path.contains(current)) {
            // 如果当前节点已经在路径中，说明路径上的节点构成了一个强连通分量
            if (DEBUG) {
                System.out.println("find strong connected component:");
                for (V vertex : path) {
                    System.out.println(vertex);
                }
            }
            Iterator<V> iterator = path.iterator();
            V start = iterator.next();
            while (iterator.hasNext()) {
                V next = iterator.next();
                merge(start, next);
            }
            path = new HashSet<>(); // 因为已经全部合并，因此可以清空路径，防止重复合并
        }
        if (visited.contains(current)) {
            return;
        }
        visited.add(current);
        path.add(current);
        HashMap<V, Integer> edges = getEdges(current);
        for (V next : edges.keySet()) {
            mergeStrongConnectedComponent(next, path, visited);
        }
    }
}
