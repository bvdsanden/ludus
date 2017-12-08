package org.ludus.backend.algorithms;

import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.graph.SingleWeightedGraph;

import java.util.*;
import java.util.function.Predicate;

/**
 * Dijkstra's shortest path algorithm.
 *
 * @author Bram van der Sanden
 */
public final class Dijkstra {

    private static <V, E, T> Predicate<V> predHasNoSuccessors(SingleWeightedGraph<V, E, T> graph) {
        return v -> graph.outgoingEdgesOf(v).isEmpty();
    }

    private static <V> Predicate<V> predInCollection(Collection<V> collection) {
        return collection::contains;
    }

    private static <V> Predicate<V> predEqualTo(V target) {
        return v -> v.equals(target);
    }


    /**
     * Dijkstra's shortest path algorithm.
     * Note: algorithm assumes that the graph has at least one vertex without outgoing edges.
     *
     * @param graph  input graph
     * @param source source vertex
     * @param <V>    vertex type
     * @param <E>    edge type
     * @return shortest path from {@code source} to some vertex without outgoing edges in input graph {@code graph}.
     */
    public static <V, E> Tuple<Double, List<E>> runDijkstra(SingleWeightedGraph<V, E, Double> graph, V source) {
        return runDijkstra(graph, source, predHasNoSuccessors(graph));
    }

    /**
     * Dijkstra's shortest path algorithm.
     *
     * @param graph     input graph
     * @param source    source vertex
     * @param targetSet set of target vertices
     * @param <V>       vertex type
     * @param <E>       edge type
     * @return shortest path from {@code source} to some target in {@code targetSet} in input graph {@code graph}.
     */
    public static <V, E> Tuple<Double, List<E>> runDijkstra(SingleWeightedGraph<V, E, Double> graph, V source, Collection<V> targetSet) {
        return runDijkstra(graph, source, predInCollection(targetSet));
    }

    /**
     * Dijkstra's shortest path algorithm.
     *
     * @param graph  input graph
     * @param source source vertex
     * @param target target vertex
     * @param <V>    vertex type
     * @param <E>    edge type
     * @return shortest path from {@code source} to {@code target} in input graph {@code graph}.
     */
    public static <V, E> Tuple<Double, List<E>> runDijkstra(SingleWeightedGraph<V, E, Double> graph, V source, V target) {
        return runDijkstra(graph, source, predEqualTo(target));
    }

    /**
     * Dijkstra's shortest path algorithm.
     *
     * @param graph                input graph
     * @param source               source vertex
     * @param terminationPredicate exploration stops as soon as the predicate is satisfied
     * @param <V>                  vertex type
     * @param <E>                  edge type
     * @return shortest path from {@code source} to {@code target} in input graph {@code graph}.
     */
    public static <V, E> Tuple<Double, List<E>> runDijkstra(SingleWeightedGraph<V, E, Double> graph, V source, Predicate<V> terminationPredicate) {

        Map<V, Double> dist = new HashMap<>(graph.getVertices().size());
        Map<V, V> prev = new HashMap<>(graph.getVertices().size());

        // Create a new candidate list and search tree.
        Comparator<CostVertex<V>> comparator = (CostVertex<V> v1, CostVertex<V> v2) ->
                (int) Math.signum((v1.cost) - (v2.cost));

        PriorityQueue<CostVertex<V>> Q = new PriorityQueue<>(graph.getEdges().size(), comparator);

        Map<V, CostVertex<V>> VtoCVMap = new HashMap<>();

        // Initialization.
        dist.put(source, 0.0);
        for (V v : graph.getVertices()) {
            if (!v.equals(source)) {
                dist.put(v, Double.POSITIVE_INFINITY);
                prev.put(v, null);
            }
            CostVertex cv = new CostVertex<>(v, dist.get(v));
            Q.add(cv);
            VtoCVMap.put(v, cv);
        }

        // Main loop.
        V target = null;
        while (!Q.isEmpty()) {
            CostVertex<V> u = Q.remove();
            VtoCVMap.remove(u.vertex);

            // We have found our target.
            if (terminationPredicate.test(u.vertex)) {
                target = u.vertex;
                break;
            }

            for (E e : graph.outgoingEdgesOf(u.vertex)) {
                V neighbor = graph.getEdgeTarget(e);
                if (VtoCVMap.containsKey(neighbor)) {
                    double alt = dist.get(u.vertex) + graph.getWeight(e);
                    if (alt < dist.get(neighbor)) {
                        dist.put(neighbor, alt);
                        prev.put(neighbor, u.vertex);

                        // Decrease with priority. Remove the element from the queue, update priority, and re-insert.
                        CostVertex cvNeighbor = VtoCVMap.get(neighbor);
                        Q.remove(cvNeighbor);

                        CostVertex<V> costNeighbor = VtoCVMap.get(neighbor);
                        costNeighbor.cost = alt;
                        Q.add(costNeighbor);
                    }
                }
            }
        }

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
     * A cost vertex contains the current cost value of a given vertex.
     *
     * @param <V> vertex type
     */
    private static class CostVertex<V> {
        public final V vertex;
        public double cost;

        /**
         * Create a new cost vertex.
         *
         * @param v    vertex
         * @param cost current cost of the vertex
         */
        public CostVertex(V v, Double cost) {
            vertex = v;
            this.cost = cost;
        }
    }
}
