package org.ludus.backend.statespace;

import org.junit.jupiter.api.Test;
import org.ludus.backend.algebra.DenseMatrix;
import org.ludus.backend.algebra.Matrix;
import org.ludus.backend.algebra.Vector;
import org.ludus.backend.fsm.impl.Edge;
import org.ludus.backend.fsm.impl.FSMImpl;
import org.ludus.backend.fsm.impl.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Bram van der Sanden
 */
public class MaxPlusStateSpaceTest {

    private final Double negativeInfinity = Double.NEGATIVE_INFINITY;

    /**
     * Test case taken from the paper "Worst-case Performance Analysis of
     * Synchronous Dataflow Scenarios (Geilen and Stuijk, 2010)".
     */
    @Test
    public void testExampleGeilenStuijk2010() {
        // FSMImpl.
        FSMImpl fsm = new FSMImpl();
        Location l0 = new Location("l0");
        Location l1 = new Location("l1");
        fsm.addLocation(l0);
        fsm.addLocation(l1);
        fsm.setInitial(l0);
        fsm.addControllable("A");
        fsm.addControllable("B");
        fsm.addEdge(new Edge(l0, "A", l0));
        fsm.addEdge(new Edge(l0, "B", l1));
        fsm.addEdge(new Edge(l1, "B", l0));
        fsm.addEdge(new Edge(l1, "B", l1));

        // Matrix A.
        Matrix MA = new DenseMatrix(3, 3,
                1.0, negativeInfinity, 3.0,
                1.0, negativeInfinity, 3.0,
                negativeInfinity, 2.0, negativeInfinity);

        // Matrix B.
        Matrix MB = new DenseMatrix(3, 3,
                1.0, negativeInfinity, 2.0,
                1.0, negativeInfinity, 2.0,
                negativeInfinity, 3.0, negativeInfinity);

        Map<String, Matrix> matrixMap = new HashMap<>();
        matrixMap.put("A", MA);
        matrixMap.put("B", MB);

        MaxPlusStateSpace space
                = ComputeStateSpace.computeMaxPlusStateSpace(fsm, 3, matrixMap);
        assertEquals(10, space.getConfigurations().size());

        Set<Configuration> states = Stream.of(
                new Configuration<>(l0, new Vector(0.0, 0.0, 0.0)),
                new Configuration<>(l1, new Vector(0.0, 0.0, 0.0)),

                new Configuration<>(l0, new Vector(0.0, 0.0, -1.0)),
                new Configuration<>(l1, new Vector(0.0, 0.0, -1.0)),

                new Configuration<>(l0, new Vector(-1.0, -1.0, 0.0)),
                new Configuration<>(l1, new Vector(-1.0, -1.0, 0.0)),

                new Configuration<>(l1, new Vector(-2.0, -2.0, 0.0)),
                new Configuration<>(l0, new Vector(-2.0, -2.0, 0.0)),

                new Configuration<>(l0, new Vector(0.0, 0.0, -3.0)),
                new Configuration<>(l0, new Vector(0.0, 0.0, -2.0))
        ).collect(Collectors.toSet());

        states.forEach((c) -> assertTrue(space.getConfigurations().contains(c)));
    }


    /**
     * Test case taken from the paper "Worst-case Performance Analysis of
     * Synchronous Dataflow Scenarios (Geilen and Stuijk, 2010)".
     */
    @Test
    public void testExampleGeilenStuijk2010Simplified() {
        // FSMImpl.
        FSMImpl fsm = new FSMImpl();
        Location l0 = new Location("l0");
        fsm.addLocation(l0);
        fsm.setInitial(l0);
        fsm.addControllable("A");
        fsm.addControllable("B");
        fsm.addEdge(new Edge(l0, "A", l0));
        fsm.addEdge(new Edge(l0, "B", l0));

        // Matrix A.
        Matrix MA = new DenseMatrix(3, 3,
                1.0, negativeInfinity, 3.0,
                1.0, negativeInfinity, 3.0,
                negativeInfinity, 2.0, negativeInfinity);

        // Matrix B.
        Matrix MB = new DenseMatrix(3, 3,
                1.0, negativeInfinity, 2.0,
                1.0, negativeInfinity, 2.0,
                negativeInfinity, 3.0, negativeInfinity);

        Map<String, Matrix> matrixMap = new HashMap<>();
        matrixMap.put("A", MA);
        matrixMap.put("B", MB);

        MaxPlusStateSpace space
                = ComputeStateSpace.computeMaxPlusStateSpace(fsm, 3, matrixMap);
        assertEquals(6, space.getConfigurations().size());

        Set<Configuration> states = Stream.of(
                new Configuration<>(l0, new Vector(0.0, 0.0, 0.0)),
                new Configuration<>(l0, new Vector(0.0, 0.0, -1.0)),
                new Configuration<>(l0, new Vector(-1.0, -1.0, 0.0)),
                new Configuration<>(l0, new Vector(0.0, 0.0, -2.0)),
                new Configuration<>(l0, new Vector(-2.0, -2.0, 0.0)),
                new Configuration<>(l0, new Vector(0.0, 0.0, -3.0))
        ).collect(Collectors.toSet());

        states.forEach((c) ->
                assertTrue(space.getConfigurations().contains(c))
        );
    }


    /**
     * Simple custom example where A and B are alternating.
     */
    @Test
    public void testExploration() {
        // FSMImpl.
        FSMImpl fsm = new FSMImpl();
        Location l0 = new Location("l0");
        Location l1 = new Location("l1");
        fsm.addLocation(l0);
        fsm.addLocation(l1);
        fsm.setInitial(l0);
        fsm.addControllable("A");
        fsm.addControllable("B");
        fsm.addEdge(new Edge(l0, "A", l1));
        fsm.addEdge(new Edge(l1, "B", l0));
        fsm.addControllable("A");
        fsm.addControllable("B");

        // Matrix A.
        Matrix MA = new DenseMatrix(2, 2,
                5.0, 2.0,
                negativeInfinity, 2.0);

        // Matrix B.
        Matrix MB = new DenseMatrix(2, 2,
                0.0, negativeInfinity,
                negativeInfinity, 5.0);

        Map<String, Matrix> matrixMap = new HashMap<>();
        matrixMap.put("A", MA);
        matrixMap.put("B", MB);

        MaxPlusStateSpace space
                = ComputeStateSpace.computeMaxPlusStateSpace(fsm, 2, matrixMap);
        assertEquals(7, space.getConfigurations().size());

        Set<Configuration> states = Stream.of(
                new Configuration<>(l0, new Vector(-5.0, 0.0)),
                new Configuration<>(l1, new Vector(0.0, -3.0)),
                new Configuration<>(l0, new Vector(0.0, 0.0)),
                new Configuration<>(l0, new Vector(-2.0, 0.0)),
                new Configuration<>(l0, new Vector(-4.0, 0.0)),
                new Configuration<>(l1, new Vector(0.0, -1.0)),
                new Configuration<>(l1, new Vector(0.0, 0.0))
        ).collect(Collectors.toSet());

        states.forEach((c) -> assertTrue(space.getConfigurations().contains(c)));
    }

}
