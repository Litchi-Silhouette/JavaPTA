import java.util.HashSet;

import pku.dataStructures.Graph;

import org.junit.Test;

public class GraphTest {
    @Test
    public void testGraph() {
        Graph<Integer> graph = new Graph<>();
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addEdge(1, 2, 0);
        graph.addEdge(2, 3, 0);
        graph.addEdge(3, 1, 1);
        assert graph.getEdge(1, 2) == 0;
        assert graph.getEdge(2, 3) == 0;
        assert graph.getEdge(3, 1) == 1;
        graph.mergeStrongConnectedComponent(1, new HashSet<>(), new HashSet<>());
    }
}