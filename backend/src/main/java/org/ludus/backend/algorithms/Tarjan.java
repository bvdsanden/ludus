package org.ludus.backend.algorithms;

import org.ludus.backend.graph.Graph;

import java.util.*;

/**
 * Tarjan's algorithm to find strongly connected components.
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @author Bram van der Sanden
 */
public class Tarjan<V, E> {

    private Stack<V> stack;
    private List<Set<V>> components;
    private Map<V, Integer> indexMap;
    private Map<V, Integer> lowlinkMap;
    private Integer index;

    /**
     * The algorithm takes a directed graph as input, and produces a partition of the graph's vertices
     * into the graph's strongly connected components. Each vertex of the graph appears in exactly one
     * of the strongly connected components. Any vertex that is not on a directed cycle forms a
     * strongly connected component all by itself
     * .
     *
     * @param graph input graph
     * @return partition of the graph's vertices into the graph's SCCs
     */
    public List<Set<V>> computeSCCs(Graph<V, E> graph) {
        stack = new Stack<>();
        // List of strongly connected components.
        components = new ArrayList<>();

        indexMap = new HashMap<>();
        lowlinkMap = new HashMap<>();
        index = 0;

        for (V v : graph.getVertices()) {
            if (!indexMap.containsKey(v)) {
                computeSCC(graph, v);
            }
        }
        return components;
    }

    /**
     * Compute the SCC for the given vertex.
     *
     * @param graph input graph
     * @param v input vertex
     */
    private void computeSCC(Graph<V, E> graph, V v) {
        // Set the depth index for v to the smallest unused index.
        indexMap.put(v, index);
        lowlinkMap.put(v, index);

        index += 1;
        stack.push(v);

        // Consider successors of v.
        for (E e : graph.outgoingEdgesOf(v)) {
            V w = graph.getEdgeTarget(e);
            if (!indexMap.containsKey(w)) {
                // Successor w has not yet been visited; recurse on it.
                computeSCC(graph, w);
                lowlinkMap.put(v, Math.min(lowlinkMap.get(v), lowlinkMap.get(w)));
            } else if (stack.contains(w)) {
                // Successor w is in stack S and hence in the current SCC.
                lowlinkMap.put(v, Math.min(lowlinkMap.get(v), indexMap.get(w)));
            }
        }

        // If v is a root node, pop the stack and generate an SCC.
        if (lowlinkMap.get(v).equals(indexMap.get(v))) {
            // Start a strongly connected component.
            Set<V> scc = new HashSet<>();
            V w;
            do {
                w = stack.pop();
                scc.add(w);
            } while (!w.equals(v));

            components.add(scc);
        }
    }
}
