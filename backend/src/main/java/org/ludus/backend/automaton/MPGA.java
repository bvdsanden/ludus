package org.ludus.backend.automaton;

import org.ludus.backend.games.ratio.solvers.policy.RatioGamePolicyIteration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Max-plus game automaton.
 * We assume that the nodes for player 1 have a negative index value.
 *
 * @author Bram van der Sanden
 */
public class MPGA<T> extends MaxPlusAutomaton<T> implements RatioGamePolicyIteration<MPAState<T>, MPATransition, Double> {

    private final Set<MPAState<T>> player0states;
    private final Set<MPAState<T>> player1states;
    private final Map<MPAState<T>, Integer> idMap;

    private Integer currentId;

    public MPGA() {
        super();
        player0states = new HashSet<>();
        player1states = new HashSet<>();
        idMap = new HashMap<>();
        currentId = 0;
    }

    @Override
    public void addState(MPAState<T> MPAState) {
        super.addState(MPAState);
        if (MPAState.getIndex() < 0) {
            player1states.add(MPAState);
        } else {
            player0states.add(MPAState);
        }
        idMap.put(MPAState, currentId);
        currentId++;
    }

    @Override
    public Set<MPAState<T>> getV0() {
        return player0states;
    }

    @Override
    public Set<MPAState<T>> getV1() {
        return player1states;
    }

    @Override
    public Double getMaxAbsValue() {
        Double max = Double.NEGATIVE_INFINITY;
        for (MPATransition t : getEdges()) {
            max = Double.max(max, getWeight1(t));
            max = Double.max(max, getWeight2(t));
        }
        return max;
    }

    @Override
    public Integer getId(MPAState<T> vertex) {
        return idMap.get(vertex);
    }
}