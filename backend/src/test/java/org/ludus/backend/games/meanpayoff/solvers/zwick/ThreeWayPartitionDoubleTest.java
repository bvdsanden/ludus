package org.ludus.backend.games.meanpayoff.solvers.zwick;

import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.tuple.Triple;
import org.ludus.backend.datastructures.weights.SingleWeightFunctionDouble;
import org.ludus.backend.games.meanpayoff.solvers.energy.MeanPayoffGameEnergy;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.meanpayoff.MPGDoubleImplJGraphT;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Bram van der Sanden
 */
public class ThreeWayPartitionDoubleTest {

    @Test
    public void testGraph1() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex x = new JGraphTVertex();
        JGraphTVertex y = new JGraphTVertex();
        graph.addToV0(x, y);

        SingleWeightFunctionDouble wf = new SingleWeightFunctionDouble();

        addEdge(graph, wf, x, y, -2.0);
        addEdge(graph, wf, y, x, -11.0);

        MeanPayoffGameEnergy game = new MPGDoubleImplJGraphT(graph, wf);

        Map<JGraphTVertex, Double> values = ZPSolverDouble.getValues(game);
        Double val = -13.0 / 2.0;
        assertEquals(val, values.get(x));
        assertEquals(val, values.get(y));

        // Run algorithm.
        Triple<Set<JGraphTVertex>, Set<JGraphTVertex>, Set<JGraphTVertex>> resultzp = ZPSolverDouble.getThreeWayPartition(game, 0.0);
        assertTrue(resultzp.getLeft().contains(x));
        assertTrue(resultzp.getLeft().contains(y));
        Triple<Set<JGraphTVertex>, Set<JGraphTVertex>, Set<JGraphTVertex>> resultzpMiddle = ZPSolverDouble.getThreeWayPartition(game, -13.0 / 2.0);
        assertTrue(resultzpMiddle.getMiddle().contains(x));
        assertTrue(resultzpMiddle.getMiddle().contains(y));
    }

    //@Test
    public void testGraph2() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex x = new JGraphTVertex();
        JGraphTVertex y = new JGraphTVertex();
        JGraphTVertex z = new JGraphTVertex();
        graph.addToV0(x, y, z);

        SingleWeightFunctionDouble wf = new SingleWeightFunctionDouble();

        addEdge(graph, wf, x, y, -20.0);
        addEdge(graph, wf, x, z, -20.0);
        addEdge(graph, wf, y, y, -1.0);
        addEdge(graph, wf, z, z, 0.0);

        MeanPayoffGameEnergy game = new MPGDoubleImplJGraphT(graph, wf);

        Triple<Set<JGraphTVertex>, Set<JGraphTVertex>, Set<JGraphTVertex>> resultzp = ZPSolverDouble.getThreeWayPartition(game, 0.0);
        assertTrue(resultzp.getMiddle().contains(x));
        assertTrue(resultzp.getLeft().contains(y));
        assertTrue(resultzp.getMiddle().contains(z));
    }

    //@Test
    public void testGraph3() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex x = new JGraphTVertex();
        JGraphTVertex y = new JGraphTVertex();
        graph.addToV0(x, y);

        SingleWeightFunctionDouble wf = new SingleWeightFunctionDouble();

        addEdge(graph, wf, x, y, -3.0);
        addEdge(graph, wf, y, x, 2.0);

        MeanPayoffGameEnergy game = new MPGDoubleImplJGraphT(graph, wf);

        Triple<Set<JGraphTVertex>, Set<JGraphTVertex>, Set<JGraphTVertex>> resultzp = ZPSolverDouble.getThreeWayPartition(game, 0.0);
        assertTrue(resultzp.getLeft().contains(x));
        assertTrue(resultzp.getLeft().contains(y));
    }


    private static void addEdge(JGraphTGraph g, SingleWeightFunctionDouble wf, JGraphTVertex src, JGraphTVertex target, Double weight) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight);
    }

}
