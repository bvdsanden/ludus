package org.ludus.backend.games.benchmarking.generator;

import org.ludus.backend.datastructures.weights.DoubleWeightFunctionDouble;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionInt;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.ratio.RGDoubleImplJGraphT;
import org.ludus.backend.graph.jgrapht.ratio.RGIntImplJGraphT;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

/**
 * Generate random game graphs similar to the SPRAND generator. See also (B. V.
 * Cherkassky, A. V. Goldberg, and T. Radzik. Shortest paths algorithms: Theory
 * and experimental evaluation. Mathematical Programming, 73:129–174, 1996.).
 *
 * @author Bram van der Sanden
 */
public class Sprand {

    /**
     * Construct a graph with a Hamiltonian cycle and add random edges to the
     * graph.
     *
     * @param numberOfVertices number of vertices
     * @param edgeRatio ratio of edges to vertices
     * @param maxWeight1 maximum weight1 value
     * @param maxWeight2 maximum weight2 value
     * @return a graph with a Hamiltonian cycle and meets the desired edge ratio
     */
    public static RGDoubleImplJGraphT generateRatioGame(Integer numberOfVertices,
                                                        Integer edgeRatio, Double maxWeight1, Double maxWeight2) {
        Random rand = new Random();

        Integer max1 = (int) Math.round(maxWeight1);
        Integer max2 = (int) Math.round(maxWeight2);

        // Number of edges.
        BigInteger numberOfEdges = BigInteger.valueOf(numberOfVertices).multiply(BigInteger.valueOf(edgeRatio));

        JGraphTGraph graph = new JGraphTGraph();
        DoubleWeightFunctionDouble<JGraphTEdge> weights = new DoubleWeightFunctionDouble<>();

        // Generate the vertices.
        ArrayList<JGraphTVertex> vertexList = new ArrayList<>();
        for (int i = 0; i < numberOfVertices; i++) {
            JGraphTVertex v = new JGraphTVertex(i);
            // Distribute nodes among the two players uniformly at random.
            boolean addToV0 = rand.nextBoolean();
            if (addToV0) {
                graph.addToV0(v);
            } else {
                graph.addToV1(v);
            }
            vertexList.add(v);
        }

        // Generate the Hamilton cycle.        
        for (int i = 0; i < vertexList.size(); i++) {
            JGraphTEdge edge = graph.addEdge(vertexList.get(i),
                    vertexList.get((i + 1) % (numberOfVertices)));
            Double weight1 = 1.0;
            Double weight2 = 1.0;
            weights.addWeight(edge, weight1, weight2);
        }

        // Generate the random edges.
        BigInteger V = BigInteger.valueOf(numberOfVertices);
        BigInteger hamiltonEdges = V.min(BigInteger.ONE);
        BigInteger maximalNumberOfEdges = V.multiply(hamiltonEdges);

        // Possible edges in graph.
        BigInteger possible = maximalNumberOfEdges.subtract(hamiltonEdges);
        // Allowed number of edges given factor x.V.
        BigInteger allowed = numberOfEdges.subtract(hamiltonEdges);

        BigInteger numberOfRandomEdges = possible.min(allowed);
        numberOfRandomEdges = numberOfRandomEdges.max(BigInteger.ZERO);

        BigInteger randomToAdd = numberOfRandomEdges;

        while (randomToAdd.compareTo(BigInteger.ZERO) > 0) {
            Integer sourceId = rand.nextInt(vertexList.size() - 1);
            Integer targetId = rand.nextInt(vertexList.size() - 1);

            JGraphTEdge edge = graph.addEdge(vertexList.get(sourceId),
                    vertexList.get(targetId));

            Double weight1 = 1.0 + (max1 - 1.0) * rand.nextDouble();
            Double weight2 = 1.0 + (max2 - 1.0) * rand.nextDouble();
            weights.addWeight(edge, weight1, weight2);
            randomToAdd = randomToAdd.subtract(BigInteger.ONE);
        }

        return new RGDoubleImplJGraphT(graph, weights);
    }

    /**
     * Construct a graph with a Hamiltonian cycle and add random edges to the
     * graph.
     *
     * @param numberOfVertices number of vertices
     * @param edgeRatio ratio of edges to vertices
     * @param maxWeight1 maximum weight1 value
     * @param maxWeight2 maximum weight2 value
     * @return a graph with a Hamiltonian cycle and meets the desired edge ratio
     */
    public static RGIntImplJGraphT generateRatioGame(Integer numberOfVertices,
                                                     Integer edgeRatio, Integer maxWeight1, Integer maxWeight2) {
        Random rand = new Random();

        // Number of edges.
        BigInteger numberOfEdges = BigInteger.valueOf(numberOfVertices).multiply(BigInteger.valueOf(edgeRatio));

        JGraphTGraph graph = new JGraphTGraph();
        DoubleWeightFunctionInt<JGraphTEdge> weights = new DoubleWeightFunctionInt<>();

        // Generate the vertices.
        ArrayList<JGraphTVertex> vertexList = new ArrayList<>();
        for (int i = 0; i < numberOfVertices; i++) {
            JGraphTVertex v = new JGraphTVertex(i);
            // Distribute nodes among the two players uniformly at random.
            boolean addToV0 = rand.nextBoolean();
            if (addToV0) {
                graph.addToV0(v);
            } else {
                graph.addToV1(v);
            }
            vertexList.add(v);
        }

        // Generate the Hamilton cycle.        
        for (int i = 0; i < vertexList.size(); i++) {
            JGraphTEdge edge = graph.addEdge(vertexList.get(i),
                    vertexList.get((i + 1) % (numberOfVertices)));
            Integer weight1 = 1;
            Integer weight2 = 1;
            weights.addWeight(edge, weight1, weight2);
        }

        // Generate the random edges.
        BigInteger V = BigInteger.valueOf(numberOfVertices);
        BigInteger hamiltonEdges = V.min(BigInteger.ONE);
        BigInteger maximalNumberOfEdges = V.multiply(hamiltonEdges);

        // Possible edges in graph.
        BigInteger possible = maximalNumberOfEdges.subtract(hamiltonEdges);
        // Allowed number of edges given factor x.V.
        BigInteger allowed = numberOfEdges.subtract(hamiltonEdges);

        BigInteger numberOfRandomEdges = possible.min(allowed);
        numberOfRandomEdges = numberOfRandomEdges.max(BigInteger.ZERO);

        BigInteger randomToAdd = numberOfRandomEdges;

        while (randomToAdd.compareTo(BigInteger.ZERO) > 0) {
            Integer sourceId = rand.nextInt(vertexList.size() - 1);
            Integer targetId = rand.nextInt(vertexList.size() - 1);

            JGraphTEdge edge = graph.addEdge(vertexList.get(sourceId),
                    vertexList.get(targetId));

            Integer weight1 = rand.nextInt(maxWeight1 - 1) + 1;
            Integer weight2 = rand.nextInt(maxWeight2 - 1) + 1;
            weights.addWeight(edge, weight1, weight2);
            randomToAdd = randomToAdd.subtract(BigInteger.ONE);
        }

        return new RGIntImplJGraphT(graph, weights);
    }
}
