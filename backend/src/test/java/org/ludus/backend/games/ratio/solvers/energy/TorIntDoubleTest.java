package org.ludus.backend.games.ratio.solvers.energy;

import org.apache.commons.math3.fraction.Fraction;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionDouble;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionInt;
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
public class TorIntDoubleTest {

    @Test
    public void runTest() {
        int size = 6;
        int maxWeight1 = 20;
        int maxWeight2 = 20;

        // 1. Integer graph.
        RGIntImplJGraphT torGraph
                = Tor.generateRatioGame(size, maxWeight1, maxWeight2);

        // Run the integer algorithm.
        long start = System.nanoTime();
        Map<JGraphTVertex, Fraction> resultInt = RatioGameValueIterationInt.solve(torGraph);
        long end = System.nanoTime();
        float timeInt = (end - start) / 1000000000.0f;
        System.out.println("Time for integers: " + timeInt);

        // 2. Convert integers to doubles.
        RGDoubleImplJGraphT torDoubleGraph = toDoubleGameGraph(torGraph);

        // Run the double algorithm.        
        long s = (long) size;
        long cubic = s * s * s * s;
        Double epsilon = 1.0 / (1.0 * cubic);
        epsilon = Double.max(10E-14, epsilon);

        long start2 = System.nanoTime();
        Map<JGraphTVertex, Double> resultDouble = RatioGameValueIterationDouble.solve(torDoubleGraph, epsilon);
        long end2 = System.nanoTime();
        float timeDouble = (end2 - start2) / 1000000000.0f;
        System.out.println("Time for doubles: " + timeDouble);

        for (JGraphTVertex v : torGraph.getVertices()) {
            System.out.println("(int,double,diff): " + resultInt.get(v).floatValue() + ", " + resultDouble.get(v).floatValue() + " , " + (resultInt.get(v).floatValue() - resultDouble.get(v).floatValue()));
        }


    }

    private RGDoubleImplJGraphT toDoubleGameGraph(RGIntImplJGraphT ratioGame) {
        DoubleWeightFunctionInt<JGraphTEdge> f = ratioGame.getEdgeWeights();
        DoubleWeightFunctionDouble<JGraphTEdge> weights = new DoubleWeightFunctionDouble<>();
        for (JGraphTEdge e : ratioGame.getEdges()) {
            weights.addWeight(e, f.getWeight1(e) * 1.0, f.getWeight2(e) * 1.0);
        }
        return new RGDoubleImplJGraphT(ratioGame.getGraph(), weights);
    }
}
