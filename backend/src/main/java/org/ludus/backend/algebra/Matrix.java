package org.ludus.backend.algebra;

/**
 * Max-plus matrix.
 *
 * @author Bram van der Sanden
 */
public abstract class Matrix implements Comparable<Matrix> {

    public abstract void put(int row, int column, Value value);

    public abstract Value get(int row, int column);

    public abstract int getColumns();

    public abstract int getRows();

    public Vector multiply(Vector vector) {
        // Ensure width of matrix is equal to vector length;
        assert (getColumns() == vector.size());

        Vector result = new Vector(vector.size());
        for (int i = 0; i < vector.size(); i++) {
            // Compute new vector value i.
            Value newVal = vector.get(i);
            for (int j = 0; j < getColumns(); j++) {
                // Addition term.
                Value newTerm = vector.get(j).add(get(i, j));
                // Take maximum.
                newVal = newVal.max(newTerm);
            }
            result.put(i, newVal);
        }
        return result;
    }

    public Matrix multiply(Matrix matrix) {
        // Ensure matrices are compatible.
        assert (getColumns() == matrix.getRows());

        Matrix result = new DenseMatrix(getRows(), matrix.getColumns());

        // Perform piece-wise multiplication.
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < matrix.getColumns(); col++) {
                Value v = Value.NEGATIVE_INFINITY;
                // Value at (row,col) = max ( this.get(row,k) + matrix.get(k,col) ), over all k.
                for (int k = 0; k < getColumns(); k++) {
                    v = v.max(get(row, k).add(matrix.get(k, col)));
                }
                result.put(row, col, v);
            }
        }

        return result;
    }

    public Matrix max(Matrix matrix) {
        assert (getColumns() == matrix.getColumns() && getRows() == matrix.getRows());

        Matrix result = new DenseMatrix(getRows(), matrix.getColumns());

        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getColumns(); col++) {
                result.put(row, col, get(row, col).max(matrix.get(row, col)));
            }
        }

        return result;
    }

    public int compareTo(Matrix otherMatrix) {
        assert otherMatrix.getRows() == this.getRows() && otherMatrix.getColumns() == this.getColumns();

        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getColumns(); col++) {
                int comparison = get(row, col).compareTo(otherMatrix.get(row, col));
                if (comparison != 0) {
                    return comparison;
                }
            }
        }

        return 0;
    }

}
