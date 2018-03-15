package org.ludus.api;

import java.util.List;

/**
 * Minimum makespan result: makespan and the events on the corresponding trace.
 *
 * @author Bram van der Sanden
 */
public class MinimumMakespanResult {
	private final double makespan;
	private final List<String> events;

	public MinimumMakespanResult(double makespan, List<String> events) {
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
		return "MinimalMakespanResult [makespan=" + makespan + ", events=" + events + "]";
	}
}
