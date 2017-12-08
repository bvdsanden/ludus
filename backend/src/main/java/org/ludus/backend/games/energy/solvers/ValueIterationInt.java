package org.ludus.backend.games.energy.solvers;

import org.ludus.backend.games.energy.EnergyGame;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Value Iteration algorithm to calculate a small energy progress measure for a
 * given energy game. The small progress measure contains the optimal value of
 * each vertex, and can be used to extract an optimal strategy.
 *
 * @author Bram van der Sanden
 */
public class ValueIterationInt {

    public static final Integer TOP = Integer.MAX_VALUE;

    /**
     * Predicate is satisfied if all successors of the vertex are negative.
     *
     * @param <V>  vertex type
     * @param <E>  edge type
     * @param game game graph
     * @return true if all outgoing edges of the vertex have a negative weight
     */
    private static <V, E> Predicate<V> allSuccessorsNegative(EnergyGame<V, E, Integer> game) {
        return v -> game.outgoingEdgesOf(v)
                .stream().allMatch(e -> game.getWeight(e) < 0);
    }

    /**
     * Predicate is satisfied if any successor of the vertex is negative.
     *
     * @param <V>  vertex type
     * @param <E>  edge type
     * @param game game graph
     * @return true if there is an outgoing edge of the vertex having a negative
     * weight
     */
    private static <V, E> Predicate<V> anySuccessorNegative(EnergyGame<V, E, Integer> game) {
        return v -> game.outgoingEdgesOf(v)
                .stream().anyMatch(e -> game.getWeight(e) < 0);
    }

    /**
     * Predicate is satisfied if the successor is consistent. A successor s of v
     * is consistent with respect to progress measure {@code sepm} if sepm(v) >=
     * Minus(sepm(s),w(v,s)).
     *
     * @param <V>  vertex type
     * @param <E>  edge type
     * @param game game graph
     * @param sepm small energy progress measure
     * @return true if the successor is consistent
     */
    private static <V, E> Predicate<E> isConsistentSuccessor(EnergyGame<V, E, Integer> game, SEPM<V, Integer> sepm) {
        return e -> sepm.getValue(game.getEdgeSource(e))
                >= Minus(game.getSumNegWeights(),
                // Value a.
                sepm.getValue(game.getEdgeTarget(e)),
                // Value b.
                game.getWeight(game.getEdge(game.getEdgeSource(e), game.getEdgeTarget(e))));
    }

    /**
     * Minus operator that works on vertex values.
     *
     * @param sumNegativeWeights sum of all negative weights in the graph
     * @param a                  value of first vertex
     * @param b                  value of second vertex
     * @return the result of the minus operator applied on a and b
     */
    private static Integer Minus(Integer sumNegativeWeights, Integer a, Integer b) {
        if (a < TOP && (a - b) <= sumNegativeWeights) {
            return Math.max(0, a - b);
        } else {
            return TOP;
        }
    }

    /**
     * Lift the value of the vertex, using the lifting operator. Player 0 wants
     * to minimize the new value, and player 1 wants to maximize the new value.
     *
     * @param <V>    vertex type
     * @param <E>    edge type
     * @param game   game graph
     * @param sepm   small energy progress measure
     * @param vertex vertex of which the value will be lifted
     * @return a new value for vertex {@code vertex}
     */
    private static <V, E> Integer Lift(EnergyGame<V, E, Integer> game, SEPM<V, Integer> sepm, V vertex) {
        Integer newValue;
        if (game.getV0().contains(vertex)) {
            newValue = game.outgoingEdgesOf(vertex)
                    .stream()
                    .mapToInt(e -> Minus(game.getSumNegWeights(), sepm.getValue(game.getEdgeTarget(e)), game.getWeight(e)))
                    .reduce(Integer.MAX_VALUE, Integer::min);
        } else {
            newValue = game.outgoingEdgesOf(vertex)
                    .stream()
                    .mapToInt(e -> Minus(game.getSumNegWeights(), sepm.getValue(game.getEdgeTarget(e)), game.getWeight(e)))
                    .reduce(Integer.MIN_VALUE, Integer::max);
        }
        return newValue;
    }

    /**
     * Value-iteration algorithm for energy games. This implementation follows
     * closely the pseudo-code taken from (Brim et al., Faster Algorithms for
     * Mean-Payoff Games, 2011, Algorithm 1).
     *
     * @param <V>  vertex type
     * @param <E>  edge type
     * @param game game graph
     * @return a small energy progress measure for the given graph
     */
    public static <V, E> SEPM<V,Integer> getProgressMeasure(EnergyGame<V, E, Integer> game) {
        assert (game != null);
        Map<V, Integer> count = new HashMap<>();

        // List of vertices that witness an inconcistency of f.
        ArrayDeque<V> L = new ArrayDeque<>();

        // Small-Energy Progress Measure
        SEPM<V, Integer> sepm = new SEPM<>();

        // Line 1: add all V0 vertices to L which have only negative successors.
        L.addAll(game.getV0()
                .stream()
                .filter(allSuccessorsNegative(game))
                .collect(Collectors.toSet()));

        // Line 2: add all V1 vertices to L which have a negative successor.
        L.addAll(game.getV1()
                .stream()
                .filter(anySuccessorNegative(game))
                .collect(Collectors.toSet()));

        // Line 4: initialize the value of each vertex to zero.
        game.getVertices().stream().forEach((v) -> sepm.setValue(v, 0));

        game.getV0().stream().forEach((v) -> {
            // Line 5 and 6.
            if (L.contains(v)) {
                // Vertex v is inconsistent.
                count.put(v, 0);
            } else {
                // Vertex v is consistent.
                int numberOfConsistentVertices = (int) game.outgoingEdgesOf(v)
                        .stream()
                        .filter(isConsistentSuccessor(game, sepm))
                        .count();

                count.put(v, numberOfConsistentVertices);
            }
        });

        // Line 7.
        while (!L.isEmpty()) {
            V v = L.pop();
            final Integer old = sepm.getValue(v);
            // Line 10: lift the value of vertex v.
            sepm.setValue(v, Lift(game, sepm, v));
            // Line 11: check inconsistency on successors.
            if (game.getV0().contains(v)) {
                int numberOfConsistentVertices = (int) game.outgoingEdgesOf(v)
                        .stream()
                        .filter(isConsistentSuccessor(game, sepm))
                        .count();

                count.put(v, numberOfConsistentVertices);
            }

            game.incomingEdgesOf(v).stream().forEach((E e) -> {
                V vPre = game.getEdgeSource(e);
                // Inconsistent vertex.                
                if (sepm.getValue(vPre) < Minus(game.getSumNegWeights(), sepm.getValue(v), game.getWeight(e))) {
                    // V0 vertex.
                    if (game.getV0().contains(vPre)) {
                        // Line 14.
                        if (sepm.getValue(vPre) >= Minus(game.getSumNegWeights(), old, game.getWeight(e))) {
                            count.put(vPre, count.get(vPre) - 1);
                        }
                        // Line 15.
                        if (count.get(vPre) <= 0) {
                            if (!L.contains(vPre)) {
                                L.add(vPre);
                            }
                        }
                    } else {
                        // Predecessor is V1 vertex, so any outgoing 
                        // inconsistency leads to vPre becoming inconsistent.
                        if (!L.contains(vPre)) {
                            L.add(vPre);
                        }
                    }
                }
            });
        }
        return sepm;
    }

    /**
     * Return a consistent successor of vertex {@code v}, given small energy
     * progress measure {@code sepm}.
     *
     * @param <V>  vertex type
     * @param <E>  edge type
     * @param game game graph
     * @param sepm small energy progress measure
     * @param v    vertex of which a consistent successor is returned
     * @return a consistent successor of {@code v}
     */
    public static <V, E> Optional<E> getConsistentSuccessor(EnergyGame<V, E, Integer> game, SEPM sepm, V v) {
        return game.outgoingEdgesOf(v)
                .stream().filter(isConsistentSuccessor(game, sepm)).findFirst();

    }

    /**
     * Return a consistent successor of vertex {@code v} having the largest
     * value.
     *
     * @param <V>  vertex type
     * @param <E>  edge type
     * @param game game graph
     * @param sepm small energy progress measure
     * @param v    vertex of which a consistent successor is returned
     * @return a consistent successor of {@code v} having the largest value
     * among all consistent successors of {@code v}.
     */
    public static <V, E> Optional<E> getLargestConsistentSuccessor(EnergyGame<V, E, Integer> game, SEPM<V, Integer> sepm, V v) {
        return game.outgoingEdgesOf(v)
                .stream().filter(isConsistentSuccessor(game, sepm)).max(
                        Comparator.comparingInt(e -> sepm.getValue(game.getEdgeTarget(e)))
                );
    }

    /**
     * Return a consistent successor of vertex {@code v} having the smallest
     * value.
     *
     * @param <V>  vertex type
     * @param <E>  edge type
     * @param game game graph
     * @param sepm small energy progress measure
     * @param v    vertex of which a consistent successor is returned
     * @return a consistent successor of {@code v} having the smallest value
     * among all consistent successors of {@code v}.
     */
    public static <V, E> Optional<E> getSmallestConsistentSuccessor(EnergyGame<V, E, Integer> game, SEPM<V, Integer> sepm, V v) {
        return game.outgoingEdgesOf(v)
                .stream().filter(isConsistentSuccessor(game, sepm)).min(
                        Comparator.comparingInt(e -> sepm.getValue(game.getEdgeTarget(e)))
                );
    }

}
