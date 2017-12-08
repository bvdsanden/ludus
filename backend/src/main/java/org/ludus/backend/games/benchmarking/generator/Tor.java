package org.ludus.backend.games.benchmarking.generator;

import org.ludus.backend.datastructures.weights.DoubleWeightFunctionDouble;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionInt;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.ratio.RGDoubleImplJGraphT;
import org.ludus.backend.graph.jgrapht.ratio.RGIntImplJGraphT;

import java.util.Random;

/**
 * Generate random game graphs similar to the TOR generator. See also (B. V.
 * Cherkassky, A. V. Goldberg, and T. Radzik. Shortest paths algorithms: Theory
 * and experimental evaluation. Mathematical Programming, 73:129–174, 1996.).
 * <p>
 * * @author Bram van der Sanden
 */
public class Tor {

    /**
     * Construct a square grid. Each vertex has an upward edge and a rightward
     * edge, with wrap-around.
     *
     * @param size       width and height of the graph. Total number of nodes is
     *                   size^2.
     * @param maxWeight1 maximum weight1 value
     * @param maxWeight2 maximum weight2 value
     * @return Tor game graph of size^2 with weights in range [0,maxWeight1] and [0,maxWeight2]
     */
    public static RGDoubleImplJGraphT generateRatioGame(Integer size, Double maxWeight1, Double maxWeight2) {
        Random rand = new Random();

        Integer max1 = (int) Math.round(maxWeight1);
        Integer max2 = (int) Math.round(maxWeight2);

        // Initialize graph.
        JGraphTGraph graph = new JGraphTGraph();
        DoubleWeightFunctionDouble<JGraphTEdge> weights = new DoubleWeightFunctionDouble<>();

        // Generate the vertices.
        JGraphTVertex[][] grid = new JGraphTVertex[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                JGraphTVertex v = new JGraphTVertex(i * size + j);
                // Distribute nodes among the two players uniformly at random.
                boolean addToV0 = rand.nextBoolean();
                if (addToV0) {
                    graph.addToV0(v);
                } else {
                    graph.addToV1(v);
                }
                grid[i][j] = v;
            }
        }

        // Generate the edges.
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // Edge rightwards.
                JGraphTEdge rightEdge = graph.addEdge(grid[i][j], grid[i][(j + 1) % size]);
                Double weight1 = 1.0 + (max1 - 1.0) * rand.nextDouble();
                Double weight2 = 1.0 + (max2 - 1.0) * rand.nextDouble();
                weights.addWeight(rightEdge, weight1, weight2);
                // Edge upwards.
                JGraphTEdge upEdge = graph.addEdge(grid[i][j], grid[mod(i - 1, size)][j]);
                weight1 = 1.0 + (max1 - 1.0) * rand.nextDouble();
                weight2 = 1.0 + (max2 - 1.0) * rand.nextDouble();
                weights.addWeight(upEdge, weight1, weight2);
            }
        }

        return new RGDoubleImplJGraphT(graph, weights);
    }


    /**
     * Construct a square grid. Each vertex has an upward edge and a rightward
     * edge, with wrap-around.
     *
     * @param size       width and height of the graph. Total number of nodes is
     *                   size^2.
     * @param maxWeight1 maximum weight1 value
     * @param maxWeight2 maximum weight2 value
     * @return Tor game graph of size^2 with weights in range [0,maxWeight1] and [0,maxWeight2]
     */
    public static RGIntImplJGraphT generateRatioGame(Integer size, Integer maxWeight1, Integer maxWeight2) {
        Random rand = new Random();

        // Initialize graph.
        JGraphTGraph graph = new JGraphTGraph();
        DoubleWeightFunctionInt<JGraphTEdge> weights = new DoubleWeightFunctionInt<>();

        // Generate the vertices.
        JGraphTVertex[][] grid = new JGraphTVertex[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                JGraphTVertex v = new JGraphTVertex(i * size + j);
                // Distribute nodes among the two players uniformly at random.
                boolean addToV0 = rand.nextBoolean();
                if (addToV0) {
                    graph.addToV0(v);
                } else {
                    graph.addToV1(v);
                }
                grid[i][j] = v;
            }
        }

        // Generate the edges.
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // Edge rightwards.
                JGraphTEdge rightEdge = graph.addEdge(grid[i][j], grid[i][(j + 1) % size]);
                Integer w1 = rand.nextInt(maxWeight1 - 1) + 1;
                Integer w2 = rand.nextInt(maxWeight2 - 1) + 1;
                weights.addWeight(rightEdge, w1, w2);
                // Edge upwards.
                JGraphTEdge upEdge = graph.addEdge(grid[i][j], grid[mod(i - 1, size)][j]);
                w1 = rand.nextInt(maxWeight1 - 1) + 1;
                w2 = rand.nextInt(maxWeight2 - 1) + 1;
                weights.addWeight(upEdge, w1, w2);
            }
        }

        return new RGIntImplJGraphT(graph, weights);
    }

    /**
     * Construct a square grid. Each vertex has an upward edge and a rightward
     * edge, with wrap-around.
     *
     * @param size       width and height of the graph. Total number of nodes is
     *                   size^2.
     * @param maxWeight1 maximum weight1 value
     * @param maxWeight2 maximum weight2 value
     * @return Tor game graph of size^2 with weights in range [0,maxWeight1] and [0,maxWeight2]
     */
    public static RGDoubleImplJGraphT generateRatioGameDouble(Integer size, Integer maxWeight1, Integer maxWeight2) {
        Random rand = new Random();

        // Initialize graph.
        JGraphTGraph graph = new JGraphTGraph();
        DoubleWeightFunctionDouble<JGraphTEdge> weights = new DoubleWeightFunctionDouble<>();

        // Generate the vertices.
        JGraphTVertex[][] grid = new JGraphTVertex[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                JGraphTVertex v = new JGraphTVertex(i * size + j);
                // Distribute nodes among the two players uniformly at random.
                boolean addToV0 = rand.nextBoolean();
                if (addToV0) {
                    graph.addToV0(v);
                } else {
                    graph.addToV1(v);
                }
                grid[i][j] = v;
            }
        }

        // Generate the edges.
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // Edge rightwards.
                JGraphTEdge rightEdge = graph.addEdge(grid[i][j], grid[i][(j + 1) % size]);
                Integer w1 = rand.nextInt(maxWeight1 - 1) + 1;
                Integer w2 = rand.nextInt(maxWeight2 - 1) + 1;
                weights.addWeight(rightEdge, w1 * 1.0, w2 * 1.0);
                // Edge upwards.
                JGraphTEdge upEdge = graph.addEdge(grid[i][j], grid[mod(i - 1, size)][j]);
                w1 = rand.nextInt(maxWeight1 - 1) + 1;
                w2 = rand.nextInt(maxWeight2 - 1) + 1;
                weights.addWeight(upEdge, w1 * 1.0, w2 * 1.0);
            }
        }

        return new RGDoubleImplJGraphT(graph, weights);
    }

    /**
     * Modulo operator that always returns a positive value.
     * By default Java returns a negative value if the dividend is negative.
     *
     * @param x input number
     * @param n modulus
     * @return x mod n that satisfies >= 0
     */
    private static int mod(int x, int n) {
        int r = x % n;
        if (r < 0) {
            r += n;
        }
        return r;
    }

}
