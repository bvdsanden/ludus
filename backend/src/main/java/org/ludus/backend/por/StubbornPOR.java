package org.ludus.backend.por;

import org.ludus.backend.fsm.FSM;
import org.ludus.backend.fsm.FSMComposition;
import org.ludus.backend.fsm.impl.Edge;
import org.ludus.backend.fsm.impl.FSMImpl;
import org.ludus.backend.fsm.impl.Location;

import java.util.*;

/**
 * Stubborn partial-order reduction.
 *
 * @author Bram van der Sanden
 */
public class StubbornPOR {

    private Map<List<Location>, Location> stateMap;
    private Integer currentStateId;
    private List<FSM<Location, Edge>> fsmList;
    private FSMImpl cFSM;
    private Set<Edge> visitedEdges;
    private Set<Location> visitedLocations;
    private DependencyInterface dependencies;

    /**
     * Compute the stubborn composition of the given FSMs.
     *
     * @param fsmList      list of individual FSMs
     * @param dependencies map of event dependencies
     */
    public FSM<Location, Edge> compute(List<FSM<Location, Edge>> fsmList, DependencyInterface dependencies) {
        stateMap = new HashMap<>();
        currentStateId = 0;
        cFSM = new FSMImpl();
        visitedEdges = new HashSet<>();
        visitedLocations = new HashSet<>();
        this.fsmList = fsmList;

        // Dependent events.
        this.dependencies = dependencies;

        // Create the initial state.
        List<Location> initialState = new ArrayList<>();
        for (int fsmId = 0; fsmId < this.fsmList.size(); fsmId++) {
            initialState.add(fsmId, this.fsmList.get(fsmId).getInitial());
        }
        Location stateLocation = getLocation(initialState);
        cFSM.setInitial(stateLocation);

        // Compute the composition on the fly using a DFS traversal.
        dfsTraversal(initialState);
        return cFSM;
    }

    /**
     * Recursive template method for a generic DFS traversal on the combined FSM.
     *
     * @param state current state in the composition
     */
    private void dfsTraversal(List<Location> state) {
        visit(state);

        for (String a : stubborn(fsmList, state)) {
            // Each event in stubborn is by definition an enabled event.
            List<Location> targetState = FSMComposition.getEdgeTarget(fsmList, state, a);

            Edge e = new Edge(getLocation(state), a, getLocation(targetState));
            if (!isVisited(e)) {
                // Found an unexplored edge, explore it.
                visit(e);

                if (!isVisited(getLocation(targetState))) {
                    dfsTraversal(targetState);
                }
            }
        }
    }

    /**
     * Try to compute the smallest stubborn set, otherwise return the set of all enabled events.
     * Note that the smallest stubborn set is a heuristic; always choosing the smallest set
     * does not always lead to the smallest reduced state space.
     *
     * @param fsmList list of individual FSMs
     * @param state   current state in the composition
     * @return stubborn set with a subset of all enabled events in {@code state}
     */
    private Set<String> stubborn(List<FSM<Location, Edge>> fsmList, List<Location> state) {
        // Compute the enabled set enabled(c).
        Set<String> enabled = FSMComposition.enabled(fsmList, state);

        // Compute the smallest ample set starting from each enabled event (fixed point algorithm).
        Set<String> ampleSmallest = enabled;
        for (String event : enabled) {
            // Compute the stubborn alphabet stubborn(c).
            Set<String> stubbornSet = getStubborn(enabled, event, fsmList, state);

            Set<String> ample = new HashSet<>(enabled);
            ample.retainAll(stubbornSet);

            if (ample.size() < ampleSmallest.size()) {
                ampleSmallest = ample;
            }
        }

        return ampleSmallest;
    }

    /**
     * Compute a stubborn set given the candidate event.
     *
     * @param enabled   set of all enabled events in {@code state}
     * @param candidate cluster is build starting from this candidate
     * @param fsmList   list of individual FSMs
     * @param state     current state in the composition
     * @return cluster
     */
    private Set<String> getStubborn(Set<String> enabled, String candidate, List<FSM<Location, Edge>> fsmList, List<Location> state) {
        Set<String> processed = new HashSet<>();
        Queue<String> toProcess = new LinkedList<>();
        toProcess.add(candidate);
        Set<String> stubborn = new HashSet<>();
        stubborn.add(candidate);

        while (!toProcess.isEmpty()) {
            String event = toProcess.remove();
            processed.add(event);

            // Add all dependent enabled actions.
            Set<String> dependentEnabled = new HashSet<>(enabled);
            dependentEnabled.retainAll(dependencies.getDependencies(event));

            Set<String> addition = new HashSet<>(dependentEnabled);

            // If the event is not enabled, then add all locally enabled actions of some automaton.
            if (!enabled.contains(event)) {
                // Add the locally enabled set of some automaton.
                for (int fsmId = 0; fsmId < fsmList.size(); fsmId++) {
                    FSM<Location, Edge> fsm = fsmList.get(fsmId);
                    if (fsm.getAlphabet().contains(event)) {
                        for (Edge e : fsm.outgoingEdgesOf(state.get(fsmId))) addition.add(e.getEvent());
                        break;
                    }
                }
            }

            // If the event is enabled, then each automaton has no shared actions, or add all locally enabled actions.
            for (int fsmId = 0; fsmId < fsmList.size(); fsmId++) {
                FSM<Location, Edge> fsm = fsmList.get(fsmId);
                if (fsm.getAlphabet().contains(event)) {
                    // Add all locally enabled actions.
                    for (Edge e : fsm.outgoingEdgesOf(state.get(fsmId))) {
                        addition.add(e.getEvent());
                    }
                }
            }

            stubborn.addAll(addition);

            // See which events we need to add to the process queue.
            for (String added : addition) {
                if (!processed.contains(added)) {
                    toProcess.add(added);
                }
            }
        }
        return stubborn;
    }

    /**
     * Mark a state as visited, and add to the combined FSM.
     *
     * @param state current state in the composition
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
     * @param state current state in the composition
     * @return the location corresponding to the given state
     */
    private Location getLocation(List<Location> state) {
        if (!stateMap.containsKey(state)) {
            currentStateId++;
            stateMap.put(state, new Location("s" + currentStateId));
        }
        return stateMap.get(state);
    }

    /**
     * Test if a location in the composition has been visited.
     *
     * @param l location to test
     * @return true if the location in the composition has been visited
     */
    private boolean isVisited(Location l) {
        return visitedLocations.contains(l);
    }

    /**
     * Test if an edge in the composition has been visited.
     *
     * @param e edge to test
     * @return true if the edge in the composition has been visited
     */
    private boolean isVisited(Edge e) {
        return visitedEdges.contains(e);
    }


}
