package org.ludus.backend.games.meanpayoff;

import org.ludus.backend.datastructures.weights.SingleWeightFunction;
import org.ludus.backend.games.GameGraph;

/**
 * Mean-payoff game is a game graph with a single weight function.
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @param <T> edge weight type
 * @author Bram van der Sanden
 */
public interface MeanPayoffGame<V, E, T> extends GameGraph<V, E>, SingleWeightFunction<T, E> {

}
