package org.ludus.backend.por;

import org.junit.jupiter.api.Test;
import org.ludus.backend.fsm.FSM;
import org.ludus.backend.fsm.impl.Edge;
import org.ludus.backend.fsm.impl.FSMImpl;
import org.ludus.backend.fsm.impl.Location;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ClusterPORCyclesTest {

    @Test
    public void testTwoEvents() {
        FSMImpl fsm1 = new FSMImpl();
        Location l10 = new Location("l0");
        Location l11 = new Location("l1");
        fsm1.addLocation(l10, l11);
        fsm1.addEdge(l10, l11, "a");
        fsm1.setInitial(l10);
        fsm1.addControllable("a");
        fsm1.setMarked(l10, l11);

        FSMImpl fsm2 = new FSMImpl();
        Location l20 = new Location("l0");
        Location l21 = new Location("l1");
        fsm2.addLocation(l20, l21);
        fsm2.addEdge(l20, l21, "b");
        fsm2.setInitial(l20);
        fsm2.addControllable("b");
        fsm2.setMarked(l20, l21);

        ClusterPORCycles cpor2 = new ClusterPORCycles();
        DependencyGraph depGraph2 = new DependencyGraph();

        FSM<Location, Edge> result2 = cpor2.compute(Arrays.asList(fsm1, fsm2), depGraph2);

        // Composition with only one interleaving of a and b.
        assertEquals(3, result2.getVertices().size());
        assertEquals(2, result2.getEdges().size());
    }


    @Test
    public void testACSDmodel() throws Exception {
        // Initialize model.
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

        DependencyGraph depGraph = new DependencyGraph();
        depGraph.addDependency("a", "b");
        depGraph.addDependency("a", "d");
        depGraph.addDependency("b", "d");
        depGraph.addDependency("c", "d");

        // Run the cluster-inspired partial order reduction in the original setting where all states are marked.
        fsm1.setMarked(l10,l11);
        fsm2.setMarked(l20,l21);
        fsm3.setMarked(l30,l31);

        ClusterPORCycles cpor = new ClusterPORCycles();
        FSM<Location, Edge> result = cpor.compute(Arrays.asList(fsm1, fsm2, fsm3), depGraph);

        System.out.println("Generated:");
        System.out.println(result.getInitial().toString());
        for (Edge e : result.getEdges()) {
            System.out.println(e.toString());
        }

        System.out.println("vertices: " + result.getVertices().size());
        System.out.println("edges: " + result.getEdges().size());

        // Run the cluster-inspired partial order reduction in the new setting where (l10,l21,l30) is marked.
        // This implies that ample(s0) must include B.
        fsm1.unsetMarked(l10,l11);
        fsm2.unsetMarked(l20,l21);
        fsm3.unsetMarked(l30,l31);
        fsm1.setMarked(l10);
        fsm2.setMarked(l21);
        fsm3.setMarked(l30);

        ClusterPORCycles cpor2 = new ClusterPORCycles();
        FSM<Location, Edge> result2 = cpor2.compute(Arrays.asList(fsm1, fsm2, fsm3), depGraph);

        System.out.println("Generated:");
        System.out.println(result2.getInitial().toString());
        for (Edge e : result2.getEdges()) {
            System.out.println(e.toString());
        }

        System.out.println("vertices: " + result2.getVertices().size());
        System.out.println("edges: " + result2.getEdges().size());




    }
}