package org.ludus.backend.fsm;

import org.ludus.backend.graph.Graph;

import java.util.Collection;
import java.util.Set;

/**
 * Finite-state machine with controllable and uncontrollable events.
 *
 * @author Bram van der Sanden
 */
public interface FSM<V, E> extends Graph<V, E> {

    Set<String> getControllable();

    Set<String> getUncontrollable();

    V getInitial();

    String getEvent(E e);

    Set<String> getAlphabet();

    boolean hasEdge(V source, V target, String event);

    /**
     * Return all edges from source to target.
     *
     * @param source source vertex
     * @param target target vertex
     * @return all edges from source to target
     */
    Collection<E> getEdges(V source, V target);

    E getEdge(V source, V target, String event);
}
