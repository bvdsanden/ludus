package org.ludus.backend.datastructures.weights;

import java.util.HashMap;
import java.util.Map;

/**
 * @param <E> edge type
 * @author Bram van der Sanden
 */
public class SingleWeightFunctionDouble<E> implements SingleWeightFunction<Double, E> {

    private final Map<E, Double> weightMap;
    private Double sumNegativeWeights;
    private Double minValue;
    private Double maxValue;
    private Double maxAbsValue;

    public SingleWeightFunctionDouble() {
        weightMap = new HashMap<>();
        sumNegativeWeights = 0.0;

        minValue = Double.POSITIVE_INFINITY;
        maxValue = Double.NEGATIVE_INFINITY;
        maxAbsValue = 0.0;
    }

    public void addWeight(E edge, Double weight) {
        weightMap.put(edge, weight);
        // Update the sum of the negative weights.
        sumNegativeWeights += Math.max(0, -weight);
        minValue = Math.min(minValue, weight);
        maxValue = Math.max(maxValue, weight);
        maxAbsValue = Math.max(maxAbsValue, Math.abs(weight));
    }

    @Override
    public Double getWeight(E edge) {
        return weightMap.get(edge);
    }

    public Double getSumNegWeights() {
        return sumNegativeWeights;
    }

    public Map<E, Double> getWeightMap() {
        return weightMap;
    }

    public Double getMinValue() {
        return minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    @Override
    public Double getMaxAbsValue() {
        return maxAbsValue;
    }

}
