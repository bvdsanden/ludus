package org.ludus.backend.games;

/**
 * @param <V> vertex type
 * @author Bram van der Sanden
 */
public interface VertexId<V> {
    /**
     * Get the unique vertex id.
     *
     * @param vertex vertex whose unique id is to be returned.
     * @return the unique vertex id of the specified vertex.
     */
    Integer getId(V vertex);
}
