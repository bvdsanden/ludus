package org.ludus.backend.algorithms;

import org.ludus.backend.graph.Graph;

import java.util.HashSet;
import java.util.Set;


/**
 * Check whether the given graph is acyclic.
 *
 * @author Bram van der Sanden
 */
public final class CycleCheck {

    /**
     * Check whether the given graph contains a cycle.
     *
     * @param graph input graph
     * @param <V>   vertex type
     * @param <E>   edge type
     * @return true iff the graph contains a cycle
     */
    public static <V, E> boolean check(Graph<V, E> graph) {
        Set<V> unmarked = new HashSet(graph.getVertices());
        Set<V> tempMarked = new HashSet();
        try {
            while (!unmarked.isEmpty()) {
                V v = unmarked.iterator().next();
                visit(graph, unmarked, tempMarked, v);
            }
            return false;
        } catch (CycleFoundException e) {
            return true;
        }
    }

    private static <V, E> void visit(Graph<V, E> graph, Set<V> unmarked, Set<V> tempMarked, V v)
            throws CycleFoundException {
        if (tempMarked.contains(v)) {
            throw new CycleFoundException();
        }

        if (unmarked.contains(v)) {
            tempMarked.add(v);
            for (E edge : graph.outgoingEdgesOf(v)) {
                visit(graph, unmarked, tempMarked, graph.getEdgeTarget(edge));
            }
            // Mark v permanently.
            unmarked.remove(v);
            // Unmark v temporarily.
            tempMarked.remove(v);
        }
    }
}
