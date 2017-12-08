package org.ludus.backend.graph.simpleSingle;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bram van der Sanden
 */
public class SSVertex implements Comparable<SSVertex> {

    private int id;
    private Map<SSVertex, SSEdge> outgoing;
    // Incoming vertices, indexed by the source. {@code this} is the target.
    private Map<SSVertex, SSEdge> incoming;

    public SSVertex() {
        this.id = this.hashCode();
        outgoing = new HashMap<>();
        incoming = new HashMap<>();
    }

    public Collection<SSEdge> getIncoming() {
        return incoming.values();
    }

    public SSEdge getIncoming(SSVertex source) {
        return incoming.get(source);
    }

    public Collection<SSEdge> getOutgoing() {
        return outgoing.values();
    }

    public SSEdge getOutgoing(SSVertex target) {
        return outgoing.get(target);
    }

    public void addIncoming(SSEdge e) {
        incoming.put(e.getSource(), e);
    }

    public void addOutgoing(SSEdge e) {
        outgoing.put(e.getTarget(), e);
    }

    public SSVertex(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public int compareTo(SSVertex other) {
        return Integer.compare(id, other.id);
    }

}