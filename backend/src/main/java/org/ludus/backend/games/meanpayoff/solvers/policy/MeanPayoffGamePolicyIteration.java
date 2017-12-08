package org.ludus.backend.games.meanpayoff.solvers.policy;

import org.ludus.backend.games.VertexId;
import org.ludus.backend.games.meanpayoff.MeanPayoffGame;

/**
 * Interface to access mean-payoff games for policy iteration.
 * Here every vertex in the game graph must have a unique identifier.
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @param <T> edge weight type
 * @author Bram van der Sanden
 */
public interface MeanPayoffGamePolicyIteration<V, E, T> extends MeanPayoffGame<V, E, T>, VertexId<V> {

}