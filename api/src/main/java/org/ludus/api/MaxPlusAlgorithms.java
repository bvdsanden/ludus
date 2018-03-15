package org.ludus.api;

import org.ludus.api.algorithm.MaximumMakespanAlgorithm;
import org.ludus.api.algorithm.MaximumThroughputAlgorithm;
import org.ludus.api.algorithm.MinimumMakespanAlgorithm;
import org.ludus.api.algorithm.MinimumThroughputAlgorithm;
import org.ludus.backend.algebra.Matrix;
import org.ludus.backend.fsm.FSM;
import org.ludus.backend.fsm.impl.Edge;
import org.ludus.backend.fsm.impl.Location;

import java.util.Map;

/**
 * Algorithms that are provided by the max-plus toolbox.
 *
 * @author Bram van der Sanden
 * @since 2017-04-27
 */
public final class MaxPlusAlgorithms {
    private MaxPlusAlgorithms() {
        // Empty for utility classes
    }

    /**
     * Given a max-plus specification with an FSM and a timing matrix for each event,
     * calculate the minimum makespan value and the corresponding event sequence.
     *
     * @param fsm       finite-state machine
     * @param matrixMap map with activity to activity matrix
     * @return minimum makespan value and corresponding event sequence
     * @throws MaxPlusException if the matrices or FSM are incorrect
     */
    public static MinimumMakespanResult calculateMinimumMakespan(FSM<Location, Edge> fsm, Map<String, Matrix> matrixMap) throws MaxPlusException {
        return MinimumMakespanAlgorithm.run(fsm, matrixMap);
    }

    /**
     * Given a max-plus specification with an FSM and a timing matrix for each event, calculate the maximum makespan
     * value and the corresponding event sequence.
     *
     * @param fsm       finite-state machine
     * @param matrixMap map with activity to activity matrix
     * @return maximum makespan value and corresponding event sequence
     * @throws MaxPlusException if the matrices or FSM are incorrect
     */
    public static MaximumMakespanResult calculateMaximumMakespan(FSM<Location, Edge> fsm, Map<String, Matrix> matrixMap) throws MaxPlusException {
        return MaximumMakespanAlgorithm.run(fsm, matrixMap);
    }

    /**
     * Given a max-plus specification with an FSM and a timing matrix for each event, calculate the maximum throughput
     * value and the corresponding event sequence.
     *
     * @param fsm       finite-state machine
     * @param matrixMap map with activity to activity matrix
     * @return maximum throughput value and corresponding event sequence
     * @throws MaxPlusException if the matrices or FSM are incorrect
     */
    public static MaximumThroughputResult calculateMaximumThroughput(FSM<Location, Edge> fsm, Map<String, Matrix> matrixMap) throws MaxPlusException {
        return MaximumThroughputAlgorithm.runMaxPlusStateSpace(fsm, matrixMap);

    }

    /**
     * Given a max-plus specification with an FSM and a timing matrix for each event, calculate the minimum throughput
     * value and the corresponding event sequence.
     *
     * @param fsm       finite-state machine
     * @param matrixMap map with activity to activity matrix
     * @return minimum throughput value and corresponding event sequence
     * @throws MaxPlusException if the matrices or FSM are incorrect
     */
    public static MinimumThroughputResult calculateMinimumThroughput(FSM<Location, Edge> fsm, Map<String, Matrix> matrixMap) throws MaxPlusException {
        return MinimumThroughputAlgorithm.run(fsm, matrixMap);
    }
}