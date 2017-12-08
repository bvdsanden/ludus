package org.ludus.backend.games.benchmarking;

import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionDouble;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionInt;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.games.benchmarking.generator.Tor;
import org.ludus.backend.games.ratio.solvers.policy.PolicyIterationDouble;
import org.ludus.backend.games.ratio.solvers.policy.PolicyIterationInt;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.ratio.RGDoubleImplJGraphT;
import org.ludus.backend.graph.jgrapht.ratio.RGIntImplJGraphT;
import org.apache.commons.math3.fraction.Fraction;

import java.io.PrintWriter;
import java.util.Map;

/**
 * @author Bram van der Sanden
 */
public class TorEpsBenchmark extends Benchmark {

    private final String name;

    private final Integer sizeMin;
    private final Integer sizeMax;
    private final Integer stepSize;

    private final Integer maxWeight1;
    private final Integer maxWeight2;

    private PrintWriter file;

    public TorEpsBenchmark(
            String name,
            Integer sizeMin,
            Integer sizeMax,
            Integer stepSize,
            Integer maxWeight1,
            Integer maxWeight2) {
        this.name = name;
        this.sizeMin = sizeMin;
        this.sizeMax = sizeMax;
        this.stepSize = stepSize;
        this.maxWeight1 = maxWeight1;
        this.maxWeight2 = maxWeight2;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void run(Integer numberOfIterations,
                    boolean runPI, boolean runEG, boolean runZP) {
        // Create a new file.
        file = getFile(name);

        file.printf("%s,%s,%s,%s,%s\n", "N", "maxWeight1", "maxWeight2", "PolicyIterationN", "PolicyIterationR");
        System.out.printf("%s,%s,%s,%s,%s\n", "N", "maxWeight1", "maxWeight2", "PolicyIterationN", "PolicyIterationR");

        // Given the settings, run all tests.
        for (int runId = 0; runId < numberOfIterations; runId++) {
            for (int V = sizeMin; V <= sizeMax; V += stepSize) {
                runAlgorithmsTor(V, maxWeight1, maxWeight2);
            }
        }

        // Close the file for the benchmark.
        file.close();
    }


    /**
     * Create a test case corresponding to the settings, and run it using the
     * algorithms that are enabled.
     *
     * @param size       number of vertices in the graph is size * size
     * @param maxWeight1 maximum edge weight for weight 1
     * @param maxWeight2 maximum edge weight for weight 2
     */
    private void runAlgorithmsTor(Integer size,
                                  Integer maxWeight1, Integer maxWeight2) {
        RGIntImplJGraphT torGraph
                = Tor.generateRatioGame(size, maxWeight1, maxWeight2);

        // Solve using integer algorithms.
        long start = System.nanoTime();
        Tuple<Map<JGraphTVertex, Fraction>,
                StrategyVector<JGraphTVertex, JGraphTEdge>> intResult
                = PolicyIterationInt.solve(torGraph);
        long end = System.nanoTime();
        float piSec = ((end - start) / 1000000000.0f);

        // Solve using double algorithms.
        // Take into account maximum reachable precision.        
        RGDoubleImplJGraphT torGraphD = toDoubleGameGraph(torGraph);

        Integer V = torGraphD.getVertices().size();
        Double epsilon = 10E-10;
        Double delta = 2 * (V - 1) * torGraphD.getMaxAbsValue() * epsilon;

        start = System.nanoTime();
        Tuple<Map<JGraphTVertex, Double>,
                StrategyVector<JGraphTVertex, JGraphTEdge>> doubleResult
                = PolicyIterationDouble.solve(torGraphD, epsilon);
        end = System.nanoTime();
        float piSecD = ((end - start) / 1000000000.0f);

        for (JGraphTVertex v : torGraphD.getVertices()) {
            Fraction v_fraction = intResult.getLeft().get(v);
            Double v_double = doubleResult.getLeft().get(v);
            Double diff = Math.abs(v_fraction.doubleValue() - v_double);
            if (diff > delta) {
                file.printf("Difference of %f\n", diff);
                System.out.println("Difference of " + diff);
            }
        }

        file.printf("%d,%d,%d,%f,%f\n", size, maxWeight1, maxWeight2, piSec, piSecD);
        System.out.printf("%d,%d,%d,%f,%f\n", size, maxWeight1, maxWeight2, piSec, piSecD);
    }

    private RGDoubleImplJGraphT toDoubleGameGraph(RGIntImplJGraphT ratioGame) {
        DoubleWeightFunctionInt f = ratioGame.getEdgeWeights();
        DoubleWeightFunctionDouble weights = new DoubleWeightFunctionDouble();
        for (JGraphTEdge e : ratioGame.getEdges()) {
            weights.addWeight(e, f.getWeight1(e) * 1.0, f.getWeight2(e) * 1.0);
        }
        return new RGDoubleImplJGraphT(ratioGame.getGraph(), weights);
    }

}
