package org.ludus.backend.graph;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * Graph interface.
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @author Bram van der Sanden
 */
public interface Graph<V, E> extends Serializable {

    Set<V> getVertices();

    Set<E> getEdges();

    Collection<E> incomingEdgesOf(V v);

    Collection<E> outgoingEdgesOf(V v);

    V getEdgeSource(E e);

    V getEdgeTarget(E e);

    /**
     * Return an edge that connects source and target.
     *
     * @param source source vertex
     * @param target target vertex
     * @return edge that connects source and target
     */
    E getEdge(V source, V target);

}
