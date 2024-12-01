package pku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import pku.dataStructures.Graph;
import pku.constraint.*;

// 注意这里我们允许对“指针”（所有的 left 和 subset）和“可能值”（所有的 Elements）分开编号
// 但需要保证如果 p 既是指针，又是可能值，则应该在两边使用相同的编号
// 另外，假定所有的指针都是可能值
public class StandardConstraintSet {
    public List<StandardSimple> sim_constraint;
    public List<StandardForallInRightSubsetLeft> has_constraint;
    public List<StandardForallInLeftContainsRight> in_constraint;
    private int node_index; // 图的节点是每个集合，对每个集合编号
    private HashMap<Integer, Integer> var_to_index; // 变量编号到图节点编号的映射
    private HashMap<Integer, Integer> element_to_index; // 元素编号到图节点编号的映射
    private Graph<Integer> graph;
    private final boolean DEBUG = true;
    private LinkedList<edge> active_edges; // 存储活跃边

    public StandardConstraintSet() {
        sim_constraint = new ArrayList<>();
        has_constraint = new ArrayList<>();
        in_constraint = new ArrayList<>();
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
    public void addSimpleConstraint(StandardSimple sc) {
        sim_constraint.add(sc);
        int left = sc.left;
        int left_graph_index = get_node_index_from_var_index(left);
        for (int subset : sc.subsets) {
            if (DEBUG) {
                System.out.println("get constraint: " + subset + " subset " + left);
            }
            int subset_graph_index = get_node_index_from_var_index(subset);
            // 如果 subset 是 left 的子集，就从 subset 指向 left 连边
            graph.addEdge(subset_graph_index, left_graph_index, 0);
        }
        for (int element : sc.elements) {
            if (DEBUG) {
                System.out.println("get constraint: " + element + " in " + left);
            }
            int element_graph_index = get_node_index_from_element_index(element);
            // 如果 element 在 left 中，就从 element 指向 left 连边，同时令其活跃
            graph.addEdge(element_graph_index, left_graph_index, 1);
            active_edges.add(new edge(element_graph_index, left_graph_index));

        }
    }

    // 由于迭代时才使用，不更新图
    public void addStandardForallInRightSubsetLeftConstraint(StandardForallInRightSubsetLeft shc) {
        has_constraint.add(shc);
        if (DEBUG) {
            System.out.println("get constraint: forall x in " + shc.right + ", f(x) subset " + shc.left);
        }
    }

    // 由于迭代时才使用，不更新图
    public void addStandardForallInLeftContainsRightConstraint(StandardForallInLeftContainsRight sic) {
        in_constraint.add(sic);
        if (DEBUG) {
            System.out.println("get constraint: forall x in " + sic.left + ", " + sic.right + " subset f(x)" );
        }
    }

    private static class edge {
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
            HashSet<Integer> effected_nodes = new HashSet<>(); // 在该轮更新中集合有更新的节点，注意存储的是 graph index 的 rrot
            // 先传导所有影响
            while (!active_edges.isEmpty()) {
                edge e = active_edges.remove(0);
                int from = e.from;
                int to = e.to;
                graph.addEdge(from, to, 0);
                if (graph.addInfos(to, graph.getInfo(from))) {
                    is_stable = false;
                    effected_nodes.add(to);
                    HashMap<Integer, Integer> edges = graph.getEdges(to);
                    for (int next : edges.keySet()) {
                        if (edges.get(next) == 0) {
                            active_edges.add(new edge(to, next));
                            // 由于 to -> next 的边一定在邻接表中，不会修改迭代器产生 undefined behavior
                            graph.addEdge(to, next, 1);
                        }
                    }
                }

            }
            for (StandardForallInLeftContainsRight inConstraint : in_constraint) {
                // 处理 forall x in left, right subset f(x)
                int left = inConstraint.left;
                int left_graph_index = get_node_index_from_var_index(left);
                if (!effected_nodes.contains(left_graph_index)) {
                    // 如果本轮中 left 没有更新，就添加新边
                    continue;
                }
                int right = inConstraint.right;
                int right_graph_index = get_node_index_from_var_index(right);
                for (int x : graph.getInfo(left_graph_index)) {
                    int x_field = inConstraint.f.convert(x);
                    int x_graph_index = get_node_index_from_var_index(x_field);
                    if (graph.addEdge(right_graph_index, x_graph_index, 1)) {
                        // 如果不是自环，就从 right_graph_index 指向 x_graph_index，同时令其活跃
                        active_edges.add(new edge(right_graph_index, x_graph_index));
                    }
                }
            }
            for (StandardForallInRightSubsetLeft hasConstraint : has_constraint) {
                // 处理 for x in right, f(x) subset left
                int left = hasConstraint.left;
                int left_graph_index = get_node_index_from_var_index(left);
                int right = hasConstraint.right;
                int right_graph_index = get_node_index_from_var_index(right);
                if (!effected_nodes.contains(right_graph_index)) {
                    // 如果本轮中 left 没有更新，就不用添加新边
                    continue;
                }
                for (int x : graph.getInfo(right_graph_index)) {
                    int x_field = hasConstraint.f.convert(x);
                    int x_field_graph_index = get_node_index_from_var_index(x_field);
                    if (graph.addEdge(x_field_graph_index, left_graph_index, 1)) {
                        // 如果不是自环，就从 x_field_graph_index 指向 left_graph_index，同时令其活跃
                        active_edges.add(new edge(x_field_graph_index, left_graph_index));
                    }
                }
            }
        }
    }

    // 尽管实现细节上，图节点编号可能和原始编号不同，但此方法输入输出都是原始编号
    public HashSet<Integer> getInfo(Integer var) {
        return graph.getInfo(get_node_index_from_var_index(var));
    }

    public void printInfo(Integer var) {
        HashSet<Integer> info = getInfo(var);
        System.out.println("possible objects of " + var + ": " + info);
    }

    public void print() {
        System.out.println("Standard Constraint Set:");
        System.out.println("Simple Constraints:");
        for (StandardSimple sc : sim_constraint) {
            System.out.println(sc);
        }
        System.out.println("Forall In Right Constraints:");
        for (StandardForallInRightSubsetLeft shc : has_constraint) {
            System.out.println(shc);
        }
        System.out.println("Forall In Left Constraints:");
        for (StandardForallInLeftContainsRight sic : in_constraint) {
            System.out.println(sic);
        }
        System.out.println();
    }
}
