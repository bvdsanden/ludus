package org.ludus.backend.algorithms;

import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.games.algorithms.DoubleFunctions;
import org.ludus.backend.graph.DoubleWeightedGraph;
import org.ludus.backend.graph.Graph;

import java.util.*;

/**
 * Howard's minimum cycle ratio algorithm.
 *
 * @author Bram van der Sanden
 */
public final class Howard {

    /**
     * Run Howard's minimum cycle ratio algorithm.
     *
     * @param <V>   vertex type
     * @param <E>   edge type
     * @param graph input graph
     */
    public static <V, E> Tuple<Double, List<E>> runHoward(DoubleWeightedGraph<V, E, Double> graph) {
        return runHoward(graph, DoubleFunctions.MACHINE_PRECISION);
    }

    /**
     * Run Howard's minimum cycle ratio algorithm.
     *
     * @param <V>   vertex type
     * @param <E>   edge type
     * @param graph input graph that must b
     * @param eps   absolute threshold for comparing cycle ratios
     */
    public static <V, E> Tuple<Double, List<E>> runHoward(DoubleWeightedGraph<V, E, Double> graph, Double eps) {
        // Upper bound on the cycle ratio.      
        E maxE = graph.getEdges().stream().max(Comparator.comparingDouble(graph::getWeight1)).get();
        Double maxWeight1 = graph.getWeight1(maxE);
        Double r_max = graph.getEdges().size() * maxWeight1 + 1.0;

        Map<V, Double> d = new HashMap<>();

        // Successor function p, that induces a successor graph Gp.
        Map<V, V> p = new HashMap<>();

        // Initialize the distance of each node to infinity.
        for (V v : graph.getVertices()) {
            d.put(v, Double.POSITIVE_INFINITY);
        }

        // Set the distance of the nodes, and initialize the successor function.
        for (E e : graph.getEdges()) {
            V source = graph.getEdgeSource(e);
            V target = graph.getEdgeTarget(e);
            if (graph.getWeight1(e) < d.get(source)) {
                d.put(source, graph.getWeight1(e));
                p.put(source, target);
            }
        }

        // Result of findRatio(). This contains the ratio value of the critical 
        // cycle and a handle. The cycle can be inferred using the p(v) function.
        Tuple<Double, Optional<V>> result;

        // Variables to store currently best known cycle.
        Double r = r_max;
        V r_handle = null;
        List<E> r_cycle = null;

        boolean changed = true;
        while (changed) {
            result = findRatio(graph, r, p);

            // Check if there exists a path from v to handle in Gp.
            if (result.getLeft() < r) {
                // Update the ratio value.
                r = result.getLeft();

                // Update the handle and cycle.
                r_handle = result.getRight().get();
                r_cycle = getCycle(graph, p, r_handle);

                // Perform a reverse BFS to update the node distances to 
                // node handle in graph Gp.
                Set<V> visited = new HashSet<>();
                Queue<V> frontier = new LinkedList<>();
                frontier.add(r_handle);
                visited.add(r_handle);

                while (!frontier.isEmpty()) {
                    V v = frontier.remove();
                    for (E e : graph.incomingEdgesOf(v)) {
                        V incoming = graph.getEdgeSource(e);
                        // Edge (incoming,v).                        
                        if (!visited.contains(incoming) && p.get(incoming).equals(v)) {
                            frontier.add(incoming);
                            // Distance of v has been set.
                            d.put(incoming, d.get(v) + graph.getWeight1(e) - r * graph.getWeight2(e));
                        }
                    }
                }
            }

            // Improve the vertex distances.
            changed = false;
            for (E edge : graph.getEdges()) {
                V u = graph.getEdgeSource(edge);
                V v = graph.getEdgeTarget(edge);
                Double dist = d.get(v) + graph.getWeight1(edge) - r * graph.getWeight2(edge);
                if (d.get(u) > dist + eps) {
                    d.put(u, dist);
                    p.put(u, v);
                    changed = true;
                }
            }
        }

        // If there is no cycle, we return a ratio value of -Infinity.
        if (r_handle == null) {
            return Tuple.of(Double.NEGATIVE_INFINITY, null);
        }

        // Return both the ratio value and the cycle.
        return Tuple.of(r, r_cycle);
    }

    private static <V, E> List<E> getCycle(Graph<V, E> graph, Map<V, V> p, V r_handle) {
        List<E> cycle = new LinkedList<>();
        V v = r_handle;
        V vInit = r_handle;

        while (true) {
            V vNext = p.get(v);
            E edge = graph.getEdge(v, vNext);
            cycle.add(edge);
            if (vNext.equals(vInit)) {
                break;
            } else {
                v = vNext;
            }
        }
        return cycle;
    }

    /**
     * Find the minimum cycle ratio in the successor graph induced by function p.
     * From each vertex, find the cycle that is reached. If the ratio is lower
     * than the currently known lowest ratio, we update the ratio and handle variables.
     * If a cycle is found with a ratio smaller than {@code r}, return a tuple
     * of the ratio value and a handle to the cycle.
     *
     * @param <V>   vertex type
     * @param <E>   edge type
     * @param graph input graph
     * @param r     currently known lowest cycle ratio
     * @param p     successor function that induces graph Gp
     * @return a tuple of the minimum cycle ratio and a handle to this cycle if
     * the cycle ratio is smaller than {@code r}, otherwise return the same
     * ratio and an empty handle.
     */
    private static <V, E> Tuple<Double, Optional<V>> findRatio(DoubleWeightedGraph<V, E, Double> graph, Double r, Map<V, V> p) {
        Map<V, V> visited = new HashMap<>();

        // Nodes are not visited initially.
        for (V v : graph.getVertices()) {
            visited.put(v, null);
        }

        // Currently known lowest cycle ratio.
        Double r_prime = r;
        Optional<V> handle = Optional.empty();

        for (V v : graph.getVertices()) {
            if (visited.get(v) != null) {
                // Vertex v is visited before.
                continue;
            }

            // Search for a new cycle. Mark nodes with v.
            V u = v;
            do {
                visited.put(u, v);
                u = p.get(u);
            } while (visited.get(u) == null);

            if (!visited.get(u).equals(v)) {
                // u is on an old cycle.
                continue;
            }

            // Vertex u is on a new cycle. Compute the cycle ratio.
            V x = u;
            double sum = 0.0;
            double length = 0.0;

            do {
                E edge = graph.getEdge(x, p.get(x));
                assert (edge != null);
                sum += graph.getWeight1(edge);
                length += graph.getWeight2(edge);
                x = p.get(x);
            } while (!x.equals(u));

            double cycleRatio = sum / length;

            // If we have found a cycle with a smaller ratio,
            // return u which is on the cycle as a handle.
            if (r_prime > cycleRatio) {
                r_prime = cycleRatio;
                handle = Optional.of(u);
            }
        }
        return Tuple.of(r_prime, handle);
    }

}
