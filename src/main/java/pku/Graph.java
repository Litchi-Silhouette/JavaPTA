package pku;

import java.util.HashMap;
import java.util.Iterator;


public class Graph <V, Label> {
    // 以 V 为节点类型，Label 为边标签的图
    private HashMap<V, HashMap<V, Label>> graph_map; // 图的邻接表
    public Graph() {
        graph_map = new HashMap<>();
    }
    public void addVertex(V vertex) {
        if (!graph_map.containsKey(vertex)) {
            graph_map.put(vertex, new HashMap<>());
        }
    }
    public void addEdge(V from, V to, Label label) {
        if (!graph_map.containsKey(from)) {
            graph_map.put(from, new HashMap<>());
        }
        graph_map.get(from).put(to, label);
    }
    public Label getEdge(V from, V to) {
        return graph_map.get(from).get(to);
    }
    public HashMap<V, Label> getEdges(V vertex) {
        return graph_map.get(vertex);
    }
    public class Edge {
        private V from;
        private V to;
        private Label label;
        public Edge(V from, V to, Label label) {
            this.from = from;
            this.to = to;
            this.label = label;
        }
        public V getFrom() {
            return from;
        }
        public V getTo() {
            return to;
        }
        public Label getLabel() {
            return label;
        }
    }
    public class VertexIterator implements Iterator<V> {
        private final Iterator<V> iterator;
        public VertexIterator() {
            iterator = graph_map.keySet().iterator();
        }
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }
        @Override
        public V next() {
            return iterator.next();
        }
    }
}
