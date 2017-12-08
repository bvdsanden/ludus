package org.ludus.backend.algebra;

/**
 * Max-plus value.
 *
 * @author Bram van der Sanden
 */
public class Value implements Comparable<Value> {

    public static Value NEGATIVE_INFINITY = new Value(Double.NEGATIVE_INFINITY);

    private Double value;

    public Value(Double value) {
        this.value = value;
    }

    public Value max(Value other) {
        return new Value(Math.max(value, other.value));
    }

    public Value min(Value other) {
        return new Value(Math.min(value, other.value));
    }

    public Value add(Value other) {
        return new Value(value + other.value);
    }

    public Value multiply(Value other) {
        return new Value(other.value * value);
    }

    public Value subtract(Value other) {
        assert (!other.equals(NEGATIVE_INFINITY));
        return new Value(value - other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Value)) return false;

        Value value1 = (Value) o;

        return value.equals(value1.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        if ((value % 1) == 0) {
            return Integer.toString(value.intValue());
        } else {
            return Double.toString(value);
        }
    }

    @Override
    public int compareTo(Value o) {
        return Double.compare(this.value, o.value);
    }

    public Double getValue() {
        return value;
    }


}
