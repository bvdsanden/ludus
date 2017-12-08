package org.ludus.backend.graph;

/**
 * Single weighted graph interface. Each edge in this graph has one weight.
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @param <T> edge weights type
 * @author Bram van der Sanden
 */
public interface SingleWeightedGraph<V, E, T> extends Graph<V, E> {

    T getWeight(E edge);

}
