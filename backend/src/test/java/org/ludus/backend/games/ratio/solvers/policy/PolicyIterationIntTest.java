package org.ludus.backend.games.ratio.solvers.policy;

import org.apache.commons.math3.fraction.Fraction;
import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionInt;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.ratio.RGIntImplJGraphT;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Bram van der Sanden
 */
public class PolicyIterationIntTest {

    @Test
    public void testGame1() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex u = new JGraphTVertex(0);
        JGraphTVertex v = new JGraphTVertex(1);
        JGraphTVertex w = new JGraphTVertex(2);
        JGraphTVertex x = new JGraphTVertex(3);

        graph.addToV1(u, v, w, x);

        DoubleWeightFunctionInt<JGraphTEdge> wf = new DoubleWeightFunctionInt<>();

        addEdge(graph, wf, u, v, 1);
        addEdge(graph, wf, v, w, 2);
        addEdge(graph, wf, w, x, 3);
        addEdge(graph, wf, x, v, 4);

        StrategyVector<JGraphTVertex,JGraphTEdge> s = new StrategyVector<>();
        graph.getEdges().forEach((e) -> s.setSuccessor(graph.getEdgeSource(e), graph.getEdgeTarget(e)));

        RGIntImplJGraphT game = new RGIntImplJGraphT(graph, wf);

        Fraction mw = new Fraction(9, 3);

        Map<JGraphTVertex, Fraction> values2 = PolicyIterationInt.solve(game).getLeft();
        assertEquals(mw, values2.get(u));
        assertEquals(mw, values2.get(v));
        assertEquals(mw, values2.get(w));
        assertEquals(mw, values2.get(x));
    }

    @Test
    public void testGame1Variation() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex u = new JGraphTVertex(0);
        JGraphTVertex v = new JGraphTVertex(1);
        JGraphTVertex w = new JGraphTVertex(2);
        JGraphTVertex x = new JGraphTVertex(3);

        graph.addToV1(u, v, w, x);

        DoubleWeightFunctionInt<JGraphTEdge> wf = new DoubleWeightFunctionInt<>();

        addEdge(graph, wf, u, v, 1, 4);
        addEdge(graph, wf, v, w, 2, 3);
        addEdge(graph, wf, w, x, 3, 2);
        addEdge(graph, wf, x, v, 4, 1);

        RGIntImplJGraphT game = new RGIntImplJGraphT(graph, wf);

        Map<JGraphTVertex, Fraction> result = PolicyIterationInt.solve(game).getLeft();
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

        DoubleWeightFunctionInt<JGraphTEdge> wf = new DoubleWeightFunctionInt<>();

        addEdge(graph, wf, u, v, 1);
        addEdge(graph, wf, v, w, 2);
        addEdge(graph, wf, w, x, 3);
        addEdge(graph, wf, x, v, 4);
        addEdge(graph, wf, y, y, 2);

        RGIntImplJGraphT game = new RGIntImplJGraphT(graph, wf);
        StrategyVector<JGraphTVertex,JGraphTEdge> s = new StrategyVector<>();

        s.setSuccessor(u, v);
        s.setSuccessor(v, w);
        s.setSuccessor(w, x);
        s.setSuccessor(x, v);
        s.setSuccessor(y, y);

        Tuple<Set<JGraphTVertex>, Map<JGraphTVertex, Fraction>> result = PolicyIterationInt.FindCyclesInRestrictedGraph(game, s);
        assertTrue(result.getLeft().contains(v));
        assertTrue(result.getLeft().contains(y));

        assertEquals(new Fraction(9, 3), result.getRight().get(v));
        assertEquals(new Fraction(2, 1), result.getRight().get(y));
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

        DoubleWeightFunctionInt<JGraphTEdge> wf = new DoubleWeightFunctionInt<>();

        addEdge(paperGraph, wf, a, d, 0);
        addEdge(paperGraph, wf, a, b, 0);
        addEdge(paperGraph, wf, d, e, 0, 1);
        addEdge(paperGraph, wf, e, d, 1, 2);
        addEdge(paperGraph, wf, b, c, 3, 0);
        addEdge(paperGraph, wf, c, b, 4, 2);

        RGIntImplJGraphT game = new RGIntImplJGraphT(paperGraph, wf);

        Map<JGraphTVertex, Fraction> result = PolicyIterationInt.solve(game).getLeft();
        assertEquals(new Fraction(7, 2), result.get(a));
        assertEquals(new Fraction(7, 2), result.get(b));
        assertEquals(new Fraction(7, 2), result.get(c));
        assertEquals(new Fraction(1, 3), result.get(d));
        assertEquals(new Fraction(1, 3), result.get(e));
    }

    private static void addEdge(JGraphTGraph g, DoubleWeightFunctionInt<JGraphTEdge> wf, JGraphTVertex src, JGraphTVertex target, Integer weight1) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight1, 1);
    }

    private static void addEdge(JGraphTGraph g, DoubleWeightFunctionInt<JGraphTEdge> wf, JGraphTVertex src, JGraphTVertex target, Integer weight1, Integer weight2) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight1, weight2);
    }
}
