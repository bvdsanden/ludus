package org.ludus.api;

import org.ludus.backend.algebra.Matrix;
import org.ludus.backend.fsm.FSM;
import org.ludus.backend.fsm.impl.Edge;
import org.ludus.backend.fsm.impl.Location;

import java.util.List;
import java.util.Map;

/**
 * Maximum makespan result: makespan and the events on the corresponding trace.
 *
 * @author Bram van der Sanden
 */
public class MaximumMakespanResult {
    private final double makespan;
    private final List<String> events;

    public MaximumMakespanResult(double makespan, List<String> events) {
        this.makespan = makespan;
        this.events = events;
    }

    public double getMakespan() {
        return makespan;
    }

    public List<String> getEvents() {
        return events;
    }

    @Override
    public String toString() {
        return "MaximumMakespanResult [makespan=" + makespan + ", events=" + events + "]";
    }

}
