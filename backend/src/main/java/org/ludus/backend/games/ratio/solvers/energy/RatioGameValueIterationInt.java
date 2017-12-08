package org.ludus.backend.games.ratio.solvers.energy;

import org.apache.commons.math3.fraction.Fraction;
import org.ludus.backend.datastructures.weights.SingleWeightFunctionInt;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.games.algorithms.GraphChecks;
import org.ludus.backend.games.energy.EnergyGame;
import org.ludus.backend.games.energy.solvers.SEPM;
import org.ludus.backend.games.energy.solvers.ValueIterationInt;
import org.ludus.backend.graph.jgrapht.energy.EGIntImplJGraphT;

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
public class RatioGameValueIterationInt {

    /**
     * Get an small energy progress measure with the optimal value of each
     * vertex in the game.
     *
     * @param <V>  vertex type
     * @param <E>  edge type
     * @param game ratio game graph
     * @return small energy progress measure with the optimal value of each
     * vertex in the game
     */
    public static <V, E> Map<V, Fraction> solve(RatioGameEnergy<V, E, Integer> game) {
        Map<V, Fraction> vertexMap = new HashMap<>();
        Fraction W = new Fraction(game.getMaxAbsValue());
        Fraction minusW = new Fraction(0);
        StrategyVector<V, E> strategy = new StrategyVector<>();

        if (GraphChecks.checkEachNodeHasSuccessor(game)) {
            findValues(game, minusW, W, vertexMap, strategy);
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
     */
    private static <V, E> void findValues(RatioGameEnergy<V, E, Integer> game,
                                          Fraction lowerBound, Fraction upperBound, Map<V, Fraction> valueMap, StrategyVector<V, E> strategy) {
        if (!game.getVertices().isEmpty()) {

            // Divide the current range in two.
            Fraction middle = lowerBound.add(upperBound).
                    multiply(Fraction.ONE_HALF);

            Integer V = game.getVertices().size();
            Integer W = game.getMaxAbsValue();
            Fraction a1 = findMaxInRange(V, W, lowerBound, middle);
            Fraction a2 = findMinInRange(V, W, middle, upperBound);
            //System.out.println("New subranges: [" + lowerBound + ", " + a1 + "], and [" + a2 + ", " + upperBound + "].");

            Integer q1 = a1.getNumerator();
            Integer l1 = a1.getDenominator();
            Integer q2 = a2.getNumerator();
            Integer l2 = a2.getDenominator();

            // Create game graph with vertices swapped.
            RatioGameEnergy<V, E, Integer> gameSwapped = game.getSwappedSubGraph(game.getVertices());

            // Determine SEPMs.
            SingleWeightFunctionInt<E> wf1
                    = convertToSingleWeightFunction(game, l1, -q1);
            EnergyGame<V, E, Integer> game1 = new EGIntImplJGraphT<>(game, wf1);
            SEPM<V, Integer> f1 = ValueIterationInt.getProgressMeasure(game1);

            SingleWeightFunctionInt<E> wf2
                    = convertToSingleWeightFunction(game, -l1, q1);
            EnergyGame<V, E, Integer> game2 = new EGIntImplJGraphT<>(gameSwapped, wf2);
            SEPM<V, Integer> f2 = ValueIterationInt.getProgressMeasure(game2);

            SingleWeightFunctionInt<E> wf3
                    = convertToSingleWeightFunction(game, l2, -q2);
            EnergyGame<V, E, Integer> game3 = new EGIntImplJGraphT<>(game, wf3);
            SEPM<V, Integer> f3 = ValueIterationInt.getProgressMeasure(game3);

            SingleWeightFunctionInt<E> wf4
                    = convertToSingleWeightFunction(game, -l2, q2);
            EnergyGame<V, E, Integer> game4 = new EGIntImplJGraphT<>(gameSwapped, wf4);
            SEPM<V, Integer> f4 = ValueIterationInt.getProgressMeasure(game4);

            // Update vertex values.
            for (V v : game.getVertices()) {
                if (!f1.getValue(v).equals(ValueIterationInt.TOP)
                        && !f2.getValue(v).equals(ValueIterationInt.TOP)) {
                    valueMap.put(v, a1);
                }

                if ((!f3.getValue(v).equals(ValueIterationInt.TOP)
                        && !f4.getValue(v).equals(ValueIterationInt.TOP))) {
                    valueMap.put(v, a2);
                }
            }

            // Vertex set V_{<a1}.
            Set<V> Vsmallera1 = new HashSet<>();
            game.getVertices().stream().filter((v)
                    -> (f1.getValue(v).equals(ValueIterationInt.TOP))).forEach(Vsmallera1::add);

            // Vertex set V_{>a2}.
            Set<V> Vlargera2 = new HashSet<>();
            game.getVertices().stream().filter((v)
                    -> (f4.getValue(v).equals(ValueIterationInt.TOP))).forEach(Vlargera2::add);

            // Contains only vertices from set V_{<a1}.
            RatioGameEnergy<V, E, Integer> subGameSmallerVertices = game.getSubGraph(Vsmallera1);

            // Contains only vertices from set V_{>a2}.
            RatioGameEnergy<V, E, Integer> subGameLargerVertices = game.getSubGraph(Vlargera2);

            // Recursive calls.
            findValues(subGameSmallerVertices, lowerBound, a1, valueMap, strategy);
            findValues(subGameLargerVertices, a2, upperBound, valueMap, strategy);
        }
    }

    /**
     * Given the parameters vertexSize and maxWeight, iterate over the set of
     * all fractions, and find the maximum in the range [lowerBound,upperBound].
     *
     * @param vertexSize number of vertices in the graph
     * @param maxWeight  maximum edge weight in the graph
     * @param lowerBound range lower bound
     * @param upperBound range upper bound
     * @return maximum value in range [lowerBound,upperBound]
     */
    private static Fraction findMaxInRange(Integer vertexSize,
                                           Integer maxWeight, Fraction lowerBound, Fraction upperBound) {
        // Minimum fraction in set S.
        Fraction min = lowerBound;
        // Current maximum value.
        Fraction max = min;
        for (int a = 0; a <= vertexSize * maxWeight; a += 1) {
            for (int b = 1; b <= vertexSize * maxWeight; b += 1) {
                Fraction pm = new Fraction(a, b);
                // Check if within range.
                if (pm.compareTo(lowerBound) != -1
                        && pm.compareTo(upperBound) != 1) {
                    // pm > max.
                    if (pm.compareTo(max) > 0) {
                        // Larger fraction found, satisfying the range.
                        max = pm;
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
     * @param maxWeight  maximum edge weight in the graph
     * @param lowerBound range lower bound
     * @param upperBound range upper bound
     * @return minimum value in range [lowerBound,upperBound]
     */
    private static Fraction findMinInRange(Integer vertexSize,
                                           Integer maxWeight, Fraction lowerBound, Fraction upperBound) {
        // Maximum fraction in set S.
        Fraction max = upperBound;
        // Current minimum value.
        Fraction min = max;
        // Iterate over all possible values.
        for (int a = 0; a <= vertexSize * maxWeight; a += 1) {
            for (int b = 1; b <= vertexSize * maxWeight; b += 1) {
                Fraction pm = new Fraction(a, b);
                // Check if within range.
                if (pm.compareTo(lowerBound) != -1
                        && pm.compareTo(upperBound) != 1) {
                    // pm > min.
                    if (pm.compareTo(min) < 0) {
                        // Smaller fraction found, satisfying the range.
                        min = pm;
                    }
                }
            }
        }
        return min;
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
    private static <V, E> SingleWeightFunctionInt<E>
    convertToSingleWeightFunction(RatioGameEnergy<V, E, Integer> game,
                                  Integer constant_a, Integer constant_b) {
        // Create an explicit single weight function.
        SingleWeightFunctionInt<E> newFunction = new SingleWeightFunctionInt<>();
        // Add the entries.
        for (E edge : game.getEdges()) {
            Integer weight = constant_a * game.getWeight1(edge) + constant_b * game.getWeight2(edge);
            newFunction.addWeight(edge, weight);
        }
        return newFunction;
    }
}
