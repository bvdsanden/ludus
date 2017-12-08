package org.ludus.backend.games.ratio.solvers.zwick;

import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionDouble;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.ratio.RGDoubleImplJGraphT;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.ludus.backend.games.algorithms.DoubleFunctions.equalTo;

/**
 * @author Bram van der Sanden
 */
public class ZPSolverDoubleTest {

    @Test
    public void testExact() {
        JGraphTGraph paperGraph = new JGraphTGraph();
        JGraphTVertex a = new JGraphTVertex();
        JGraphTVertex b = new JGraphTVertex();

        paperGraph.addToV0(a, b);

        DoubleWeightFunctionDouble wf = new DoubleWeightFunctionDouble();
        addEdge(paperGraph, wf, a, b, 4.001, 2.0);
        addEdge(paperGraph, wf, b, a, 1.0, 1.0);

        RGDoubleImplJGraphT game = new RGDoubleImplJGraphT(paperGraph, wf);

        Map<JGraphTVertex, Double> result = SolverZPDouble.getValues(game);
        Double val = 5.001 / 3.0;
        assertTrue(equalTo(val, result.get(a)));
        assertTrue(equalTo(val, result.get(b)));
    }


    private static void addEdge(JGraphTGraph g, DoubleWeightFunctionDouble wf, JGraphTVertex src, JGraphTVertex target, Double weight1, Double weight2) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight1, weight2);
    }

}
