package org.ludus.backend.games.meanpayoff.solvers.energy;

import org.ludus.backend.datastructures.weights.SingleWeightFunctionInt;
import org.ludus.backend.games.algorithms.GraphChecks;
import org.ludus.backend.games.energy.EnergyGame;
import org.ludus.backend.games.energy.solvers.SEPM;
import org.ludus.backend.games.energy.solvers.ValueIterationInt;
import org.ludus.backend.graph.jgrapht.energy.EGIntImplJGraphT;
import org.apache.commons.math3.fraction.Fraction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Bram van der Sanden
 */
public class ValueIterationReductionInt {

    /**
     * Compute the value for each vertex
     * @param game game graph
     * @param <V> vertex type
     * @param <E> edge type
     * @return map with value for each vertex
     */
    public static <V, E> Map<V, Fraction> solve(MeanPayoffGameEnergy<V, E, Integer> game) {
        Map<V, Fraction> vertexMap = new HashMap<>();
        Fraction W = new Fraction(game.getMaxAbsValue());
        Fraction minusW = W.negate();

        if (GraphChecks.checkEachNodeHasSuccessor(game)) {
            findValues(game, minusW, W, vertexMap);
            return vertexMap;
        } else {
            System.out.println("Input game graph is not valid. "
                    + "Not every vertex has a successor.");
            return null;
        }
    }

    /**
     * Algorithm 2 of paper by (Brim et al., 2009) to solve the value problem
     * for mean-payoff games.
     *
     * @param <V> vertex type
     * @param <E> edge type
     * @param game game graph
     * @param lowerBound minimum value
     * @param upperBound maximum value
     * @param valueMap map with value of each vertex
     */
    private static <V, E> void findValues(MeanPayoffGameEnergy<V, E, Integer> game,
                                          Fraction lowerBound, Fraction upperBound, Map<V, Fraction> valueMap) {
        if (!game.getVertices().isEmpty()) {
            Fraction middle = lowerBound.add(upperBound).
                    multiply(Fraction.ONE_HALF);

            Integer V = game.getVertices().size();
            Integer W = game.getMaxAbsValue();

            // Split the range into two parts: [lowerBound,a1] [a2,upperBound].
            // Ensure that both fractions a1 and a2 are valid in the set of 
            // possible values.
            Fraction a1 = findMaxInRange(V, W, lowerBound, middle);
            Fraction a2 = findMinInRange(V, W, middle, upperBound);

            Integer q1 = a1.getNumerator();
            Integer l1 = a1.getDenominator();
            Integer q2 = a2.getNumerator();
            Integer l2 = a2.getDenominator();

            // Create game graph with vertices swapped.
            MeanPayoffGameEnergy<V,E,Integer> gameSwapped =
                    game.getSwappedSubGraph(game.getVertices());

            // Determine SEPMs.
            SingleWeightFunctionInt<E> wf1
                    = reweight(game, l1, -q1);
            EnergyGame<V,E,Integer> game1 = new EGIntImplJGraphT<>(game, wf1);
            SEPM<V,Integer> f1 = ValueIterationInt.getProgressMeasure(game1);

            SingleWeightFunctionInt wf2
                    = reweight(game, -l1, q1);
            EnergyGame<V,E,Integer> game2 = new EGIntImplJGraphT<>(gameSwapped, wf2);
            SEPM<V,Integer> f2 = ValueIterationInt.getProgressMeasure(game2);

            SingleWeightFunctionInt wf3
                    = reweight(game, l2, -q2);
            EnergyGame<V,E,Integer> game3 = new EGIntImplJGraphT<>(game, wf3);
            SEPM<V,Integer> f3 = ValueIterationInt.getProgressMeasure(game3);

            SingleWeightFunctionInt wf4
                    = reweight(game, -l2, q2);
            EnergyGame<V,E,Integer> game4 = new EGIntImplJGraphT<>(gameSwapped, wf4);
            SEPM<V,Integer> f4 = ValueIterationInt.getProgressMeasure(game4);

            // Update vertex values.
            game.getVertices().stream().map((v) -> {
                if (!f1.getValue(v).equals(ValueIterationInt.TOP)
                        && !f2.getValue(v).equals(ValueIterationInt.TOP)) {
                    valueMap.put(v, a1);
                }
                return v;
            }).filter((v) -> (!f3.getValue(v).equals(ValueIterationInt.TOP)
                    && !f4.getValue(v).equals(ValueIterationInt.TOP))).forEach((v) -> valueMap.put(v, a2));

            // Vertex set V_{<a1}.
            Set<V> Vsmallera1 = new HashSet<>();
            game.getVertices().stream().filter((v)
                    -> (f1.getValue(v).equals(ValueIterationInt.TOP))).forEach(Vsmallera1::add);

            // Vertex set V_{>a2}.
            Set<V> Vlargera2 = new HashSet<>();
            game.getVertices().stream().filter((v)
                    -> (f4.getValue(v).equals(ValueIterationInt.TOP))).forEach(Vlargera2::add);

            MeanPayoffGameEnergy<V,E,Integer> subGameSmallerVertices = game.getSubGraph(Vsmallera1);
            MeanPayoffGameEnergy<V,E,Integer> subGameLargerVertices = game.getSubGraph(Vlargera2);

            // Recursive calls.
            findValues(subGameSmallerVertices, lowerBound, a1, valueMap);
            findValues(subGameLargerVertices, a2, upperBound, valueMap);
        }
    }

    /**
     * Given the parameters vertexSize and maxWeight, iterate over the set of
     * all fractions, and find the maximum in the range [lowerBound,upperBound].
     *
     * @param vertexSize number of vertices
     * @param maxWeight maximum weight
     * @param lowerBound minimum value
     * @param upperBound maximum value
     * @return maximum value in range [lowerBound,upperBound]
     */
    private static Fraction findMaxInRange(Integer vertexSize, Integer maxWeight, Fraction lowerBound, Fraction upperBound) {
        // Minimum fraction in set S.
        Fraction min = lowerBound;
        // Current maximum value.
        Fraction max = min;
        for (int m = 1; m <= vertexSize; m += 1) {
            for (int p = -m * maxWeight; p <= m * maxWeight; p += 1) {
                Fraction pm = new Fraction(p, m);
                // Check if within range.
                if (pm.compareTo(lowerBound) != -1 && pm.compareTo(upperBound) != 1) {
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
     * @param vertexSize number of vertices
     * @param maxWeight maximum weight
     * @param lowerBound minimum value
     * @param upperBound maximum value
     * @return minimum value in range [lowerBound,upperBound]
     */
    private static Fraction findMinInRange(Integer vertexSize, Integer maxWeight, Fraction lowerBound, Fraction upperBound) {
        // Maximum fraction in set S.
        Fraction max = upperBound;
        // Current minimum value.
        Fraction min = max;
        for (int m = 1; m <= vertexSize; m += 1) {
            for (int p = -m * maxWeight; p <= m * maxWeight; p += 1) {
                Fraction pm = new Fraction(p, m);
                // Check if within range.
                if (pm.compareTo(lowerBound) != -1 && pm.compareTo(upperBound) != 1) {
                    // pm > min.
                    if (pm.compareTo(min) > 0) {
                        // Smaller fraction found, satisfying the range.
                        min = pm;
                    }
                }
            }
        }
        return min;
    }

    /**
     * Reweight each weight in the function to {@code multiplyConstant}*weight +
     * {@code addConstant}.
     *
     * @param game game graph
     * @param multiplyConstant constant to multiply
     * @param addConstant constant to add
     * @return new weight function weight(e) = multiplyConstant * weight(e) + addConstant
     */
    private static <V, E> SingleWeightFunctionInt<E> reweight(MeanPayoffGameEnergy<V, E, Integer> game, Integer multiplyConstant, Integer addConstant) {
        // Create an explicit single weight function.
        SingleWeightFunctionInt<E> newFunction = new SingleWeightFunctionInt<>();
        // Add the entries.
        for (E edge : game.getEdges()) {
            Integer weight = multiplyConstant * game.getWeight(edge) + addConstant;
            newFunction.addWeight(edge, weight);
        }
        return newFunction;
    }
}
