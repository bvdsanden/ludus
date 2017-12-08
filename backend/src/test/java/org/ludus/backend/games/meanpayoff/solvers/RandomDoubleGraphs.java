package org.ludus.backend.games.meanpayoff.solvers;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.datastructures.weights.SingleWeightFunctionDouble;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.games.meanpayoff.solvers.policy.PolicyIterationDouble;
import org.ludus.backend.games.meanpayoff.solvers.zwick.ZPSolverDouble;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.meanpayoff.MPGDoubleImplJGraphT;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.ludus.backend.games.algorithms.DoubleFunctions.equalTo;

/**
 * @author Bram van der Sanden
 */
@Tag("slow")
public class RandomDoubleGraphs {

    public static MPGDoubleImplJGraphT constructGameGraphImpl() {
        JGraphTGraph paperGraph = new JGraphTGraph();
        Set<JGraphTVertex> list = new HashSet();

        int n = 10;
        int W = 10;

        for (int i = 0; i < n; i++) {
            JGraphTVertex v = new JGraphTVertex();
            list.add(v);
            if (i % 2 == 0) {
                paperGraph.addToV0(v);
            } else {
                paperGraph.addToV1(v);
            }
        }

        SingleWeightFunctionDouble wf = new SingleWeightFunctionDouble();

        Iterator<JGraphTVertex> slowI = list.iterator();
        Iterator<JGraphTVertex> fastI;

        while (slowI.hasNext()) { //While there are more vertices in the set
            JGraphTVertex latestVertex = slowI.next();
            fastI = list.iterator();
            //Jump to the first vertex *past* latestVertex
            while (fastI.next() != latestVertex) {
            }
            //And, add edges to all remaining vertices
            JGraphTVertex temp;
            while (fastI.hasNext()) {
                temp = fastI.next();
                Random r = new Random();
                Double rand1 = 1.0 + (W - 1.0) * r.nextDouble();
                Double rand2 = 1.0 + (W - 1.0) * r.nextDouble();
                addEdge(paperGraph, wf, latestVertex, temp, rand1);
                addEdge(paperGraph, wf, temp, latestVertex, rand2);

            }
        }
        return new MPGDoubleImplJGraphT(paperGraph, wf);
    }

    @Test
    public void testAlgorithms() {
        for (int i = 0; i < 2; i++) {
            // Construct the game graph.
            MPGDoubleImplJGraphT game = constructGameGraphImpl();

            long start = System.nanoTime();
            Map<JGraphTVertex, Double> resultZP = ZPSolverDouble.getValues(game);
            long end = System.nanoTime();
            System.out.println("Solved using ZwickPaterson: " + ((end - start) / 1000000000.0) + " sec");

            start = System.nanoTime();
            Tuple<Map<JGraphTVertex, Double>, StrategyVector<JGraphTVertex, JGraphTEdge>> resultPI = PolicyIterationDouble.solve(game);
            end = System.nanoTime();
            System.out.println("Solved using PolicyIteration: " + ((end - start) / 1000000000.0) + " sec");

            start = System.nanoTime();
            //Map<JGraphTVertex, Double> resultVI = ValueIterationReductionDouble.solve(game);
            end = System.nanoTime();
            System.out.println("Solved using ValueIteration: " + ((end - start) / 1000000000.0) + " sec");

            for (JGraphTVertex v : game.getVertices()) {
                assertTrue(equalTo(resultZP.get(v), resultPI.getLeft().get(v)));
            }
        }

        System.out.println("Finished!");
    }

    private static void addEdge(JGraphTGraph g, SingleWeightFunctionDouble wf, JGraphTVertex src, JGraphTVertex target, Double weight) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight);
    }
}
