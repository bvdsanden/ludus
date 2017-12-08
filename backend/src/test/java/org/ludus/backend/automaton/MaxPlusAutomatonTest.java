package org.ludus.backend.automaton;

import org.junit.jupiter.api.Test;
import org.ludus.backend.algebra.DenseMatrix;
import org.ludus.backend.algebra.Matrix;
import org.ludus.backend.algorithms.Howard;
import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.fsm.impl.FSMImpl;
import org.ludus.backend.fsm.impl.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test the construction of a max-plus automaton, and run Howard to get the MCM result.
 */
public class MaxPlusAutomatonTest {

    final Double negativeInfinity = Double.NEGATIVE_INFINITY;

    @Test
    public void test1() {
        Matrix mA = new DenseMatrix(3, 3,
                1.0, negativeInfinity, 3.0,
                1.0, negativeInfinity, 3.0,
                negativeInfinity, 2.0, negativeInfinity);

        Matrix mB = new DenseMatrix(3, 3,
                1.0, negativeInfinity, 2.0,
                1.0, negativeInfinity, 2.0,
                negativeInfinity, 3.0, negativeInfinity);

        FSMImpl fsm = new FSMImpl();
        Location l0 = new Location("l0");
        Location l1 = new Location("l1");
        fsm.addLocation(l0);
        fsm.addLocation(l1);
        fsm.addEdge(l0, l1, "a");
        fsm.addEdge(l1, l1, "b");

        Map<String, Matrix> matrixMap = new HashMap<>();
        matrixMap.put("a", mA);
        matrixMap.put("b", mB);

        MaxPlusAutomaton<Location> mpa = ComputeMPA.computeMaxPlusAutomaton(fsm, 3, matrixMap);

        Tuple<Double, List<MPATransition>> result = Howard.runHoward(mpa);
        assertEquals(Double.valueOf(0.4d), result.getLeft());

        MaxPlusAutomaton<Location> mpaSwapped = ComputeMPA.swapWeights(mpa);

        Tuple<Double, List<MPATransition>> result2 = Howard.runHoward(mpaSwapped);
        assertEquals(Double.valueOf(1.0d), result2.getLeft());
    }

}
