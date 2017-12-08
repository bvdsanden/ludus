package org.ludus.backend.games;

/**
 * @param <V> vertex type
 * @param <E> edge type
 * @author Bram van der Sanden
 */
public interface GameSubgraph<V, E> extends GameGraph<V, E> {

    /**
     * Remove all redundant edges given the current strategy.
     *
     * @param strategyVector strategy vector
     * @return subgraph induced by the current strategy
     */
    GameGraph<V, E> getSubgraph(StrategyVector<V, E> strategyVector);
}
