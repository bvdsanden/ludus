package org.ludus.backend.fsm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ludus.backend.fsm.impl.Edge;
import org.ludus.backend.fsm.impl.FSMImpl;
import org.ludus.backend.fsm.impl.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FSMCompositionTest {

    // Test FSMs.
    FSMImpl fsm1, fsm2, fsm3;
    Location l10, l11, l20, l21, l30, l31;
    List<FSM<Location, Edge>> fsmList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        fsm1 = new FSMImpl();
        l10 = new Location("l0");
        l11 = new Location("l1");
        fsm1.addLocation(l10, l11);
        fsm1.addEdge(l10, l11, "a");
        fsm1.addEdge(l11, l10, "b");
        fsm1.setInitial(l10);
        fsm1.addControllable("a", "b");

        fsm2 = new FSMImpl();
        l20 = new Location("l0");
        l21 = new Location("l1");
        fsm2.addLocation(l20, l21);
        fsm2.addEdge(l20, l21, "a");
        fsm2.addEdge(l21, l20, "c");
        fsm2.setInitial(l20);
        fsm2.addControllable("a", "c");

        fsm3 = new FSMImpl();
        l30 = new Location("l0");
        l31 = new Location("l1");
        fsm3.addLocation(l30, l31);
        fsm3.addEdge(l30, l31, "c");
        fsm3.setInitial(l30);
        fsm3.addControllable("c");

        fsmList = new ArrayList<>();
        fsmList.add(fsm1);
        fsmList.add(fsm2);
        fsmList.add(fsm3);
    }


    @Test
    public void testCompute() {
        FSMComposition comp = new FSMComposition();
        FSM<Location, Edge> combined = comp.compute(Arrays.asList(fsm1, fsm2));
        assertEquals(4, combined.getVertices().size());
        assertEquals(5, combined.getEdges().size());
        assertEquals(1, combined.outgoingEdgesOf(combined.getInitial()).size());


        FSMComposition comp2 = new FSMComposition();
        FSM<Location, Edge> combined2 = comp2.compute(Arrays.asList(fsm2, fsm3));
        assertEquals(4, combined2.getVertices().size());
        assertEquals(3, combined2.getEdges().size());
        assertEquals(1, combined2.outgoingEdgesOf(combined2.getInitial()).size());
    }

    @Test
    public void testIsEnabled() {
        List<Location> state = new ArrayList<>();
        state.add(fsm1.getInitial());
        state.add(fsm2.getInitial());
        state.add(fsm3.getInitial());

        assertTrue(FSMComposition.isEnabled(fsmList, state, "a"));
        assertFalse(FSMComposition.isEnabled(fsmList, state, "b"));
        assertFalse(FSMComposition.isEnabled(fsmList, state, "c"));


        List<Location> state3 = Arrays.asList(l10, l21, l30);
        assertFalse(FSMComposition.isEnabled(fsm1, l10, "c"));
        assertTrue(FSMComposition.isEnabled(fsm2, l21, "c"));
        assertTrue(FSMComposition.isEnabled(fsm3, l30, "c"));
        assertTrue(FSMComposition.isEnabled(fsmList, state3, "c"));


        List<Location> state4 = Arrays.asList(l21, l31);
        assertFalse(FSMComposition.isEnabled(Arrays.asList(fsm2, fsm3), state4, "c"));
    }

    @Test
    public void testEdgeTarget() {
        List<Location> state = Arrays.asList(fsm1.getInitial(), fsm2.getInitial(), fsm3.getInitial());

        // Event a.
        List<Location> targetState = FSMComposition.getEdgeTarget(fsmList, state, "a");
        assertEquals(l11, targetState.get(0));
        assertEquals(l21, targetState.get(1));
        assertEquals(state.get(2), targetState.get(2));

        // Event b.
        List<Location> state2 = Arrays.asList(l11, fsm2.getInitial(), fsm3.getInitial());
        targetState = FSMComposition.getEdgeTarget(fsmList, state2, "b");
        assertEquals(state, targetState);

        // Event c.
        List<Location> state3 = Arrays.asList(l10, l21, l30);
        targetState = FSMComposition.getEdgeTarget(fsmList, state3, "c");

        List<Location> state4 = Arrays.asList(l10, l20, l31);
        assertEquals(state4, targetState);
    }

}