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
    // 注意若不存在则会初始化
    public int get_node_index_from_var_index(int var_index) {
        if (!var_to_index.containsKey(var_index)) {
            var_to_index.put(var_index, node_index);
            graph.addVertex(node_index);

            node_index++;
        }
        return var_to_index.get(var_index);
    }
    // 注意初始化时，会自动将 element_index 加入到图节点的信息中
    public int get_node_index_from_element_index(int element_index) {
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
        boolean is_stable = false;
        while (!is_stable && !active_edges.isEmpty()) {
            is_stable = true;
            // 注意这里尽管可能造成并查集更新，但我们存储的索引依然有效，因为查询接口都是会先寻找根节点的
            graph.mergeStrongConnectedComponentAll();
            HashSet<Integer> effected_nodes = new HashSet<>();  // 在该轮更新中集合有更新的节点，注意存储的是 graph index
            // 先传导所有影响
            while (!active_edges.isEmpty()){
                edge e = active_edges.remove(0);
                int from = e.from;
                int to = e.to;
                graph.addEdge(from, to, 0);
                if (graph.addInfos(to, graph.getInfo(from)))
                {
                    is_stable = false;
                    effected_nodes.add(to);
                }
                HashMap<Integer, Integer> edges = graph.getEdges(to);
                for (int next : edges.keySet()) {
                    if (edges.get(next) == 0) {
                        active_edges.add(new edge(to, next));
                        // 由于 to -> next 的边一定在邻接表中，不会修改迭代器产生 undefined behavior
                        graph.addEdge(to, next, 1);
                    }
                }
            }
            for (AllInConstraint inConstraint : allin_constraint) {
                // 处理 forall x in left, x contains in right;
                int left = inConstraint.getLeft();
                int left_graph_index = get_node_index_from_var_index(left);
                if (!effected_nodes.contains(left_graph_index)) {
                    // 如果本轮中 left 没有更新，就不用更新
                    continue;
                }
                int right = inConstraint.getRight();
                int right_graph_index = get_node_index_from_var_index(right);
                HashSet<Integer> possible_objects = graph.getInfo(left_graph_index);
                for (int x : possible_objects) {
                    int x_graph_index = get_node_index_from_element_index(x);
                    if (graph.isSameVertex(left_graph_index, x_graph_index)) {
                        // 不考虑自环
                        continue;
                    }
                    // 否则从 x_graph_index 指向 right_graph_index，同时令其活跃
                    graph.addEdge(x_graph_index, right_graph_index, 1);
                    active_edges.add(new edge(x_graph_index, right_graph_index));
                }
            }
            for (AllHasConstraint hasConstraint : allhas_constraint) {
                // 处理 forall x in left, x contains e;
                int left = hasConstraint.getLeft();
                int left_graph_index = get_node_index_from_var_index(left);
                if (!effected_nodes.contains(left_graph_index)) {
                    // 如果本轮中 left 没有更新，就不用更新
                    continue;
                }
                HashSet<Integer> possible_objects = graph.getInfo(left_graph_index);
                for (int possible_object : possible_objects) {
                    for (int e : hasConstraint.getElements()) {
                        int e_graph_index = get_node_index_from_element_index(e);
                        int possible_object_graph_index = get_node_index_from_element_index(possible_object);
                        // 由于单点集不会和元素的可能指向集处在同一个连通分支，因此不会产生自环
                        graph.addEdge(e_graph_index, possible_object_graph_index, 1);
                        active_edges.add(new edge(e_graph_index, possible_object_graph_index));
                    }
                }
            }
        }
    }
    public HashSet<Integer> getInfo(Integer var){
        return graph.getInfo(get_node_index_from_var_index(var));
    }
    public void printInfo(Integer var){
        HashSet<Integer> info = graph.getInfo(get_node_index_from_var_index(var));
        System.out.println("possible objects of " + var + ": " + info);
    }
}
