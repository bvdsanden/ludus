package org.ludus.backend.automaton;

import org.ludus.backend.algebra.Matrix;
import org.ludus.backend.algebra.Value;
import org.ludus.backend.algorithms.Tarjan;
import org.ludus.backend.fsm.FSM;

import java.util.*;

/**
 * Functions to construct max-plus automata.
 *
 * @author Bram van der Sanden
 */
public class ComputeMPA {

    private static final Value DEFAULT_REWARD = new Value(1.0);

    /**
     * Compute a max plus automaton, where the reward of each edge event is set to
     * {@code DEFAULT_REWARD}.
     *
     * @param fsm        finite-state machine
     * @param vectorSize size of the starting vector
     * @param matrices   mapping of edge label to a corresponding matrix
     * @return max plus automaton
     */
    public static <V, E> MaxPlusAutomaton<V> computeMaxPlusAutomaton(FSM<V, E> fsm, Integer vectorSize, Map<String, Matrix> matrices) {
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
    public static <V, E> MaxPlusAutomaton<V> computeMaxPlusAutomaton(FSM<V, E> fsm, Integer vectorSize, Map<String, Matrix> matrices, Map<String, Value> rewardMap) {
        MaxPlusAutomaton<V> mpa = new MaxPlusAutomaton<>();

        // Add the states.
        for (V v : fsm.getVertices()) {
            for (int index = 0; index < vectorSize; index++) {
                mpa.addState(new MPAState<>(v, index));
            }
        }

        // Add the transitions.
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

                    // Add transition if the edge duration is not -Infinity.
                    if (!duration.equals(Value.NEGATIVE_INFINITY)) {
                        mpa.addTransition(new MPATransition(source, event, reward, duration, target));
                    }
                }
            }
        }

        return mpa;
    }

    /**
     * Compute a new max plus automaton where the weights are swapped.
     * Duration becomes reward, and reward becomes duration.
     *
     * @param automaton input automaton
     * @return equivalent automaton where weights are swapped
     */
    public static <T> MaxPlusAutomaton<T> swapWeights(MaxPlusAutomaton<T> automaton) {
        Map<MPAState, MPAState> mapping = new HashMap<>();

        MaxPlusAutomaton<T> mpa = new MaxPlusAutomaton<>();
        for (MPAState<T> s : automaton.getVertices()) {
            MPAState<T> s_new = new MPAState<>(s.getLocation(), s.getIndex());
            mpa.addState(s_new);
            mapping.put(s, s_new);
        }

        for (MPAState<T> c : automaton.getVertices()) {
            for (MPATransition t : automaton.outgoingEdgesOf(c)) {
                mpa.addTransition(new MPATransition(
                        mapping.get(t.getSource()),
                        t.getEvent(),
                        t.getDuration(),
                        t.getReward(),
                        mapping.get(t.getTarget())));
            }
        }
        return mpa;
    }

    /**
     * Return a list of strongly connected components. Note that no initial state is set!
     *
     * @param automaton input max-plus automaton
     * @return list of max-plus automata, consisting of all strongly connected components
     */
    public static <T> List<MaxPlusAutomaton<T>> getSCCs(MaxPlusAutomaton<T> automaton) {
        // Output list.
        List<MaxPlusAutomaton<T>> sccList = new ArrayList<>();

        // SCCs in terms of MPAstates.
        Tarjan tarjan = new Tarjan();
        List<Set<MPAState<T>>> sccVerticesList = tarjan.computeSCCs(automaton);
        for (Set<MPAState<T>> scc : sccVerticesList) {
            // Generate a max-plus automaton for the given scc.
            MaxPlusAutomaton<T> mpaSCC = new MaxPlusAutomaton<>();

            for (MPAState<T> s : scc) {
                mpaSCC.addState(s);
                for (MPATransition t : automaton.outgoingEdgesOf(s)) {
                    if (scc.contains(t.getTarget())) {
                        mpaSCC.addTransition(t);
                    }
                }
            }

            // Add only SCCs that actually have edges.
            if (mpaSCC.getEdges().size() > 0) {
                sccList.add(mpaSCC);
            }
        }
        return sccList;
    }

}
