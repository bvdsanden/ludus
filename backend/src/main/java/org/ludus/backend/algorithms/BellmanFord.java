package org.ludus.backend.algorithms;

import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.graph.SingleWeightedGraph;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Bellman-Ford shortest path algorithm.
 *
 * @author Bram van der Sanden
 */
public final class BellmanFord {

    private static <V, E, T> Predicate<V> predHasNoSuccessors(SingleWeightedGraph<V, E, T> graph) {
        return v -> graph.outgoingEdgesOf(v).isEmpty();
    }

    /**
     * Bellman-Ford shortest path algorithm. It is slower than Dijkstra's algorithm for the same problem,
     * but more versatile, as it is capable of handling graphs with negative edge weights.
     *
     * @param graph  input graph
     * @param source source vertex
     * @param <V>    vertex type
     * @param <E>    edge type
     * @return a tuple with the length of the shortest path and the shortest path
     */
    public static <V, E> Tuple<Double, List<E>> runBellmanFord(SingleWeightedGraph<V, E, Double> graph, V source) {
        // Run the Bellman Ford algorithm to compute the distance and previous value for each vertex.
        Optional<Tuple<Map<V, Double>, Map<V, V>>> result = computeDistPrev(graph, source);

        if (!result.isPresent()) {
            // Graph contains a negative-weight cycle.
            return Tuple.of(Double.NEGATIVE_INFINITY, new LinkedList<E>());
        }

        Map<V, Double> dist = result.get().getLeft();
        Map<V, V> prev = result.get().getRight();

        // Find all vertices with no outgoing edges.
        Set<V> endVertices = graph.getVertices().stream().filter(predHasNoSuccessors(graph)).collect(Collectors.toSet());

        // Find the vertex with the minimal distance.
        V target = endVertices.stream().min(Comparator.comparingDouble(dist::get)).get();

        // Construct the shortest path from source to target.
        List<E> path = new LinkedList<>();
        V u = target;
        while (prev.containsKey(u)) {
            path.add(0, graph.getEdge(prev.get(u), u));
            u = prev.get(u);
        }

        return Tuple.of(dist.get(target), path);
    }

    /**
     * Bellman-Ford shortest path algorithm. It is slower than Dijkstra's algorithm for the same problem,
     * but more versatile, as it is capable of handling graphs with negative edge weights.
     *
     * @param graph  input graph
     * @param source source vertex
     * @param target target vertex
     * @param <V>    vertex type
     * @param <E>    edge type
     * @return a tuple with the length of the shortest path and the shortest path
     */
    public static <V, E> Tuple<Double, List<E>> runBellmanFord(SingleWeightedGraph<V, E, Double> graph, V source, V target) {
        // Run the Bellman Ford algorithm to compute the distance and previous value for each vertex.
        Optional<Tuple<Map<V, Double>, Map<V, V>>> result = computeDistPrev(graph, source);

        if (!result.isPresent()) {
            // Graph contains a negative-weight cycle.
            return Tuple.of(Double.NEGATIVE_INFINITY, new LinkedList<E>());
        }

        Map<V, Double> dist = result.get().getLeft();
        Map<V, V> prev = result.get().getRight();

        // Construct the shortest path from source to target.
        List<E> path = new LinkedList<>();
        V u = target;
        while (prev.containsKey(u)) {
            path.add(0, graph.getEdge(prev.get(u), u));
            u = prev.get(u);
        }

        return Tuple.of(dist.get(target), path);
    }


    /**
     * Run the Bellman-Ford shortest path algorithm. Return the distance and previous value for each vertex.
     *
     * @param graph  input graph
     * @param source source vertex
     * @param <V>    vertex type
     * @param <E>    edge type
     * @return a tuple with the distance-value map and the previous-value map
     */
    private static <V, E> Optional<Tuple<Map<V, Double>, Map<V, V>>> computeDistPrev(SingleWeightedGraph<V, E, Double> graph, V source) {
        Map<V, Double> dist = new HashMap<>(graph.getVertices().size());
        Map<V, V> prev = new HashMap<>(graph.getVertices().size());

        // Initialization.
        for (V v : graph.getVertices()) {
            dist.put(v, Double.POSITIVE_INFINITY);
            prev.put(v, null);
        }
        dist.put(source, 0.0);
        prev.remove(source);

        // Main loop: relax edges repeatedly.
        for (int i = 1; i < graph.getVertices().size(); i++) {
            for (E edge : graph.getEdges()) {
                V u = graph.getEdgeSource(edge);
                V v = graph.getEdgeTarget(edge);
                Double w = graph.getWeight(edge);

                if (dist.get(u) + w < dist.get(v)) {
                    dist.put(v, dist.get(u) + w);
                    prev.put(v, u);
                }
            }
        }

        // Check for negative-weight cycles.
        for (E edge : graph.getEdges()) {
            V u = graph.getEdgeSource(edge);
            V v = graph.getEdgeTarget(edge);
            if (dist.get(u) + graph.getWeight(edge) < dist.get(v)) {
                // Graph contains a negative-weight cycle.
                return Optional.empty();
            }
        }

        return Optional.of(Tuple.of(dist, prev));
    }

}
