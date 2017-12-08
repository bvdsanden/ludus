package org.ludus.backend.algorithms;

import org.junit.jupiter.api.Test;
import org.ludus.backend.graph.simpleSingle.SSEdge;
import org.ludus.backend.graph.simpleSingle.SSGraph;
import org.ludus.backend.graph.simpleSingle.SSVertex;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Bram van der Sanden
 */
public class CycleCheckTest {

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
        SSEdge e3 = graph.addEdge(v2, v0, 4.0);

        assertTrue(CycleCheck.check(graph));
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

        SSEdge e1 = graph.addEdge(v0, v1, 2.0);
        SSEdge e2 = graph.addEdge(v1, v2, 3.0);
        SSEdge e3 = graph.addEdge(v0, v2, 4.0);

        assertFalse(CycleCheck.check(graph));
    }

}
