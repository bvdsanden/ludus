package org.ludus.backend.games.ratio.solvers.energy;

import org.ludus.backend.datastructures.weights.SingleWeightFunctionDouble;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.games.algorithms.DoubleFunctions;
import org.ludus.backend.games.algorithms.GraphChecks;
import org.ludus.backend.games.energy.EnergyGame;
import org.ludus.backend.games.energy.solvers.SEPM;
import org.ludus.backend.games.energy.solvers.ValueIterationDouble;
import org.ludus.backend.graph.jgrapht.energy.EGDoubleImplJGraphT;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Value Iteration algorithm to calculate a small energy progress measure for a
 * given ratio game. This measure is calculated by using the value iteration
 * algorithm for energy games.
 *
 * @author Bram van der Sanden
 */
public class RatioGameValueIterationDouble {

    /**
     * Find the values of the vertices for the given ratio game.
     *
     * @param <V>  vertex type
     * @param <E>  edge type
     * @param game ratio game graph
     * @return value of each vertex
     */
    public static <V, E> Map<V, Double> solve(RatioGameEnergy<V, E, Double> game) {
        return solve(game, DoubleFunctions.EPSILON);
    }

    /**
     * Find the values of the vertices for the given ratio game.
     *
     * @param <V>     vertex type
     * @param <E>     edge type
     * @param game    ratio game graph
     * @param epsilon epsilon value for equality on real numbers
     * @return value of each vertex
     */
    public static <V, E> Map<V, Double> solve(RatioGameEnergy<V, E, Double> game, Double epsilon) {
        Map<V, Double> vertexMap = new HashMap<>();
        Double W = game.getMaxAbsValue();
        Double minusW = 0.0;
        StrategyVector<V, E> strategy = new StrategyVector<>();

        if (GraphChecks.checkEachNodeHasSuccessor(game)) {
            findValues(game, minusW, W, vertexMap, strategy, epsilon);
            return vertexMap;
        } else {
            System.out.println("Input game graph is not valid. "
                    + "Not every vertex has a successor.");
            return null;
        }
    }

    /**
     * Find the optimal value of each vertex in the ratio game.
     *
     * @param <V>        vertex type
     * @param <E>        edge type
     * @param game       ratio game graph
     * @param lowerBound lower bound on value interval
     * @param upperBound upper bound on value interval
     * @param valueMap   value map of vertices
     * @param strategy   current strategy vector
     * @param epsilon    epsilon value for equality on real numbers
     */
    private static <V, E> void findValues(RatioGameEnergy<V, E, Double> game,
                                          Double lowerBound, Double upperBound, Map<V, Double> valueMap, StrategyVector<V, E> strategy, Double epsilon) {
        // Divide the current range in two.
        double middle = (lowerBound + upperBound) / 2.0;

        if (DoubleFunctions.equalTo(upperBound, lowerBound, epsilon)) {
            // Update vertex values.
            for (V v : game.getVertices()) {
                valueMap.put(v, middle);
            }
        } else {
            // Determine (V>= middle, V<middle). 
            // V>= middle = {v | v in V && f1(v) != TOP}
            // V<  middle = {v | v in V && f1(v) = TOP}
            SingleWeightFunctionDouble<E> wf1
                    = convertToSingleWeightFunction(game, 1.0, -middle);
            EnergyGame<V, E, Double> game1 = new EGDoubleImplJGraphT<>(game, wf1);
            SEPM<V,Double> f1 = ValueIterationDouble.getProgressMeasure(game1);

            // Vertex set V_{<middle}.
            Set<V> VleftHalve = new HashSet<>();
            game.getVertices().stream().filter((v)
                    -> (f1.getValue(v).equals(ValueIterationDouble.TOP))).forEach(VleftHalve::add);

            // Vertex set V_{>=middle}.
            Set<V> VrightHalve = new HashSet<>();
            game.getVertices().stream().filter((v)
                    -> (!f1.getValue(v).equals(ValueIterationDouble.TOP))).forEach(VrightHalve::add);

            // Contains only vertices from set V_{<a1}.
            RatioGameEnergy subGameSmallerVertices = game.getSubGraph(VleftHalve);

            // Contains only vertices from set V_{>a2}.
            RatioGameEnergy subGameLargerVertices = game.getSubGraph(VrightHalve);

            // Recursive calls.
            findValues(subGameSmallerVertices, lowerBound, middle, valueMap, strategy, epsilon);
            findValues(subGameLargerVertices, middle, upperBound, valueMap, strategy, epsilon);
        }
    }

    /**
     * @param <V>        vertex type
     * @param <E>        edge type
     * @param game       game graph
     * @param constant_a multiplied by weight w1
     * @param constant_b multiplied by weight w2
     * @return new weight function where the weight of each edge e is computed as
     * constant_a * weight1(e) + constant_b * weight2(e)
     */
    private static <V, E> SingleWeightFunctionDouble<E>
    convertToSingleWeightFunction(RatioGameEnergy<V, E, Double> game,
                                  double constant_a, double constant_b) {
        // Create an explicit single weight function.
        SingleWeightFunctionDouble<E> newFunction = new SingleWeightFunctionDouble<>();
        // Add the entries.
        for (E edge : game.getEdges()) {
            double weight = constant_a * game.getWeight1(edge) + constant_b * game.getWeight2(edge);
            newFunction.addWeight(edge, weight);
        }
        return newFunction;
    }
}
