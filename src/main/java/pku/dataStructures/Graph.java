package pku.dataStructures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Graph <V> {
    // 以 V 为节点类型，Integer 为边标签的有向图
    // 为了并查集的安全，我们保证出现在 graph_map 和 info_map 的 value 中的值一定是当前并查集的根
    private HashMap<V, HashMap<V, Integer>> graph_map; // 图的邻接表
    private UnionFind<V> union_find; // 并查集，用于快速合并
    private HashMap<V, HashSet<Integer>> info_map; // 用于存储节点的信息
    private static final boolean DEBUG = true;
    public static final int ACTIVE = 1;
    public static final int INACTIVE = 0;
//    private HashMap<V, HashSet<V>> active_edges; // 活跃边
    public Graph() {
        graph_map = new HashMap<>();
        union_find = new UnionFind<>();
        info_map = new HashMap<>();
    }
    public void addVertex(V vertex) {
        if (!graph_map.containsKey(vertex)) {
            graph_map.put(vertex, new HashMap<>());
            union_find.add(vertex);
            info_map.put(vertex, new HashSet<>());
//            active_edges.put(vertex, new HashSet<>());
            if (DEBUG) {
                System.out.println("Add vertex " + vertex);
            }
        }
        else if (DEBUG) {
            System.out.println("Vertex " + vertex + " already exists");
        }
    }
    public boolean isSameVertex(V vertex1, V vertex2) {
        return union_find.find(vertex1) == union_find.find(vertex2);
    }
    // 这里我们假设用 0 表示边活跃，用非零表示边不活跃。注意这里我们不检查自环
    public void addEdge(V from, V to, Integer label) {
        V from1 = union_find.find(from);
        V to1 = union_find.find(to);
        if (DEBUG && !graph_map.containsKey(from1)) {
            System.out.println("Vertex " + from + " not exists in add info");
        }
        graph_map.get(from1).put(to1, label);
        // 如果边是活跃的，那么将其加入到活跃边中
//        if (label == ACTIVE) {
////            active_edges.get(from1).add(to1);
//        }
        if (DEBUG) {
            System.out.println("Add edge from " + from + " to " + to + " with label " + label);
        }
    }
    public void addInfo(V vertex, Integer info) {
        V vertex1 = union_find.find(vertex);
        if (DEBUG && vertex1 == null) {
            System.out.println("Vertex " + vertex + " not exists in add info");
        }
        info_map.get(vertex1).add(info);
    }
    // 如果有信息更新，返回 true，否则返回 false
    public boolean addInfos(V vertex, HashSet<Integer> infos) {
        V vertex1 = union_find.find(vertex);
        if (DEBUG && vertex1 == null) {
            System.out.println("Vertex " + vertex + " not exists in add infos");
        }
        HashSet<Integer> info = info_map.computeIfAbsent(vertex1, k -> new HashSet<>());
        if (DEBUG) {
            if (info.addAll(infos)) {
                System.out.println("Add infos to vertex " + vertex);
                System.out.println("Infos : " + infos);
                return true;
            }
            return false;
        }
        return info.addAll(infos);
    }
    public HashSet<Integer> getInfo(V vertex) {
        V vertex1 = union_find.find(vertex);
        if (DEBUG && vertex1 == null) {
            System.out.println("Vertex " + vertex + " not exists in get info");
        }
        return info_map.get(vertex1);
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
        graph_map.put(vertex1, new HashMap<>());
        // 重置活跃边信息
//        active_edges.put(vertex1, new HashSet<>());
        for (V to : edges.keySet()) {
            V to1 = union_find.find(to);
            if (to1 == vertex1) {
                // 防止自环
                continue;
            }
//            if (edges.get(to1) == ACTIVE) {
//                active_edges.get(vertex1).add(to1);
//            }
            graph_map.get(vertex1).put(to1, edges.get(to1));
        }
    }
    // 由于 active_edges 的安全性保证，这里的返回值使用原始值即可
//    public HashSet<V> getActiveEdges(V vertex) {
//        V vertex1 = union_find.find(vertex);
//        return active_edges.get(vertex1);
//    }
    public Iterator<V> getVerticesIterator() {
        return union_find.getRootsIterator();
    }
    // 只有两个边都不活跃时，合并后的边才不活跃
    private Integer mergeLabel(Integer a, Integer b) {
        if (a == INACTIVE && b == INACTIVE) {
            return INACTIVE;
        }
        else {
            return ACTIVE;
        }
    }
    public void merge(V vertex1, V vertex2) {
        // 并查集合并前，先对两个节点对应的邻接表进行合并
        if (DEBUG) {
            System.out.println("Merge vertex " + vertex1 + " and vertex " + vertex2);
            System.out.println("Before merge:");
            System.out.println("Vertex " + vertex1 + "edges : " + getEdges(vertex1));
            System.out.println("Vertex " + vertex1 + "info : " + getInfo(vertex1));
            System.out.println("Vertex " + vertex2 + "edges : " + getEdges(vertex2));
            System.out.println("Vertex " + vertex2 + "info : " + getInfo(vertex2));
        }
        HashMap<V, Integer> edges_1 = getEdges(vertex1);
        HashMap<V, Integer> edges_2 = getEdges(vertex2);
        for (V vertex : edges_2.keySet()) {
            if (!edges_1.containsKey(vertex)) {
                edges_1.put(vertex, edges_2.get(vertex));
            }
            else {
                // 如果边出现两次，那么合并后的边的标签为两者的标签合并
                edges_1.put(vertex, mergeLabel(edges_1.get(vertex), edges_2.get(vertex)));
            }
        }
        HashSet<Integer> info_1 = getInfo(vertex1);
        addInfos(vertex2, info_1);
        union_find.union(vertex1, vertex2);
        // 将 vertex2 对应的邻接表（也是 vertex1 对应的邻接表）设置为合并后的邻接表
        // 为了保证安全，特地在 union 之后调用，这样 set 方法会将所有 value 重置为对应的根节点
        setEdges(vertex2, edges_1); // 注意该方法会自动处理活跃边问题
        // 由于 union 方法将 vertex1 的根节点指向了 vertex2 的根节点，因此 vertex1 对应的信息和邻接表都不再需要
        info_1.clear(); // 将 vertex1 对应的信息清空
        edges_1.clear(); // 将 vertex1 对应的邻接表清空
        if (DEBUG) {
            System.out.println("After merge:");
            System.out.println("Vertex " + vertex1 + "edges : " + getEdges(vertex1));
            System.out.println("Vertex " + vertex1 + "info : " + getInfo(vertex1));
            System.out.println("Vertex " + vertex2 + "edges : " + getEdges(vertex2));
            System.out.println("Vertex " + vertex2 + "info : " + getInfo(vertex2));
        }
    }
    // 从 current 开始，深度优先搜索，找到一个环，将环上的节点合并。 如果 current 不在 path 中，但在 visited 中，说明已经处理过，直接返回
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
    public void mergeStrongConnectedComponentAll () {
        HashSet<V> visited = new HashSet<>();
        // 由于循环中并查集一直在改变，不能使用 getVerticesIterator，因此这里直接遍历原始序号
        for (V node : graph_map.keySet()) {
            mergeStrongConnectedComponent(node, new HashSet<>(), visited);
        }
    }
}
