package org.ludus.backend.games.ratio.solvers;

import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionInt;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.games.ratio.solvers.energy.RatioGameValueIterationStrategyInt;
import org.ludus.backend.games.ratio.solvers.zwick.SolverZPInt;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.ratio.RGIntImplJGraphT;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Bram van der Sanden
 */
public class FindStrategySingleCycleTest {

    @Test
    public void testStrategyFinding() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex a = new JGraphTVertex();
        JGraphTVertex b = new JGraphTVertex();
        JGraphTVertex c = new JGraphTVertex();

        graph.addToV0(a, b, c);

        DoubleWeightFunctionInt<JGraphTEdge> wf = new DoubleWeightFunctionInt<>();
        addEdge(graph, wf, a, b, 10, 20);
        addEdge(graph, wf, a, c, 10, 20);
        addEdge(graph, wf, b, b, 5, 1);
        addEdge(graph, wf, c, c, 6, 1);

        RGIntImplJGraphT game = new RGIntImplJGraphT(graph, wf);

        StrategyVector<JGraphTVertex, JGraphTEdge> result = SolverZPInt.getOptimalStrategy(game);
        assertEquals(c, result.getSuccessor(a));

        StrategyVector<JGraphTVertex, JGraphTEdge> result2 = RatioGameValueIterationStrategyInt.solve(game).getRight();
        assertEquals(c, result2.getSuccessor(a));
    }

    private static JGraphTEdge addEdge(JGraphTGraph g, DoubleWeightFunctionInt<JGraphTEdge> wf, JGraphTVertex src, JGraphTVertex target, Integer weight1, Integer weight2) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight1, weight2);
        return e;
    }

}
