package org.ludus.backend.algebra;

import org.ludus.backend.datastructures.tuple.Tuple;

import java.util.HashMap;
import java.util.Map;

/**
 * Max-plus sparse matrix.
 *
 * @author Bram van der Sanden
 */
public class SparseMatrix extends Matrix {

    private int rows;
    private int columns;
    private Map<Tuple, Value> valueMap;

    public SparseMatrix(int rows, int columns) {
        this.columns = columns;
        this.rows = rows;
        valueMap = new HashMap<>();
    }

    @Override
    public void put(int row, int column, Value value) {
        valueMap.put(Tuple.of(row, column), value);
    }

    @Override
    public Value get(int row, int column) {
        Tuple<Integer, Integer> index = Tuple.of(row, column);
        if (valueMap.containsKey(index)) {
            return valueMap.get(index);
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