package org.ludus.backend.graph.simpleDouble;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bram van der Sanden
 */
public class SDVertex implements Comparable<SDVertex> {

    private int id;
    private Map<SDVertex, SDEdge> outgoing;
    // Incoming vertices, indexed by the source. {@code this} is the target.
    private Map<SDVertex, SDEdge> incoming;

    public SDVertex() {
        this.id = this.hashCode();
        outgoing = new HashMap<>();
        incoming = new HashMap<>();
    }

    public Collection<SDEdge> getIncoming() {
        return incoming.values();
    }

    public SDEdge getIncoming(SDVertex source) {
        return incoming.get(source);
    }

    public Collection<SDEdge> getOutgoing() {
        return outgoing.values();
    }

    public SDEdge getOutgoing(SDVertex target) {
        return outgoing.get(target);
    }

    public void addIncoming(SDEdge e) {
        incoming.put(e.getSource(), e);
    }

    public void addOutgoing(SDEdge e) {
        outgoing.put(e.getTarget(), e);
    }

    public SDVertex(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public int compareTo(SDVertex other) {
        return Integer.compare(id, other.id);
    }

}