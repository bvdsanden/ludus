package org.ludus.backend.fsm.impl;

/**
 * Edge in a finite-state machine.
 *
 * @author Bram van der Sanden
 */
public class Edge {

    private final Location source;
    private final Location target;
    private final String event;

    public Edge(Location source, String name, Location target) {
        this.source = source;
        this.target = target;
        this.event = name;
    }

    public Location getSource() {
        return source;
    }

    public Location getTarget() {
        return target;
    }

    public String getEvent() {
        return event;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((event == null) ? 0 : event.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Edge other = (Edge) obj;
        if (event == null) {
            if (other.event != null)
                return false;
        } else if (!event.equals(other.event))
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (target == null) {
            return other.target == null;
        } else return target.equals(other.target);
    }

    @Override
    public String toString() {
        return source.toString() + "-" + event + "->" + target.toString();
    }

}
