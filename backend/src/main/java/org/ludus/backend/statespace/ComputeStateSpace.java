package org.ludus.backend.statespace;

import org.ludus.backend.algebra.Matrix;
import org.ludus.backend.algebra.Value;
import org.ludus.backend.algebra.Vector;
import org.ludus.backend.algorithms.Tarjan;
import org.ludus.backend.fsm.FSM;

import java.util.*;

/**
 * Functions to construct normalized max-plus state spaces.
 *
 * @author Bram van der Sanden
 */
public final class ComputeStateSpace {

    private static final Value DEFAULT_REWARD = new Value(1.0);

    /**
     * Compute the normalized max-plus state space, where the reward of each edge event is given by
     * {@code DEFAULT_REWARD}.
     *
     * @param fsm        finite-state machine
     * @param vectorSize size of the starting vector
     * @param matrices   mapping of edge label to a corresponding matrix
     * @return max plus state space
     */
    public static <V, E> MaxPlusStateSpace computeMaxPlusStateSpace(FSM<V, E> fsm, Integer vectorSize, Map<String, Matrix> matrices) {
        return computeMaxPlusStateSpace(fsm, vectorSize, matrices, new HashMap<>());
    }

    /**
     * Compute the normalized max-plus state space.
     *
     * @param fsm        finite-state machine
     * @param vectorSize size of the starting vector
     * @param matrices   mapping of edge label to a corresponding matrix
     * @param rewardMap  mapping of edge label to a reward
     * @return max plus state space
     */
    public static <V, E> MaxPlusStateSpace computeMaxPlusStateSpace(FSM<V, E> fsm, Integer vectorSize, Map<String, Matrix> matrices, Map<String, Value> rewardMap) {
        // Start with the zero vector and the initial FSMImpl state.
        Vector vInitial = new Vector(vectorSize, new Value(0.0));
        Configuration<V> cInitial = new Configuration<>(fsm.getInitial(), vInitial);

        MaxPlusStateSpace S = new MaxPlusStateSpace();
        S.addConfiguration(cInitial);
        S.setInitialConfiguration(cInitial);

        Stack<Configuration<V>> stack = new Stack<>();

        // All edges that have been added to the stack at some point.
        Set<Configuration> visited = new HashSet<>();

        stack.add(cInitial);
        visited.add(cInitial);

        while (!stack.isEmpty()) {
            Configuration<V> c = stack.pop();
            for (E e : fsm.outgoingEdgesOf(c.getLocation())) {
                // Compute new normalized vector.
                String event = fsm.getEvent(e);
                Matrix eventMatrix = matrices.get(event);
                Vector newVector = eventMatrix.multiply(c.getVector());
                Vector newVectorNormalized = newVector.normalize();

                // Add new configuration.
                Configuration<V> cTarget = new Configuration<>(fsm.getEdgeTarget(e), newVectorNormalized);
                S.addConfiguration(cTarget);

                // Add transition to new configuration.
                Value duration = newVector.getNorm();
                Value reward = rewardMap.getOrDefault(event, DEFAULT_REWARD);

                Transition t = new Transition(c, fsm.getEvent(e), reward, duration, cTarget);
                S.addTransition(t);

                if (!visited.contains(cTarget)) {
                    // Add configuration to stack if we have not yet processed this one.
                    visited.add(cTarget);
                    stack.push(cTarget);
                }
            }
        }

        return S;
    }

    /**
     * Compute a new state space where both weights are negated.
     *
     * @param stateSpace input state space
     * @return equivalent state space where weights are negated
     */
    public static MaxPlusStateSpace negateWeights(MaxPlusStateSpace stateSpace) {
        Map<Configuration, Configuration> mapping = new HashMap<>();

        MaxPlusStateSpace mpss = new MaxPlusStateSpace();
        for (Configuration c : stateSpace.getVertices()) {
            Configuration c_new = new Configuration<>(c.getLocation(), c.getVector());
            mpss.addConfiguration(c_new);
            if (stateSpace.hasInitialConfiguration() && stateSpace.getInitialConfiguration().equals(c)) {
                mpss.setInitialConfiguration(c_new);
            }
            mapping.put(c, c_new);
        }

        for (Configuration c : stateSpace.getVertices()) {
            for (Transition t : stateSpace.outgoingEdgesOf(c)) {
                Value negate = new Value(-1.0d);
                mpss.addTransition(new Transition(
                        mapping.get(t.getSource()),
                        t.getEvent(),
                        t.getReward().multiply(negate),
                        t.getDuration().multiply(negate),
                        mapping.get(t.getTarget())));
            }
        }
        return mpss;
    }

    /**
     * Compute a new state space where the weights are swapped.
     * Duration becomes reward, and reward becomes duration.
     *
     * @param stateSpace input state space
     * @return equivalent state space where weights are swapped
     */
    public static MaxPlusStateSpace swapWeights(MaxPlusStateSpace stateSpace) {
        Map<Configuration, Configuration> mapping = new HashMap<>();

        MaxPlusStateSpace mpss = new MaxPlusStateSpace();
        for (Configuration c : stateSpace.getVertices()) {
            Configuration c_new = new Configuration<>(c.getLocation(), c.getVector());
            mpss.addConfiguration(c_new);
            if (stateSpace.hasInitialConfiguration() && stateSpace.getInitialConfiguration().equals(c)) {
                mpss.setInitialConfiguration(c_new);
            }
            mapping.put(c, c_new);
        }

        for (Configuration c : stateSpace.getVertices()) {
            for (Transition t : stateSpace.outgoingEdgesOf(c)) {
                mpss.addTransition(new Transition(
                        mapping.get(t.getSource()),
                        t.getEvent(),
                        t.getDuration(),
                        t.getReward(),
                        mapping.get(t.getTarget())));
            }
        }
        return mpss;
    }

    /**
     * Return a list of strongly connected components. Note that no initial state is set!
     *
     * @param stateSpace state space
     * @return list of strongly connected components
     */
    public static List<MaxPlusStateSpace> getSCCs(MaxPlusStateSpace stateSpace) {
        // Output list.
        List<MaxPlusStateSpace> sccList = new ArrayList<>();

        // SCCs in terms of configurations.
        Tarjan<Configuration, Transition> tarjan = new Tarjan<>();
        List<Set<Configuration>> sccVerticesList = tarjan.computeSCCs(stateSpace);
        for (Set<Configuration> scc : sccVerticesList) {
            // Generate a max-plus state space for the given scc.
            MaxPlusStateSpace mpssSCC = new MaxPlusStateSpace();

            for (Configuration c : scc) {
                mpssSCC.addConfiguration(c);
                for (Transition t : stateSpace.outgoingEdgesOf(c)) {
                    if (scc.contains(t.getTarget())) {
                        mpssSCC.addTransition(t);
                    }
                }
            }

            // Add only SCCs that actually have edges.
            if (mpssSCC.getEdges().size() > 0) {
                sccList.add(mpssSCC);
            }
        }
        return sccList;
    }

}
