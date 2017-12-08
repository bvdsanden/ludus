package org.ludus.backend.games.ratio.solvers.zwick;

import org.apache.commons.math3.fraction.Fraction;
import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionInt;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.ratio.RGIntImplJGraphT;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Bram van der Sanden
 */
public class ZPSolverTest {

    @Test
    public void testGraph1() {
        JGraphTGraph paperGraph = new JGraphTGraph();
        JGraphTVertex a = new JGraphTVertex();
        JGraphTVertex b = new JGraphTVertex();

        paperGraph.addToV0(a, b);

        DoubleWeightFunctionInt<JGraphTEdge> wf = new DoubleWeightFunctionInt<>();
        addEdge(paperGraph, wf, a, b, 1, 2);
        addEdge(paperGraph, wf, b, a, 4, 1);

        RGIntImplJGraphT game = new RGIntImplJGraphT(paperGraph, wf);

        Map<JGraphTVertex, Fraction> result = SolverZPInt.getValues(game);
        assertEquals(new Fraction(5, 3), result.get(a));
        assertEquals(new Fraction(5, 3), result.get(b));
    }


    private static void addEdge(JGraphTGraph g, DoubleWeightFunctionInt<JGraphTEdge> wf, JGraphTVertex src, JGraphTVertex target, Integer weight1, Integer weight2) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight1, weight2);
    }

}
