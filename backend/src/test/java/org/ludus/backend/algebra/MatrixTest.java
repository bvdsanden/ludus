package org.ludus.backend.algebra;


import org.junit.jupiter.api.Test;
import org.ludus.backend.algorithms.Howard;
import org.ludus.backend.automaton.ComputeMPA;
import org.ludus.backend.automaton.MaxPlusAutomaton;
import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.fsm.impl.FSMImpl;
import org.ludus.backend.fsm.impl.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Bram van der Sanden
 */
public class MatrixTest {

    @Test
    public void testMultiplication1() {
        Matrix m = new DenseMatrix(2, 2,
                2.0, Double.NEGATIVE_INFINITY,
                1.0, 0.0);

        Vector v = new Vector(4.0, 5.0);

        Vector result = m.multiply(v);
        assertEquals(new Value(6.0), result.get(0));
        assertEquals(new Value(5.0), result.get(1));

        Vector v2 = new Vector(3.0, 0.0);

        Vector result2 = m.multiply(v2);
        assertEquals(new Value(5.0), result2.get(0));
        assertEquals(new Value(4.0), result2.get(1));
    }

    @Test
    public void testMultiplication2() {
        Matrix MA = new DenseMatrix(2, 2,
                5.0, 2.0,
                Double.NEGATIVE_INFINITY, 2.0);

        Vector v = new Vector(0.0, 0.0);

        Vector result = MA.multiply(v);
        assertEquals(new Value(5.0), result.get(0));
        assertEquals(new Value(2.0), result.get(1));

    }

    @Test
    public void testMultiplication() {
        Matrix m = new DenseMatrix(2, 2,
                2.0, Double.NEGATIVE_INFINITY,
                5.0, 3.0);

        Vector v0 = new Vector(0.0, 0.0);
        Vector v1 = m.multiply(v0);
        System.out.println(v1.normalize());
        Vector v2 = m.multiply(v1.normalize());
        System.out.println(v2.normalize());

        FSMImpl f = new FSMImpl();
        Location l = new Location("l0");
        f.addLocation(l);
        f.addEdge(l, l, "A");
        Map<String, Matrix> map = new HashMap<>();
        map.put("A", m);
        MaxPlusAutomaton mpa = ComputeMPA.computeMaxPlusAutomaton(f, 2, map);
        mpa = ComputeMPA.swapWeights(mpa);
        Tuple<Double, List<String>> result = Howard.runHoward(mpa);
        System.out.println(result.getLeft());
    }

    @Test
    public void testCompare() {
        Matrix MA = new DenseMatrix(2, 2,
                5.0, 2.0,
                Double.NEGATIVE_INFINITY, 2.0);

        Matrix MB = new DenseMatrix(2, 2,
                12.0, 2.0,
                Double.NEGATIVE_INFINITY, 2.0);

        assertEquals(0, MA.compareTo(MA));
        assertEquals(-1, MA.compareTo(MB));
        assertEquals(1, MB.compareTo(MA));
    }

}
