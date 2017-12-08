package org.ludus.backend.algebra;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Bram van der Sanden
 */
public class VectorTest {

    Vector v1;
    Vector v2;
    Vector v3;

    public VectorTest() {
        Value a = Value.NEGATIVE_INFINITY;
        Value b = new Value(1.3);
        Value c = new Value(6.0);

        v1 = new Vector(a, b);
        v2 = new Vector(a, c);
        v3 = new Vector(b, c);
    }

    @Test
    public void testComparable() {
        assertEquals(Integer.valueOf(2), v1.size());

        assertEquals(-1, v1.compareTo(v2));
        assertEquals(0, v1.compareTo(v1));
        assertEquals(1, v2.compareTo(v1));
    }

    @Test
    public void testNormalization() {
        // Normalize vector v1.
        assertEquals(new Value(1.3), v1.getNorm());
        Vector normalized = v1.normalize();
        assertEquals(new Value(0.0), normalized.getNorm());

        assertEquals(Value.NEGATIVE_INFINITY, normalized.get(0));
        assertEquals(new Value(0.0), normalized.get(1));

        // Normalize vector 3.
        assertEquals(new Value(6.0), v3.getNorm());
        normalized = v3.normalize();
        assertEquals(new Value(0.0), normalized.getNorm());

        assertEquals(new Value(-4.7), normalized.get(0));
        assertEquals(new Value(0.0), normalized.get(1));
    }

}
