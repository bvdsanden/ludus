package org.ludus.backend.games.meanpayoff.solvers.energy;

import org.ludus.backend.games.meanpayoff.MeanPayoffGame;

import java.util.Set;

/**
 * @param <V> vertex type
 * @param <E> edge type
 * @param <T> edge weight type
 * @author Bram van der Sanden
 */
public interface MeanPayoffGameEnergy<V, E, T> extends MeanPayoffGame<V, E, T> {

    /**
     * Construct a subgraph given the set of vertices
     *
     * @param vertexSubset subset of vertices
     * @return subgraph induces by the vertices in vertexSubset
     */
    MeanPayoffGameEnergy<V, E, T> getSubGraph(Set<V> vertexSubset);

    /**
     * Construct a subgraph given the set of vertices where the vertices of Player 0 and Player 1 are swapped
     *
     * @param vertexSubset subset of vertices
     * @return subgraph induces by the vertices in vertexSubset where vertices of Player 0 and Player 1 are swapped
     */
    MeanPayoffGameEnergy<V, E, T> getSwappedSubGraph(Set<V> vertexSubset);

}
