package org.ludus.backend.algorithms;

import org.junit.jupiter.api.Test;
import org.ludus.backend.algebra.Value;
import org.ludus.backend.algebra.Vector;
import org.ludus.backend.fsm.impl.Location;
import org.ludus.backend.graph.simpleDouble.SDEdge;
import org.ludus.backend.graph.simpleDouble.SDGraph;
import org.ludus.backend.graph.simpleDouble.SDVertex;
import org.ludus.backend.statespace.Configuration;
import org.ludus.backend.statespace.MaxPlusStateSpace;
import org.ludus.backend.statespace.Transition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author bram
 */
public class HowardTest {

    private Double eps = 0.0005;

    public HowardTest() {
    }

    /**
     * Graph with only a cycle.
     */
    @Test
    public void testSimpleCycle() {
        SDGraph graph = new SDGraph();
        SDVertex v0 = new SDVertex();
        SDVertex v1 = new SDVertex();
        SDVertex v2 = new SDVertex();

        graph.addVertex(v0);
        graph.addVertex(v1);
        graph.addVertex(v2);

        SDEdge e1 = graph.addEdge(v0, v1, 1.0, 1.0);
        SDEdge e2 = graph.addEdge(v1, v2, 10.0, 1.0);
        SDEdge e3 = graph.addEdge(v2, v0, 1.0, 1.0);

        Double result = Howard.runHoward(graph, eps).getLeft();
        assertEquals(Double.valueOf(4.0), result);
        assertEquals(3, Howard.runHoward(graph, eps).getRight().size());
        assertTrue(Howard.runHoward(graph, eps).getRight().contains(e1));
        assertTrue(Howard.runHoward(graph, eps).getRight().contains(e2));
        assertTrue(Howard.runHoward(graph, eps).getRight().contains(e3));
    }

    /**
     * Graph with a path towards a cycle.
     */
    @Test
    public void testHeadCycle() {
        SDGraph graph = new SDGraph();

        SDVertex p0 = new SDVertex();
        SDVertex p1 = new SDVertex();
        SDVertex p2 = new SDVertex();

        SDVertex v0 = new SDVertex();
        SDVertex v1 = new SDVertex();
        SDVertex v2 = new SDVertex();

        graph.addVertex(p0);
        graph.addVertex(p1);
        graph.addVertex(p2);
        graph.addVertex(v0);
        graph.addVertex(v1);
        graph.addVertex(v2);

        graph.addEdge(p0, p1, 5.0, 6.0);
        graph.addEdge(p1, p2, 6.0, 7.0);
        graph.addEdge(p2, v0, 7.0, 8.0);

        SDEdge e1 = graph.addEdge(v0, v1, 1.0, 1.0);
        SDEdge e2 = graph.addEdge(v1, v2, 10.0, 1.0);
        SDEdge e3 = graph.addEdge(v2, v0, 1.0, 1.0);

        Double result = Howard.runHoward(graph, eps).getLeft();
        assertEquals(Double.valueOf(4.0), result);
        assertEquals(3, Howard.runHoward(graph, eps).getRight().size());
        assertTrue(Howard.runHoward(graph, eps).getRight().contains(e1));
        assertTrue(Howard.runHoward(graph, eps).getRight().contains(e2));
        assertTrue(Howard.runHoward(graph, eps).getRight().contains(e3));
    }

    /**
     * Graph with two cycles.
     */
    @Test
    public void testDoubleCycle() {
        SDGraph graph = new SDGraph();
        SDVertex v0 = new SDVertex();
        SDVertex v1 = new SDVertex();
        SDVertex v2 = new SDVertex();

        SDVertex v3 = new SDVertex();

        graph.addVertex(v0);
        graph.addVertex(v1);
        graph.addVertex(v2);
        graph.addVertex(v3);

        // Cycle 1.
        graph.addEdge(v0, v1, 1.0, 1.0);
        graph.addEdge(v1, v2, 10.0, 1.0);
        graph.addEdge(v2, v0, 1.0, 1.0);

        // Cycle 2.
        graph.addEdge(v0, v3, 1.0, 1.0);
        graph.addEdge(v3, v0, 2.0, 1.0);

        Double result = Howard.runHoward(graph, eps).getLeft();
        assertEquals(Double.valueOf(1.5), result);
    }

    @Test
    public void testSimpleCycle1() {
        MaxPlusStateSpace sp = new MaxPlusStateSpace();
        Location l0 = new Location("l0");
        Vector v0 = new Vector(5.0);
        Configuration c = new Configuration<>(l0, v0);
        sp.addConfiguration(c);
        Transition t = new Transition(c, "Drill", new Value(5.0), new Value(1.0), c);
        sp.addTransition(t);

        Double result = Howard.runHoward(sp, eps).getLeft();
        assertEquals(Double.valueOf(5.0), result);
    }
}
