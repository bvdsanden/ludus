package org.ludus.backend.games.meanpayoff.solvers.policy;

import org.ludus.backend.games.ratio.solvers.policy.RatioGamePolicyIteration;

import java.util.Collection;
import java.util.Set;

/**
 * @author Bram van der Sanden
 */
public class RatioGameAdapter<V, E> implements RatioGamePolicyIteration<V, E, Double> {

    private MeanPayoffGamePolicyIteration<V, E, Double> game;

    public RatioGameAdapter(MeanPayoffGamePolicyIteration<V, E, Double> game) {
        this.game = game;
    }

    @Override
    public Set<V> getV0() {
        return game.getV0();
    }

    @Override
    public Set<V> getV1() {
        return game.getV1();
    }

    @Override
    public Set<V> getVertices() {
        return game.getVertices();
    }

    @Override
    public Set<E> getEdges() {
        return game.getEdges();
    }

    @Override
    public Collection<E> incomingEdgesOf(V v) {
        return game.incomingEdgesOf(v);
    }

    @Override
    public Collection<E> outgoingEdgesOf(V v) {
        return game.outgoingEdgesOf(v);
    }

    @Override
    public V getEdgeSource(E e) {
        return game.getEdgeSource(e);
    }

    @Override
    public V getEdgeTarget(E e) {
        return game.getEdgeTarget(e);
    }

    @Override
    public E getEdge(V source, V target) {
        return game.getEdge(source, target);
    }

    @Override
    public Double getWeight1(E edge) {
        return game.getWeight(edge);
    }

    @Override
    public Double getWeight2(E edge) {
        return 1.0;
    }

    @Override
    public Double getMaxAbsValue() {
        return game.getMaxAbsValue();
    }

    @Override
    public Integer getId(V vertex) {
        return game.getId(vertex);
    }

}
