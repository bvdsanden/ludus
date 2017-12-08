package org.ludus.backend.automaton;

import org.ludus.backend.algebra.Matrix;
import org.ludus.backend.algebra.Value;
import org.ludus.backend.fsm.FSM;

import java.util.HashMap;
import java.util.Map;

/**
 * Functions to construct max-plus automata.
 *
 * @author Bram van der Sanden
 */
public class ComputeMPGA {

    private static final Value DEFAULT_REWARD = new Value(1.0);

    /**
     * Compute a max plus automaton, where the reward of each edge event is set to
     * {@code DEFAULT_REWARD}.
     *
     * @param fsm        finite-state machine
     * @param vectorSize size of the starting vector
     * @param matrices   mapping of edge label to a corresponding matrix
     * @return max plus game automaton
     */
    public static <V, E> MPGA<V> computeMaxPlusAutomaton(FSM<V, E> fsm, Integer vectorSize, Map<String, Matrix> matrices) {
        return computeMaxPlusAutomaton(fsm, vectorSize, matrices, new HashMap<>());
    }

    /**
     * Compute a max-plus automaton.
     *
     * @param fsm        finite-state machine
     * @param vectorSize size of the starting vector
     * @param matrices   mapping of edge label to a corresponding matrix
     * @param rewardMap  mapping of edge label to a reward
     * @return max plus automaton
     */
    public static <V, E> MPGA<V> computeMaxPlusAutomaton(FSM<V, E> fsm, Integer vectorSize, Map<String, Matrix> matrices, Map<String, Value> rewardMap) {
        MPGA<V> mpa = new MPGA<>();

        // Add the player 0 states.
        for (V v : fsm.getVertices()) {
            for (int index = 0; index < vectorSize; index++) {
                mpa.addState(new MPAState<>(v, index));
            }
        }

        // Add the player 1 states and the transitions.
        for (E e : fsm.getEdges()) {
            String event = fsm.getEvent(e);
            Matrix m = matrices.get(event);

            // For each matrix entry
            for (int row = 0; row < m.getRows(); row++) {
                for (int col = 0; col < m.getColumns(); col++) {
                    // Reward value.
                    Value reward = rewardMap.getOrDefault(event, DEFAULT_REWARD);
                    // Duration value.
                    Value duration = m.get(row, col);

                    MPAState source = mpa.getState(fsm.getEdgeSource(e), row);
                    MPAState target = mpa.getState(fsm.getEdgeTarget(e), col);

                    MPAState intermediateState = mpa.getState(fsm.getEdgeTarget(e), getMirrorIndex(row));
                    if (intermediateState == null) {
                        intermediateState = new MPAState<>(fsm.getEdgeTarget(e), getMirrorIndex(row));
                        mpa.addState(intermediateState);
                    }

                    // Add transition if the edge duration is not -Infinity.
                    if (!duration.equals(Value.NEGATIVE_INFINITY)) {
                        // Transition from source to intermediate.
                        mpa.addTransition(new MPATransition(source, event, new Value(0.0d), new Value(0.0d), intermediateState));
                        // Transition from intermediate to target.
                        mpa.addTransition(new MPATransition(intermediateState, event, reward, duration, target));
                    }
                }
            }
        }

        return mpa;
    }

    /**
     * Given an matrix index, we define a negative mirror index that is used for player 2 vertices.
     *
     * @param index input index
     * @return negative mirror index
     */
    public static Integer getMirrorIndex(Integer index) {
        return (index * -1) - 1;
    }

}
