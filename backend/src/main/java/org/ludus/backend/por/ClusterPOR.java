package org.ludus.backend.por;

import org.ludus.backend.fsm.FSM;
import org.ludus.backend.fsm.FSMComposition;
import org.ludus.backend.fsm.impl.Edge;
import org.ludus.backend.fsm.impl.FSMImpl;
import org.ludus.backend.fsm.impl.Location;

import java.util.*;

/**
 * Cluster partial-order reduction.
 *
 * @author Bram van der Sanden
 */
public class ClusterPOR {

    private Map<List<Location>, Location> stateMap;
    private Integer currentStateId;
    private List<FSM<Location, Edge>> fsmList;
    private FSMImpl cFSM;
    private Set<String> alphabet;
    private Set<Edge> visitedEdges;
    private Set<Location> visitedLocations;
    private DependencyInterface dependencies;
    private Map<String, Set<Integer>> occursInMap;
    private Map<String, Set<String>> alphabetMap;

    /**
     * Compute the clustered cample composition of the given FSMs.
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

        // For each event, find all FSMs that have this event in their alphabet, and also compute the combined alphabet.
        occursInMap = new HashMap<>();
        alphabetMap = new HashMap<>();
        for (String event : alphabet) {
            Set<Integer> occursList = new HashSet<>();
            Set<String> alphabetList = new HashSet<>();
            // Check if FSM contains event.
            for (int fsmId = 0; fsmId < this.fsmList.size(); fsmId++) {
                FSM<Location, Edge> fsm = fsmList.get(fsmId);
                if (fsm.getAlphabet().contains(event)) {
                    occursList.add(fsmId);
                    alphabetList.addAll(fsm.getAlphabet());
                }
            }
            occursInMap.put(event, occursList);
            alphabetMap.put(event, alphabetList);
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

        for (String a : cample(fsmList, state)) {
            // Each event in cample is by definition an enabled event.
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
     * Try to compute the smallest cample set, otherwise return the set of all enabled events.
     * Note that the smallest cample set is a heuristic; always choosing the smallest set
     * does not always lead to the smallest reduced state space.
     *
     * @param fsmList list of individual FSMs
     * @param state   current state in the composition
     * @return cample set with a subset of all enabled events in {@code state}
     */
    private Set<String> cample(List<FSM<Location, Edge>> fsmList, List<Location> state) {
        // Compute the enabled set enabled(c).
        Set<String> enabled = FSMComposition.enabled(fsmList, state);

        // Compute the smallest clusterEnabled set starting from each enabled event (fixed point algorithm).
        Set<String> clusterEnabled = enabled;
        for (String event : enabled) {
            // Compute a valid cluster.
            Set<Integer> cluster = getCluster(fsmList, state, enabled, event);
            // Check the conditions on enabled(c) && cluster(c).
            Optional<Set<String>> clusterEnabledCheck = checkCluster(fsmList, state, enabled, cluster);
            if (clusterEnabledCheck.isPresent() && clusterEnabledCheck.get().size() < clusterEnabled.size()) {
                clusterEnabled = clusterEnabledCheck.get();
            }
            if (!clusterEnabledCheck.isPresent()) {
                System.out.println("The cluster that has been computed is incorrect.");
            }
        }

        return clusterEnabled;
    }

    /**
     * Compute a cluster starting form a candidate event.
     * Iterate until a fixed point is reached.
     *
     * @param enabled   set of all enabled events in {@code state}
     * @param candidate cluster is build starting from this candidate
     * @return cluster
     */
    private Set<Integer> getCluster(List<FSM<Location, Edge>> fsmList, List<Location> state, Set<String> enabled, String candidate) {
        Set<String> processed = new HashSet<>();
        Set<String> toProcess = new HashSet<>();
        Set<Integer> cluster = new HashSet<>();
        Set<String> clusterAlphabet = new HashSet<>();
        Set<String> locallyEnabled;

        toProcess.add(candidate);

        while (!toProcess.isEmpty()) {
            String event = toProcess.iterator().next();
            processed.add(event);

            // Globally enabled event.
            if (enabled.contains(event)) {
                // 1. Add all FSMs that contain this event.
                cluster.addAll(occursInMap.get(event));
                clusterAlphabet.addAll(alphabetMap.get(event));

                // 2. For each resource-dependent event, add one corresponding automaton to the cluster.
                // This means that the dependent event becomes part of the cluster.
                for (String dep : getDependencies(event)) {
                    if (!clusterAlphabet.contains(dep)) {
                        // Pick the first automaton that has event {@code dep} and add this automaton to the cluster.
                        Integer fsmId = occursInMap.get(dep).iterator().next();
                        cluster.add(fsmId);
                        clusterAlphabet.addAll(fsmList.get(fsmId).getAlphabet());
                    }
                }
            }

            // Update locally enabled information.
            locallyEnabled = getLocallyEnabled(fsmList, cluster, state);

            // Pure locally enabled event.
            if (!enabled.contains(event) && locallyEnabled.contains(event)) {
                for (int fsmId = 0; fsmId < fsmList.size(); fsmId++) {
                    // Find one automaton outside C that has this event, and disables it in the composition.
                    // After we add this automaton, the event is no longer locally enabled.
                    if (fsmList.get(fsmId).getAlphabet().contains(event)
                            && !cluster.contains(fsmId)
                            && !FSMComposition.isEnabled(fsmList.get(fsmId), state.get(fsmId), event)) {
                        cluster.add(fsmId);
                        clusterAlphabet.addAll(fsmList.get(fsmId).getAlphabet());
                        break;
                    }
                }

                // Update locally enabled information.
                locallyEnabled = getLocallyEnabled(fsmList, cluster, state);
            }

            // Continue with the remaining activities.
            Set<String> newQueue = new HashSet<>(locallyEnabled);
            newQueue.removeAll(processed);
            toProcess = newQueue;
        }
        return cluster;
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
     * Get all the events that are dependent on {@code event}.
     *
     * @param event input event
     * @return set of all events that are dependent on input event
     */
    private Set<String> getDependencies(String event) {
        return dependencies.getDependencies(event);
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
    private Optional<Set<String>> checkCluster(List<FSM<Location, Edge>> fsmList,
                                               List<Location> state, Set<String> enabled, Set<Integer> cluster) {
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
    private Set<String> getLocallyEnabled(List<FSM<Location, Edge>> fsmList,
                                          Set<Integer> cluster, List<Location> state) {
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
