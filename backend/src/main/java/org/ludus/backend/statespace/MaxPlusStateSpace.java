package org.ludus.backend.statespace;

import org.ludus.backend.graph.DoubleWeightedGraph;
import org.ludus.backend.graph.SingleWeightedGraph;

import java.util.*;


/**
 * @author Bram van der Sanden
 */
public class MaxPlusStateSpace implements SingleWeightedGraph<Configuration, Transition, Double>, DoubleWeightedGraph<Configuration, Transition, Double> {

    private final Set<Configuration> configurations;
    private final Set<Transition> transitions;
    private Configuration initial;
    private final Map<Configuration, Set<Transition>> outgoingMap;
    private final Map<Configuration, Set<Transition>> incomingMap;


    public MaxPlusStateSpace() {
        configurations = new HashSet<>();
        transitions = new HashSet<>();
        outgoingMap = new HashMap<>();
        incomingMap = new HashMap<>();
    }

    public Set<Configuration> getConfigurations() {
        return configurations;
    }

    public Configuration getInitialConfiguration() {
        return initial;
    }

    public void setInitialConfiguration(Configuration initialConfiguration) {
        initial = initialConfiguration;
    }

    public boolean hasInitialConfiguration() {
        return initial != null;
    }

    public void addConfiguration(Configuration configuration) {
        configurations.add(configuration);
    }

    public void addTransition(Transition transition) {
        Configuration src = transition.getSource();
        Configuration tgt = transition.getTarget();

        outgoingMap.putIfAbsent(src, new HashSet<>());
        Set<Transition> out = outgoingMap.get(src);
        out.add(transition);

        incomingMap.putIfAbsent(tgt, new HashSet<>());
        Set<Transition> in = incomingMap.get(tgt);
        in.add(transition);

        transitions.add(transition);
    }

    @Override
    public Double getWeight1(Transition edge) {
        return edge.getReward().getValue();
    }

    @Override
    public Double getWeight2(Transition edge) {
        return edge.getDuration().getValue();
    }

    @Override
    public Double getWeight(Transition edge) {
        return edge.getDuration().getValue();
    }

    @Override
    public Set<Configuration> getVertices() {
        return getConfigurations();
    }

    @Override
    public Set<Transition> getEdges() {
        return transitions;
    }

    @Override
    public Collection<Transition> incomingEdgesOf(Configuration configuration) {
        return incomingMap.getOrDefault(configuration, Collections.EMPTY_SET);
    }

    @Override
    public Collection<Transition> outgoingEdgesOf(Configuration configuration) {
        return outgoingMap.getOrDefault(configuration, Collections.EMPTY_SET);
    }

    @Override
    public Configuration getEdgeSource(Transition transition) {
        return transition.getSource();
    }

    @Override
    public Configuration getEdgeTarget(Transition transition) {
        return transition.getTarget();
    }

    @Override
    public Transition getEdge(Configuration source, Configuration target) {
        for (Transition t : outgoingEdgesOf(source)) {
            if (t.getTarget().equals(target)) {
                return t;
            }
        }
        return null;
    }
}
