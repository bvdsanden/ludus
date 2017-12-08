package org.ludus.backend.graph.jgrapht.ratio;

import org.ludus.backend.datastructures.weights.DoubleWeightFunctionInt;
import org.ludus.backend.games.ratio.solvers.energy.RatioGameEnergy;
import org.ludus.backend.games.ratio.solvers.policy.RatioGamePolicyIteration;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;

import java.util.Collection;
import java.util.Set;

/**
 * Ratio Game implementation using the game graph interface and the double
 * weight function.
 *
 * @author Bram van der Sanden
 */
public class RGIntImplJGraphT implements RatioGamePolicyIteration<JGraphTVertex, JGraphTEdge, Integer>, RatioGameEnergy<JGraphTVertex, JGraphTEdge, Integer> {

    private final JGraphTGraph graph;
    private final DoubleWeightFunctionInt<JGraphTEdge> edgeWeights;

    public RGIntImplJGraphT(JGraphTGraph graph, DoubleWeightFunctionInt<JGraphTEdge> edgeWeights) {
        this.graph = graph;
        this.edgeWeights = edgeWeights;

        // Check whether all weight values are non-negative. 
        if (edgeWeights.getMin1Value() < 0) {
            throw new IllegalStateException("Edge weights for nominator must be positive");
        }
        if (edgeWeights.getMin2Value() < 0) {
            throw new IllegalStateException("Edge weights for denominator must be positive");
        }
    }

    public JGraphTGraph getGraph() {
        return graph;
    }

    public DoubleWeightFunctionInt<JGraphTEdge> getEdgeWeights() {
        return edgeWeights;
    }

    @Override
    public Integer getId(JGraphTVertex vertex) {
        return vertex.getId();
    }

    @Override
    public Set<JGraphTVertex> getV0() {
        return graph.getV0();
    }

    @Override
    public Set<JGraphTVertex> getV1() {
        return graph.getV1();
    }

    @Override
    public Set<JGraphTVertex> getVertices() {
        return graph.getVertices();
    }

    @Override
    public Set<JGraphTEdge> getEdges() {
        return graph.getEdges();
    }

    @Override
    public Collection<JGraphTEdge> incomingEdgesOf(JGraphTVertex v) {
        return graph.incomingEdgesOf(v);
    }

    @Override
    public Collection<JGraphTEdge> outgoingEdgesOf(JGraphTVertex v) {
        return graph.outgoingEdgesOf(v);
    }

    @Override
    public JGraphTVertex getEdgeSource(JGraphTEdge e) {
        return graph.getEdgeSource(e);
    }

    @Override
    public JGraphTVertex getEdgeTarget(JGraphTEdge e) {
        return graph.getEdgeTarget(e);
    }

    @Override
    public JGraphTEdge getEdge(JGraphTVertex source, JGraphTVertex target) {
        return graph.getEdge(source, target);
    }

    @Override
    public Integer getWeight1(JGraphTEdge edge) {
        return edgeWeights.getWeight1(edge);
    }

    @Override
    public Integer getWeight2(JGraphTEdge edge) {
        return edgeWeights.getWeight2(edge);
    }

    @Override
    public Integer getMaxAbsValue() {
        return edgeWeights.getMaxAbsValue();
    }

    @Override
    public RGIntImplJGraphT getSubGraph(Set<JGraphTVertex> vertexSubset) {
        // Construct subgraph.
        JGraphTGraph subGraph = graph.getSubgraph(vertexSubset);
        return new RGIntImplJGraphT(subGraph, edgeWeights);
    }

    @Override
    public RGIntImplJGraphT getSwappedSubGraph(Set<JGraphTVertex> vertexSubset) {
        // Construct subgraph.
        JGraphTGraph subGraph = graph.getSwappedSubgraph(vertexSubset);
        return new RGIntImplJGraphT(subGraph, edgeWeights);
    }

    public RGIntImplJGraphT getSubGraphEdges(Set<JGraphTEdge> edgeSubset) {
        JGraphTGraph subGraph = graph.getSubgraphRestrictEdges(edgeSubset);
        return new RGIntImplJGraphT(subGraph, edgeWeights);
    }

}
