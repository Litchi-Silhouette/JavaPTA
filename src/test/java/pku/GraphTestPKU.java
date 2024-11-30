package pku;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import pku.dataStructures.Graph;


public class GraphTestPKU {
    @Test
    public void testGraph() {
        Graph<Integer> graph = new Graph<>();
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);
        graph.addEdge(1, 2, 0);
        graph.addEdge(2, 3, 0);
        graph.addEdge(3, 1, 1);
        graph.addEdge(3, 4, 0);
        graph.addEdge(4, 5, 0);
        assert graph.getEdge(1, 2) == 0;
        assert graph.getEdge(2, 3) == 0;
        assert graph.getEdge(3, 1) == 1;
        graph.mergeStrongConnectedComponent(1, new ArrayList<>(), new HashSet<>());
        assert graph.getEdges(1) == graph.getEdges(2);
        assert graph.getEdges(2) == graph.getEdges(3);
    }
}
