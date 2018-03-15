package org.ludus.api.algorithm;

import org.ludus.api.MaxPlusException;
import org.ludus.backend.algebra.Matrix;
import org.ludus.backend.algorithms.CycleCheck;
import org.ludus.backend.fsm.FSM;
import org.ludus.backend.fsm.impl.Edge;
import org.ludus.backend.fsm.impl.Location;

import java.util.*;

public class MaxPlusAlgorithm {

    protected static <V, E> void checkNotCyclic(FSM<V, E> fsm) throws MaxPlusException {
        // Check if the FSM is cyclic.
        if (CycleCheck.check(fsm)) {
            throw new MaxPlusException("Finite-state machine contains a cycle.");
        }
    }

    protected static <V, E> void checkCyclic(FSM<V, E> fsm) throws MaxPlusException {
        // Check if the FSM is cyclic.
        if (!CycleCheck.check(fsm)) {
            throw new MaxPlusException("Finite-state machine does not contain a cycle.");
        }
    }

    protected static <V, E> void checkNoDeadlocks(FSM<V, E> fsm) throws MaxPlusException {
        // Check if there are no deadlocks.
        for (V l : fsm.getVertices()) {
            if (fsm.outgoingEdgesOf(l).isEmpty()) {
                throw new MaxPlusException("Vertex " + l.toString() + " has no outgoing edges.");
            }
        }
    }

    /**
     * Check that all the matrices have the same number of rows and columns.
     *
     * @param matrixCollection collection of matrices to compare
     * @return true if and only if all matrices in the collection have the same size
     */
    protected static void checkAllMatricesSameSize(Collection<Matrix> matrixCollection) throws MaxPlusException {
        Matrix first = matrixCollection.iterator().next();
        int rows = first.getRows();
        int columns = first.getColumns();

        for (Matrix m : matrixCollection) {
            if (m.getRows() != rows) {
                throw new MaxPlusException("Matrices have different row sizes.");
            }
            if (m.getColumns() != columns) {
                throw new MaxPlusException("Matrices have different column sizes.");
            }
        }
    }

    protected static void checkEventMapping(FSM<Location, Edge> fsm, Map<String,Matrix> matrixMap) throws MaxPlusException {
        for (Edge e : fsm.getEdges()) {
            if (!matrixMap.containsKey(e.getEvent())) {
                throw new MaxPlusException("No matrix found for event " + e.getEvent());
            }
        }
    }

}
