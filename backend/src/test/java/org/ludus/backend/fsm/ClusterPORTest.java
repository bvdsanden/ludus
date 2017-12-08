package org.ludus.backend.fsm;

import org.junit.jupiter.api.Test;
import org.ludus.backend.fsm.impl.Edge;
import org.ludus.backend.fsm.impl.FSMImpl;
import org.ludus.backend.fsm.impl.Location;
import org.ludus.backend.por.AmplePOR;
import org.ludus.backend.por.ClusterPOR;
import org.ludus.backend.por.DependencyGraph;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ClusterPORTest {

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

        ClusterPOR cpor2 = new ClusterPOR();
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

        ClusterPOR cpor = new ClusterPOR();
        // Events a and b are dependent.
        DependencyGraph depGraph = new DependencyGraph();
        depGraph.addDependency("a", "b");

        FSM<Location, Edge> result = cpor.compute(Arrays.asList(fsm1, fsm2, fsm3), depGraph);

        // Composition with both interleavings of a and b, and c independent.
        assertEquals(5, result.getVertices().size());
        assertEquals(5, result.getEdges().size());


        ClusterPOR cpor2 = new ClusterPOR();
        DependencyGraph depGraph2 = new DependencyGraph();
        FSM<Location, Edge> result2 = cpor2.compute(Arrays.asList(fsm1, fsm2, fsm3), depGraph2);

        // Composition with only one interleaving of a and b.
        assertEquals(4, result2.getVertices().size());
        assertEquals(3, result2.getEdges().size());
    }


    @Test
    public void testACSDmodel() throws Exception {
        FSMImpl fsm1 = new FSMImpl();
        Location l10 = new Location("l0");
        Location l11 = new Location("l1");
        fsm1.setInitial(l10);
        fsm1.addLocation(l10, l11);
        fsm1.addControllable("a", "d");
        fsm1.addEdge(l10, l11, "a");
        fsm1.addEdge(l11, l10, "d");

        FSMImpl fsm2 = new FSMImpl();
        Location l20 = new Location("l0");
        Location l21 = new Location("l1");
        fsm2.setInitial(l20);
        fsm2.addLocation(l20, l21);
        fsm2.addControllable("b", "d");
        fsm2.addEdge(l20, l21, "b");
        fsm2.addEdge(l21, l20, "d");

        FSMImpl fsm3 = new FSMImpl();
        Location l30 = new Location("l0");
        Location l31 = new Location("l1");
        fsm3.setInitial(l30);
        fsm3.addLocation(l30, l31);
        fsm3.addControllable("c", "d");
        fsm3.addEdge(l30, l31, "c");
        fsm3.addEdge(l31, l30, "d");

        ClusterPOR cpor = new ClusterPOR();
        DependencyGraph depGraph = new DependencyGraph();
        depGraph.addDependency("a", "b");
        depGraph.addDependency("a", "d");
        depGraph.addDependency("b", "d");
        depGraph.addDependency("c", "d");

        FSM<Location, Edge> result = cpor.compute(Arrays.asList(fsm1, fsm2, fsm3), depGraph);

        System.out.println("Generated:");
        System.out.println(result.getInitial().toString());
        for (Edge e : result.getEdges()) {
            System.out.println(e.toString());
        }

        System.out.println("vertices: " + result.getVertices().size());
        System.out.println("edges: " + result.getEdges().size());

        AmplePOR apor = new AmplePOR();
        FSM<Location, Edge> resultAmple = apor.compute(Arrays.asList(fsm1, fsm2, fsm3), depGraph);

        System.out.println("vertices ample: " + resultAmple.getVertices().size());
        System.out.println("edges ample: " + resultAmple.getEdges().size());
    }

    @Test
    public void testEnabledness() {
        FSMImpl fsm1 = new FSMImpl();
        Location l10 = new Location("l0");
        Location l11 = new Location("l1");
        Location l12 = new Location("l2");
        fsm1.addLocation(l10, l11, l12);
        fsm1.addControllable("b", "e");
        fsm1.setInitial(l10);
        fsm1.addEdge(l10, l11, "b");
        fsm1.addEdge(l10, l12, "e");

        FSMImpl fsm2 = new FSMImpl();
        Location l20 = new Location("l0");
        Location l21 = new Location("l1");
        Location l22 = new Location("l2");
        fsm2.addLocation(l20, l21, l22);
        fsm2.addEdge(l20, l21, "a");
        fsm2.addEdge(l21, l22, "b");
        fsm2.setInitial(l20);
        fsm2.addControllable("a", "b");

        ClusterPOR cpor = new ClusterPOR();
        DependencyGraph depGraph = new DependencyGraph();
        FSM<Location, Edge> result = cpor.compute(Arrays.asList(fsm1, fsm2), depGraph);

        assertEquals(4, result.getVertices().size());
        assertEquals(3, result.getEdges().size());
    }


    @Test
    public void testPaperMockup() throws Exception {
        FSMImpl fsm1 = new FSMImpl();
        Location l10 = new Location("l0");
        Location l11 = new Location("l1");
        fsm1.setInitial(l10);
        fsm1.addLocation(l10, l11);
        fsm1.addControllable("a", "d");
        fsm1.addEdge(l10, l11, "a");
        fsm1.addEdge(l11, l10, "d");

        FSMImpl fsm2 = new FSMImpl();
        Location l20 = new Location("l0");
        Location l21 = new Location("l1");
        fsm2.setInitial(l20);
        fsm2.addLocation(l20, l21);
        fsm2.addControllable("b", "d");
        fsm2.addEdge(l20, l21, "b");
        fsm2.addEdge(l21, l20, "d");

        FSMImpl fsm3 = new FSMImpl();
        Location l30 = new Location("l0");
        Location l31 = new Location("l1");
        fsm3.setInitial(l30);
        fsm3.addLocation(l30, l31);
        fsm3.addControllable("c", "e");
        fsm3.addEdge(l30, l31, "c");
        fsm3.addEdge(l31, l30, "e");

        ClusterPOR cpor = new ClusterPOR();
        // Events a and b are dependent.
        DependencyGraph depGraph = new DependencyGraph();
        depGraph.addDependency("a", "b");

        FSM<Location, Edge> result = cpor.compute(Arrays.asList(fsm1, fsm2, fsm3), depGraph);

        // Note that part is ignored in the state space.
        // In our setting this cannot occur, since it will yield an infinite max-plus state space.
        assertEquals(2, result.getVertices().size());
        assertEquals(2, result.getEdges().size());

        // Full state space.
        ClusterPOR cpor2 = new ClusterPOR();
        DependencyGraph depGraph2 = new DependencyGraph();
        List<String> events = Arrays.asList("a", "b", "c", "d", "e");
        for (String a : events) {
            for (String b : events) {
                depGraph2.addDependency(a, b);
            }
        }
        FSM<Location, Edge> result2 = cpor2.compute(Arrays.asList(fsm1, fsm2, fsm3), depGraph2);

        assertEquals(8, result2.getVertices().size());
        assertEquals(18, result2.getEdges().size());
    }
}