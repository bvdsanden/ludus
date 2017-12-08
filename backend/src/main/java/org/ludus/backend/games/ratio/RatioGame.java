package org.ludus.backend.games.ratio;

import org.ludus.backend.datastructures.weights.DoubleWeightFunction;
import org.ludus.backend.games.GameGraph;

/**
 * Interface to access ratio games. A ratio game consists of a game graph
 * and a function that assigns two weights to each edge in the game.
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @param <T> edge weight type
 * @author Bram van der Sanden
 */
public interface RatioGame<V, E, T> extends GameGraph<V, E>, DoubleWeightFunction<T, E> {

}
