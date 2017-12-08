package org.ludus.backend.por;

import org.ludus.backend.fsm.FSM;
import org.ludus.backend.fsm.FSMComposition;
import org.ludus.backend.fsm.impl.Edge;
import org.ludus.backend.fsm.impl.FSMImpl;
import org.ludus.backend.fsm.impl.Location;

import java.util.*;

/**
 * Ample set partial-order reduction.
 *
 * @author Bram van der Sanden
 */
public class AmplePOR {

    private Map<List<Location>, Location> stateMap;
    private Integer currentStateId;
    private List<FSM<Location, Edge>> fsmList;
    private FSMImpl cFSM;
    private Set<String> alphabet;
    private Set<Edge> visitedEdges;
    private Set<Location> visitedLocations;
    private DependencyInterface dependencies;

    /**
     * Compute the ample composition of the given FSMs.
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

        // Compute the combined alphabet.
        alphabet = new HashSet<>();
        for (FSM<Location, Edge> fsm : fsmList) {
            alphabet.addAll(fsm.getAlphabet());
        }

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

        for (String a : ample(fsmList, state)) {
            // Each event in ample is by definition an enabled event.
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
     * Try to compute a ample set, otherwise return the set of all enabled events.
     *
     * @param fsmList list of individual FSMs
     * @param state   current state in the composition
     * @return ample set with a subset of all enabled events in {@code state}
     */
    private Set<String> ample(List<FSM<Location, Edge>> fsmList, List<Location> state) {
        Set<String> enabled = FSMComposition.enabled(fsmList, state);

        // Try the individual FSMs.
        Set<String> currentAmple = enabled;
        for (int fsmId = 0; fsmId < fsmList.size(); fsmId++) {
            Set<Integer> cluster = new HashSet<>();
            cluster.add(fsmId);
            Optional<Set<String>> ampleResult = checkCluster(fsmList, state, enabled, cluster);
            if (ampleResult.isPresent() && ampleResult.get().size() < currentAmple.size()) {
                currentAmple = ampleResult.get();
            }
        }

        return currentAmple;
    }

    /**
     * Check whether two events are dependent.
     *
     * @param eventA first event
     * @param eventB second event
     * @return true if both events are dependent
     */
    private boolean dependent(String eventA, String eventB) {
        return dependencies.hasDependency(eventA, eventB);
    }

    /**
     * Check the cample conditions on the given cluster.
     *
     * @param fsmList list of individual FSMs
     * @param state   current state in the composition
     * @param enabled set of all enabled events in {@code state}
     * @param cluster cluster set
     * @return subset of the enabled actions if the cample set is valid, otherwise no set since no reduction is possible
     */
    private Optional<Set<String>> checkCluster(List<FSM<Location, Edge>> fsmList, List<Location> state, Set<String> enabled, Set<Integer> cluster) {
        // Compute cluster alphabet.
        Set<String> clusterAlphabet = new HashSet<>();
        for (int fsmId = 0; fsmId < fsmList.size(); fsmId++) {
            if (cluster.contains(fsmId)) {
                clusterAlphabet.addAll(fsmList.get(fsmId).getAlphabet());
            }
        }

        // Compute clusterEnabled(c) = enabled(c) && cluster(c)
        Set<String> clusterEnabled = new HashSet<>(enabled);
        clusterEnabled.retainAll(clusterAlphabet);

        // (A1) Check that enabled(c) intersected with C is not empty.
        if (clusterEnabled.isEmpty()) {
            return Optional.empty();
        }

        // (A2.1) Each event in clusterEnabled(c) is independent with all events outside cluster.
        Set<String> outsideCluster = new HashSet<>(alphabet);
        outsideCluster.removeAll(clusterAlphabet);

        for (String event : clusterEnabled) {
            for (String other : outsideCluster) {
                if (dependent(event, other)) {
                    return Optional.empty();
                }
            }
        }

        // (A2.2) Each event in locallyEnabled(c) does not occur outside of the cluster.
        Set<String> locallyEnabled = getLocallyEnabled(fsmList, cluster, state);
        for (int fsmId = 0; fsmId < fsmList.size(); fsmId++) {
            if (!cluster.contains(fsmId)) {
                // FSM is outside of the cluster, then no locally enabled event is in the alphabet.
                FSM<Location, Edge> fsm = fsmList.get(fsmId);
                if (fsm.getAlphabet().stream().anyMatch(locallyEnabled::contains)) {
                    return Optional.empty();
                }

            }
        }

        return Optional.of(clusterEnabled);
    }

    /**
     * Get all events that are enabled in the cluster, without considering any FSM outside of the cluster.
     *
     * @param fsmList list of individual FSMs
     * @param cluster list of the ids of FSMs that are in the cluster
     * @param state   current state in the composition
     * @return all events that are enabled in the cluster composition, not looking at FSMs outside cluster
     */
    private Set<String> getLocallyEnabled(List<FSM<Location, Edge>> fsmList, Set<Integer> cluster, List<Location> state) {
        List<FSM<Location, Edge>> clusterFSMs = new ArrayList<>();
        List<Location> clusterState = new ArrayList<>();
        // Create the FSM list and the state in the cluster.
        for (Integer fid : cluster) {
            clusterFSMs.add(fsmList.get(fid));
            clusterState.add(state.get(fid));
        }
        // Compute all events that are enabled just looking at the cluster.
        return FSMComposition.enabled(clusterFSMs, clusterState);
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
