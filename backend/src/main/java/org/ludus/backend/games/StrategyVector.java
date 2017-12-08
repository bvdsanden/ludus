package org.ludus.backend.games;

import org.ludus.backend.datastructures.tuple.Tuple;

import java.util.*;

/**
 * Vector that can store the strategy for both players in the graph. For any
 * vertex in the graph this map contains the unique successor given the current
 * strategy. Note that some vertices might be unreachable given a strategy.
 *
 * @param <V> vertex type
 * @param <E> edge type
 * @author Bram van der Sanden
 */
public class StrategyVector<V, E> {

    private Map<V, V> strategyVector;

    public StrategyVector() {
        strategyVector = new HashMap<>();
    }

    public StrategyVector(StrategyVector<V, E> vector) {
        this.strategyVector = new HashMap<>(vector.strategyVector);
    }

    /**
     * Given the graph, initialize a random strategy. For each vertex in the
     * graph we choose one of the successors as the successor given the
     * strategy.
     *
     * @param graph game graph on which a random strategy is initialized
     */
    public void initializeRandomStrategy(GameGraph<V, E> graph) {
        graph.getVertices().stream().forEach((v) -> {
            E e = graph.outgoingEdgesOf(v).iterator().next();
            strategyVector.put(v, graph.getEdgeTarget(e));
        });
    }

    /**
     * Update the strategy by changing the unique successor of the given vertex.
     *
     * @param v         vertex of which a new successor is set
     * @param successor successor of the given vertex in the strategy
     */
    public void setSuccessor(V v, V successor) {
        strategyVector.put(v, successor);
    }

    /**
     * Return the unique successor given the current strategy.
     *
     * @param v vertex of which the unique successor is returned.
     * @return the successor of {@code v} given the current strategy.
     */
    public V getSuccessor(V v) {
        return strategyVector.get(v);
    }

    /**
     * Return all the vertices for which a successor is set in the strategy.
     *
     * @return all the vertices for which a successor is set in the strategy.
     */
    public Set<V> getVertices() {
        return strategyVector.keySet();
    }

    /**
     * Return the current strategy.
     *
     * @return current strategy with (vertex, successor(vertex)) pairs.
     */
    public Map<V, V> getMap() {
        return strategyVector;
    }

    /**
     * Given the strategy vector, extract the path starting from the given initial vertex.
     *
     * @param vector        strategy vector
     * @param initialVertex initial vertex of the path
     * @param <V>           vertex type
     * @param <E>           edge type
     * @return tuple of the path up to the recurring state and the recurrent state itself
     */
    public static <V, E> Tuple<List<V>, V> getPath(StrategyVector<V, E> vector, V initialVertex) {
        List<V> path = new ArrayList<>();
        V current = initialVertex;
        while (!path.contains(vector.getSuccessor(current))) {
            // Keep exploring.
            V succ = vector.getSuccessor(current);
            path.add(succ);
            current = succ;
        }

        return Tuple.of(path, vector.getSuccessor(current));
    }

    /**
     * Given the strategy vector, extract the reachable cycle starting from the given initial vertex.
     *
     * @param vector        strategy vector
     * @param initialVertex initial vertex of the path
     * @param <V>           vertex type
     * @return the reachable cycle
     */
    public static <V, E> List<V> getCycle(StrategyVector<V, E> vector, V initialVertex) {
        List<V> path = new ArrayList<>();
        V current = initialVertex;
        while (!path.contains(vector.getSuccessor(current))) {
            // Keep exploring.
            V succ = vector.getSuccessor(current);
            path.add(succ);
            current = succ;
        }
        // Successor is already in path, we found the recurrent vertex.
        V handle = vector.getSuccessor(current);
        List<V> cycleList = path.subList(path.indexOf(handle), path.size() - 1);

        return cycleList;
    }
}
