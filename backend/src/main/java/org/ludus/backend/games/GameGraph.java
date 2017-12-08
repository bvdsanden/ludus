package org.ludus.backend.games;

import org.ludus.backend.graph.Graph;

import java.io.Serializable;
import java.util.Set;

/**
 * @param <V> vertex type
 * @param <E> edge type
 * @author Bram van der Sanden
 */
public interface GameGraph<V, E> extends Graph<V, E>, Serializable {

    Set<V> getV0();

    Set<V> getV1();
}
