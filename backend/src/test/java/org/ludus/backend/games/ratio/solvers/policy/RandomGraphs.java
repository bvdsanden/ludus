package org.ludus.backend.games.ratio.solvers.policy;

import org.apache.commons.math3.fraction.Fraction;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionInt;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.games.ratio.solvers.policy.invalid.PolicyIterationIntManyMany;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.ratio.RGIntImplJGraphT;
import org.ludus.backend.graph.weighted.WIntEdge;
import org.ludus.backend.graph.weighted.WIntGraph;
import org.ludus.backend.graph.weighted.WVertex;
import org.ludus.backend.graph.weighted.ratio.WDoubleWeightedGraph;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Bram van der Sanden
 */
@Tag("slow")
public class RandomGraphs {

    public static RGIntImplJGraphT constructJGraph() {
        JGraphTGraph paperGraph = new JGraphTGraph();
        Set<JGraphTVertex> list = new HashSet<>();

        int n = 500;
        int W = 50;

        for (int i = 0; i < n; i++) {
            JGraphTVertex v = new JGraphTVertex();
            list.add(v);
            if (i % 2 == 0) {
                paperGraph.addToV0(v);
            } else {
                paperGraph.addToV1(v);
            }
        }

        DoubleWeightFunctionInt wf = new DoubleWeightFunctionInt();

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
                addEdge(paperGraph, wf, latestVertex, temp, rand1, rand2);
                addEdge(paperGraph, wf, temp, latestVertex, rand2, rand1);

            }
        }
        return new RGIntImplJGraphT(paperGraph, wf);
    }

    public static WDoubleWeightedGraph constructWGraph() {
        WIntGraph paperGraph = new WIntGraph();
        Set<WVertex> list = new HashSet<>();

        int n = 500;
        int W = 50;

        for (int i = 0; i < n; i++) {
            WVertex v = new WVertex();
            list.add(v);
            if (i % 2 == 0) {
                paperGraph.addToV0(v);
            } else {
                paperGraph.addToV1(v);
            }
        }

        Iterator<WVertex> slowI = list.iterator();
        Iterator<WVertex> fastI;

        while (slowI.hasNext()) { //While there are more vertices in the set
            WVertex latestVertex = slowI.next();
            fastI = list.iterator();
            //Jump to the first vertex *past* latestVertex
            while (fastI.next() != latestVertex) {
            }
            //And, add edges to all remaining vertices
            WVertex temp;
            while (fastI.hasNext()) {
                temp = fastI.next();
                Random r = new Random();
                Integer rand1 = r.nextInt(W - 1) + 1;
                Integer rand2 = r.nextInt(W - 1) + 1;
                paperGraph.addEdge(latestVertex, temp, rand1, rand2);
                paperGraph.addEdge(temp, latestVertex, rand2, rand1);

            }
        }

        return new WDoubleWeightedGraph(paperGraph);
    }

    @Test
    public void testRandomGraphs() {

        for (int i = 0; i < 5; i++) {
            RatioGamePolicyIteration<JGraphTVertex, JGraphTEdge, Integer> game = constructJGraph();

            long start2 = System.nanoTime();
            Tuple<Map<JGraphTVertex, Fraction>, StrategyVector<JGraphTVertex, JGraphTEdge>> result = PolicyIterationIntManyMany.solve(game);
            long end2 = System.nanoTime();
            System.out.println("Solve jGraphT manymany: Time elapsed for compact: " + ((end2 - start2) / 1000000000.0) + " sec");

            start2 = System.nanoTime();
            Tuple<Map<JGraphTVertex, Fraction>, StrategyVector<JGraphTVertex, JGraphTEdge>> result2 = PolicyIterationInt.solve(game);
            end2 = System.nanoTime();
            System.out.println("Solve jGraphT: Time elapsed for compact: " + ((end2 - start2) / 1000000000.0) + " sec");

            for (JGraphTVertex v : game.getVertices()) {
                assertEquals(0, result.getLeft().get(v).compareTo(result2.getLeft().get(v)));
            }
        }

        for (int i = 0; i < 5; i++) {
            RatioGamePolicyIteration<WVertex, WIntEdge, Integer> game = constructWGraph();
            long start2 = System.nanoTime();
            Tuple<Map<WVertex, Fraction>, StrategyVector<WVertex, WIntEdge>> result = PolicyIterationInt.solve(game);
            long end2 = System.nanoTime();
            System.out.println("Solve WGraph: Time elapsed for compact: " + ((end2 - start2) / 1000000000.0) + " sec");

            start2 = System.nanoTime();
            Tuple<Map<WVertex, Fraction>, StrategyVector<WVertex, WIntEdge>> result2 = PolicyIterationIntManyMany.solve(game);
            end2 = System.nanoTime();
            System.out.println("Solve WGraph manymany: Time elapsed for compact: " + ((end2 - start2) / 1000000000.0) + " sec");

            for (WVertex v : game.getVertices()) {
                assertEquals(0, result.getLeft().get(v).compareTo(result2.getLeft().get(v)));
            }
        }


        System.out.println("Finished!");
    }

    private static void addEdge(JGraphTGraph g, DoubleWeightFunctionInt wf, JGraphTVertex src, JGraphTVertex target, Integer weight1, Integer weight2) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight1, weight2);
    }
}
