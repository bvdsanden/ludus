package org.ludus.backend.games.benchmarking;

import org.ludus.backend.datastructures.weights.DoubleWeightFunctionDouble;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionInt;
import org.ludus.backend.games.benchmarking.generator.Sprand;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.ratio.RGDoubleImplJGraphT;
import org.ludus.backend.graph.jgrapht.ratio.RGIntImplJGraphT;

import java.io.PrintWriter;

/**
 * @author Bram van der Sanden
 */
public class SprandIntDoubleEncodingBenchmark extends Benchmark {

    private final String name;

    private final Integer numberOfVerticesMin;
    private final Integer numberOfVerticesMax;
    private final Integer stepSize;
    private final Integer edgeRatio;

    private final Integer maxWeight1;
    private final Integer maxWeight2;

    private PrintWriter file;

    public SprandIntDoubleEncodingBenchmark(
            String name,
            Integer numberOfVerticesMin,
            Integer numberOfVerticesMax,
            Integer stepSize,
            Integer edgeRatio,
            Integer maxWeight1,
            Integer maxWeight2) {
        this.name = name;
        this.numberOfVerticesMin = numberOfVerticesMin;
        this.numberOfVerticesMax = numberOfVerticesMax;
        this.stepSize = stepSize;
        this.edgeRatio = edgeRatio;
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

        file.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n", "N", "edgeRatio", "maxWeight1", "maxWeight2", "PolicyIterationN", "EnergyGameN", "ZwickPatersonN", "PolicyIterationR", "EnergyGameR", "ZwickPatersonR");
        System.out.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n", "N", "edgeRatio", "maxWeight1", "maxWeight2", "PolicyIterationN", "EnergyGameN", "ZwickPatersonN", "PolicyIterationR", "EnergyGameR", "ZwickPatersonR");

        // Given the settings, run all tests.
        for (int runId = 0; runId < numberOfIterations; runId++) {
            for (int V = numberOfVerticesMin; V <= numberOfVerticesMax; V += stepSize) {
                runAlgorithmsSprand(V, edgeRatio, maxWeight1, maxWeight2,
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
     * @param numberOfVertices number of vertices in the graph to be created
     * @param edgeRatio        ratio of edges to vertices
     * @param maxWeight1       maximum edge weight for weight 1
     * @param maxWeight2       maximum edge weight for weight 2
     * @param runPI            whether to run the policy iteration algorithm
     * @param runEG            whether to run the energy game algorithm
     * @param runZP            whether to run the Zwick-Paterson algorithm
     */
    private void runAlgorithmsSprand(Integer numberOfVertices, Integer edgeRatio,
                                     Integer maxWeight1, Integer maxWeight2,
                                     boolean runPI, boolean runEG, boolean runZP) {
        RGIntImplJGraphT sprandGraph
                = Sprand.generateRatioGame(numberOfVertices, edgeRatio, maxWeight1, maxWeight2);


        // Solve using integer algorithms.
        float piSec = 0.0f;
        if (runPI) {
            long piResult = runPI(sprandGraph);
            piSec = ((piResult) / 1000000000.0f);
        }
        float egSec = 0.0f;
        if (runEG) {
            long egResult = runEG(sprandGraph);
            egSec = ((egResult) / 1000000000.0f);
        }
        float zpSec = 0.0f;
        if (runZP) {
            long zpResult = runZP(sprandGraph);
            zpSec = ((zpResult) / 1000000000.0f);
        }


        RGDoubleImplJGraphT sprandDoubleGraph = toDoubleGameGraph(sprandGraph);

        // Solve using double algorithms.

        // Determine epsilon. Take into account maximal reachable precision.
        long vertexNum = (long) numberOfVertices;
        long numberOfEdges = vertexNum * vertexNum;
        Double epsilon = 1.0 / numberOfEdges;
        epsilon = Double.max(10E-14, epsilon);
        float piSecD = 0.0f;
        if (runPI) {
            long piResult = runPI(sprandDoubleGraph, epsilon);
            piSecD = ((piResult) / 1000000000.0f);
        }
        float egSecD = 0.0f;
        if (runEG) {
            long egResult = runEG(sprandDoubleGraph, epsilon);
            egSecD = ((egResult) / 1000000000.0f);
        }
        float zpSecD = 0.0f;
        if (runZP) {
            long zpResult = runZP(sprandDoubleGraph, epsilon);
            zpSecD = ((zpResult) / 1000000000.0f);
        }

        file.printf("%d,%d,%d,%d,%f,%f,%f,%f,%f,%f\n", numberOfVertices, edgeRatio, maxWeight1, maxWeight2, piSec, egSec, zpSec, piSecD, egSecD, zpSecD);
        System.out.printf("%d,%d,%d,%d,%f,%f,%f,%f,%f,%f\n", numberOfVertices, edgeRatio, maxWeight1, maxWeight2, piSec, egSec, zpSec, piSecD, egSecD, zpSecD);
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
