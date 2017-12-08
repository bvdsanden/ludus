package org.ludus.backend.games.energy.solvers;

import java.util.HashMap;
import java.util.Map;

/**
 * Small Energy Progress Measure.
 *
 * @param <V> vertex type of the game graph
 * @param <T> value type
 * @author Bram van der Sanden
 */
public class SEPM<V, T> {

    private final Map<V, T> valueMap;

    public SEPM() {
        valueMap = new HashMap<>();
    }

    /**
     * Set the progress measure value for the given vertex.
     *
     * @param vertex vertex of which the value is to be set
     * @param value  new progress measure value of the vertex
     */
    public void setValue(V vertex, T value) {
        valueMap.put(vertex, value);
    }

    /**
     * Get the progress measure value of the given vertex.
     *
     * @param vertex vertex of which the value is returned
     * @return progress measure value for the given vertex
     */
    public T getValue(V vertex) {
        return valueMap.get(vertex);
    }

}
