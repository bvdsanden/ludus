package org.ludus.backend.games.benchmarking;

import org.ludus.backend.games.benchmarking.generator.Tor;
import org.ludus.backend.graph.jgrapht.ratio.RGDoubleImplJGraphT;

import java.io.PrintWriter;

/**
 * @author Bram van der Sanden
 */
public class TorIntDoubleEncodingBenchmark extends Benchmark {

    private final String name;

    private final Integer sizeMin;
    private final Integer sizeMax;
    private final Integer stepSize;

    private final Integer maxWeight1;
    private final Integer maxWeight2;

    private PrintWriter file;

    public TorIntDoubleEncodingBenchmark(
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

        file.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s\n", "N", "maxWeight1", "maxWeight2", "PolicyIterationN", "EnergyGameN", "ZwickPatersonN", "PolicyIterationR", "EnergyGameR", "ZwickPatersonR");
        System.out.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s\n", "N", "maxWeight1", "maxWeight2", "PolicyIterationN", "EnergyGameN", "ZwickPatersonN", "PolicyIterationR", "EnergyGameR", "ZwickPatersonR");

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
                                  Integer maxWeight1, Integer maxWeight2,
                                  boolean runPI, boolean runEG, boolean runZP) {
        RGDoubleImplJGraphT torGraph
                = Tor.generateRatioGameDouble(size, maxWeight1, maxWeight2);

        // Solve using integer algorithms.
        float piSec = 0.0f;
        if (runPI) {
            long piResult = runPI(torGraph);
            piSec = ((piResult) / 1000000000.0f);
        }
        float egSec = 0.0f;
        if (runEG) {
            long egResult = runEG(torGraph);
            egSec = ((egResult) / 1000000000.0f);
        }
        float zpSec = 0.0f;
        if (runZP) {
            long zpResult = runZP(torGraph);
            zpSec = ((zpResult) / 1000000000.0f);
        }

        // Solve using double algorithms.
        // Take into account maximum reachable precision.
        long s = (long) size;
        long cubic = s * s * s * s;
        Double epsilon = 1.0 / (1.0 * cubic);
        epsilon = Double.max(10E-14, epsilon);

        float piSecD = 0.0f;
        if (runPI) {
            long piResult = runPI(torGraph, epsilon);
            piSecD = ((piResult) / 1000000000.0f);
        }
        float egSecD = 0.0f;
        if (runEG) {
            long egResult = runEG(torGraph, epsilon);
            egSecD = ((egResult) / 1000000000.0f);
        }
        float zpSecD = 0.0f;
        if (runZP) {
            long zpResult = runZP(torGraph, epsilon);
            zpSecD = ((zpResult) / 1000000000.0f);
        }

        file.printf("%d,%d,%d,%f,%f,%f,%f,%f,%f\n", size, maxWeight1, maxWeight2, piSec, egSec, zpSec, piSecD, egSecD, zpSecD);
        System.out.printf("%d,%d,%d,%f,%f,%f,%f,%f,%f\n", size, maxWeight1, maxWeight2, piSec, egSec, zpSec, piSecD, egSecD, zpSecD);
    }

}
