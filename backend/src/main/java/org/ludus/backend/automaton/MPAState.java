package org.ludus.backend.automaton;


/**
 * Max plus automaton state.
 *
 * @author Bram van der Sanden
 */
public class MPAState<T> {

    private final T location;
    private final Integer index;

    public MPAState(T location, Integer index) {
        this.location = location;
        this.index = index;
    }

    public T getLocation() {
        return location;
    }

    public Integer getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MPAState)) return false;

        MPAState<?> mpaState = (MPAState<?>) o;

        if (!location.equals(mpaState.location)) return false;
        return index.equals(mpaState.index);
    }

    @Override
    public int hashCode() {
        int result = location.hashCode();
        result = 31 * result + index.hashCode();
        return result;
    }
}
