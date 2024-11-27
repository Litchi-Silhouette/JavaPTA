package pku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import pku.dataStructures.Graph;

public class StandardConstraintSet {
    private ArrayList<SimpleConstraint> sim_constraint;
    private ArrayList<AllInConstraint> allin_constraint;
    private ArrayList<AllHasConstraint> allhas_constraint;
    private int node_index;  // 图的节点是每个集合，对每个集合编号
    private HashMap<Integer, Integer> var_to_index; // 变量编号到图节点编号的映射
    private HashMap<Integer, Integer> element_to_index; // 元素编号到图节点编号的映射
    private Graph<Integer> graph;
    private LinkedList<edge> active_edges;   // 存储活跃边
    public StandardConstraintSet() {
        sim_constraint = new ArrayList<>();
        allin_constraint = new ArrayList<>();
        allhas_constraint = new ArrayList<>();
        var_to_index = new HashMap<>();
        element_to_index = new HashMap<>();
        active_edges = new LinkedList<>();
        node_index = 0;
        graph = new Graph<Integer>();
    }
    private int get_node_index_from_var_index(int var_index) {
        if (!var_to_index.containsKey(var_index)) {
            var_to_index.put(var_index, node_index);
            graph.addVertex(node_index);

            node_index++;
        }
        return var_to_index.get(var_index);
    }
    // 注意初始化时，会自动将 element_index 加入到图节点的信息中
    private int get_node_index_from_element_index(int element_index) {
        if (!element_to_index.containsKey(element_index)) {
            element_to_index.put(element_index, node_index);
            graph.addVertex(node_index);
            graph.addInfo(node_index, element_index);
            node_index++;
        }
        return element_to_index.get(element_index);
    }
    // 加入时自动构建图
    public void addSimpleConstraint(SimpleConstraint sc) {
        sim_constraint.add(sc);
        int left = sc.getLeft();
        int left_graph_index = get_node_index_from_var_index(left);
        for (int subset : sc.getSubsets()) {
            int subset_graph_index = get_node_index_from_var_index(subset);
            // 如果 subset 是 left 的子集，就从 subset 指向 left 连边
            graph.addEdge(subset_graph_index, left_graph_index, 0);
        }
        for (int element : sc.getElements()) {
            int element_graph_index = get_node_index_from_element_index(element);
            // 如果 element 在 left 中，就从 element 指向 left 连边，同时令其活跃
            graph.addEdge(element_graph_index, left_graph_index, 1);
            active_edges.add(new edge(element_graph_index, left_graph_index));

        }
    }
    // 由于迭代时才使用，不更新图
    public void addAllInConstraint(AllInConstraint aic) {
        allin_constraint.add(aic);
    }

    // 由于迭代时才使用，不更新图
    public void addAllHasConstraint(AllHasConstraint ahc) {
        allhas_constraint.add(ahc);
    }

    private class edge {
        int from;
        int to;
        public edge(int from, int to) {
            this.from = from;
            this.to = to;
        }
    }

    public void solve() {
        // 求解约束
        graph.mergeStrongConnectedComponentAll();

        // 先传导所有影响
        while (!active_edges.isEmpty()){
            edge e = active_edges.remove(0);
            int from = e.from;
            int to = e.to;
            graph.addInfos(to, graph.getInfo(from));
            HashMap<Integer, Integer> edges = graph.getEdges(to);
            for (int next : edges.keySet()) {
                if (edges.get(next) == 0) {
                    active_edges.add(new edge(to, next));
                    graph.addEdge(to, next, 1);
                }
            }
        }
    }
}
