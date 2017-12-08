package org.ludus.backend.graph.weighted;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bram van der Sanden
 */
public class WVertex implements Comparable<WVertex> {

    private int id;
    private Map<WVertex, WIntEdge> outgoing;
    // Incoming vertices, indexed by the source. {@code this} is the target.
    private Map<WVertex, WIntEdge> incoming;

    public WVertex() {
        this.id = this.hashCode();
        outgoing = new HashMap<>();
        incoming = new HashMap<>();
    }

    public Collection<WIntEdge> getIncoming() {
        return incoming.values();
    }

    public WIntEdge getIncoming(WVertex source) {
        return incoming.get(source);
    }

    public Collection<WIntEdge> getOutgoing() {
        return outgoing.values();
    }

    public WIntEdge getOutgoing(WVertex target) {
        return outgoing.get(target);
    }

    public void addIncoming(WIntEdge e) {
        incoming.put(e.getSource(), e);
    }

    public void addOutgoing(WIntEdge e) {
        outgoing.put(e.getTarget(), e);
    }

    public WVertex(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public int compareTo(WVertex other) {
        return Integer.compare(id, other.id);
    }

}