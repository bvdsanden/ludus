package org.ludus.backend.datastructures.weights;

import java.util.HashMap;
import java.util.Map;

/**
 * @param <E> edge type
 * @author Bram van der Sanden
 */
public class SingleWeightFunctionInt<E> implements SingleWeightFunction<Integer, E> {

    private final Map<E, Integer> weightMap;
    private Integer sumNegativeWeights;
    private Integer minValue;
    private Integer maxValue;
    private Integer maxAbsValue;

    public SingleWeightFunctionInt() {
        weightMap = new HashMap<>();
        sumNegativeWeights = 0;

        minValue = Integer.MAX_VALUE;
        maxValue = Integer.MIN_VALUE;
        maxAbsValue = 0;
    }

    public void addWeight(E edge, Integer weight) {
        weightMap.put(edge, weight);
        // Update the sum of the negative weights.
        sumNegativeWeights += Math.max(0, -weight);
        minValue = Math.min(minValue, weight);
        maxValue = Math.max(maxValue, weight);
        maxAbsValue = Math.max(maxAbsValue, Math.abs(weight));
    }

    @Override
    public Integer getWeight(E edge) {
        return weightMap.get(edge);
    }

    public Integer getSumNegWeights() {
        return sumNegativeWeights;
    }

    public Map<E, Integer> getWeightMap() {
        return weightMap;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    @Override
    public Integer getMaxAbsValue() {
        return maxAbsValue;
    }

}
