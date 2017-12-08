package org.ludus.backend.games.benchmarking;

import org.ludus.backend.games.benchmarking.generator.Tor;
import org.ludus.backend.graph.jgrapht.ratio.RGDoubleImplJGraphT;

import java.io.PrintWriter;

/**
 * @author Bram van der Sanden
 */
public class TorDoubleBenchmark extends Benchmark {

    private final String name;

    private final Integer sizeMin;
    private final Integer sizeMax;
    private final Integer stepSize;

    private final Double maxWeight1;
    private final Double maxWeight2;

    private PrintWriter file;

    public TorDoubleBenchmark(
            String name,
            Integer sizeMin,
            Integer sizeMax,
            Integer stepSize,
            Double maxWeight1,
            Double maxWeight2) {
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

        file.printf("%s,%s,%s,%s,%s,%s\n", "N", "maxWeight1", "maxWeight2", "PolicyIteration", "EnergyGame", "ZwickPaterson");
        System.out.printf("%s,%s,%s,%s,%s,%s\n", "N", "maxWeight1", "maxWeight2", "PolicyIteration", "EnergyGame", "ZwickPaterson");

        // Given the settings, run all tests.
        for (int runId = 0; runId < numberOfIterations; runId++) {
            for (int V = sizeMin; V <= sizeMax; V += stepSize) {
                runAlgorithmsTor(V, maxWeight1, maxWeight2,
                        runPI, runEG, runZP);
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
     * @param runPI      whether to run the policy iteration algorithm
     * @param runEG      whether to run the energy game algorithm
     * @param runZP      whether to run the Zwick-Paterson algorithm
     */
    private void runAlgorithmsTor(Integer size,
                                  Double maxWeight1, Double maxWeight2,
                                  boolean runPI, boolean runEG, boolean runZP) {
        RGDoubleImplJGraphT torGraph
                = Tor.generateRatioGame(size, maxWeight1, maxWeight2);

        // Solve using policy iteration.  
        float piSec = 0.0f;
        if (runPI) {
            long piResult = runPI(torGraph);
            piSec = ((piResult) / 1000000000.0f);
        }

        // Solve using energy games.
        float egSec = 0.0f;
        if (runEG) {
            long egResult = runEG(torGraph);
            egSec = ((egResult) / 1000000000.0f);
        }

        // Solve using Zwick-Paterson.
        float zpSec = 0.0f;
        if (runZP) {
            long zpResult = runZP(torGraph);
            zpSec = ((zpResult) / 1000000000.0f);
        }

        file.printf("%d,%f,%f,%f,%f,%f\n", size, maxWeight1, maxWeight2, piSec, egSec, zpSec);
        System.out.printf("%d,%f,%f,%f,%f,%f\n", size, maxWeight1, maxWeight2, piSec, egSec, zpSec);
    }

}
