package org.ludus.backend.games.meanpayoff.solvers.policy;

import org.ludus.backend.datastructures.tuple.Triple;
import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.games.StrategyVector;
import org.apache.commons.math3.fraction.Fraction;

import java.util.*;

/**
 * Policy Iteration Algorithm to solve mean-payoff games.
 * <p>
 * The algorithm start with random strategies for both players, and then starts
 * improving the strategies until convergence. When the algorithm terminates,
 * both strategies are optimal.
 * <p>
 * The algorithm has been adapted from the MDG algorithm by Chaloupka, see also
 * Chaloupka, 2011, Algorithms for Mean-Payoff and Energy Games (PhD thesis).
 *
 * @author Bram van der Sanden
 */
public class PolicyIterationInt {

    private static final Fraction MINUS_INFINITY = new Fraction(Integer.MIN_VALUE);

    /**
     * Determine the exact value of each vertex in the given ratio game.
     *
     * @param <V>  vertex type
     * @param <E>  edge type
     * @param game ratio game that is solved
     * @return the exact value of each vertex in the given ratio game and the
     * optimal strategy.
     */
    public static <V, E> Tuple<Map<V, Fraction>, StrategyVector>
    solve(MeanPayoffGamePolicyIteration<V, E, Integer> game) {

        Collection<V> vertices = game.getVertices();

        boolean improvement = true;

        // Initial distance vector.
        Map<V, Fraction> distVector = initializeVector(vertices, Fraction.ZERO);

        // Initial ratio vector.
        Map<V, Fraction> meanPayoffVector = initializeVector(vertices, MINUS_INFINITY);

        // Initialize arbitrary positional strategies.
        StrategyVector<V, E> currentStrategy = new StrategyVector<>();
        currentStrategy.initializeRandomStrategy(game);

        while (improvement) {
            // Improve the strategy of player 1.
            Triple<Map<V, Fraction>, Map<V, Fraction>, StrategyVector<V, E>> result
                    = improveStrategyPlayer1(game, currentStrategy, distVector, meanPayoffVector);
            distVector = result.getLeft();
            meanPayoffVector = result.getMiddle();
            currentStrategy = result.getRight();

            improvement = false;
            for (V v : game.getV0()) {
                for (E e : game.outgoingEdgesOf(v)) {
                    V u = game.getEdgeTarget(e);

                    Fraction mw = meanPayoffVector.get(u);
                    Integer weight = game.getWeight(e);

                    Fraction reweighted = new Fraction(weight).subtract(mw);

                    if (isSmaller(meanPayoffVector.get(v), meanPayoffVector.get(u))
                            || (meanPayoffVector.get(v).equals(meanPayoffVector.get(u))
                            && isSmaller(distVector.get(v), (distVector.get(u).add(reweighted))))) {
                        currentStrategy.setSuccessor(v, u);
                        improvement = true;
                    }
                }
            }
        }

        return Tuple.of(meanPayoffVector, currentStrategy);
    }

    /**
     * Improve the strategy of player 1 until the strategy is optimal against
     * the current strategy of player 0. This procedure has been adapted from
     * MDG in (Chaloupka, 2011).
     *
     * @param <V>             vertex type
     * @param <E>             edge type
     * @param game            current ratio game
     * @param currentStrategy current strategy of both players
     * @param d_prev          previous distance vector
     * @param m_prev          previous mean-payoff vector
     * @return new distance and ratio vectors, and a new strategy vector with an
     * updated strategy for player 1
     */
    protected static <V, E> Triple<Map<V, Fraction>, Map<V, Fraction>, StrategyVector<V, E>>
    improveStrategyPlayer1(MeanPayoffGamePolicyIteration<V, E, Integer> game, StrategyVector<V, E> currentStrategy,
                           Map<V, Fraction> d_prev, Map<V, Fraction> m_prev) {
        int t = -1;
        boolean improvement = true;
        Map<V, Fraction> d_i_t = new HashMap<>(d_prev);
        Map<V, Fraction> m_i_t = new HashMap<>(m_prev);
        StrategyVector<V, E> s_i_t = new StrategyVector<>(currentStrategy);
        while (improvement) {
            t = t + 1;
            Tuple<Map<V, Fraction>, Map<V, Fraction>> evalResult = EvaluateStrategy(game, s_i_t, d_prev, m_prev);
            d_i_t = evalResult.getLeft();
            m_i_t = evalResult.getRight();
            improvement = false;
            for (V v : game.getV1()) {
                for (E e : game.outgoingEdgesOf(v)) {
                    V u = game.getEdgeTarget(e);

                    Fraction mw = m_i_t.get(u);
                    Integer weight = game.getWeight(e);

                    Fraction reweighted = new Fraction(weight).subtract(mw);

                    if (isLarger(m_i_t.get(v), m_i_t.get(u))
                            || ((m_i_t.get(v).equals(m_i_t.get(u)) && isLarger(d_i_t.get(v), (d_i_t.get(u).add(reweighted)))))) {
                        // Improve strategy.
                        s_i_t.setSuccessor(v, u);
                        improvement = true;
                    }
                }
            }
        }
        return Triple.of(d_i_t, m_i_t, s_i_t);
    }

    /**
     * Evaluate the given strategies of both players, i.e. calculate both the
     * distance and ratio of each vertex in the out-degree-one graph, and return
     * the vectors.
     *
     * @param <V>            vertex type
     * @param <E>            edge type
     * @param game           out-degree-one graph to be evaluated
     * @param strategy       current strategy of both players
     * @param distanceVector old distance vector
     * @param payoffVector   old payoff vector
     * @return the new distance and payoff vectors
     */
    protected static <V, E> Tuple<Map<V, Fraction>, Map<V, Fraction>> EvaluateStrategy(
            MeanPayoffGamePolicyIteration<V, E, Integer> game, StrategyVector<V, E> strategy,
            Map<V, Fraction> distanceVector, Map<V, Fraction> payoffVector) {
        // Find the selected vertex in each cycle, and store the ratio value.
        Tuple<Set<V>, Map<V, Fraction>> cycleResult
                = FindCyclesInRestrictedGraph(game, strategy);
        Map<V, Fraction> m_i_t = cycleResult.getRight();
        // Calculate the values for both vectors given the selected vertices 
        // and the ratio of each cycle.
        Tuple<Map<V, Fraction>, Map<V, Fraction>> cd
                = ComputeDistances(game, strategy, cycleResult.getLeft(),
                m_i_t, distanceVector, payoffVector);
        // Values of the new vectors.
        Map<V, Fraction> d_i_t = cd.getLeft();
        m_i_t = cd.getRight();
        return Tuple.of(d_i_t, m_i_t);
    }

    /**
     * Find all cycles in the out-degree-one graph given the current strategy.
     * Return all selected vertices in those cycles and the ratio of each cycle,
     * stored in the ratio vector at the selected vertex entry.
     *
     * @param game            out-degree-one graph to be evaluated
     * @param currentStrategy current strategy of both players
     * @return selected vertex in each cycle and the ratio value
     */
    protected static <V, E> Tuple<Set<V>, Map<V, Fraction>> FindCyclesInRestrictedGraph(
            MeanPayoffGamePolicyIteration<V, E, Integer> game, StrategyVector<V, E> currentStrategy) {
        final V BOTTOM_VERTEX = null;

        Set<V> selectedVertices = new HashSet<>();
        // Initially, all vertices are unvisited.
        Map<V, V> visited
                = initializeVector(game.getVertices(), BOTTOM_VERTEX);

        Map<V, Fraction> m_i_t = new HashMap<>();
        for (V v : game.getVertices()) {
            if (visited.get(v) == BOTTOM_VERTEX) {
                V u = v;
                while (visited.get(u) == BOTTOM_VERTEX) {
                    visited.put(u, v);
                    u = currentStrategy.getSuccessor(u);
                }
                if (visited.get(u) == v) {
                    V v_s = u;
                    V x = currentStrategy.getSuccessor(u);

                    // Initialize both numerator and denominator.
                    E e = game.getEdge(u, currentStrategy.getSuccessor(u));

                    Integer cycleWeight = game.getWeight(e);
                    Integer cycleLength = 1;

                    while (x != u) {
                        // Find the vertex with the lowest id for unique 
                        // ordering, to ensure always the same vertex is the 
                        // "selected vertex" in the cycle.
                        if (game.getId(x) < game.getId(v_s)) {
                            v_s = x;
                        }
                        E x_sucx = game.getEdge(x, currentStrategy.getSuccessor(x));

                        cycleWeight += game.getWeight(x_sucx);
                        cycleLength += 1;
                        x = currentStrategy.getSuccessor(x);
                    }
                    // Store the cycle ratio for the cycle containing v_s.
                    m_i_t.put(v_s, new Fraction(cycleWeight, cycleLength));
                    selectedVertices.add(v_s);
                }
            }
        }
        return Tuple.of(selectedVertices, m_i_t);
    }

    /**
     * Compute the new distance and mean-payoff vectors.
     *
     * @param game             ratio game
     * @param currentStrategy  current strategy of both players
     * @param selectedVertices selected vertices of all cycles in the graph
     * @param m_i_t            ratio vector, given the new player-1 strategy
     * @param d_prev           previous distance vector
     * @param r_prev           previous ratio vector
     * @return
     */
    protected static <V, E> Tuple<Map<V, Fraction>, Map<V, Fraction>> ComputeDistances(
            MeanPayoffGamePolicyIteration<V, E, Integer> game, StrategyVector<V, E> currentStrategy,
            Collection<V> selectedVertices,
            Map<V, Fraction> m_i_t,
            Map<V, Fraction> d_prev, Map<V, Fraction> r_prev) {
        Stack<V> stack = new Stack<>();
        // Initialize visited vector.
        Map<V, Boolean> visited = initializeVector(game.getVertices(), false);
        // Initialize selected vertices.
        Map<V, Fraction> d_i_t = new HashMap<>();

        for (V u : selectedVertices) {
            if (r_prev.get(u).equals(m_i_t.get(u))) {
                d_i_t.put(u, d_prev.get(u));
            } else {
                d_i_t.put(u, Fraction.ZERO);
            }
            visited.put(u, true);
        }

        // For all vertices.
        for (V v : game.getVertices()) {
            if (!visited.get(v)) {
                V u = v;
                while (!visited.get(u)) {
                    visited.put(u, true);
                    stack.push(u);
                    u = currentStrategy.getSuccessor(u);
                }
                while (!stack.isEmpty()) {
                    V x = stack.pop();
                    E e = game.getEdge(x, u);

                    Integer weight = game.getWeight(e);
                    Fraction cyclePayoff = m_i_t.get(u);
                    // Update cycle mean-payoff.
                    m_i_t.put(x, cyclePayoff);
                    // Update distance to cycle.
                    d_i_t.put(x, d_i_t.get(u).add(weight).subtract(m_i_t.get(u)));
                    u = x;

                }
            }
        }
        return Tuple.of(d_i_t, m_i_t);
    }

    /**
     * Check whether fraction {@code f1} is smaller than {@code f2}
     *
     * @param frac1 first fraction
     * @param frac2 second fraction
     * @return true if and only if frac1 < frac2.
     */
    private static boolean isSmaller(Fraction frac1, Fraction frac2) {
        return frac1.compareTo(frac2) < 0;
    }

    /**
     * Check whether fraction {@code f1} is larger than {@code f2}
     *
     * @param frac1 first fraction
     * @param frac2 second fraction
     * @return true if and only if frac1 > frac2.
     */
    private static boolean isLarger(Fraction frac1, Fraction frac2) {
        return frac1.compareTo(frac2) > 0;
    }

    /**
     * Initialize a vector, where each vertex is mapped to a default value
     * {@code value}.
     *
     * @param <T>      default value type
     * @param vertices keys
     * @param value    default value for each key
     * @return hash map where each vertex is mapped to the default value
     */
    private static <V, T> Map<V, T> initializeVector(Collection<V> vertices, T value) {
        Map<V, T> vector = new HashMap<>();
        vertices.stream().forEach((v) -> vector.put(v, value));
        return vector;
    }

}
