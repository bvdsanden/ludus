package org.ludus.backend.games.ratio.solvers.policy.precision;

import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionDouble;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionInt;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.games.ratio.solvers.policy.PolicyIterationDouble;
import org.ludus.backend.games.ratio.solvers.policy.PolicyIterationInt;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.ratio.RGDoubleImplJGraphT;
import org.ludus.backend.graph.jgrapht.ratio.RGIntImplJGraphT;

/**
 * @author Bram van der Sanden
 */
public class PrecisionTest {

    public Tuple<RGIntImplJGraphT, StrategyVector<JGraphTVertex, JGraphTEdge>> createGameGraph() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex v0 = new JGraphTVertex(0);
        JGraphTVertex v1 = new JGraphTVertex(1);
        JGraphTVertex v2 = new JGraphTVertex(2);
        JGraphTVertex v3 = new JGraphTVertex(3);
        graph.addToV0(v0, v1);
        graph.addToV1(v2, v3);
        JGraphTEdge v01 = graph.addEdge(v0, v1);
        JGraphTEdge v02 = graph.addEdge(v0, v2);
        JGraphTEdge v10 = graph.addEdge(v1, v0);
        JGraphTEdge v13 = graph.addEdge(v1, v3);
        JGraphTEdge v20 = graph.addEdge(v2, v0);
        JGraphTEdge v23 = graph.addEdge(v2, v3);
        JGraphTEdge v31 = graph.addEdge(v3, v1);
        JGraphTEdge v32 = graph.addEdge(v3, v2);
        DoubleWeightFunctionInt weights = new DoubleWeightFunctionInt();
        weights.addWeight(v01, 4, 4);
        weights.addWeight(v02, 6, 4);
        weights.addWeight(v10, 3, 6);
        weights.addWeight(v13, 3, 3);
        weights.addWeight(v23, 5, 7);
        weights.addWeight(v20, 2, 4);
        weights.addWeight(v32, 4, 3);
        weights.addWeight(v31, 6, 9);

        RGIntImplJGraphT game = new RGIntImplJGraphT(graph, weights);

        StrategyVector s = new StrategyVector();
        s.setSuccessor(v0, v2);
        s.setSuccessor(v1, v3);
        s.setSuccessor(v2, v3);
        s.setSuccessor(v3, v1);

        return Tuple.of(game, s);
    }

    @Test
    public void testAlgorithms() {
        Tuple<RGIntImplJGraphT, StrategyVector<JGraphTVertex, JGraphTEdge>> t = createGameGraph();
        RGIntImplJGraphT intGame = t.getLeft();
        RGDoubleImplJGraphT doubleGame = toDoubleGameGraph(t.getLeft());


        PolicyIterationInt.solve(intGame, t.getRight());
        PolicyIterationDouble.solve(doubleGame, t.getRight(), 10E-3);
    }

    private RGDoubleImplJGraphT toDoubleGameGraph(RGIntImplJGraphT ratioGame) {
        DoubleWeightFunctionInt f = ratioGame.getEdgeWeights();
        DoubleWeightFunctionDouble weights = new DoubleWeightFunctionDouble();
        for (JGraphTEdge e : ratioGame.getEdges()) {
            weights.addWeight(e, f.getWeight1(e) * 1.0, f.getWeight2(e) * 1.0);
        }
        return new RGDoubleImplJGraphT(ratioGame.getGraph(), weights);
    }
}
