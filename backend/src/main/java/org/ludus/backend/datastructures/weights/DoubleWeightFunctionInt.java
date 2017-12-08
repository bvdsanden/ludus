package org.ludus.backend.datastructures.weights;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @param <E> edge type
 * @author Bram van der Sanden
 */
public class DoubleWeightFunctionInt<E> implements DoubleWeightFunction<Integer, E>, Serializable {

    private final Map<E, Integer> weight1Map;
    private final Map<E, Integer> weight2Map;
    private Integer min1Value;
    private Integer min2Value;
    private Integer max1Value;
    private Integer max2Value;
    private Integer maxAbsValue;

    public DoubleWeightFunctionInt() {
        weight1Map = new HashMap<>();
        weight2Map = new HashMap<>();

        min1Value = Integer.MAX_VALUE;
        max1Value = Integer.MIN_VALUE;
        min2Value = Integer.MAX_VALUE;
        max2Value = Integer.MIN_VALUE;
        maxAbsValue = 0;
    }

    public void addWeight(E edge, Integer weight1, Integer weight2) {
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
    public Integer getWeight1(E edge) {
        return weight1Map.get(edge);
    }

    @Override
    public Integer getWeight2(E edge) {
        return weight2Map.get(edge);
    }

    public Integer getMin1Value() {
        return min1Value;
    }

    public Integer getMax1Value() {
        return max1Value;
    }

    public Integer getMin2Value() {
        return min1Value;
    }

    public Integer getMax2Value() {
        return max1Value;
    }

    @Override
    public Integer getMaxAbsValue() {
        return maxAbsValue;
    }

}
