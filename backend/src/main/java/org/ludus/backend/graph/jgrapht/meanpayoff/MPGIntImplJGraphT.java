package org.ludus.backend.graph.jgrapht.meanpayoff;

import org.ludus.backend.datastructures.weights.SingleWeightFunctionInt;
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
public class MPGIntImplJGraphT implements MeanPayoffGamePolicyIteration<JGraphTVertex, JGraphTEdge, Integer>, MeanPayoffGameEnergy<JGraphTVertex, JGraphTEdge, Integer> {

    private final JGraphTGraph graph;
    private final SingleWeightFunctionInt edgeWeights;

    public MPGIntImplJGraphT(JGraphTGraph graph, SingleWeightFunctionInt edgeWeights) {
        this.graph = graph;
        this.edgeWeights = edgeWeights;
    }

    public SingleWeightFunctionInt getEdgeWeights() {
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
    public Integer getWeight(JGraphTEdge edge) {
        return edgeWeights.getWeight(edge);
    }

    @Override
    public Integer getMaxAbsValue() {
        return edgeWeights.getMaxAbsValue();
    }

    @Override
    public MPGIntImplJGraphT getSubGraph(Set<JGraphTVertex> vertexSubset) {
        // Construct subgraph.
        JGraphTGraph subGraph = graph.getSubgraph(vertexSubset);
        return new MPGIntImplJGraphT(subGraph, edgeWeights);
    }

    @Override
    public MPGIntImplJGraphT getSwappedSubGraph(Set<JGraphTVertex> vertexSubset) {
        // Construct subgraph.
        JGraphTGraph subGraph = graph.getSwappedSubgraph(vertexSubset);
        return new MPGIntImplJGraphT(subGraph, edgeWeights);
    }

    @Override
    public Integer getId(JGraphTVertex vertex) {
        return vertex.getId();
    }
}
