package org.ludus.backend.algebra;

import java.util.Arrays;

/**
 * Max-plus vector.
 *
 * @author Bram van der Sanden
 */
public class Vector implements Comparable<Vector> {

    private Value[] vector;

    public Vector(Integer length, Value value) {
        vector = new Value[length];
        for (int i = 0; i < length; i++) {
            put(i, value);
        }
    }

    public Vector(Vector otherVector) {
        vector = new Value[otherVector.size()];
        for (int i = 0; i < vector.length; i++) {
            put(i, otherVector.get(i));
        }
    }

    public Vector(Double... values) {
        vector = new Value[values.length];
        for (int i = 0; i < values.length; i++) {
            put(i, new Value(values[i]));
        }
    }

    public Vector(Value... values) {
        vector = new Value[values.length];
        for (int i = 0; i < values.length; i++) {
            put(i, values[i]);
        }
    }

    public Vector(Integer length) {
        vector = new Value[length];
    }

    public void put(Integer index, Value mpValue) {
        vector[index] = mpValue;
    }

    public Integer size() {
        return vector.length;
    }

    public Value get(Integer index) {
        return vector[index];
    }

    public Value getNorm() {
        Value norm = Value.NEGATIVE_INFINITY;
        for (Value v : vector) {
            norm = norm.max(v);
        }
        return norm;
    }

    public Vector normalize() {
        Value maximalElement = getNorm();
        assert (!maximalElement.equals(Value.NEGATIVE_INFINITY));
        Vector result = new Vector(size());
        for (int i = 0; i < size(); i++) {
            result.put(i, get(i).subtract(maximalElement));
        }
        return result;
    }

    public Vector plus(Value value) {
        Vector vec = new Vector(this.size());
        for (int i = 0; i < size(); i++) {
            vec.put(i, vector[i].add(value));
        }
        return vec;
    }

    public Vector max(Vector mpVector) {
        // Assert both vectors have the same size.
        assert mpVector.size().equals(this.size());
        // Create a new vector.
        Vector vec = new Vector(this.size());
        for (int i = 0; i < mpVector.size(); i++) {
            vec.put(i, mpVector.get(i).max(this.get(i)));
        }
        return vec;
    }

    @Override
    public int compareTo(Vector mpVector) {
        assert mpVector.size().equals(this.size());
        for (int i = 0; i < mpVector.size(); i++) {
            int comparison = vector[i].compareTo(mpVector.get(i));
            if (comparison != 0) {
                return comparison;
            }
        }
        return 0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector)) return false;

        Vector vector1 = (Vector) o;

        return Arrays.equals(vector, vector1.vector);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vector);
    }

    @Override
    public String toString() {
        return Arrays.toString(vector);
    }


}
