package org.ludus.backend.algebra;

/**
 * Max-plus dense matrix.
 *
 * @author Bram van der Sanden
 */
public class DenseMatrix extends Matrix {

    private int columns;
    private int rows;
    private Value[][] matrix;

    public DenseMatrix(int rows, int columns) {
        this.columns = columns;
        this.rows = rows;
        matrix = new Value[rows][columns];
    }

    public DenseMatrix(int rows, int columns, Double... values) {
        this.columns = columns;
        this.rows = rows;
        matrix = new Value[rows][columns];

        int i = 0;
        for (Double val : values) {
            put(i / getColumns(), i % getRows(), new Value(val));
            i++;
        }
    }

    @Override
    public void put(int row, int column, Value value) {
        matrix[row][column] = value;
    }

    @Override
    public Value get(int row, int column) {
        if (matrix[row][column] != null) {
            return matrix[row][column];
        } else {
            // Default value needed.
            if (row == column) {
                return new Value(0.0);
            } else {
                return Value.NEGATIVE_INFINITY;
            }
        }
    }

    @Override
    public int getColumns() {
        return columns;
    }

    @Override
    public int getRows() {
        return rows;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            sb.append("| ");
            for (int j = 0; j < columns; j++) {
                sb.append(get(i, j).toString());
                sb.append("\t");
            }
            sb.append("|\n");
        }
        return sb.toString();
    }


}
