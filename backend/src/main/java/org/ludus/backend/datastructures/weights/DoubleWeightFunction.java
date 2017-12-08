package org.ludus.backend.datastructures.weights;

/**
 * Double weight function that stores two weights for each edge.
 *
 * @param <T> value type
 * @param <E> edge type
 * @author Bram van der Sanden
 */
public interface DoubleWeightFunction<T, E> {

    T getWeight1(E edge);

    T getWeight2(E edge);

    /**
     * Returns the maximum absolute value of all weights in the graph.
     *
     * @return maximum absolute value of all weights in the graph, both weight1
     * and weight2.
     */
    T getMaxAbsValue();

}
