package org.ludus.backend.por;

import org.ludus.backend.fsm.FSM;
import org.ludus.backend.fsm.impl.Edge;
import org.ludus.backend.fsm.impl.FSMImpl;
import org.ludus.backend.fsm.impl.Location;

import java.util.Set;
import java.util.stream.Collectors;


public class Preprocessor {
    public static final String BOTTOM = "_dump";
    public static final String OMEGA = "_omega";

    /**
     * Add an omega-selfloop to each marked state in the given automaton.
     * @param fsm input finite state machine
     * @return copy of the fsm where each marked state has a self-loop with an omega event.
     */
    public static FSM<Location, Edge> addOmegaLoops(FSMImpl fsm) {
        FSMImpl newFSM = FSMImpl.clone(fsm);
        for (Location m : newFSM.getMarkedVertices()) {
            newFSM.addEdge(m,m,OMEGA);
        }
        return newFSM;
    }

    /**
     * Plantify a given requirement automaton. This transformation preserves controllability.
     * @param fsm input finite state machine
     * @return plantified automaton
     */
    public static FSM<Location,Edge> plantify(FSMImpl fsm) {
        FSMImpl newFSM = FSMImpl.clone(fsm);
        Set<String> uEvents = newFSM.getUncontrollable();

        // Add the dump state with the self loop.
        Location dump = new Location(BOTTOM);
        newFSM.addLocation(dump);
        for (String event : newFSM.getAlphabet()) {
            newFSM.addEdge(dump,dump,event);
        }

        // For each location, if there is no edge for some uncontrollable event
        // add it to the dump state.
        for (Location l : newFSM.getVertices()) {
            for (String uEvent : uEvents) {
                Set<String> outgoingEvents = newFSM.outgoingEdgesOf(l).stream().map(Edge::getEvent).collect(Collectors.toSet());
                if(!outgoingEvents.contains(uEvent)) {
                    // Create an edge to the dump state.
                    newFSM.addEdge(l,dump,uEvent);
                }
            }
        }

        return newFSM;
    }
}
