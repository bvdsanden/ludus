package org.ludus.backend.games.ratio.solvers.policy;

import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionDouble;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.ratio.RGDoubleImplJGraphT;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Bram van der Sanden
 */
public class PolicyIterationDoubleTest {

    @Test
    public void testGame1() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex u = new JGraphTVertex(0);
        JGraphTVertex v = new JGraphTVertex(1);
        JGraphTVertex w = new JGraphTVertex(2);
        JGraphTVertex x = new JGraphTVertex(3);

        graph.addToV1(u, v, w, x);

        DoubleWeightFunctionDouble<JGraphTEdge> wf = new DoubleWeightFunctionDouble<>();

        addEdge(graph, wf, u, v, 1.0);
        addEdge(graph, wf, v, w, 2.0);
        addEdge(graph, wf, w, x, 3.0);
        addEdge(graph, wf, x, v, 4.0);

        StrategyVector<JGraphTVertex,JGraphTEdge> s = new StrategyVector<>();
        graph.getEdges().forEach((e) -> s.setSuccessor(graph.getEdgeSource(e), graph.getEdgeTarget(e)));

        RGDoubleImplJGraphT game = new RGDoubleImplJGraphT(graph, wf);

        Double ratio = 9.0 / 3.0;

        Map<JGraphTVertex, Double> result = PolicyIterationDouble.solve(game).getLeft();
        assertTrue(ratio.equals(result.get(u)));
        assertTrue(ratio.equals(result.get(v)));
        assertTrue(ratio.equals(result.get(w)));
        assertTrue(ratio.equals(result.get(x)));
    }


    @Test
    public void testGame1Variation() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex u = new JGraphTVertex(0);
        JGraphTVertex v = new JGraphTVertex(1);
        JGraphTVertex w = new JGraphTVertex(2);
        JGraphTVertex x = new JGraphTVertex(3);

        graph.addToV1(u, v, w, x);

        DoubleWeightFunctionDouble<JGraphTEdge> wf = new DoubleWeightFunctionDouble<>();

        addEdge(graph, wf, u, v, 1.0, 4.0);
        addEdge(graph, wf, v, w, 2.0, 3.0);
        addEdge(graph, wf, w, x, 3.0, 2.0);
        addEdge(graph, wf, x, v, 4.0, 1.0);

        RGDoubleImplJGraphT game = new RGDoubleImplJGraphT(graph, wf);

        Double ratio = 9.0 / 6.0;

        Map<JGraphTVertex, Double> result = PolicyIterationDouble.solve(game).getLeft();
        assertTrue(ratio.equals(result.get(u)));
        assertTrue(ratio.equals(result.get(v)));
        assertTrue(ratio.equals(result.get(w)));
        assertTrue(ratio.equals(result.get(x)));
    }

    @Test
    public void testGame1Variation2() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex u = new JGraphTVertex(0);
        JGraphTVertex v = new JGraphTVertex(1);
        JGraphTVertex w = new JGraphTVertex(2);
        JGraphTVertex x = new JGraphTVertex(3);

        graph.addToV1(u, v, w, x);

        DoubleWeightFunctionDouble<JGraphTEdge> wf = new DoubleWeightFunctionDouble<>();

        addEdge(graph, wf, u, v, 1.0, 3.0);
        addEdge(graph, wf, v, w, 2.0, 3.0);
        addEdge(graph, wf, w, x, 3.0, 1.0);
        addEdge(graph, wf, x, v, 4.0, 7.0);

        RGDoubleImplJGraphT game = new RGDoubleImplJGraphT(graph, wf);

        Double ratio = 9.0 / 11.0;

        Map<JGraphTVertex, Double> result = PolicyIterationDouble.solve(game).getLeft();
        assertTrue(ratio.equals(result.get(u)));
        assertTrue(ratio.equals(result.get(v)));
        assertTrue(ratio.equals(result.get(w)));
        assertTrue(ratio.equals(result.get(x)));
    }

    @Test
    public void testGame3() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex u = new JGraphTVertex(0);
        JGraphTVertex v = new JGraphTVertex(1);
        JGraphTVertex w = new JGraphTVertex(2);
        JGraphTVertex x = new JGraphTVertex(3);
        JGraphTVertex y = new JGraphTVertex(4);

        graph.addToV1(u, v, w, x, y);

        DoubleWeightFunctionDouble<JGraphTEdge> wf = new DoubleWeightFunctionDouble<>();

        addEdge(graph, wf, u, v, 1.0);
        addEdge(graph, wf, v, w, 2.0);
        addEdge(graph, wf, w, x, 3.0);
        addEdge(graph, wf, x, v, 4.0);
        addEdge(graph, wf, y, y, 2.0);

        RGDoubleImplJGraphT game = new RGDoubleImplJGraphT(graph, wf);
        StrategyVector<JGraphTVertex,JGraphTEdge> s = new StrategyVector<>();

        s.setSuccessor(u, v);
        s.setSuccessor(v, w);
        s.setSuccessor(w, x);
        s.setSuccessor(x, v);
        s.setSuccessor(y, y);

        Tuple<Set<JGraphTVertex>, Map<JGraphTVertex, Double>> result = PolicyIterationDouble.FindCyclesInRestrictedGraph(game, s);
        assertTrue(result.getLeft().contains(v));
        assertTrue(result.getLeft().contains(y));

        Double valV = 9.0 / 3.0;
        assertEquals(valV, result.getRight().get(v));
        Double valY = 2.0 / 1.0;
        assertEquals(valY, result.getRight().get(y));
    }

    @Test
    public void testGame4() {
        JGraphTGraph paperGraph = new JGraphTGraph();
        JGraphTVertex a = new JGraphTVertex();
        JGraphTVertex b = new JGraphTVertex();
        JGraphTVertex c = new JGraphTVertex();
        JGraphTVertex d = new JGraphTVertex();
        JGraphTVertex e = new JGraphTVertex();

        paperGraph.addToV0(a, c, e);
        paperGraph.addToV1(d, b);

        DoubleWeightFunctionDouble<JGraphTEdge> wf = new DoubleWeightFunctionDouble<>();

        addEdge(paperGraph, wf, a, d, 0.0);
        addEdge(paperGraph, wf, a, b, 0.0);
        addEdge(paperGraph, wf, d, e, 0.0, 1.0);
        addEdge(paperGraph, wf, e, d, 1.0, 2.0);
        addEdge(paperGraph, wf, b, c, 3.0, 0.0);
        addEdge(paperGraph, wf, c, b, 4.0, 2.0);

        RGDoubleImplJGraphT game = new RGDoubleImplJGraphT(paperGraph, wf);

        Map<JGraphTVertex, Double> result = PolicyIterationDouble.solve(game).getLeft();
        Double val1 = 7.0 / 2.0;
        Double val2 = 1.0 / 3.0;

        assertEquals(val1, result.get(a));
        assertEquals(val1, result.get(b));
        assertEquals(val1, result.get(c));
        assertEquals(val2, result.get(d));
        assertEquals(val2, result.get(e));
    }

    @Test
    public void testGraph5() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex v0 = new JGraphTVertex(0);
        JGraphTVertex v1 = new JGraphTVertex(1);
        JGraphTVertex v2 = new JGraphTVertex(2);
        JGraphTVertex v3 = new JGraphTVertex(3);
        graph.addToV0(v0, v3);
        graph.addToV1(v1, v2);
        JGraphTEdge v01 = graph.addEdge(v0, v1);
        JGraphTEdge v02 = graph.addEdge(v0, v2);
        JGraphTEdge v10 = graph.addEdge(v1, v0);
        JGraphTEdge v13 = graph.addEdge(v1, v3);
        JGraphTEdge v20 = graph.addEdge(v2, v0);
        JGraphTEdge v23 = graph.addEdge(v2, v3);
        JGraphTEdge v31 = graph.addEdge(v3, v1);
        JGraphTEdge v32 = graph.addEdge(v3, v2);
        DoubleWeightFunctionDouble<JGraphTEdge> weights = new DoubleWeightFunctionDouble<>();
        weights.addWeight(v01, 19, 35);
        weights.addWeight(v02, 8, 43);
        weights.addWeight(v10, 34, 46);
        weights.addWeight(v13, 43, 27);
        weights.addWeight(v23, 3, 36);
        weights.addWeight(v20, 34, 46);
        weights.addWeight(v32, 41, 31);
        weights.addWeight(v31, 41, 31);

        RGDoubleImplJGraphT game = new RGDoubleImplJGraphT(graph, weights);

        StrategyVector<JGraphTVertex,JGraphTEdge> s = new StrategyVector<>();
        s.setSuccessor(v0, v1);
        s.setSuccessor(v1, v0);
        s.setSuccessor(v2, v3);
        s.setSuccessor(v3, v2);

        PolicyIterationDouble.solve(game, s, 10E-3);
    }

    private static void addEdge(JGraphTGraph g, DoubleWeightFunctionDouble<JGraphTEdge> wf, JGraphTVertex src, JGraphTVertex target, Double weight1) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight1, 1);
    }

    private static void addEdge(JGraphTGraph g, DoubleWeightFunctionDouble<JGraphTEdge> wf, JGraphTVertex src, JGraphTVertex target, Double weight1, Double weight2) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight1, weight2);
    }
}
