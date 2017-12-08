package org.ludus.backend.games.algorithms;

import org.ludus.backend.games.GameGraph;

import java.util.function.Predicate;

/**
 * @author Bram van der Sanden
 */
public class GraphChecks {

    /**
     * Check whether each vertex in the game graph has at least one successor.
     *
     * @param graph game graph
     * @return true if each vertex in the given game graph contains at least one
     * successor.
     */
    public static boolean checkEachNodeHasSuccessor(GameGraph graph) {
        return graph.getVertices().stream().allMatch(checkHasSuccessors(graph));
    }

    /**
     * Return true if the vertex has at least one successor.
     *
     * @param graph game graph
     * @return true if the vertex has at least one successor
     */
    private static <V, E> Predicate<V> checkHasSuccessors(GameGraph<V, E> graph) {
        return v -> graph.outgoingEdgesOf(v).size() > 0;
    }

}
