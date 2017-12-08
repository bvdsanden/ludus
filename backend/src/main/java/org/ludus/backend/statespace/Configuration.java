package org.ludus.backend.statespace;

import org.ludus.backend.algebra.Vector;

/**
 * @param <T> location type
 * @author Bram van der Sanden
 */
public class Configuration<T> {

    T location;
    Vector vector;

    public Configuration(T location, Vector vector) {
        this.location = location;
        this.vector = vector;
    }

    public T getLocation() {
        return location;
    }

    public Vector getVector() {
        return vector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Configuration)) return false;

        Configuration<?> that = (Configuration<?>) o;

        if (!location.equals(that.location)) return false;
        return vector.equals(that.vector);
    }

    @Override
    public int hashCode() {
        int result = location.hashCode();
        result = 31 * result + vector.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "<" + location.toString() + "," + vector.toString() + ">";
    }


}
