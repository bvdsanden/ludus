package org.ludus.backend.games.ratio.solvers;

import org.apache.commons.math3.fraction.Fraction;
import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionInt;
import org.ludus.backend.games.ratio.solvers.energy.RatioGameValueIterationInt;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.ratio.RGIntImplJGraphT;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Bram van der Sanden
 */
public class ValueProblemTest {

    @Test
    public void testValueSolvingExample1() {
        // Test example graph 1.
        JGraphTGraph paperGraph = new JGraphTGraph();
        JGraphTVertex x = new JGraphTVertex();
        JGraphTVertex z = new JGraphTVertex();
        JGraphTVertex v = new JGraphTVertex();
        JGraphTVertex y = new JGraphTVertex();
        JGraphTVertex w = new JGraphTVertex();

        paperGraph.addToV0(x, v, w);
        paperGraph.addToV1(z, y);

        DoubleWeightFunctionInt wf = new DoubleWeightFunctionInt();

        addEdge(paperGraph, wf, x, z, 6);
        addEdge(paperGraph, wf, z, v, 2);
        addEdge(paperGraph, wf, v, y, 3);
        addEdge(paperGraph, wf, y, w, 1);
        addEdge(paperGraph, wf, w, z, 4);

        RGIntImplJGraphT game = new RGIntImplJGraphT(paperGraph, wf);

        Map<JGraphTVertex, Fraction> result = RatioGameValueIterationInt.solve(game);
        assertEquals(result.get(x), new Fraction(5, 2));
        assertEquals(result.get(z), new Fraction(5, 2));
        assertEquals(result.get(v), new Fraction(5, 2));
        assertEquals(result.get(y), new Fraction(5, 2));
        assertEquals(result.get(w), new Fraction(5, 2));
    }

    @Test
    public void testValueSolvingExample2() {
        // Test example graph 1.
        JGraphTGraph paperGraph = new JGraphTGraph();
        JGraphTVertex a = new JGraphTVertex();
        JGraphTVertex b = new JGraphTVertex();
        JGraphTVertex c = new JGraphTVertex();
        JGraphTVertex d = new JGraphTVertex();
        JGraphTVertex e = new JGraphTVertex();

        paperGraph.addToV0(a, c, e);
        paperGraph.addToV1(d, b);

        DoubleWeightFunctionInt wf = new DoubleWeightFunctionInt();

        addEdge(paperGraph, wf, a, d, 0);
        addEdge(paperGraph, wf, a, b, 0);
        addEdge(paperGraph, wf, d, e, 2);
        addEdge(paperGraph, wf, e, d, 1);
        addEdge(paperGraph, wf, b, c, 3);
        addEdge(paperGraph, wf, c, b, 4);

        RGIntImplJGraphT game = new RGIntImplJGraphT(paperGraph, wf);

        Map<JGraphTVertex, Fraction> result = RatioGameValueIterationInt.solve(game);
        assertEquals(result.get(a), new Fraction(7, 2));
        assertEquals(result.get(b), new Fraction(7, 2));
        assertEquals(result.get(c), new Fraction(7, 2));
        assertEquals(result.get(d), new Fraction(3, 2));
        assertEquals(result.get(e), new Fraction(3, 2));
    }

    @Test
    public void testValueSolvingExample3() {
        // Test example graph 1.
        JGraphTGraph paperGraph = new JGraphTGraph();
        JGraphTVertex a = new JGraphTVertex();
        JGraphTVertex b = new JGraphTVertex();
        JGraphTVertex c = new JGraphTVertex();
        JGraphTVertex d = new JGraphTVertex();
        JGraphTVertex e = new JGraphTVertex();

        paperGraph.addToV0(a, c, e);
        paperGraph.addToV1(d, b);

        DoubleWeightFunctionInt wf = new DoubleWeightFunctionInt();

        addEdge(paperGraph, wf, a, d, 0);
        addEdge(paperGraph, wf, a, b, 0);
        addEdge(paperGraph, wf, d, e, 0, 1);
        addEdge(paperGraph, wf, e, d, 1, 2);
        addEdge(paperGraph, wf, b, c, 3, 0);
        addEdge(paperGraph, wf, c, b, 4, 2);

        RGIntImplJGraphT game = new RGIntImplJGraphT(paperGraph, wf);

        Map<JGraphTVertex, Fraction> result = RatioGameValueIterationInt.solve(game);
        assertEquals(new Fraction(7, 2), result.get(a));
        assertEquals(new Fraction(7, 2), result.get(b));
        assertEquals(new Fraction(7, 2), result.get(c));
        assertEquals(new Fraction(1, 3), result.get(d));
        assertEquals(new Fraction(1, 3), result.get(e));
    }

    private static void addEdge(JGraphTGraph g, DoubleWeightFunctionInt wf, JGraphTVertex src, JGraphTVertex target, Integer weight1) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight1, 1);
    }

    private static void addEdge(JGraphTGraph g, DoubleWeightFunctionInt wf, JGraphTVertex src, JGraphTVertex target, Integer weight1, Integer weight2) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight1, weight2);
    }

}
