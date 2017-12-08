package org.ludus.backend.graph.jgrapht.ratio;

import org.ludus.backend.datastructures.weights.DoubleWeightFunctionDouble;
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
public class RGDoubleImplJGraphT implements RatioGamePolicyIteration<JGraphTVertex, JGraphTEdge, Double>, RatioGameEnergy<JGraphTVertex, JGraphTEdge, Double> {

    private final JGraphTGraph graph;
    private final DoubleWeightFunctionDouble edgeWeights;

    public RGDoubleImplJGraphT(JGraphTGraph graph, DoubleWeightFunctionDouble edgeWeights) {
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

    public DoubleWeightFunctionDouble getEdgeWeights() {
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
    public Double getWeight1(JGraphTEdge edge) {
        return edgeWeights.getWeight1(edge);
    }

    @Override
    public Double getWeight2(JGraphTEdge edge) {
        return edgeWeights.getWeight2(edge);
    }

    @Override
    public Double getMaxAbsValue() {
        return edgeWeights.getMaxAbsValue();
    }

    @Override
    public RGDoubleImplJGraphT getSubGraph(Set<JGraphTVertex> vertexSubset) {
        // Construct subgraph.
        JGraphTGraph subGraph = graph.getSubgraph(vertexSubset);
        return new RGDoubleImplJGraphT(subGraph, edgeWeights);
    }

    @Override
    public RGDoubleImplJGraphT getSwappedSubGraph(Set<JGraphTVertex> vertexSubset) {
        // Construct subgraph.
        JGraphTGraph subGraph = graph.getSwappedSubgraph(vertexSubset);
        return new RGDoubleImplJGraphT(subGraph, edgeWeights);
    }

    public RGDoubleImplJGraphT getSubGraphEdges(Set<JGraphTEdge> edgeSubset) {
        JGraphTGraph subGraph = graph.getSubgraphRestrictEdges(edgeSubset);
        return new RGDoubleImplJGraphT(subGraph, edgeWeights);
    }

    /**
     * Truncate the weights in the given ratio game to the precision given by epsilon.
     *
     * @param game game graph
     * @param epsilon precision parameter
     * @return new game where the weights are truncated to a precision given by epsilon
     */
    private RGDoubleImplJGraphT truncate(RGDoubleImplJGraphT game, Double epsilon) {
        DoubleWeightFunctionDouble<JGraphTEdge> w = new DoubleWeightFunctionDouble<>();
        for (JGraphTEdge e : game.getEdges()) {
            Double w1 = Math.floor(game.getWeight1(e) * (1.0 / epsilon)) / (1.0 / epsilon);
            Double w2 = Math.floor(game.getWeight2(e) * (1.0 / epsilon)) / (1.0 / epsilon);
            w.addWeight(e, w1, w2);
        }

        return new RGDoubleImplJGraphT(game.getGraph(), w);
    }

}
