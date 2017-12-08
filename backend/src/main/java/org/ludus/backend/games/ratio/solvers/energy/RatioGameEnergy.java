package org.ludus.backend.games.ratio.solvers.energy;

import org.ludus.backend.games.ratio.RatioGame;

import java.util.Set;

/**
 * Ratio game interface for energy games that also allows the construction of
 * subgraphs.
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @param <T> edge weight type
 * @author Bram van der Sanden
 */
public interface RatioGameEnergy<V, E, T> extends RatioGame<V, E, T> {

    RatioGameEnergy<V,E,T> getSubGraph(Set<V> vertexSubset);

    RatioGameEnergy<V,E,T> getSwappedSubGraph(Set<V> vertexSubset);

}
