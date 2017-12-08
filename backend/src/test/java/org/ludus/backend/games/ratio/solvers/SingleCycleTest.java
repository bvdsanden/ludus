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
public class SingleCycleTest {

    @Test
    public void testZeroCycle() {
        // (100,20) (0,0)*
        JGraphTGraph paperGraph = new JGraphTGraph();
        JGraphTVertex a = new JGraphTVertex();
        JGraphTVertex b = new JGraphTVertex();

        paperGraph.addToV0(a, b);

        DoubleWeightFunctionInt wf = new DoubleWeightFunctionInt();
        addEdge(paperGraph, wf, a, b, 100, 20);
        addEdge(paperGraph, wf, b, b, 5, 1);

        RGIntImplJGraphT game = new RGIntImplJGraphT(paperGraph, wf);

        Map<JGraphTVertex, Fraction> result = RatioGameValueIterationInt.solve(game);
        assertEquals(new Fraction(5, 1), result.get(a));
        assertEquals(new Fraction(5, 1), result.get(b));


    }

    @Test
    public void testNominatorZeroCycle() {
        // (100,20) (0,1)*
        JGraphTGraph paperGraph = new JGraphTGraph();
        JGraphTVertex a = new JGraphTVertex();
        JGraphTVertex b = new JGraphTVertex();

        paperGraph.addToV0(a, b);

        DoubleWeightFunctionInt wf = new DoubleWeightFunctionInt();
        addEdge(paperGraph, wf, a, b, 100, 20);
        addEdge(paperGraph, wf, b, b, 0, 1);

        RGIntImplJGraphT game = new RGIntImplJGraphT(paperGraph, wf);

        Map<JGraphTVertex, Fraction> result = RatioGameValueIterationInt.solve(game);
        assertEquals(Fraction.ZERO, result.get(a));
        assertEquals(Fraction.ZERO, result.get(b));
    }


    private static void addEdge(JGraphTGraph g, DoubleWeightFunctionInt wf, JGraphTVertex src, JGraphTVertex target, Integer weight1, Integer weight2) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight1, weight2);
    }

}
