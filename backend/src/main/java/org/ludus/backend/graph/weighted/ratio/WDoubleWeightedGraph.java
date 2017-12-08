package org.ludus.backend.graph.weighted.ratio;

import org.ludus.backend.games.ratio.solvers.policy.RatioGamePolicyIteration;
import org.ludus.backend.graph.weighted.WIntEdge;
import org.ludus.backend.graph.weighted.WIntGraph;
import org.ludus.backend.graph.weighted.WVertex;

import java.util.Collection;
import java.util.Set;

/**
 * Ratio game data structure implementation suited for policy iteration games.
 *
 * @author Bram van der Sanden
 */
public class WDoubleWeightedGraph implements RatioGamePolicyIteration<WVertex, WIntEdge, Integer> {

    private final WIntGraph graph;

    private final Integer maxAbsValue;

    public WDoubleWeightedGraph(WIntGraph graph) {
        this.graph = graph;

        for (WIntEdge e : graph.getEdges()) {
            if (e.getWeight1() < 0) {
                throw new IllegalStateException("Edge weights for nominator must be positive");
            }
            if (e.getWeight2() < 0) {
                throw new IllegalStateException("Edge weights for denominator must be positive");
            }
        }

        Integer val = 0;
        for (WIntEdge edge : graph.getEdges()) {
            val = Math.max(val, Math.abs(edge.getWeight1()));
            val = Math.max(val, Math.abs(edge.getWeight2()));
        }
        maxAbsValue = val;
    }

    @Override
    public Integer getId(WVertex vertex) {
        return graph.getId(vertex);
    }

    @Override
    public Set<WVertex> getV0() {
        return graph.getV0();
    }

    @Override
    public Set<WVertex> getV1() {
        return graph.getV1();
    }

    @Override
    public Set<WVertex> getVertices() {
        return graph.getVertices();
    }

    @Override
    public Set<WIntEdge> getEdges() {
        return graph.getEdges();
    }

    @Override
    public Collection<WIntEdge> incomingEdgesOf(WVertex v) {
        return graph.incomingEdgesOf(v);
    }

    @Override
    public Collection<WIntEdge> outgoingEdgesOf(WVertex v) {
        return graph.outgoingEdgesOf(v);
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
    public Integer getWeight1(WIntEdge edge) {
        return edge.getWeight1();
    }

    @Override
    public Integer getWeight2(WIntEdge edge) {
        return edge.getWeight2();
    }

    @Override
    public Integer getMaxAbsValue() {
        return maxAbsValue;
    }

}
