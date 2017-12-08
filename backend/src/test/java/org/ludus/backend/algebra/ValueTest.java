package org.ludus.backend.algebra;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author bram
 */
public class ValueTest {

    public ValueTest() {
    }

    @Test
    public void testOperations() {
        Value a = Value.NEGATIVE_INFINITY;
        Value b = new Value(10.0);
        Value c = new Value(2.4);

        // Max operator.
        assertEquals(b, a.max(b));
        assertEquals(b, b.max(a));
        assertEquals(b, b.max(c));
        assertEquals(b, c.max(b));

        // Min operator.
        assertEquals(a, a.min(b));
        assertEquals(a, b.min(a));
        assertEquals(c, b.min(c));
        assertEquals(c, c.min(b));

        // Plus operator.
        assertEquals(a, a.add(b));
        assertEquals(a, b.add(a));
        assertEquals(new Value(12.4), c.add(b));
        assertEquals(new Value(12.4), b.add(c));

        // Minus operator.
        assertEquals(a, a.subtract(b));
        assertEquals(new Value(-7.6), c.subtract(b));
        assertEquals(new Value(7.6), b.subtract(c));
    }
}
