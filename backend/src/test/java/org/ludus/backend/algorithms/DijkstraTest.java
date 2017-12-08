package org.ludus.backend.algorithms;

import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.graph.simpleSingle.SSEdge;
import org.ludus.backend.graph.simpleSingle.SSGraph;
import org.ludus.backend.graph.simpleSingle.SSVertex;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Bram van der Sanden
 */
public class DijkstraTest {

    public DijkstraTest() {
    }

    @Test
    public void testCase1() {
        SSGraph graph = new SSGraph();
        SSVertex v0 = new SSVertex();
        SSVertex v1 = new SSVertex();
        SSVertex v2 = new SSVertex();

        graph.addVertex(v0);
        graph.addVertex(v1);
        graph.addVertex(v2);

        SSEdge e1 = graph.addEdge(v0, v1, 2.0);
        SSEdge e2 = graph.addEdge(v1, v2, 3.0);
        SSEdge e3 = graph.addEdge(v0, v2, 4.0);

        Tuple<Double, List<SSEdge>> result = Dijkstra.runDijkstra(graph, v0, v2);
        assertEquals(Double.valueOf(4.0), result.getLeft());
        List<SSEdge> expected = Arrays.asList(e3);
        assertEquals(expected, result.getRight());
    }

    @Test
    public void testCase2() {
        SSGraph graph = new SSGraph();
        SSVertex v0 = new SSVertex();
        SSVertex v1 = new SSVertex();
        SSVertex v2 = new SSVertex();

        graph.addVertex(v0);
        graph.addVertex(v1);
        graph.addVertex(v2);

        SSEdge e1 = graph.addEdge(v0, v1, 1.0);
        SSEdge e2 = graph.addEdge(v1, v2, 2.0);
        SSEdge e3 = graph.addEdge(v0, v2, 4.0);

        Tuple<Double, List<SSEdge>> result = Dijkstra.runDijkstra(graph, v0, v2);
        assertEquals(Double.valueOf(3.0), result.getLeft());
        List<SSEdge> expected = Arrays.asList(e1, e2);
        assertEquals(expected, result.getRight());
    }

    @Test
    public void testCase3() {
        SSGraph graph = new SSGraph();
        SSVertex v0 = new SSVertex();
        SSVertex v1 = new SSVertex();
        SSVertex v2 = new SSVertex();

        graph.addVertex(v0);
        graph.addVertex(v1);
        graph.addVertex(v2);

        graph.addEdge(v0, v1, 1.0);
        graph.addEdge(v1, v2, 2.0);
        graph.addEdge(v0, v2, 4.0);
        graph.addEdge(v1, v1, 8.0);

        Tuple<Double, List<SSEdge>> result = Dijkstra.runDijkstra(graph, v0, v2);
        assertEquals(Double.valueOf(3.0), result.getLeft());
    }
}
