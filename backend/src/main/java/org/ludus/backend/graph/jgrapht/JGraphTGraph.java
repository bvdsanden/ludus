package org.ludus.backend.graph.jgrapht;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.ludus.backend.games.GameGraph;
import org.ludus.backend.games.GameSubgraph;
import org.ludus.backend.games.StrategyVector;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Bram van der Sanden
 */
public class JGraphTGraph implements GameGraph<JGraphTVertex, JGraphTEdge>, GameSubgraph<JGraphTVertex, JGraphTEdge>, Serializable {

    private static final long serialVersionUID = -1306760703066967345L;

    private final Graph<JGraphTVertex, JGraphTEdge> graph;

    private final Set<JGraphTVertex> verticesP0;

    private final Set<JGraphTVertex> verticesP1;

    public JGraphTGraph() {
        graph = new DefaultDirectedGraph<>(JGraphTEdge.class);
        verticesP0 = new HashSet<>();
        verticesP1 = new HashSet<>();
    }

    public JGraphTGraph(JGraphTGraph gameGraph) {
        graph = gameGraph.graph;
        verticesP0 = gameGraph.getV0();
        verticesP1 = gameGraph.getV1();
    }

    public JGraphTGraph(AsSubgraph<JGraphTVertex, JGraphTEdge> directedGraph, Set<JGraphTVertex> V0, Set<JGraphTVertex> V1) {
        graph = directedGraph;
        verticesP0 = V0;
        verticesP1 = V1;
    }

    public void addToV0(JGraphTVertex... vertices) {
        for (JGraphTVertex v : vertices) {
            graph.addVertex(v);
            verticesP0.add(v);
        }
    }

    public void addToV1(JGraphTVertex... vertices) {
        for (JGraphTVertex v : vertices) {
            graph.addVertex(v);
            verticesP1.add(v);
        }
    }

    /**
     * Add an edge between the given source and target vertices
     *
     * @param source source vertex of edge
     * @param target target vertex of edge
     * @return new edge between source and target
     */
    public JGraphTEdge addEdge(JGraphTVertex source, JGraphTVertex target) {
        return graph.addEdge(source, target);
    }

    protected Graph<JGraphTVertex, JGraphTEdge> getGraph() {
        return graph;
    }

    /**
     * Get the vertices of Player 0.
     *
     * @return all vertices of Player 0 in the graph.
     */
    @Override
    public Set<JGraphTVertex> getV0() {
        return verticesP0;
    }

    /**
     * Get the vertices of Player 1.
     *
     * @return all vertices of Player 1 in the graph.
     */
    @Override
    public Set<JGraphTVertex> getV1() {
        return verticesP1;
    }

    @Override
    public Set<JGraphTVertex> getVertices() {
        return graph.vertexSet();
    }

    @Override
    public Set<JGraphTEdge> getEdges() {
        return graph.edgeSet();
    }

    @Override
    public Set<JGraphTEdge> incomingEdgesOf(JGraphTVertex v) {
        return graph.incomingEdgesOf(v);
    }

    @Override
    public Set<JGraphTEdge> outgoingEdgesOf(JGraphTVertex v) {
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

    public JGraphTGraph getSubgraph(Set<JGraphTVertex> subgraphVertices) {
        AsSubgraph<JGraphTVertex, JGraphTEdge> subGraph = new AsSubgraph<>(getGraph(), subgraphVertices, null);
        // Intersect V0 with subgraph vertex set.
        HashSet<JGraphTVertex> V0 = new HashSet<>(getV0());
        V0.retainAll(subgraphVertices);
        HashSet<JGraphTVertex> V1 = new HashSet<>(getV1());
        V1.retainAll(subgraphVertices);
        return new JGraphTGraph(subGraph, V0, V1);
    }

    public JGraphTGraph getSwappedSubgraph(Set<JGraphTVertex> subgraphVertices) {
        AsSubgraph<JGraphTVertex, JGraphTEdge> subGraph = new AsSubgraph<>(getGraph(), subgraphVertices, null);
        // Intersect V0 with subgraph vertex set.
        HashSet<JGraphTVertex> V0 = new HashSet<>(getV0());
        V0.retainAll(subgraphVertices);
        HashSet<JGraphTVertex> V1 = new HashSet<>(getV1());
        V1.retainAll(subgraphVertices);
        return new JGraphTGraph(subGraph, V1, V0);
    }

    public JGraphTGraph getSubgraphRestrictEdges(Set<JGraphTEdge> subgraphEdges) {
        AsSubgraph<JGraphTVertex, JGraphTEdge> subGraph = new AsSubgraph<>(getGraph(), null, subgraphEdges);
        return new JGraphTGraph(subGraph, getV1(), getV0());
    }

    @Override
    public GameGraph<JGraphTVertex, JGraphTEdge> getSubgraph(StrategyVector<JGraphTVertex, JGraphTEdge> strategyVector) {
        JGraphTGraph subGraph = new JGraphTGraph();

        for (JGraphTVertex v : getVertices()) {
            JGraphTVertex succ = strategyVector.getSuccessor(v);

            // Add source vertex.
            if (getV0().contains(v)) {
                subGraph.addToV0(v);
            } else {
                subGraph.addToV1(v);
            }
            // Add edge.
            subGraph.addEdge(v, succ);
        }
        return subGraph;

    }

}
