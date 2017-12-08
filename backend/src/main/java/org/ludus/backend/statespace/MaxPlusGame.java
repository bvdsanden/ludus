package org.ludus.backend.statespace;

import org.ludus.backend.games.ratio.solvers.policy.RatioGamePolicyIteration;

import java.util.*;

/**
 * @author Bram van der Sanden
 */
public class MaxPlusGame implements RatioGamePolicyIteration<Configuration, Transition, Double> {

    private final Set<Configuration> V0;
    private final Set<Configuration> V1;
    private final Set<Configuration> vertices;
    private final Map<Configuration, Integer> vertexIds;
    private final Set<Transition> transitions;
    private final Map<Configuration, Set<Transition>> outgoingMap;
    private final Map<Configuration, Set<Transition>> incomingMap;

    private final Map<Transition, Double> weightMap1;
    private final Map<Transition, Double> weightMap2;
    private final Double maxWeight;

    /**
     * Generate a max-plus game graph given a max-plus state space.
     *
     * @param stateSpace
     * @param controllableEvents   set of controllable event names
     * @param uncontrollableEvents set of uncontrollable event names
     */
    public MaxPlusGame(MaxPlusStateSpace stateSpace,
                       Set<String> controllableEvents, Set<String> uncontrollableEvents) {
        V0 = new HashSet();
        V1 = new HashSet();
        vertexIds = new HashMap<>();

        // Vertices.
        vertices = stateSpace.getConfigurations();
        Configuration initial = stateSpace.getInitialConfiguration();

        // Edges.
        transitions = new HashSet();
        outgoingMap = new HashMap<>();
        incomingMap = new HashMap<>();
        weightMap1 = new HashMap<>();
        weightMap2 = new HashMap<>();

        for (Configuration c : vertices) {
            for (Transition t : stateSpace.outgoingEdgesOf(c)) {
                addIncoming(t.getTarget(), t);
                addOutgoing(c, t);
                weightMap1.put(t, t.getDuration().getValue());
                weightMap2.put(t, t.getReward().getValue());
            }
        }

        int id = 0;
        for (Configuration c : vertices) {
            if (allInSet(c, controllableEvents)) {
                V0.add(c);
            } else if (allInSet(c, uncontrollableEvents)) {
                V1.add(c);
            } else {
                System.out.println("Configuration found with two types of outgoing edges! " + c.toString());
            }
            vertexIds.put(c, id);
            id += 1;
        }

        Double max1 = weightMap1.values().stream().reduce(Double::max).get();
        Double max2 = weightMap2.values().stream().reduce(Double::max).get();

        maxWeight = Math.max(Math.abs(max1), Math.abs(max2));
    }

    private boolean allInSet(Configuration c, Set<String> eventSet) {
        return outgoingEdgesOf(c).stream().noneMatch((t) -> (!eventSet.contains(t.getEvent())));
    }

    private void addIncoming(Configuration c, Transition t) {
        if (incomingMap.containsKey(c)) {
            incomingMap.get(c).add(t);
        } else {
            Set<Transition> set = new HashSet();
            set.add(t);
            incomingMap.put(c, set);
        }
    }

    private void addOutgoing(Configuration c, Transition t) {
        if (outgoingMap.containsKey(c)) {
            outgoingMap.get(c).add(t);
        } else {
            Set<Transition> set = new HashSet();
            set.add(t);
            outgoingMap.put(c, set);
        }
    }

    @Override
    public Set<Configuration> getV0() {
        return V0;
    }

    @Override
    public Set<Configuration> getV1() {
        return V1;
    }

    @Override
    public Set<Configuration> getVertices() {
        return vertices;
    }

    @Override
    public Set<Transition> getEdges() {
        return transitions;
    }

    @Override
    public Collection<Transition> incomingEdgesOf(Configuration v) {
        return incomingMap.getOrDefault(v, Collections.EMPTY_SET);
    }

    @Override
    public Collection<Transition> outgoingEdgesOf(Configuration v) {
        return outgoingMap.getOrDefault(v, Collections.EMPTY_SET);
    }

    @Override
    public Configuration getEdgeSource(Transition e) {
        return e.getSource();
    }

    @Override
    public Configuration getEdgeTarget(Transition e) {
        return e.getTarget();
    }

    @Override
    public Transition getEdge(Configuration source, Configuration target) {
        for (Transition t : outgoingMap.get(source)) {
            if (t.getTarget().equals(target)) {
                return t;
            }
        }
        return null;
    }

    @Override
    public Double getWeight1(Transition edge) {
        return weightMap1.get(edge);
    }

    @Override
    public Double getWeight2(Transition edge) {
        return weightMap2.get(edge);
    }

    @Override
    public Double getMaxAbsValue() {
        return maxWeight;
    }

    @Override
    public Integer getId(Configuration vertex) {
        return vertexIds.get(vertex);
    }

}
