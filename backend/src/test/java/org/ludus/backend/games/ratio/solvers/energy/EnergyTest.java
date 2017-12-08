package org.ludus.backend.games.ratio.solvers.energy;

import org.apache.commons.math3.fraction.Fraction;
import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionDouble;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionInt;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.ratio.RGDoubleImplJGraphT;
import org.ludus.backend.graph.jgrapht.ratio.RGIntImplJGraphT;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.ludus.backend.games.algorithms.DoubleFunctions.equalTo;

/**
 * @author Bram van der Sanden
 */
public class EnergyTest {

    public EnergyTest() {
    }

    @Test
    public void testGraphInt() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex a = new JGraphTVertex();
        JGraphTVertex b = new JGraphTVertex();
        JGraphTVertex c = new JGraphTVertex();
        JGraphTVertex d = new JGraphTVertex();

        graph.addToV0(a, d);
        graph.addToV1(b, c);

        DoubleWeightFunctionInt wf = new DoubleWeightFunctionInt();

        addEdge(graph, wf, a, b, 1, 1);
        addEdge(graph, wf, b, c, 1, 1);
        addEdge(graph, wf, c, d, 1, 1);
        addEdge(graph, wf, d, a, 1, 1);

        addEdge(graph, wf, a, a, 0, 2);
        addEdge(graph, wf, b, b, 3, 1);
        addEdge(graph, wf, c, b, 1, 4);
        addEdge(graph, wf, c, a, 0, 2);
        RGIntImplJGraphT game = new RGIntImplJGraphT(graph, wf);

        Map<JGraphTVertex, Fraction> result = RatioGameValueIterationInt.solve(game);
        assertEquals(new Fraction(2, 5), result.get(a));
        assertEquals(new Fraction(2, 5), result.get(b));
        assertEquals(new Fraction(2, 5), result.get(c));
        assertEquals(new Fraction(2, 5), result.get(d));

        long start = System.nanoTime();
        Tuple<Map<JGraphTVertex, Fraction>, StrategyVector<JGraphTVertex, JGraphTEdge>> result2 = RatioGameValueIterationStrategyInt.solve(game);
        long end = System.nanoTime();
        System.out.println("Int elapsed: " + (end - start) / 1000000000.0);
        assertEquals(b, result2.getRight().getSuccessor(a));
        assertEquals(a, result2.getRight().getSuccessor(d));
    }

    @Test
    public void testGraphDouble() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex a = new JGraphTVertex();
        JGraphTVertex b = new JGraphTVertex();
        JGraphTVertex c = new JGraphTVertex();
        JGraphTVertex d = new JGraphTVertex();

        graph.addToV0(a, d);
        graph.addToV1(b, c);

        DoubleWeightFunctionDouble wf = new DoubleWeightFunctionDouble();

        addEdge(graph, wf, a, b, 1.0, 1.0);
        addEdge(graph, wf, b, c, 1.0, 1.0);
        addEdge(graph, wf, c, d, 1.0, 1.0);
        addEdge(graph, wf, d, a, 1.0, 1.0);

        addEdge(graph, wf, a, a, 0.0, 2.0);
        addEdge(graph, wf, b, b, 3.0, 1.0);
        addEdge(graph, wf, c, b, 1.0, 4.0);
        addEdge(graph, wf, c, a, 0.0, 2.0);
        RGDoubleImplJGraphT game = new RGDoubleImplJGraphT(graph, wf);

        long start = System.nanoTime();
        Map<JGraphTVertex, Double> result = RatioGameValueIterationDouble.solve(game);
        long end = System.nanoTime();
        System.out.println("Double elapsed: " + (end - start) / 1000000000.0);

        double value = 2.0 / 5.0;

        assertTrue(equalTo(value, result.get(a)));
        assertTrue(equalTo(value, result.get(b)));
        assertTrue(equalTo(value, result.get(c)));
        assertTrue(equalTo(value, result.get(d)));
    }

    private static void addEdge(JGraphTGraph g, DoubleWeightFunctionInt wf, JGraphTVertex src, JGraphTVertex target, Integer weight1, Integer weight2) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight1, weight2);
    }

    private static void addEdge(JGraphTGraph g, DoubleWeightFunctionDouble wf, JGraphTVertex src, JGraphTVertex target, Double weight1, Double weight2) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight1, weight2);
    }
}
