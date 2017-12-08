package org.ludus.backend.games.benchmarking;

import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionDouble;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionInt;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.games.benchmarking.generator.Tor;
import org.ludus.backend.games.ratio.solvers.policy.PolicyIterationDoubleVars;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.ratio.RGDoubleImplJGraphT;
import org.ludus.backend.graph.jgrapht.ratio.RGIntImplJGraphT;

import java.io.PrintWriter;
import java.util.Map;

/**
 * @author Bram van der Sanden
 */
public class TorEpsDeltaFuzzyBenchmark extends Benchmark {

    private final String name;

    private final Integer sizeMin;
    private final Integer sizeMax;
    private final Integer stepSize;

    private final Integer maxWeight1;
    private final Integer maxWeight2;

    private PrintWriter file;

    public TorEpsDeltaFuzzyBenchmark(
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

        // Solve using double algorithms.
        // Take into account maximum reachable precision.        
        RGDoubleImplJGraphT torGraphD = toDoubleGameGraph(torGraph);

        Integer V = torGraphD.getVertices().size();
        Double epsilon = 10E-10;
        Double delta = (V - 1) * torGraphD.getMaxAbsValue() * epsilon;

        long start, end;
        start = System.nanoTime();
        Tuple<Map<JGraphTVertex, Double>,
                StrategyVector<JGraphTVertex, JGraphTEdge>> doubleResult
                = PolicyIterationDoubleVars.solve(torGraphD, epsilon, delta);
        end = System.nanoTime();
        float piSecD = ((end - start) / 1000000000.0f);

        file.printf("%d,%d,%d,%f\n", size, maxWeight1, maxWeight2, piSecD);
        System.out.printf("%d,%d,%d,%f\n", size, maxWeight1, maxWeight2, piSecD);
    }

    private RGDoubleImplJGraphT toDoubleGameGraph(RGIntImplJGraphT ratioGame) {
        DoubleWeightFunctionInt<JGraphTEdge> f = ratioGame.getEdgeWeights();
        DoubleWeightFunctionDouble<JGraphTEdge> weights = new DoubleWeightFunctionDouble<>();
        for (JGraphTEdge e : ratioGame.getEdges()) {
            weights.addWeight(e, f.getWeight1(e) * 0.62, f.getWeight2(e) * 0.34);
        }
        return new RGDoubleImplJGraphT(ratioGame.getGraph(), weights);
    }

}
