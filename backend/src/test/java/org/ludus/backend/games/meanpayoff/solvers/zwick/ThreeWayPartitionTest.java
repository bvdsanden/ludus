package org.ludus.backend.games.meanpayoff.solvers.zwick;

import org.apache.commons.math3.fraction.Fraction;
import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.tuple.Triple;
import org.ludus.backend.datastructures.weights.SingleWeightFunctionInt;
import org.ludus.backend.games.meanpayoff.solvers.energy.MeanPayoffGameEnergy;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.meanpayoff.MPGIntImplJGraphT;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Bram van der Sanden
 */
public class ThreeWayPartitionTest {

    @Test
    public void testGraph1() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex x = new JGraphTVertex();
        JGraphTVertex y = new JGraphTVertex();
        graph.addToV0(x, y);

        SingleWeightFunctionInt wf = new SingleWeightFunctionInt();

        addEdge(graph, wf, x, y, -2);
        addEdge(graph, wf, y, x, -11);

        MeanPayoffGameEnergy<JGraphTVertex, JGraphTEdge, Integer> game = new MPGIntImplJGraphT(graph, wf);

        Map<JGraphTVertex, Fraction> values = ZPSolverInt.getValues(game);
        assertEquals(new Fraction(-13, 2), values.get(x));
        assertEquals(new Fraction(-13, 2), values.get(y));

        // Run algorithm.
        Triple<Set<JGraphTVertex>, Set<JGraphTVertex>, Set<JGraphTVertex>> resultzp = ZPSolverInt.getThreeWayPartition(game, Fraction.ZERO);
        assertTrue(resultzp.getLeft().contains(x));
        assertTrue(resultzp.getLeft().contains(y));
        Triple<Set<JGraphTVertex>, Set<JGraphTVertex>, Set<JGraphTVertex>> resultzpMiddle = ZPSolverInt.getThreeWayPartition(game, new Fraction(-13, 2));
        assertTrue(resultzpMiddle.getMiddle().contains(x));
        assertTrue(resultzpMiddle.getMiddle().contains(y));
    }

    @Test
    public void testGraph2() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex x = new JGraphTVertex();
        JGraphTVertex y = new JGraphTVertex();
        JGraphTVertex z = new JGraphTVertex();
        graph.addToV0(x, y, z);

        SingleWeightFunctionInt<JGraphTEdge> wf = new SingleWeightFunctionInt<>();

        addEdge(graph, wf, x, y, -20);
        addEdge(graph, wf, x, z, -20);
        addEdge(graph, wf, y, y, -1);
        addEdge(graph, wf, z, z, 0);

        MeanPayoffGameEnergy<JGraphTVertex, JGraphTEdge, Integer> game = new MPGIntImplJGraphT(graph, wf);

        Triple<Set<JGraphTVertex>, Set<JGraphTVertex>, Set<JGraphTVertex>> resultzp = ZPSolverInt.getThreeWayPartition(game, Fraction.ZERO);
        assertTrue(resultzp.getMiddle().contains(x));
        assertTrue(resultzp.getLeft().contains(y));
        assertTrue(resultzp.getMiddle().contains(z));
    }

    @Test
    public void testGraph3() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex x = new JGraphTVertex();
        JGraphTVertex y = new JGraphTVertex();
        graph.addToV0(x, y);

        SingleWeightFunctionInt<JGraphTEdge> wf = new SingleWeightFunctionInt<>();

        addEdge(graph, wf, x, y, -3);
        addEdge(graph, wf, y, x, 2);

        MeanPayoffGameEnergy<JGraphTVertex, JGraphTEdge, Integer> game = new MPGIntImplJGraphT(graph, wf);

        Triple<Set<JGraphTVertex>, Set<JGraphTVertex>, Set<JGraphTVertex>> resultzp = ZPSolverInt.getThreeWayPartition(game, Fraction.ZERO);
        assertTrue(resultzp.getLeft().contains(x));
        assertTrue(resultzp.getLeft().contains(y));
    }


    private static void addEdge(JGraphTGraph g, SingleWeightFunctionInt<JGraphTEdge> wf, JGraphTVertex src, JGraphTVertex target, Integer weight) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight);
    }

}
