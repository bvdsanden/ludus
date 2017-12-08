package org.ludus.backend.datastructures.weights;

/**
 * @param <T> value type
 * @param <E> edge type
 * @author Bram van der Sanden
 */
public interface SingleWeightFunction<T, E> {

    T getWeight(E edge);

    /**
     * Get the maximum absolute value of all weights in the graph.
     *
     * @return maximum absolute value of all weights
     */
    T getMaxAbsValue();

}
