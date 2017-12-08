package org.ludus.backend.games.ratio.solvers.policy;

import org.ludus.backend.datastructures.tuple.Quadruple;
import org.ludus.backend.datastructures.tuple.Triple;
import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.games.algorithms.DoubleFunctions;
import org.ludus.backend.games.algorithms.GraphChecks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Policy Iteration Algorithm to solve ratio games.
 * <p>
 * The algorithm start with random strategies for both players, and then starts
 * improving the strategies until convergence. When the algorithm terminates,
 * both strategies are optimal.
 * <p>
 * The algorithm has been adapted from the MDG algorithm by Chaloupka, see also
 * Chaloupka, 2011, Algorithms for Mean-Payoff and Energy Games (PhD thesis).
 * <p>
 * Note that the distance comparision in this algorithm is a fixed value.
 * In the other implementation, delta is dynamically determined.
 *
 * @author Bram van der Sanden
 */
public class PolicyIterationDoubleVars {

    final static Logger logger = LoggerFactory.getLogger(PolicyIterationDoubleVars.class);

    private static final double MINUS_INFTY = Double.NEGATIVE_INFINITY;


    /**
     * Find the values of the vertices and an optimal strategy for the given
     * ratio game.
     *
     * @param <V>  vertex type
     * @param <E>  edge type
     * @param game game graph
     * @return value of each vertex and optimal strategy
     */
    public static <V, E> Tuple<Map<V, Double>, StrategyVector<V, E>> solve(RatioGamePolicyIteration<V, E, Double> game) {
        return solve(game, DoubleFunctions.EPSILON, DoubleFunctions.DELTA);
    }

    /**
     * Find the values of the vertices and an optimal strategy for the given
     * ratio game.
     *
     * @param <V>     vertex type
     * @param <E>     edge type
     * @param game    game graph
     * @param epsilon vertex ratio value equivalence is up to epsilon
     * @param delta   vertex distance value equivalence is up to delta
     * @return value of each vertex and optimal strategy
     */
    public static <V, E> Tuple<Map<V, Double>, StrategyVector<V, E>> solve(RatioGamePolicyIteration<V, E, Double> game,
                                                                           Double epsilon, Double delta) {
        if (GraphChecks.checkEachNodeHasSuccessor(game)) {

            // Initialize arbitrary positional strategies.
            StrategyVector<V, E> initialStrategy = new StrategyVector<>();
            initialStrategy.initializeRandomStrategy(game);
            return policyIteration(game, initialStrategy, epsilon, delta);
        } else {
            System.out.println("Input game graph is not valid. "
                    + "Not every vertex has a successor.");
            return null;
        }
    }

    /**
     * Find the values of the vertices and an optimal strategy for the given
     * ratio game.
     *
     * @param <V>             vertex type
     * @param <E>             edge type
     * @param game            game graph
     * @param initialStrategy initial strategy
     * @param epsilon         vertex ratio value equivalence is up to epsilon
     * @param delta           vertex distance value equivalence is up to delta
     * @return value of each vertex and optimal strategy
     */
    public static <V, E> Tuple<Map<V, Double>, StrategyVector<V, E>> solve(RatioGamePolicyIteration<V, E, Double> game,
                                                                           StrategyVector<V, E> initialStrategy,
                                                                           Double epsilon, Double delta) {
        if (GraphChecks.checkEachNodeHasSuccessor(game)) {
            return policyIteration(game, initialStrategy, epsilon, delta);
        } else {
            System.out.println("Input game graph is not valid. "
                    + "Not every vertex has a successor.");
            return null;
        }
    }

    /**
     * Determine the exact value of each vertex in the given ratio game.
     *
     * @param game            ratio game that is solved
     * @param initialStrategy initial strategies
     * @param epsilon         vertex ratio value equivalence is up to epsilon
     * @param delta           vertex distance value equivalence is up to delta
     * @param <V>             vertex type
     * @param <E>             edge type
     * @return the exact value of each vertex in the given ratio game and the
     * optimal strategy.
     */
    private static <V, E> Tuple<Map<V, Double>, StrategyVector<V, E>> policyIteration(
            RatioGamePolicyIteration<V, E, Double> game, StrategyVector<V, E> initialStrategy,
            Double epsilon, Double delta) {

        Collection<V> vertices = game.getVertices();

        boolean improvement = true;

        // Initial distance vector.
        Map<V, Double> distVector = initializeVector(vertices, Double.POSITIVE_INFINITY);

        Map<V, Double> dw2vector = initializeVector(vertices, Double.POSITIVE_INFINITY);

        // Initial ratio vector.
        Map<V, Double> ratioVector = initializeVector(vertices, MINUS_INFTY);

        // Initialize arbitrary positional strategies.
        StrategyVector<V, E> currentStrategy = initialStrategy;

        //logger.debug("Initial strategy: {}", printStrategy(game,currentStrategy));

        while (improvement) {
            improvement = false;

            // Improve the strategy of player 1.
            Quadruple<Map<V, Double>, Map<V, Double>, StrategyVector<V, E>, Map<V, Double>> result
                    = improveStrategyPlayer1(game, currentStrategy, distVector, ratioVector, dw2vector, epsilon, delta);

            //System.out.println("Start Player 0");
            distVector = result.getLeft();
            ratioVector = result.getMiddleLeft();
            currentStrategy = result.getMiddleRight();
            dw2vector = result.getRight();

            //logger.debug("Vectors after stategy improvement player 1: ");
            //logger.debug("Distance vector: {}", printVector(game,distVector));
            //logger.debug("Ratio vector: {}", printVector(game,ratioVector));

            // Improve the strategy of player 0, just one iteration.            
            //logger.debug("Player0: Start strategy improvement.");

            for (V v : game.getV0()) {
                for (E e : game.outgoingEdgesOf(v)) {
                    V u = game.getEdgeTarget(e);

                    double mw = ratioVector.get(u);
                    double w1 = game.getWeight1(e);
                    double w2 = game.getWeight2(e);

                    double reweighted = w1 - mw * w2;

                    Double dv = distVector.get(v);
                    Double du = distVector.get(u) + reweighted;

                    // Player 0 wants to maximize the ratio.
                    if (DoubleFunctions.lessThan(ratioVector.get(v), ratioVector.get(u), epsilon)
                            || (DoubleFunctions.equalTo(ratioVector.get(v), ratioVector.get(u), epsilon)
                            && (DoubleFunctions.lessThan(dv, du, delta)))) {
                        currentStrategy.setSuccessor(v, u);
                        improvement = true;

                        //logger.debug("Player0 strategy improvement: {} --> {}", game.getId(v), game.getId(u));
                    }
                }
            }
            //logger.debug("Player0: End strategy improvement.");
        }

        return Tuple.of(ratioVector, currentStrategy);
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
     * @param r_prev          previous ratio vector
     * @param epsilon         epsilon value for equality on real numbers
     * @return new distance and ratio vectors, and a new strategy vector with an
     * updated strategy for player 1
     */
    private static <V, E> Quadruple<Map<V, Double>, Map<V, Double>, StrategyVector<V, E>, Map<V, Double>>
    improveStrategyPlayer1(RatioGamePolicyIteration<V, E, Double> game, StrategyVector<V, E> currentStrategy,
                           Map<V, Double> d_prev, Map<V, Double> r_prev, Map<V, Double> dw2_prev,
                           Double epsilon, Double delta) {

        // Start strategy improvement of Player 1.
        //logger.debug("Player1: Start strategy improvement.");

        boolean improvement = true;
        Map<V, Double> d_i_t = new HashMap<>(d_prev);
        Map<V, Double> r_i_t = new HashMap<>(r_prev);
        Map<V, Double> dw2_i_t = new HashMap<>(dw2_prev);
        StrategyVector<V, E> s_i_t = new StrategyVector<>(currentStrategy);

        //logger.debug("Distance vector: {}", printVector(game,d_i_t));
        //logger.debug("Ratio vector: {}", printVector(game,r_i_t));

        while (improvement) {
            improvement = false;
            // Evaluate the current strategies of both players.            
            Triple<Map<V, Double>, Map<V, Double>, Map<V, Double>> evalResult
                    = EvaluateStrategy(game, s_i_t, d_prev, r_prev, dw2_prev, epsilon);

            // Update the current vectors.  
            d_i_t = evalResult.getLeft();
            r_i_t = evalResult.getMiddle();
            dw2_i_t = evalResult.getRight();

            for (V v : game.getV1()) {
                for (E e : game.outgoingEdgesOf(v)) {
                    V u = game.getEdgeTarget(e);

                    double cycleRatio = r_i_t.get(u);
                    double w1 = game.getWeight1(e);
                    double w2 = game.getWeight2(e);

                    double reweighted = w1 - cycleRatio * w2;

                    Double dv = d_i_t.get(v);
                    Double du = d_i_t.get(u) + reweighted;

                    if (DoubleFunctions.greaterThan(r_i_t.get(v), r_i_t.get(u), epsilon)
                            || (DoubleFunctions.equalTo(r_i_t.get(v), r_i_t.get(u), epsilon)
                            && (DoubleFunctions.greaterThan(dv, du, delta)))) {

                        // Improve strategy if either a smaller ratio 
                        // can be obtained, or the distance becomes smaller.
                        s_i_t.setSuccessor(v, u);
                        //logger.debug("Player1 strategy improvement: {} --> {}", 
                        //        game.getId(v), game.getId(u));
                        improvement = true;
                    }
                }
            }

        }
        //logger.debug("Player1: End strategy improvement.");
        return Quadruple.of(d_i_t, r_i_t, s_i_t, dw2_i_t);
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
     * @param ratioVector    old ratio vector
     * @return the new distance and ratio vectors
     */
    private static <V, E> Triple<Map<V, Double>, Map<V, Double>, Map<V, Double>> EvaluateStrategy(
            RatioGamePolicyIteration<V, E, Double> game, StrategyVector<V, E> strategy,
            Map<V, Double> distanceVector, Map<V, Double> ratioVector, Map<V, Double> dw2, Double epsilon) {
        // Find the selected vertex in each cycle, and store the ratio value.
        Tuple<Set<V>, Map<V, Double>> cycleResult
                = FindCyclesInRestrictedGraph(game, strategy);
        Map<V, Double> r_i_t = cycleResult.getRight();
        // Calculate the values for both vectors given the selected vertices 
        // and the ratio of each cycle.
        Triple<Map<V, Double>, Map<V, Double>, Map<V, Double>> cd
                = ComputeDistances(game, strategy, cycleResult.getLeft(),
                r_i_t, distanceVector, ratioVector, dw2, epsilon);
        // Values of the new vectors.
        Map<V, Double> d_i_t = cd.getLeft();
        r_i_t = cd.getMiddle();
        Map<V, Double> dw2_i_t = cd.getRight();
        return Triple.of(d_i_t, r_i_t, dw2_i_t);
    }

    /**
     * Find all cycles in the out-degree-one graph given the current strategy.
     * Return all selected vertices in those cycles and the ratio of each cycle,
     * stored in the ratio vector at the selected vertex entry.
     *
     * @param <V>             vertex type
     * @param <E>             edge type
     * @param game            out-degree-one graph to be evaluated
     * @param currentStrategy current strategy of both players
     * @return selected vertex in each cycle and the ratio value
     */
    private static <V, E> Tuple<Set<V>, Map<V, Double>> FindCyclesInRestrictedGraph(
            RatioGamePolicyIteration<V, E, Double> game, StrategyVector<V, E> currentStrategy) {
        final V BOTTOM_VERTEX = null;

        Set<V> selectedVertices = new HashSet<>();
        // Initially, all vertices are unvisited.
        Map<V, V> visited
                = initializeVector(game.getVertices(), BOTTOM_VERTEX);

        Map<V, Double> r_i_t = new HashMap<>();
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
                    double w1sum = game.getWeight1(e);
                    double w2sum = game.getWeight2(e);

                    while (x != u) {
                        // Find the vertex with the lowest id for unique 
                        // ordering, to ensure always the same vertex is the 
                        // "selected vertex" in the cycle.
                        if (game.getId(x) < game.getId(v_s)) {
                            v_s = x;
                        }
                        E x_sucx = game.getEdge(x, currentStrategy.getSuccessor(x));
                        double w1 = game.getWeight1(x_sucx);
                        double w2 = game.getWeight2(x_sucx);

                        w1sum += w1;
                        w2sum += w2;
                        x = currentStrategy.getSuccessor(x);
                    }
                    // Store the cycle ratio for the cycle containing v_s.
                    r_i_t.put(v_s, w1sum / w2sum);
                    selectedVertices.add(v_s);
                }
            }
        }
        return Tuple.of(selectedVertices, r_i_t);
    }

    /**
     * Compute the new distance and ratio vectors.
     *
     * @param <V>              vertex type
     * @param <E>              edge type
     * @param game             ratio game
     * @param currentStrategy  current stategy of both players
     * @param selectedVertices selected vertices of all cycles in the graph
     * @param r_i_t            ratio vector, given the new player-1 strategy
     * @param d_prev           previous distance vector
     * @param r_prev           previous ratio vector
     * @return new distance vector and ratio vector
     */
    private static <V, E> Triple<Map<V, Double>, Map<V, Double>, Map<V, Double>> ComputeDistances(
            RatioGamePolicyIteration<V, E, Double> game, StrategyVector<V, E> currentStrategy,
            Collection<V> selectedVertices,
            Map<V, Double> r_i_t,
            Map<V, Double> d_prev, Map<V, Double> r_prev, Map<V, Double> dw2_prev, Double epsilon) {
        Stack<V> stack = new Stack<>();
        // Initialize visited vector.
        Map<V, Boolean> visited = initializeVector(game.getVertices(), false);
        // Initialize selected vertices.
        Map<V, Double> d_i_t = new HashMap<>();
        Map<V, Double> dw2 = new HashMap<>();

        for (V u : selectedVertices) {
            if (DoubleFunctions.equalTo(r_i_t.get(u), r_prev.get(u), epsilon)) {
                d_i_t.put(u, d_prev.get(u));
                dw2.put(u, dw2_prev.get(u));
            } else {
                d_i_t.put(u, 0.0);
                dw2.put(u, 0.0);
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
                    double w1 = game.getWeight1(e);
                    double w2 = game.getWeight2(e);

                    double cycleRatio = r_i_t.get(u);

                    double reweighted = w1 - cycleRatio * w2;

                    // Update cycle ratio.
                    r_i_t.put(x, cycleRatio);
                    // Update distance to cycle.
                    d_i_t.put(x, d_i_t.get(u) + reweighted);
                    dw2.put(x, dw2.get(u) + w2);
                    u = x;

                }
            }
        }
        return Triple.of(d_i_t, r_i_t, dw2);
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
        vertices.forEach((v) -> vector.put(v, value));
        return vector;
    }

    private static <V, E> String printVector(RatioGamePolicyIteration<V, E, Double> game, Map<V, Double> values) {
        StringBuilder output = new StringBuilder();
        for (V v : values.keySet()) {
            output.append(game.getId(v)).append(": ").append(values.get(v)).append(", ");
        }
        return output.toString();
    }

    private static <V, E> String printStrategy(RatioGamePolicyIteration<V, E, Double> game,
                                               StrategyVector<V, E> strategy) {
        return strategy.getVertices().stream()
                .map(v -> game.getId(v).toString() + "-->" + game.getId(strategy.getSuccessor(v)).toString())
                .collect(Collectors.joining(", ", "{", "}"));
    }

}
