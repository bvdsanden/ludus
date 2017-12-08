package org.ludus.backend.fsm;

import org.ludus.backend.fsm.impl.Edge;
import org.ludus.backend.fsm.impl.FSMImpl;
import org.ludus.backend.fsm.impl.Location;

import java.util.*;

/**
 * Compute the parallel composition of two or more FSMs.
 *
 * @author Bram van der Sanden
 */
public class FSMComposition {

    private Map<List<Location>, Location> stateMap;
    private Integer currentStateId;
    private List<FSM<Location, Edge>> fsmList;
    private FSMImpl cFSM;
    private Set<String> alphabet;
    private Set<Edge> visitedEdges;
    private Set<Location> visitedLocations;

    public FSMComposition() {
    }

    /**
     * Compute the parallel composition of the given FSMs.
     *
     * @param fsms list of FSMs to be composed
     */
    public FSM<Location, Edge> compute(List<FSM<Location, Edge>> fsms) {
        stateMap = new HashMap<>();
        currentStateId = 0;
        cFSM = new FSMImpl();
        visitedEdges = new HashSet<>();
        visitedLocations = new HashSet<>();
        fsmList = fsms;

        // Compute the combined alphabet.
        alphabet = new HashSet<>();
        for (FSM<Location, Edge> fsm : fsms) {
            alphabet.addAll(fsm.getAlphabet());
        }

        // Create the initial state.
        List<Location> initialState = new ArrayList<>();
        for (int fsmId = 0; fsmId < fsmList.size(); fsmId++) {
            initialState.add(fsmId, fsmList.get(fsmId).getInitial());
        }
        Location stateLocation = getLocation(initialState);
        cFSM.setInitial(stateLocation);

        // Compute the composition on the fly using a DFS traversal.
        dfsTraversal(initialState);
        return cFSM;
    }

    /**
     * Recursive template method for a generic DFS traversal on the combined FSM.
     */
    private void dfsTraversal(List<Location> state) {
        visit(state);
        for (String a : alphabet) {
            if (isEnabled(fsmList, state, a)) {
                List<Location> targetState = getEdgeTarget(fsmList, state, a);

                Edge e = new Edge(getLocation(state), a, getLocation(targetState));
                if (!isVisited(e)) {
                    // Found an unexplored edge, explore it.
                    visit(e);

                    if (!isVisited(getLocation(targetState))) {
                        dfsTraversal(targetState);
                    } //else {
                    // Vertex w is explored, this is a back edge.
                    // Back edge from e to v.
                    //}

                }
            }
        }
    }


    /**
     * Mark a state as visited, and add to the combined FSM.
     *
     * @param state list of references to each individual current FSM location
     */
    private void visit(List<Location> state) {
        visitedLocations.add(getLocation(state));
        cFSM.addLocation(stateMap.get(state));
    }

    /**
     * Mark an edge as visited, and add to the combined FSM.
     *
     * @param e combined edge
     */
    private void visit(Edge e) {
        visitedEdges.add(e);
        cFSM.addEdge(e);
    }

    /**
     * Return a location for the given state.
     *
     * @param state local state in each automaton in the composition
     * @return location in the composition
     */
    private Location getLocation(List<Location> state) {
        if (!stateMap.containsKey(state)) {
            currentStateId++;
            stateMap.put(state, new Location("s" + currentStateId));
        }
        return stateMap.get(state);
    }

    private boolean isVisited(Location location) {
        return visitedLocations.contains(location);
    }

    private boolean isVisited(Edge e) {
        return visitedEdges.contains(e);
    }

    /**
     * Compute the set of enabled events in the given state in the composition.
     *
     * @param fsmList list of the individual FSMs
     * @param state   current state in the composition
     * @return all events that are enabled in the composition in the given state
     */
    public static Set<String> enabled(List<FSM<Location, Edge>> fsmList, List<Location> state) {
        // Compute all locally enabled actions.
        Set<String> locallyEnabled = new HashSet<>();
        for (int fsmId = 0; fsmId < fsmList.size(); fsmId++) {
            FSM<Location, Edge> fsm = fsmList.get(fsmId);
            for (Edge e : fsm.outgoingEdgesOf(state.get(fsmId))) {
                locallyEnabled.add(e.getEvent());
            }
        }

        // Determine which events are globally enabled.
        Set<String> globallyEnabled = new HashSet<>();
        for (String event : locallyEnabled) {
            if (isEnabled(fsmList, state, event)) {
                globallyEnabled.add(event);
            }
        }
        return globallyEnabled;
    }


    /**
     * Check if a given event is enabled in the composition.
     *
     * @param fsmList list of the individual FSMs
     * @param state   current state in the composition
     * @param event   event to check
     * @return true if the event is enabled in the composition given the current state
     */
    public static boolean isEnabled(List<FSM<Location, Edge>> fsmList, List<Location> state, String event) {
        // Event is enabled if and only if it is enabled in each individual FSM.
        boolean found = false;

        for (int fsmId = 0; fsmId < fsmList.size(); fsmId++) {
            FSM<Location, Edge> fsm = fsmList.get(fsmId);
            Location loc = state.get(fsmId);

            if (fsm.getAlphabet().contains(event)) {
                // FSM is involved in the synchronization.            	
                if (isEnabled(fsm, loc, event)) {
                    // We've found an automaton that enables the event.
                    found = true;
                } else {
                    // The event will be globally disabled.
                    return false;
                }
            }
        }
        return found;
    }

    /**
     * Check if an event is enabled in the given location.
     *
     * @param fsm      finite-state machine
     * @param location current location in the given finite-state machine
     * @param event    event to execute
     * @return true if event is enabled in the given location
     */
    public static boolean isEnabled(FSM<Location, Edge> fsm, Location location, String event) {
        for (Edge e : fsm.outgoingEdgesOf(location)) {
            if (fsm.getEvent(e).equals(event)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the new location after executing the given event in the composition.
     *
     * @param fsmList list of the individual automata
     * @param state   location in the composition
     * @param event   event to execute
     * @return new location reached after executing {@code event} from {@code state}
     */
    public static List<Location> getEdgeTarget(List<FSM<Location, Edge>> fsmList, List<Location> state, String event) {
        assert (isEnabled(fsmList, state, event));
        List<Location> targetState = new ArrayList<>(state);
        for (int fsmId = 0; fsmId < fsmList.size(); fsmId++) {
            // Current location.
            FSM<Location, Edge> fsm = fsmList.get(fsmId);
            Location loc = state.get(fsmId);
            for (Edge e : fsm.outgoingEdgesOf(loc)) {
                if (fsm.getEvent(e).equals(event)) {
                    // Set target location as the new state.
                    targetState.set(fsmId, fsm.getEdgeTarget(e));
                    // Found the edge, assuming deterministic FSMs we are done.
                    break;
                }
            }
        }
        return targetState;
    }


}
