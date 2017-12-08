package org.ludus.backend.graph.jgrapht;

import java.io.Serializable;

/**
 * @author Bram van der Sanden
 */
public class JGraphTVertex implements Comparable<JGraphTVertex>, Serializable {

    Integer id;

    public JGraphTVertex() {
        this.id = this.hashCode();
    }

    /**
     * @param id vertex id
     */
    public JGraphTVertex(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public int compareTo(JGraphTVertex other) {
        return Integer.compare(id, other.id);
    }

}