package org.ludus.backend.games.meanpayoff.solvers;

import org.apache.commons.math3.fraction.Fraction;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.datastructures.weights.SingleWeightFunctionInt;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.games.meanpayoff.solvers.energy.ValueIterationReductionInt;
import org.ludus.backend.games.meanpayoff.solvers.policy.PolicyIterationInt;
import org.ludus.backend.games.meanpayoff.solvers.zwick.ZPSolverInt;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.meanpayoff.MPGIntImplJGraphT;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Bram van der Sanden
 */
@Tag("slow")
public class RandomIntGraphs {

    public static MPGIntImplJGraphT constructGameGraphImpl() {
        JGraphTGraph paperGraph = new JGraphTGraph();
        Set<JGraphTVertex> list = new HashSet();

        int n = 10;
        int W = 100;

        for (int i = 0; i < n; i++) {
            JGraphTVertex v = new JGraphTVertex();
            list.add(v);
            if (i % 2 == 0) {
                paperGraph.addToV0(v);
            } else {
                paperGraph.addToV1(v);
            }
        }

        SingleWeightFunctionInt wf = new SingleWeightFunctionInt();

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
                Integer rand1 = r.nextInt(W - 1) + 1;
                Integer rand2 = r.nextInt(W - 1) + 1;
                addEdge(paperGraph, wf, latestVertex, temp, rand1);
                addEdge(paperGraph, wf, temp, latestVertex, rand2);

            }
        }
        return new MPGIntImplJGraphT(paperGraph, wf);
    }

    @Test
    public void testAlgorithms() {
        for (int i = 0; i < 10; i++) {
            // Construct the game graph.
            MPGIntImplJGraphT game = constructGameGraphImpl();

            long start = System.nanoTime();
            Map<JGraphTVertex, Fraction> resultZP = ZPSolverInt.getValues(game);
            long end = System.nanoTime();
            System.out.println("Solved using ZwickPaterson: " + ((end - start) / 1000000000.0) + " sec");

            start = System.nanoTime();
            Tuple<Map<JGraphTVertex, Fraction>, StrategyVector> resultPI = PolicyIterationInt.solve(game);
            end = System.nanoTime();
            System.out.println("Solved using PolicyIteration: " + ((end - start) / 1000000000.0) + " sec");

            start = System.nanoTime();
            Map<JGraphTVertex, Fraction> resultVI = ValueIterationReductionInt.solve(game);
            end = System.nanoTime();
            System.out.println("Solved using ValueIteration: " + ((end - start) / 1000000000.0) + " sec");

            for (JGraphTVertex v : game.getVertices()) {
                assertEquals(resultZP.get(v), resultPI.getLeft().get(v));
                assertEquals(resultZP.get(v), resultVI.get(v));
            }
        }

        System.out.println("Finished!");
    }

    private static void addEdge(JGraphTGraph g, SingleWeightFunctionInt wf, JGraphTVertex src, JGraphTVertex target, Integer weight) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight);
    }
}
