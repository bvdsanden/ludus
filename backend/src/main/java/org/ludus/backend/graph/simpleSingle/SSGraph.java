package org.ludus.backend.graph.simpleSingle;

import org.ludus.backend.graph.SingleWeightedGraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A graph implementation using an edge set, vertex set.
 * Each vertex stores the incoming and outgoing edges.
 * Each edge stores its weight.
 *
 * @author Bram van der Sanden
 */
public class SSGraph implements SingleWeightedGraph<SSVertex, SSEdge, Double> {

    private final Set<SSVertex> vertexSet;
    private final Set<SSEdge> edgeSet;

    public SSGraph() {
        vertexSet = new HashSet();
        edgeSet = new HashSet();
    }

    @Override
    public Set<SSEdge> getEdges() {
        return edgeSet;
    }

    public void addVertex(SSVertex vertex) {
        vertexSet.add(vertex);
    }

    public SSEdge addEdge(SSVertex source, SSVertex target, Double weight) {
        SSEdge e = new SSEdge(source, target, weight);
        source.addOutgoing(e);
        target.addIncoming(e);
        edgeSet.add(e);
        return e;
    }

    @Override
    public Set<SSVertex> getVertices() {
        return vertexSet;
    }

    @Override
    public Collection<SSEdge> incomingEdgesOf(SSVertex v) {
        return v.getIncoming();
    }

    @Override
    public Collection<SSEdge> outgoingEdgesOf(SSVertex v) {
        return v.getOutgoing();
    }

    @Override
    public SSVertex getEdgeSource(SSEdge e) {
        return e.getSource();
    }

    @Override
    public SSVertex getEdgeTarget(SSEdge e) {
        return e.getTarget();
    }

    @Override
    public SSEdge getEdge(SSVertex source, SSVertex target) {
        return source.getOutgoing(target);
    }

    @Override
    public Double getWeight(SSEdge edge) {
        return edge.getWeight();
    }

}
