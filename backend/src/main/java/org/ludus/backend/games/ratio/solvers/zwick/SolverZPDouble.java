package org.ludus.backend.games.ratio.solvers.zwick;

import org.ludus.backend.datastructures.tuple.Triple;
import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.datastructures.weights.SingleWeightFunctionDouble;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.games.algorithms.DoubleFunctions;
import org.ludus.backend.games.meanpayoff.MeanPayoffGame;
import org.ludus.backend.games.meanpayoff.solvers.zwick.ZPSolverDouble;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.meanpayoff.MPGDoubleImplJGraphT;
import org.ludus.backend.graph.jgrapht.ratio.RGDoubleImplJGraphT;

import java.util.*;

/**
 * Solve ratio games by translation to mean-payoff games solved by
 * Zwick-Paterson.
 *
 * @author Bram van der Sanden
 */
public class SolverZPDouble {

    /**
     * Compute the optimal strategy using the value algorithm and the group
     * testing technique.
     *
     * @param game ratio game
     * @return strategy vector
     */
    public static Tuple<Map<JGraphTVertex, Double>, StrategyVector<JGraphTVertex, JGraphTEdge>> solve(RGDoubleImplJGraphT game) {
        return solve(game, DoubleFunctions.EPSILON);
    }

    /**
     * Compute the optimal strategy using the value algorithm and the group
     * testing technique.
     *
     * @param game    ratio game
     * @param epsilon epsilon value for equality on real numbers
     * @return strategy vector
     */
    public static Tuple<Map<JGraphTVertex, Double>, StrategyVector<JGraphTVertex, JGraphTEdge>> solve(RGDoubleImplJGraphT game, Double epsilon) {
        Map<JGraphTVertex, Double> values = getValues(game, epsilon);

        StrategyVector<JGraphTVertex, JGraphTEdge> strategyVector = new StrategyVector<>();

        // Group testing technique.
        for (JGraphTVertex vertex : game.getVertices()) {
            Set<JGraphTEdge> outgoing = new HashSet<>(game.outgoingEdgesOf(vertex));
            JGraphTEdge edge = findOutgoingEdge(game, vertex, values.get(vertex), outgoing, epsilon);
            strategyVector.setSuccessor(vertex, game.getEdgeTarget(edge));
        }
        return Tuple.of(values, strategyVector);
    }

    /**
     * Get the optimal value for each vertex in the given game.
     *
     * @param game ratio game
     * @return a value map containing the optimal value for each vertex.
     */
    public static Map<JGraphTVertex, Double> getValues(RGDoubleImplJGraphT game) {
        return getValues(game, DoubleFunctions.EPSILON);
    }

    /**
     * Get the optimal value for each vertex in the given game.
     *
     * @param game    ratio game
     * @param epsilon absolute error bound between true value and calculated value for each vertex
     * @return a value map containing the optimal value for each vertex.
     */
    public static Map<JGraphTVertex, Double> getValues(RGDoubleImplJGraphT game, Double epsilon) {
        Map<JGraphTVertex, Double> valueMap = new HashMap<>();

        Integer V = game.getVertices().size();
        Double W = game.getMaxAbsValue();

        // Range of all possible vertex values.
        Double lowerBound = 0.0;
        Double upperBound = V * W * 1.0;

        for (JGraphTVertex v : game.getVertices()) {
            // Calculate the exact value.
            Double value = getValue(game, v, lowerBound, upperBound, epsilon);
            valueMap.put(v, value);
        }
        return valueMap;
    }

    private static JGraphTEdge findOutgoingEdge(
            RGDoubleImplJGraphT game, JGraphTVertex vertex,
            Double oldValue, Set<JGraphTEdge> outgoing, Double epsilon) {
        if (outgoing.size() < 2) {
            return outgoing.iterator().next();
        } else {
            Double halve = Math.ceil(outgoing.size() / 2.0);
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
            RGDoubleImplJGraphT subGraph = game.getSubGraphEdges(subGraphEdges);

            // Compute new value for the vertex.
            Map<JGraphTVertex, Double> newValues = getValues(subGraph, epsilon);
            Double newValue = newValues.get(vertex);

            // Check whether the removed edges play a role.
            if (DoubleFunctions.equalTo(newValue, oldValue, epsilon)) {
                // There is a positional optimal strategy that does not use
                // any of the removed edges.
                return findOutgoingEdge(game, vertex, newValue, outgoingLeftHalve, epsilon);
            } else {
                // On of the removed edges is part of the optimal strategy.
                return findOutgoingEdge(game, vertex, newValue, outgoingRightHalve, epsilon);
            }
        }
    }

    /**
     * Find the value of a vertex by binary search within the range of possible
     * values.
     *
     * @param lowerBoundRatio minimum value
     * @param upperBoundRatio maximum value
     * @return double in range [lowerBoundRatio,upperBoundRatio]
     */
    private static Double getValue(RGDoubleImplJGraphT game, JGraphTVertex vertex,
                                   Double lowerBoundRatio, Double upperBoundRatio, Double epsilon) {
        // Split the range in halve.
        Double middle = (lowerBoundRatio + upperBoundRatio) / 2.0;

        // Three-way partition.
        MeanPayoffGame<JGraphTVertex, JGraphTEdge, Double> mpg = convertToMeanPayoffGame(game, middle);
        Triple<Set<JGraphTVertex>, Set<JGraphTVertex>, Set<JGraphTVertex>> split
                = ZPSolverDouble.getThreeWayPartition(mpg, 0.0, epsilon);

        if (split.getMiddle().contains(vertex)) {
            return middle;
        } else if (split.getLeft().contains(vertex)) {
            // Search in left interval.
            return getValue(game, vertex, lowerBoundRatio, middle, epsilon);
        } else {
            return getValue(game, vertex, middle, upperBoundRatio, epsilon);
        }
    }

    /**
     * Convert the given ratio game to a mean-payoff game by converting the two
     * weights into a single weight.
     *
     * @param game game graph
     * @param value multiplication factor for weight2
     * @return new weight function where weight(e) = weight1(e) - value * weight2(e)
     */
    private static MeanPayoffGame<JGraphTVertex, JGraphTEdge, Double>
    convertToMeanPayoffGame(RGDoubleImplJGraphT game, Double value) {
        JGraphTGraph graph = game.getGraph();

        SingleWeightFunctionDouble<JGraphTEdge> wf = new SingleWeightFunctionDouble<>();
        for (JGraphTEdge e : graph.getEdges()) {
            wf.addWeight(e, 1.0 * game.getWeight1(e) - value * game.getWeight2(e));
        }
        return new MPGDoubleImplJGraphT(graph, wf);
    }
}
