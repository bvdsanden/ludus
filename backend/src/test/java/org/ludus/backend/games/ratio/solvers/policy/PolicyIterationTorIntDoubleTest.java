package org.ludus.backend.games.ratio.solvers.policy;

import org.apache.commons.math3.fraction.Fraction;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionDouble;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionInt;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.games.algorithms.DoubleFunctions;
import org.ludus.backend.games.benchmarking.generator.Tor;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.ratio.RGDoubleImplJGraphT;
import org.ludus.backend.graph.jgrapht.ratio.RGIntImplJGraphT;

import java.util.Map;

/**
 * @author Bram van der Sanden
 */
@Tag("slow")
public class PolicyIterationTorIntDoubleTest {

    @Test
    public void run() {
        for (int i = 0; i < 50; i++) {
            runTest();
        }
    }

    public void runTest() {
        int size = 50;
        int maxWeight1 = 2;
        int maxWeight2 = 50;

        // 1. Integer graph.
        RGIntImplJGraphT torGraph
                = Tor.generateRatioGame(size, maxWeight1, maxWeight2);

        // Run the integer algorithm.
        long start = System.nanoTime();
        Tuple<Map<JGraphTVertex, Fraction>, StrategyVector<JGraphTVertex, JGraphTEdge>> resultInt = PolicyIterationInt.solve(torGraph);
        long end = System.nanoTime();
        float timeInt = (end - start) / 1000000000.0f;
        System.out.println("Integer algo: " + timeInt);

        // 2. Convert integers to doubles.
        RGDoubleImplJGraphT torDoubleGraph = toDoubleGameGraph(torGraph);

        // Run the doubles algorithm.
        Integer V = torDoubleGraph.getVertices().size();
        Double epsilon = 2 * (V - 1) * DoubleFunctions.MACHINE_PRECISION;
        Double delta = 2 * V * (V - 1) * DoubleFunctions.MACHINE_PRECISION;

        System.out.println("epsilon=" + epsilon);

        long start2 = System.nanoTime();
        Tuple<Map<JGraphTVertex, Double>, StrategyVector<JGraphTVertex, JGraphTEdge>> resultDouble = PolicyIterationDoubleVars.solve(torDoubleGraph, epsilon, delta);
        long end2 = System.nanoTime();
        float timeDouble = (end2 - start2) / 1000000000.0f;
        System.out.println("Double algo: " + timeDouble);

        for (JGraphTVertex v : torGraph.getVertices()) {
            Double diff = resultInt.getLeft().get(v).doubleValue() - resultDouble.getLeft().get(v);
            if (Math.abs(diff) > delta) {

                System.out.println("Vertices:");
                for (JGraphTVertex v0 : torGraph.getV0()) {
                    System.out.println("In V0: " + torGraph.getId(v0));
                }
                for (JGraphTVertex v1 : torGraph.getV1()) {
                    System.out.println("In V1: " + torGraph.getId(v1));
                }

                System.out.println("Edge weights:");
                for (JGraphTEdge e : torGraph.getEdges()) {
                    System.out.format("(%d,%d) w1=%d, w2=%d",
                            torGraph.getId(torGraph.getEdgeSource(e)),
                            torGraph.getId(torGraph.getEdgeTarget(e)),
                            torGraph.getEdgeWeights().getWeight1(e),
                            torGraph.getEdgeWeights().getWeight2(e));
                }
                System.out.println("");

                System.out.println("In Doubles:");
                for (JGraphTVertex v2 : resultDouble.getRight().getVertices()) {
                    System.out.print(torGraph.getId(v2) + " ->" + torGraph.getId(resultDouble.getRight().getSuccessor(v2)) + ", ");
                }

                for (JGraphTVertex v2 : resultDouble.getRight().getVertices()) {
                    System.out.print("Value of " + torGraph.getId(v2) + " ->" + resultDouble.getLeft().get(v2) + ", ");
                }

                System.out.println("In Int:");
                for (JGraphTVertex v2 : resultInt.getRight().getVertices()) {
                    System.out.print(torGraph.getId(v2) + " ->" + torGraph.getId(resultInt.getRight().getSuccessor(v2)) + ", ");
                }

                for (JGraphTVertex v2 : resultInt.getRight().getVertices()) {
                    System.out.print("Value of " + torGraph.getId(v2) + " ->" + resultInt.getLeft().get(v2) + ", ");
                }

                System.out.println("first: " + resultInt.getLeft().get(v).doubleValue());
                System.out.println("second: " + resultDouble.getLeft().get(v));
                System.out.printf("diff threshold %.12f, value vertex \"%.12f\n", DoubleFunctions.EPSILON, Math.abs(diff));
            }
        }
    }

    private RGDoubleImplJGraphT toDoubleGameGraph(RGIntImplJGraphT ratioGame) {
        DoubleWeightFunctionInt f = ratioGame.getEdgeWeights();
        DoubleWeightFunctionDouble weights = new DoubleWeightFunctionDouble();
        for (JGraphTEdge e : ratioGame.getEdges()) {
            weights.addWeight(e, f.getWeight1(e) * 1.0, f.getWeight2(e) * 1.0);
        }
        return new RGDoubleImplJGraphT(ratioGame.getGraph(), weights);
    }

    public static double MAX(double first, double... rest) {
        double ret = first;
        for (double val : rest) {
            ret = Math.max(ret, val);
        }
        return ret;
    }
}
