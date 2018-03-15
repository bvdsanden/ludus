package org.ludus.api;

import java.util.List;

/**
 * Minimum throughput result: throughput and the events on the corresponding cycle.
 *
 * @author Bram van der Sanden
 */
public class MinimumThroughputResult {
	private final double throughput;
	private final List<String> events;

	public MinimumThroughputResult(double throughput, List<String> events) {
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
		return "MinimumThroughputResult [throughput=" + throughput + ", events=" + events + "]";
	}
}
