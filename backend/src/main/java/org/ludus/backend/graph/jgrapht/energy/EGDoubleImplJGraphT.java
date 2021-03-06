package org.ludus.backend.graph.jgrapht.energy;

import org.ludus.backend.datastructures.weights.SingleWeightFunctionDouble;
import org.ludus.backend.games.GameGraph;
import org.ludus.backend.games.energy.EnergyGame;

import java.util.Collection;
import java.util.Set;

/**
 * Energy game implementation using the JGraphT library.
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @author Bram van der Sanden
 */
public class EGDoubleImplJGraphT<V, E> implements EnergyGame<V, E, Double> {

    private final GameGraph<V, E> graph;
    private final SingleWeightFunctionDouble<E> edgeWeights;

    public EGDoubleImplJGraphT(GameGraph<V, E> graph,
                               SingleWeightFunctionDouble edgeWeights) {
        this.graph = graph;
        this.edgeWeights = edgeWeights;
    }

    @Override
    public Set<V> getV0() {
        return graph.getV0();
    }

    @Override
    public Set<V> getV1() {
        return graph.getV1();
    }

    @Override
    public Set<V> getVertices() {
        return graph.getVertices();
    }

    @Override
    public Set<E> getEdges() {
        return graph.getEdges();
    }

    @Override
    public Collection<E> incomingEdgesOf(V v) {
        return graph.incomingEdgesOf(v);
    }

    @Override
    public Collection<E> outgoingEdgesOf(V v) {
        return graph.outgoingEdgesOf(v);
    }

    @Override
    public V getEdgeSource(E e) {
        return graph.getEdgeSource(e);
    }

    @Override
    public V getEdgeTarget(E e) {
        return graph.getEdgeTarget(e);
    }

    @Override
    public E getEdge(V source, V target) {
        return graph.getEdge(source, target);
    }

    @Override
    public Double getWeight(E edge) {
        return edgeWeights.getWeight(edge);
    }

    @Override
    public Double getMaxAbsValue() {
        return edgeWeights.getMaxAbsValue();
    }

    @Override
    public Double getSumNegWeights() {
        return edgeWeights.getSumNegWeights();
    }

}
