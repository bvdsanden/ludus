package org.ludus.backend.datastructures.weights;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @param <E> edge type
 * @author Bram van der Sanden
 */
public class DoubleWeightFunctionDouble<E> implements DoubleWeightFunction<Double, E>, Serializable {

    private final Map<E, Double> weight1Map;
    private final Map<E, Double> weight2Map;
    private Double min1Value;
    private Double min2Value;
    private Double max1Value;
    private Double max2Value;
    private Double maxAbsValue;

    public DoubleWeightFunctionDouble() {
        weight1Map = new HashMap<>();
        weight2Map = new HashMap<>();

        min1Value = Double.POSITIVE_INFINITY;
        max1Value = Double.NEGATIVE_INFINITY;
        min2Value = Double.POSITIVE_INFINITY;
        max2Value = Double.NEGATIVE_INFINITY;
        maxAbsValue = 0.0;
    }

    public void addWeight(E edge, double weight1, double weight2) {
        weight1Map.put(edge, weight1);
        weight2Map.put(edge, weight2);
        min1Value = Math.min(min1Value, weight1);
        max1Value = Math.max(max1Value, weight1);
        min2Value = Math.min(min2Value, weight2);
        max2Value = Math.max(max2Value, weight2);
        maxAbsValue = Math.max(maxAbsValue, Math.abs(weight1));
        maxAbsValue = Math.max(maxAbsValue, Math.abs(weight2));
    }

    @Override
    public Double getWeight1(E edge) {
        return weight1Map.get(edge);
    }

    @Override
    public Double getWeight2(E edge) {
        return weight2Map.get(edge);
    }

    public Double getMin1Value() {
        return min1Value;
    }


    public Double getMax1Value() {
        return max1Value;
    }

    public Double getMin2Value() {
        return min1Value;
    }

    public Double getMax2Value() {
        return max1Value;
    }

    public Double getMaxAbsValue() {
        return maxAbsValue;
    }

}
