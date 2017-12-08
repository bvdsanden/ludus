package org.ludus.backend.games.ratio;

import org.apache.commons.math3.fraction.Fraction;
import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.games.benchmarking.generator.Tor;
import org.ludus.backend.games.ratio.solvers.energy.RatioGameValueIterationStrategyInt;
import org.ludus.backend.games.ratio.solvers.policy.PolicyIterationInt;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.ratio.RGIntImplJGraphT;

import java.util.Map;

/**
 * @author Bram van der Sanden
 */
public class CompareStrategies {


    @Test
    public void testOutcome() {
        RGIntImplJGraphT game = Tor.generateRatioGame(4, 5, 5);
        Tuple<Map<JGraphTVertex, Fraction>, StrategyVector<JGraphTVertex, JGraphTEdge>> piResult = PolicyIterationInt.solve(game);
        Tuple<Map<JGraphTVertex, Fraction>, StrategyVector<JGraphTVertex, JGraphTEdge>> egResult = RatioGameValueIterationStrategyInt.solve(game);

        for (JGraphTVertex v : piResult.getLeft().keySet()) {
            if (game.getV0().contains(v)) {
                // Same value.
                assert (piResult.getLeft().get(v).equals(egResult.getLeft().get(v)));
                // Same strategy successor.
                if (!piResult.getRight().getSuccessor(v).equals(egResult.getRight().getSuccessor(v))) {
                    System.out.println("not equal.");
                    System.out.println("Successor values in PI:");
                    for (JGraphTEdge out : game.outgoingEdgesOf(v)) {
                        System.out.println(piResult.getLeft().get(game.getEdgeTarget(out)));
                    }
                    System.out.println("Successor values in EG:");
                    for (JGraphTEdge out : game.outgoingEdgesOf(v)) {
                        System.out.println(egResult.getLeft().get(game.getEdgeTarget(out)));
                    }
                }
            }
        }
    }

}
