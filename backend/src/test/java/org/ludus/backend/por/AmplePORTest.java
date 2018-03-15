package org.ludus.backend.por;

import org.junit.jupiter.api.Test;
import org.ludus.backend.fsm.FSM;
import org.ludus.backend.fsm.impl.Edge;
import org.ludus.backend.fsm.impl.FSMImpl;
import org.ludus.backend.fsm.impl.Location;
import org.ludus.backend.por.AmplePOR;
import org.ludus.backend.por.DependencyGraph;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class AmplePORTest {

    @Test
    public void testTwoEvents() throws Exception {
        FSMImpl fsm1 = new FSMImpl();
        Location l10 = new Location("l0");
        Location l11 = new Location("l1");
        fsm1.addLocation(l10, l11);
        fsm1.addEdge(l10, l11, "a");
        fsm1.setInitial(l10);
        fsm1.addControllable("a");

        FSMImpl fsm2 = new FSMImpl();
        Location l20 = new Location("l0");
        Location l21 = new Location("l1");
        fsm2.addLocation(l20, l21);
        fsm2.addEdge(l20, l21, "b");
        fsm2.setInitial(l20);
        fsm2.addControllable("b");

        AmplePOR cpor = new AmplePOR();

        DependencyGraph depGraph = new DependencyGraph();
        depGraph.addDependency("a", "b");

        assertTrue(depGraph.hasDependency("a", "b"));
        assertTrue(depGraph.hasDependency("b", "a"));

        FSM<Location, Edge> result = cpor.compute(Arrays.asList(fsm1, fsm2), depGraph);

        // Composition with both interleavings of a and b.
        assertEquals(4, result.getVertices().size());
        assertEquals(4, result.getEdges().size());

        AmplePOR cpor2 = new AmplePOR();
        DependencyGraph depGraph2 = new DependencyGraph();
        FSM<Location, Edge> result2 = cpor2.compute(Arrays.asList(fsm1, fsm2), depGraph2);

        // Composition with only one interleaving of a and b.
        assertEquals(3, result2.getVertices().size());
        assertEquals(2, result2.getEdges().size());
    }

    @Test
    public void testThreeEvents() throws Exception {
        FSMImpl fsm1 = new FSMImpl();
        Location l10 = new Location("l0");
        Location l11 = new Location("l1");
        fsm1.addLocation(l10, l11);
        fsm1.addEdge(l10, l11, "a");
        fsm1.setInitial(l10);
        fsm1.addControllable("a");

        FSMImpl fsm2 = new FSMImpl();
        Location l20 = new Location("l0");
        Location l21 = new Location("l1");
        fsm2.addLocation(l20, l21);
        fsm2.addEdge(l20, l21, "b");
        fsm2.setInitial(l20);
        fsm2.addControllable("b");

        FSMImpl fsm3 = new FSMImpl();
        Location l30 = new Location("l0");
        Location l31 = new Location("l1");
        fsm3.addLocation(l30, l31);
        fsm3.addEdge(l30, l31, "c");
        fsm3.setInitial(l30);
        fsm3.addControllable("c");

        AmplePOR cpor = new AmplePOR();
        DependencyGraph depGraph = new DependencyGraph();
        depGraph.addDependency("a", "b");

        FSM<Location, Edge> result = cpor.compute(Arrays.asList(fsm1, fsm2, fsm3), depGraph);

        // Composition with both interleavings of a and b, and c independent.
        assertEquals(5, result.getVertices().size());
        assertEquals(5, result.getEdges().size());

        AmplePOR cpor2 = new AmplePOR();
        DependencyGraph depGraph2 = new DependencyGraph();
        FSM<Location, Edge> result2 = cpor2.compute(Arrays.asList(fsm1, fsm2, fsm3), depGraph2);

        // Composition with only one interleaving of a and b.
        assertEquals(4, result2.getVertices().size());
        assertEquals(3, result2.getEdges().size());
    }

    @Test
    public void testIndependence() {
        FSMImpl fsm1 = new FSMImpl();
        Location l10 = new Location("l0");
        Location l11 = new Location("l1");
        Location l12 = new Location("l2");
        fsm1.addLocation(l10, l11, l12);
        fsm1.addEdge(l10, l11, "i");
        fsm1.addEdge(l11, l12, "b");
        fsm1.setInitial(l10);
        fsm1.addControllable("b", "i");

        FSMImpl fsm2 = new FSMImpl();
        Location l20 = new Location("l0");
        Location l21 = new Location("l1");
        Location l22 = new Location("l2");
        fsm2.addLocation(l20, l21, l22);
        fsm2.addEdge(l20, l21, "a");
        fsm2.addEdge(l20, l22, "b");
        fsm2.setInitial(l20);
        fsm2.addControllable("a", "b");

        DependencyGraph depGraph = new DependencyGraph();
        depGraph.addDependency("a", "b");

        AmplePOR amplePOR = new AmplePOR();
        FSM<Location, Edge> result = amplePOR.compute(Arrays.asList(fsm1, fsm2), depGraph);

        for (Edge e : result.getEdges()) {
            System.out.println(e.toString());
        }
    }

}