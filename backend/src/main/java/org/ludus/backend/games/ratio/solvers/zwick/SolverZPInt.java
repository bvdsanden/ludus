package org.ludus.backend.games.ratio.solvers.zwick;

import org.ludus.backend.datastructures.tuple.Triple;
import org.ludus.backend.datastructures.weights.SingleWeightFunctionInt;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.games.meanpayoff.MeanPayoffGame;
import org.ludus.backend.games.meanpayoff.solvers.zwick.ZPSolverInt;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.meanpayoff.MPGIntImplJGraphT;
import org.ludus.backend.graph.jgrapht.ratio.RGIntImplJGraphT;
import org.apache.commons.math3.fraction.Fraction;

import java.util.*;

/**
 * Solve ratio games by translation to mean-payoff games solved by
 * Zwick-Paterson.
 *
 * @author Bram van der Sanden
 */
public class SolverZPInt {

    /**
     * Compute the optimal strategy using the value algorithm and the group
     * testing technique.
     *
     * @param game ratio game
     * @return optimal strategy
     */
    public static StrategyVector<JGraphTVertex, JGraphTEdge> getOptimalStrategy(RGIntImplJGraphT game) {
        Map<JGraphTVertex, Fraction> values = getValues(game);

        StrategyVector<JGraphTVertex, JGraphTEdge> strategyVector = new StrategyVector<>();

        // Group testing technique.
        for (JGraphTVertex vertex : game.getVertices()) {
            Set<JGraphTEdge> outgoing = new HashSet<>(game.outgoingEdgesOf(vertex));
            JGraphTEdge edge = findOutgoingEdge(game, vertex, values.get(vertex), outgoing);
            strategyVector.setSuccessor(vertex, game.getEdgeTarget(edge));
        }
        return strategyVector;
    }

    private static JGraphTEdge findOutgoingEdge(
            RGIntImplJGraphT game, JGraphTVertex vertex,
            Fraction oldValue, Set<JGraphTEdge> outgoing) {
        if (outgoing.size() < 2) {
            return outgoing.iterator().next();
        } else {
            Double halve = Math.ceil(outgoing.size() / 2.0f);
            Integer leftHalveSize = halve.intValue();

            // Outgoing edges.
            Iterator<JGraphTEdge> outgoingEdgeIterator = outgoing.iterator();

            // Split this set of outgoing edges in two halves.
            Set<JGraphTEdge> outgoingLeftHalve = new HashSet<>();
            Set<JGraphTEdge> outgoingRightHalve = new HashSet<>();

            for (int i = 0; i < outgoing.size(); i++) {
                if (i < leftHalveSize) {
                    outgoingLeftHalve.add(outgoingEdgeIterator.next());
                } else {
                    outgoingRightHalve.add(outgoingEdgeIterator.next());
                }
            }

            // Construct subgraph by removing the edges in outgoingRightHalve.
            Set<JGraphTEdge> subGraphEdges = new HashSet<>(game.getEdges());
            subGraphEdges.removeAll(outgoingRightHalve);
            RGIntImplJGraphT subGraph = game.getSubGraphEdges(subGraphEdges);

            // Compute new value for the vertex.
            Map<JGraphTVertex, Fraction> newValues = getValues(subGraph);
            Fraction newValue = newValues.get(vertex);

            // Check whether the removed edges play a role.
            if (newValue.equals(oldValue)) {
                // There is a positional optimal strategy that does not use
                // any of the removed edges.
                return findOutgoingEdge(game, vertex, newValue, outgoingLeftHalve);
            } else {
                // On of the removed edges is part of the optimal strategy.
                return findOutgoingEdge(game, vertex, newValue, outgoingRightHalve);
            }
        }
    }

    /**
     * Get the optimal value for each vertex in the given game.
     *
     * @param game ratio game
     * @return a value map containing the optimal value for each vertex.
     */
    public static Map<JGraphTVertex, Fraction> getValues(RGIntImplJGraphT game) {
        Map<JGraphTVertex, Fraction> valueMap = new HashMap<>();

        Integer V = game.getVertices().size();
        Integer W = game.getMaxAbsValue();

        // Range of all possible vertex values.
        Fraction lowerBound = Fraction.ZERO;
        Fraction upperBound = new Fraction(V * W, 1);

        for (JGraphTVertex v : game.getVertices()) {
            // Calculate the exact value.
            Fraction value = getValue(game, v, V, W, lowerBound, upperBound);
            valueMap.put(v, value);
        }
        return valueMap;
    }

    /**
     * Find the value of a vertex by binary search within the range of possible
     * values.
     *
     * @param lowerBoundRatio minimum value
     * @param upperBoundRatio maximum value
     * @return fraction in range [lowerBoundRatio,upperBoundRatio]
     */
    private static Fraction getValue(RGIntImplJGraphT game, JGraphTVertex vertex,
                                     Integer V, Integer W, Fraction lowerBoundRatio, Fraction upperBoundRatio) {
        // Split the range in halve.
        Fraction middleFraction = lowerBoundRatio.add(upperBoundRatio).multiply(Fraction.ONE_HALF);
        Fraction middleMax = findMaxInRange(V, W, lowerBoundRatio, middleFraction);
        Fraction middleMin = findMinInRange(V, W, middleFraction, upperBoundRatio);

        // Three-way partition.
        MeanPayoffGame<JGraphTVertex, JGraphTEdge, Integer> mpg = convertToMeanPayoffGame(game, middleMax);
        Triple<Set<JGraphTVertex>, Set<JGraphTVertex>, Set<JGraphTVertex>> split
                = ZPSolverInt.getThreeWayPartition(mpg, Fraction.ZERO);

        if (split.getMiddle().contains(vertex)) {
            return middleMax;
        } else if (split.getLeft().contains(vertex)) {
            // Search in left interval.
            return getValue(game, vertex, V, W, lowerBoundRatio, middleMax);
        } else {
            return getValue(game, vertex, V, W, middleMin, upperBoundRatio);
        }
    }

    /**
     * Given the parameters vertexSize and maxWeight, iterate over the set of
     * all fractions, and find the maximum in the range [lowerBound,upperBound].
     *
     * @param vertexSize number of vertices in the graph
     * @param maxWeight maximum weight in the graph
     * @param lowerBound minimum value
     * @param upperBound maximum value
     * @return maximum value in range [lowerBound,upperBound]
     */
    private static Fraction findMaxInRange(Integer vertexSize,
                                           Integer maxWeight, Fraction lowerBound, Fraction upperBound) {
        // Minimum fraction in set S.
        Fraction min = lowerBound;
        // Current maximum value.
        Fraction max = min;
        for (int nom = 1; nom <= vertexSize * maxWeight; nom += 1) {
            for (int denom = vertexSize * maxWeight; denom >= 1; denom -= 1) {
                Fraction frac = new Fraction(nom, denom);
                // Check if within range.
                if (frac.compareTo(lowerBound) != -1 && frac.compareTo(upperBound) != 1) {
                    // frac > max.
                    if (frac.compareTo(max) > 0) {
                        // Larger fraction found, satisfying the range.
                        max = frac;
                    }
                }
            }
        }
        return max;
    }

    /**
     * Given the parameters vertexSize and maxWeight, iterate over the set of
     * all fractions, and find the minimum in the range [lowerBound,upperBound].
     *
     * @param vertexSize number of vertices in the graph
     * @param maxWeight maximum weight in the graph
     * @param lowerBound minimum value
     * @param upperBound maximum value
     * @return minimum value in range [lowerBound,upperBound]
     */
    private static Fraction findMinInRange(Integer vertexSize,
                                           Integer maxWeight, Fraction lowerBound, Fraction upperBound) {
        // Maximum fraction in set S.
        Fraction max = upperBound;
        // Current minimum value.
        Fraction min = max;
        for (int nom = 1; nom <= vertexSize * maxWeight; nom += 1) {
            for (int denom = vertexSize * maxWeight; denom >= 1; denom -= 1) {
                Fraction frac = new Fraction(nom, denom);
                // Check if within range.
                if (frac.compareTo(lowerBound) != -1 && frac.compareTo(upperBound) != 1) {
                    // frac > min.
                    if (frac.compareTo(min) < 0) {
                        // Smaller fraction found, satisfying the range.
                        min = frac;
                    }
                }
            }
        }
        return min;
    }

    /**
     * Convert the given ratio game to a mean-payoff game by converting the two
     * weights into a single weight.
     *
     * @param game game graph
     * @param value ratio value
     * @return new weight function where weight(e) = weight1(e) - value * weight2(e)
     */
    private static MeanPayoffGame<JGraphTVertex, JGraphTEdge, Integer>
    convertToMeanPayoffGame(RGIntImplJGraphT game, Fraction value) {
        JGraphTGraph graph = game.getGraph();

        SingleWeightFunctionInt<JGraphTEdge> wf = new SingleWeightFunctionInt<>();
        for (JGraphTEdge e : graph.getEdges()) {
            wf.addWeight(e, value.getDenominator() * game.getWeight1(e) - value.getNumerator() * game.getWeight2(e));
        }
        return new MPGIntImplJGraphT(graph, wf);
    }

}
