package org.ludus.backend.automaton;

import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.graph.DoubleWeightedGraph;
import org.ludus.backend.graph.SingleWeightedGraph;

import java.util.*;

/**
 * Max-plus automaton.
 *
 * @author Bram van der Sanden
 */
public class MaxPlusAutomaton<T> implements SingleWeightedGraph<MPAState<T>, MPATransition, Double>, DoubleWeightedGraph<MPAState<T>, MPATransition, Double> {

    private final Set<MPAState<T>> states;
    private final Set<MPATransition> MPATransitions;
    private final Map<MPAState<T>, Set<MPATransition>> outgoingMap;
    private final Map<MPAState<T>, Set<MPATransition>> incomingMap;
    private final Map<Tuple<T, Integer>, MPAState<T>> stateMap;

    public MaxPlusAutomaton() {
        states = new HashSet<>();
        MPATransitions = new HashSet<>();
        outgoingMap = new HashMap<>();
        incomingMap = new HashMap<>();
        stateMap = new HashMap<>();
    }

    public void addState(MPAState<T> MPAState) {
        states.add(MPAState);
        stateMap.put(Tuple.of(MPAState.getLocation(), MPAState.getIndex()), MPAState);
    }

    public void addTransition(MPATransition MPATransition) {
        MPAState src = MPATransition.getSource();
        MPAState tgt = MPATransition.getTarget();

        outgoingMap.putIfAbsent(src, new HashSet<>());
        Set<MPATransition> out = outgoingMap.get(src);
        out.add(MPATransition);

        incomingMap.putIfAbsent(tgt, new HashSet<>());
        Set<MPATransition> in = incomingMap.get(tgt);
        in.add(MPATransition);

        MPATransitions.add(MPATransition);
    }

    @Override
    public Double getWeight1(MPATransition edge) {
        return edge.getReward().getValue();
    }

    @Override
    public Double getWeight2(MPATransition edge) {
        return edge.getDuration().getValue();
    }

    @Override
    public Double getWeight(MPATransition edge) {
        return edge.getDuration().getValue();
    }

    public MPAState<T> getState(T fsmState, Integer index) {
        return stateMap.get(Tuple.of(fsmState, index));
    }

    @Override
    public Set<MPAState<T>> getVertices() {
        return states;
    }

    @Override
    public Set<MPATransition> getEdges() {
        return MPATransitions;
    }

    @Override
    public Collection<MPATransition> incomingEdgesOf(MPAState<T> MPAState) {
        return incomingMap.getOrDefault(MPAState, Collections.EMPTY_SET);
    }

    @Override
    public Collection<MPATransition> outgoingEdgesOf(MPAState<T> MPAState) {
        return outgoingMap.getOrDefault(MPAState, Collections.EMPTY_SET);
    }

    @Override
    public MPAState getEdgeSource(MPATransition MPATransition) {
        return MPATransition.getSource();
    }

    @Override
    public MPAState getEdgeTarget(MPATransition MPATransition) {
        return MPATransition.getTarget();
    }

    @Override
    public MPATransition getEdge(MPAState<T> source, MPAState<T> target) {
        for (MPATransition t : outgoingEdgesOf(source)) {
            if (t.getTarget().equals(target)) {
                return t;
            }
        }
        return null;
    }
}