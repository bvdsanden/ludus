package org.ludus.backend.games.energy;

import org.ludus.backend.datastructures.weights.SingleWeightFunction;
import org.ludus.backend.games.GameGraph;


/**
 * Interface to access energy games.
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @param <T> edge weight type
 * @author Bram van der Sanden
 */
public interface EnergyGame<V, E, T> extends GameGraph<V, E>, SingleWeightFunction<T, E> {

    T getSumNegWeights();

}
