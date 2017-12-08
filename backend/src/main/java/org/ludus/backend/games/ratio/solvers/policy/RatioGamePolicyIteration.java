package org.ludus.backend.games.ratio.solvers.policy;

import org.ludus.backend.games.VertexId;
import org.ludus.backend.games.ratio.RatioGame;

/**
 * Interface to access ratio games for policy iteration.
 * Here every vertex in the game graph must have a unique identifier.
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @param <T> edge weight type
 * @author Bram van der Sanden
 */
public interface RatioGamePolicyIteration<V, E, T> extends RatioGame<V, E, T>, VertexId<V> {

}