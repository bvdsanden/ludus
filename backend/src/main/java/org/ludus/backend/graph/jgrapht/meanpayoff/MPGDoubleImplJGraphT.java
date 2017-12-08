package org.ludus.backend.graph.jgrapht.meanpayoff;

import org.ludus.backend.datastructures.weights.SingleWeightFunctionDouble;
import org.ludus.backend.games.meanpayoff.solvers.energy.MeanPayoffGameEnergy;
import org.ludus.backend.games.meanpayoff.solvers.policy.MeanPayoffGamePolicyIteration;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;

import java.util.Collection;
import java.util.Set;

/**
 * @author Bram van der Sanden
 */
public class MPGDoubleImplJGraphT implements MeanPayoffGamePolicyIteration<JGraphTVertex, JGraphTEdge, Double>, MeanPayoffGameEnergy<JGraphTVertex, JGraphTEdge, Double> {

    private final JGraphTGraph graph;
    private final SingleWeightFunctionDouble<JGraphTEdge> edgeWeights;

    public MPGDoubleImplJGraphT(JGraphTGraph graph, SingleWeightFunctionDouble<JGraphTEdge> edgeWeights) {
        this.graph = graph;
        this.edgeWeights = edgeWeights;
    }

    public SingleWeightFunctionDouble getEdgeWeights() {
        return edgeWeights;
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
    public Double getWeight(JGraphTEdge edge) {
        return edgeWeights.getWeight(edge);
    }

    @Override
    public Double getMaxAbsValue() {
        return edgeWeights.getMaxAbsValue();
    }

    @Override
    public MPGDoubleImplJGraphT getSubGraph(Set<JGraphTVertex> vertexSubset) {
        JGraphTGraph subGraph = graph.getSubgraph(vertexSubset);
        return new MPGDoubleImplJGraphT(subGraph, edgeWeights);
    }

    @Override
    public MPGDoubleImplJGraphT getSwappedSubGraph(Set<JGraphTVertex> vertexSubset) {
        JGraphTGraph subGraph = graph.getSwappedSubgraph(vertexSubset);
        return new MPGDoubleImplJGraphT(subGraph, edgeWeights);
    }

    @Override
    public Integer getId(JGraphTVertex vertex) {
        return vertex.getId();
    }
}
