package org.ludus.backend.algorithms;

import org.ludus.backend.graph.Graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Topological sort using a DFS.
 *
 * @author Bram van der Sanden
 */
public final class TopologicalSort {

    /**
     * Return a topological sort on the given input graph. If the graph is not acyclic, an exception is thrown.
     *
     * @param graph input graph
     * @param <V>   vertex type
     * @param <E>   edge type
     * @return a topological sorted vertex list
     * @throws CycleFoundException thrown if a cycle is found
     */
    public static <V, E> List<V> topologicalSort(Graph<V, E> graph) throws CycleFoundException {
        List<V> L = new LinkedList<>();
        Set<V> unmarked = new HashSet<>(graph.getVertices());
        Set<V> tempMarked = new HashSet<>();
        while (!unmarked.isEmpty()) {
            V v = unmarked.iterator().next();
            visit(graph, L, unmarked, tempMarked, v);
        }
        return L;
    }

    /**
     * Recursively visit the nodes using a DFS.
     *
     * @param graph      input graph
     * @param L          list of topologically sorted vertices
     * @param unmarked   set of unmarked vertices
     * @param tempMarked set of temporarily marked vertices
     * @param v          current vertex
     * @param <V>        vertex type
     * @param <E>        edge type
     * @throws CycleFoundException thrown if a cycle is found
     */
    private static <V, E> void visit(Graph<V, E> graph, List<V> L, Set<V> unmarked, Set<V> tempMarked, V v) throws CycleFoundException {
        if (tempMarked.contains(v)) {
            throw new CycleFoundException();
        }

        if (unmarked.contains(v)) {
            tempMarked.add(v);
            for (E edge : graph.outgoingEdgesOf(v)) {
                visit(graph, L, unmarked, tempMarked, graph.getEdgeTarget(edge));
            }
            // Mark v permanently.
            unmarked.remove(v);
            // Unmark v temporarily.
            tempMarked.remove(v);
            // Add n to head of L.
            L.add(0, v);
        }
    }

}
