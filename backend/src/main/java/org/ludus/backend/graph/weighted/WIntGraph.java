package org.ludus.backend.graph.weighted;

import org.ludus.backend.games.GameGraph;
import org.ludus.backend.games.GameSubgraph;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.games.VertexId;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Bram van der Sanden
 */
public class WIntGraph implements GameGraph<WVertex, WIntEdge>, VertexId<WVertex>, GameSubgraph<WVertex, WIntEdge> {

    private final Set<WVertex> verticesP0;
    private final Set<WVertex> verticesP1;
    private final Set<WVertex> vertexSet;
    private final Set<WIntEdge> edgeSet;

    public WIntGraph() {
        verticesP0 = new HashSet<>();
        verticesP1 = new HashSet<>();
        vertexSet = new HashSet<>();
        edgeSet = new HashSet<>();
    }

    public void addToV0(WVertex... vertices) {
        for (WVertex v : vertices) {
            verticesP0.add(v);
            vertexSet.add(v);
        }
    }

    public void addToV1(WVertex... vertices) {
        for (WVertex v : vertices) {
            verticesP1.add(v);
            vertexSet.add(v);
        }
    }

    @Override
    public Set<WIntEdge> getEdges() {
        return edgeSet;
    }

    public WIntEdge addEdge(WVertex source, WVertex target, Integer w1, Integer w2) {
        WIntEdge e = new WIntEdge(source, target, w1, w2);
        source.addOutgoing(e);
        target.addIncoming(e);
        edgeSet.add(e);
        return e;
    }

    @Override
    public Set<WVertex> getV0() {
        return verticesP0;
    }

    @Override
    public Set<WVertex> getV1() {
        return verticesP1;
    }

    @Override
    public Set<WVertex> getVertices() {
        return vertexSet;
    }

    @Override
    public Collection<WIntEdge> incomingEdgesOf(WVertex v) {
        return v.getIncoming();
    }

    @Override
    public Collection<WIntEdge> outgoingEdgesOf(WVertex v) {
        return v.getOutgoing();
    }

    @Override
    public WVertex getEdgeSource(WIntEdge e) {
        return e.getSource();
    }

    @Override
    public WVertex getEdgeTarget(WIntEdge e) {
        return e.getTarget();
    }

    @Override
    public WIntEdge getEdge(WVertex source, WVertex target) {
        return source.getOutgoing(target);
    }

    @Override
    public Integer getId(WVertex vertex) {
        return vertex.getId();
    }

    public GameGraph<WVertex, WIntEdge> getSubgraph(StrategyVector<WVertex, WIntEdge> strategyVector) {
        WIntGraph subGraph = new WIntGraph();

        for (WVertex v : getVertices()) {
            WVertex succ = strategyVector.getSuccessor(v);
            WIntEdge edge = getEdge(v, succ);

            // Add source vertex.
            if (getV0().contains(v)) {
                subGraph.addToV0(v);
            } else {
                subGraph.addToV1(v);
            }
            // Add edge.
            subGraph.addEdge(v, succ, edge.getWeight1(), edge.getWeight2());

        }
        return subGraph;
    }


}
