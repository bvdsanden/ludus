package org.ludus.api;

import java.util.List;

/**
 * Maximum throughput result: throughput and the events on the corresponding trace.
 *
 * @author Bram van der Sanden
 */
public class MaximumThroughputResult {
    private final double throughput;
    private final List<String> events;

    public MaximumThroughputResult(double throughput, List<String> events) {
        this.throughput = throughput;
        this.events = events;
    }

    public double getThroughput() {
        return throughput;
    }

    public List<String> getEvents() {
        return events;
    }

    @Override
    public String toString() {
        return "MaximumThroughputResult [throughput=" + throughput + ", events=" + events + "]";
    }
}
