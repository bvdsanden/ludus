package org.ludus.backend.algorithms;

import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.games.algorithms.DoubleFunctions;
import org.ludus.backend.graph.DoubleWeightedGraph;

import java.util.LinkedList;
import java.util.List;

/**
 * YTO minimum cycle ratio algorithm.
 *
 * TODO: Implementation
 *
 * @author Bram van der Sanden
 */
public final class YTO {

    /**
     * Run YTO minimum cycle ratio algorithm.
     *
     * @param <V>   vertex type
     * @param <E>   edge type
     * @param graph input graph
     */
    public static <V, E> Tuple<Double, List<E>> runYTO(DoubleWeightedGraph<V, E, Double> graph) {
        return runYTO(graph, DoubleFunctions.MACHINE_PRECISION);
    }

    /**
     * Run YTO minimum cycle ratio algorithm.
     *
     * @param <V>   vertex type
     * @param <E>   edge type
     * @param graph input graph
     * @param eps   absolute threshold for comparing cycle ratios
     */
    public static <V, E> Tuple<Double, List<E>> runYTO(DoubleWeightedGraph<V, E, Double> graph, Double eps) {
        return Tuple.of(Double.NEGATIVE_INFINITY, new LinkedList<>());
    }
}
