package org.ludus.backend.graph.simpleDouble;

import org.ludus.backend.graph.DoubleWeightedGraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A graph implementation using an edge set, vertex set.
 * Each vertex stores the incoming and outgoing edges.
 * Each edge stores the two weights.
 *
 * @author Bram van der Sanden
 */
public class SDGraph implements DoubleWeightedGraph<SDVertex, SDEdge, Double> {

    private final Set<SDVertex> vertexSet;
    private final Set<SDEdge> edgeSet;

    public SDGraph() {
        vertexSet = new HashSet();
        edgeSet = new HashSet();
    }

    @Override
    public Set<SDEdge> getEdges() {
        return edgeSet;
    }

    public void addVertex(SDVertex vertex) {
        vertexSet.add(vertex);
    }

    public SDEdge addEdge(SDVertex source, SDVertex target, Double w1, Double w2) {
        SDEdge e = new SDEdge(source, target, w1, w2);
        source.addOutgoing(e);
        target.addIncoming(e);
        edgeSet.add(e);
        return e;
    }

    @Override
    public Set<SDVertex> getVertices() {
        return vertexSet;
    }

    @Override
    public Collection<SDEdge> incomingEdgesOf(SDVertex v) {
        return v.getIncoming();
    }

    @Override
    public Collection<SDEdge> outgoingEdgesOf(SDVertex v) {
        return v.getOutgoing();
    }

    @Override
    public SDVertex getEdgeSource(SDEdge e) {
        return e.getSource();
    }

    @Override
    public SDVertex getEdgeTarget(SDEdge e) {
        return e.getTarget();
    }

    @Override
    public SDEdge getEdge(SDVertex source, SDVertex target) {
        return source.getOutgoing(target);
    }

    @Override
    public Double getWeight1(SDEdge edge) {
        return edge.getWeight1();
    }

    @Override
    public Double getWeight2(SDEdge edge) {
        return edge.getWeight2();
    }
}
