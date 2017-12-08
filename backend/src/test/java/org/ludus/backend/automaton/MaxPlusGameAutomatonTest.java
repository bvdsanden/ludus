package org.ludus.backend.automaton;

import org.junit.jupiter.api.Test;
import org.ludus.backend.algebra.DenseMatrix;
import org.ludus.backend.algebra.Matrix;
import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.fsm.impl.FSMImpl;
import org.ludus.backend.fsm.impl.Location;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.games.algorithms.DoubleFunctions;
import org.ludus.backend.games.ratio.solvers.policy.PolicyIterationDouble;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test the construction of a max-plus game automaton.
 */
public class MaxPlusGameAutomatonTest {


    final Double negativeInfinity = Double.NEGATIVE_INFINITY;

    @Test
    public void test1() {
        Matrix mA = new DenseMatrix(3, 3,
                2.0, 0.0, negativeInfinity,
                5.0, 3.0, negativeInfinity,
                negativeInfinity, negativeInfinity, 0.0);

        FSMImpl fsm = new FSMImpl();
        Location l0 = new Location("l0");
        fsm.addLocation(l0);
        fsm.addEdge(l0, l0, "a");

        Map<String, Matrix> matrixMap = new HashMap<>();
        matrixMap.put("a", mA);

        Integer vectorSize = 3;
        MPGA<Location> mpa = ComputeMPGA.computeMaxPlusAutomaton(fsm, vectorSize, matrixMap);

        Tuple<Map<MPAState<Location>, Double>, StrategyVector<MPAState<Location>, MPATransition>> result = PolicyIterationDouble.solve(mpa, DoubleFunctions.EPSILON);

        assertEquals(Double.valueOf(1.0 / 3.0), result.getLeft().get(mpa.getState(l0, 0)));
        assertEquals(Double.valueOf(1.0 / 3.0), result.getLeft().get(mpa.getState(l0, 1)));
        assertEquals(Double.valueOf(Double.POSITIVE_INFINITY), result.getLeft().get(mpa.getState(l0, 2)));

        // Find starting state with maximal value.
        // TODO: fix the case with positive infinity
        MPAState<Location> initialState = mpa.getState(l0, 0);
        for (Integer index = 0; index < vectorSize; index++) {
            MPAState<Location> s = mpa.getState(l0, index);
            if (result.getLeft().get(s) > result.getLeft().get(initialState)) {
                initialState = s;
            }
        }

        StrategyVector<MPAState<Location>, MPATransition> sv = result.getRight();

        // Follow this state to get to the cycle.
        System.out.println(StrategyVector.getCycle(sv, initialState));
    }

}
