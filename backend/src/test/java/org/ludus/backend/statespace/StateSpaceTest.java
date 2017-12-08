package org.ludus.backend.statespace;

import org.junit.jupiter.api.Test;
import org.ludus.backend.algebra.Value;
import org.ludus.backend.algebra.Vector;
import org.ludus.backend.fsm.impl.Location;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Bram van der Sanden
 */
public class StateSpaceTest {

    @Test
    public void testLocation() {
        Location l1 = new Location("l0");
        Location l2 = new Location("l0");
        assertEquals(l1, l2);
    }

    @Test
    public void testConfiguration() {
        Location l = new Location("l0");
        Vector v = new Vector(3, new Value(0.0));
        Configuration<Location> c1 = new Configuration<>(l, v);
        Configuration<Location> c2 = new Configuration<>(l, v);
        assertEquals(c1, c2);

        Location l1 = new Location("l0");
        Vector v1 = new Vector(3, new Value(0.0));
        Configuration<Location> c3 = new Configuration<>(l1, v1);

        assertEquals(l, l1);
        assertEquals(v, v1);
        assertEquals(c1, c3);

    }

    @Test
    public void testTransition() {
        Location l = new Location("l0");
        Vector v = new Vector(3, new Value(0.0));
        Configuration<Location> c1 = new Configuration<>(l, v);
        Configuration<Location> c2 = new Configuration<>(l, v);

        Location l1 = new Location("l0");
        Vector v1 = new Vector(3, new Value(0.0));
        Configuration<Location> c3 = new Configuration<>(l1, v1);

        Value norm = v.getNorm();
        Transition t = new Transition(c1, "a", norm, new Value(0.0), c2);

        Value norm1 = v1.getNorm();
        Transition t1 = new Transition(c1, "a", norm1, new Value(0.0), c3);
        assertEquals(t, t1);
    }

}
