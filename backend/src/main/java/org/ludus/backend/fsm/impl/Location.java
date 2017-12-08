package org.ludus.backend.fsm.impl;

/**
 * Location in a finite-state machine.
 *
 * @author Bram van der Sanden
 */
public class Location {

    private final String name;

    public Location(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;

        Location location = (Location) o;

        return name.equals(location.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

}
