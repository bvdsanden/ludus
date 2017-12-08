package org.ludus.backend.games.meanpayoff.solvers.zwick;

import org.apache.commons.math3.fraction.Fraction;
import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.weights.SingleWeightFunctionInt;
import org.ludus.backend.games.meanpayoff.solvers.energy.MeanPayoffGameEnergy;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.meanpayoff.MPGIntImplJGraphT;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Bram van der Sanden
 */
public class ValueTest {

    @Test
    public void testValue() {
        JGraphTGraph paperGraph = new JGraphTGraph();
        JGraphTVertex x = new JGraphTVertex();
        JGraphTVertex y = new JGraphTVertex();
        paperGraph.addToV0(x, y);

        SingleWeightFunctionInt<JGraphTEdge> wf = new SingleWeightFunctionInt<>();

        addEdge(paperGraph, wf, x, y, -7);
        addEdge(paperGraph, wf, y, x, 7);

        MeanPayoffGameEnergy<JGraphTVertex, JGraphTEdge, Integer> game = new MPGIntImplJGraphT(paperGraph, wf);

        Map<JGraphTVertex, Fraction> values = ZPSolverInt.getValues(game);
        assertEquals(Fraction.ZERO, values.get(x));
        assertEquals(Fraction.ZERO, values.get(y));

        Map<JGraphTVertex, Fraction> estimates = ZwickPatersonInt.computeEstimate(game);
        assertEquals(Fraction.ZERO, estimates.get(x));
        assertEquals(Fraction.ZERO, estimates.get(y));
    }

    private static void addEdge(JGraphTGraph g, SingleWeightFunctionInt<JGraphTEdge> wf, JGraphTVertex src, JGraphTVertex target, Integer weight) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight);
    }

}
