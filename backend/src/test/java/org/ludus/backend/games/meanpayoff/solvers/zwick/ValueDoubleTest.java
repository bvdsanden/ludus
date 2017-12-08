package org.ludus.backend.games.meanpayoff.solvers.zwick;

import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.weights.SingleWeightFunctionDouble;
import org.ludus.backend.games.algorithms.DoubleFunctions;
import org.ludus.backend.games.meanpayoff.solvers.energy.MeanPayoffGameEnergy;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.meanpayoff.MPGDoubleImplJGraphT;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Bram van der Sanden
 */
public class ValueDoubleTest {

    //@Test
    public void testValue() {
        JGraphTGraph paperGraph = new JGraphTGraph();
        JGraphTVertex x = new JGraphTVertex();
        JGraphTVertex y = new JGraphTVertex();
        paperGraph.addToV0(x, y);

        SingleWeightFunctionDouble wf = new SingleWeightFunctionDouble();

        addEdge(paperGraph, wf, x, y, 3.4222);
        addEdge(paperGraph, wf, y, x, 4.60);

        MeanPayoffGameEnergy game = new MPGDoubleImplJGraphT(paperGraph, wf);

        Map<JGraphTVertex, Double> values = ZPSolverDouble.getValues(game);
        Double val = 4.0111;
        assertTrue(DoubleFunctions.equalTo(val, values.get(x)));
        assertTrue(DoubleFunctions.equalTo(val, values.get(y)));
    }

    @Test
    public void testValueNegative() {
        JGraphTGraph paperGraph = new JGraphTGraph();
        JGraphTVertex x = new JGraphTVertex();
        JGraphTVertex y = new JGraphTVertex();
        paperGraph.addToV0(x, y);

        SingleWeightFunctionDouble wf = new SingleWeightFunctionDouble();

        addEdge(paperGraph, wf, x, y, -3.4222);
        addEdge(paperGraph, wf, y, x, -4.60);

        MeanPayoffGameEnergy game = new MPGDoubleImplJGraphT(paperGraph, wf);

        Map<JGraphTVertex, Double> values = ZPSolverDouble.getValues(game);
        Double val = -4.0111;
        assertTrue(DoubleFunctions.equalTo(val, values.get(x)));
        assertTrue(DoubleFunctions.equalTo(val, values.get(y)));
    }


    private static void addEdge(JGraphTGraph g, SingleWeightFunctionDouble wf, JGraphTVertex src, JGraphTVertex target, Double weight) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight);
    }

}
