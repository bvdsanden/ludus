package org.ludus.backend.graph;

/**
 * Double weighted graph interface. Each edge in this graph has two weights.
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @param <T> edge weights type
 * @author Bram van der Sanden
 */
public interface DoubleWeightedGraph<V, E, T> extends Graph<V, E> {

    T getWeight1(E edge);

    T getWeight2(E edge);

}
