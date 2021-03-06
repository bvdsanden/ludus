package org.ludus.backend.statespace;

import org.ludus.backend.algebra.Value;

/**
 * State space transition.
 *
 * @author Bram van der Sanden
 */
public class Transition {

    private final Configuration source;
    private final Configuration target;
    private final String event;
    private final Value duration;
    private final Value reward;

    /**
     * State space transition with a reward and duration weight.
     *
     * @param source   source vertex
     * @param event    event name
     * @param reward   reward value
     * @param duration duration value
     * @param target   target vertex
     */
    public Transition(Configuration source, String event, Value reward, Value duration, Configuration target) {
        this.source = source;
        this.target = target;
        this.event = event;
        this.reward = reward;
        this.duration = duration;
    }

    public Configuration getSource() {
        return source;
    }

    public Configuration getTarget() {
        return target;
    }

    public String getEvent() {
        return event;
    }

    public Value getDuration() {
        return duration;
    }

    public Value getReward() {
        return reward;
    }

    @Override
    public String toString() {
        return "Transition{" + "source=" + source + ", target=" + target + ", event=" + event + ", duration=" + duration + ", reward=" + reward + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transition)) return false;

        Transition that = (Transition) o;

        if (!source.equals(that.source)) return false;
        if (!target.equals(that.target)) return false;
        if (!event.equals(that.event)) return false;
        if (!reward.equals(that.reward)) return false;
        return duration.equals(that.duration);
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + target.hashCode();
        result = 31 * result + event.hashCode();
        result = 31 * result + event.hashCode();
        result = 31 * result + reward.hashCode();
        result = 31 * result + duration.hashCode();
        return result;
    }
}
