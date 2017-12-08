package org.ludus.backend.games.meanpayoff.solvers.policy;

import org.apache.commons.math3.fraction.Fraction;
import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.weights.SingleWeightFunctionDouble;
import org.ludus.backend.datastructures.weights.SingleWeightFunctionInt;
import org.ludus.backend.games.StrategyVector;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.meanpayoff.MPGDoubleImplJGraphT;
import org.ludus.backend.graph.jgrapht.meanpayoff.MPGIntImplJGraphT;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Run the policy iteration algorithm on mean-payoff game graphs.
 *
 * @author Bram van der Sanden
 */
public class PolicyIterationTest {

    @Test
    public void testGame1() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex u = new JGraphTVertex(0);
        JGraphTVertex v = new JGraphTVertex(1);
        JGraphTVertex w = new JGraphTVertex(2);
        JGraphTVertex x = new JGraphTVertex(3);

        graph.addToV1(u, v, w, x);

        SingleWeightFunctionInt wf = new SingleWeightFunctionInt();

        addEdge(graph, wf, u, v, 1);
        addEdge(graph, wf, v, w, 2);
        addEdge(graph, wf, w, x, 3);
        addEdge(graph, wf, x, v, 4);

        StrategyVector s = new StrategyVector();
        graph.getEdges().stream().forEach((e) -> s.setSuccessor(graph.getEdgeSource(e), graph.getEdgeTarget(e)));

        MeanPayoffGamePolicyIteration<JGraphTVertex, JGraphTEdge, Integer> game
                = new MPGIntImplJGraphT(graph, wf);

        // Solve the game.
        Map<JGraphTVertex, Fraction> values
                = PolicyIterationInt.solve(game).getLeft();

        // Check results.        
        Fraction mw = new Fraction(9, 3);
        assertEquals(mw, values.get(u));
        assertEquals(mw, values.get(v));
        assertEquals(mw, values.get(w));
        assertEquals(mw, values.get(x));
    }


    @Test
    public void testGame1Doubles() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex u = new JGraphTVertex(0);
        JGraphTVertex v = new JGraphTVertex(1);
        JGraphTVertex w = new JGraphTVertex(2);
        JGraphTVertex x = new JGraphTVertex(3);

        graph.addToV1(u, v, w, x);

        SingleWeightFunctionDouble wf = new SingleWeightFunctionDouble();

        addEdge(graph, wf, u, v, 1.0);
        addEdge(graph, wf, v, w, 2.0);
        addEdge(graph, wf, w, x, 3.0);
        addEdge(graph, wf, x, v, 4.0);

        StrategyVector s = new StrategyVector();
        graph.getEdges().stream().forEach((e) -> s.setSuccessor(graph.getEdgeSource(e), graph.getEdgeTarget(e)));

        MeanPayoffGamePolicyIteration<JGraphTVertex, JGraphTEdge, Double> game
                = new MPGDoubleImplJGraphT(graph, wf);

        // Solve the game.
        Map<JGraphTVertex, Double> values
                = PolicyIterationDouble.solve(game).getLeft();

        // Check results.   
        Double mw = 9.0 / 3.0;
        assertEquals(mw, values.get(u));
        assertEquals(mw, values.get(v));
        assertEquals(mw, values.get(w));
        assertEquals(mw, values.get(x));
    }


    @Test
    public void tesGame3() {
        JGraphTGraph graph = new JGraphTGraph();
        JGraphTVertex u = new JGraphTVertex(0);
        JGraphTVertex v = new JGraphTVertex(1);
        JGraphTVertex w = new JGraphTVertex(2);
        JGraphTVertex x = new JGraphTVertex(3);
        JGraphTVertex y = new JGraphTVertex(4);

        graph.addToV1(u, v, w, x, y);

        SingleWeightFunctionInt wf = new SingleWeightFunctionInt();

        addEdge(graph, wf, u, v, 1);
        addEdge(graph, wf, v, w, 2);
        addEdge(graph, wf, w, x, 3);
        addEdge(graph, wf, x, v, 4);
        addEdge(graph, wf, y, y, 2);

        MeanPayoffGamePolicyIteration<JGraphTVertex, JGraphTEdge, Integer> game
                = new MPGIntImplJGraphT(graph, wf);

        // Solve game.        
        Map<JGraphTVertex, Fraction> values
                = PolicyIterationInt.solve(game).getLeft();

        // Check results.        
        Fraction mw = new Fraction(9, 3);
        assertEquals(mw, values.get(u));
        assertEquals(mw, values.get(v));
        assertEquals(mw, values.get(w));
        assertEquals(mw, values.get(x));
    }

    @Test
    public void testValueSolvingExample1() {
        // Test example graph 1.
        JGraphTGraph paperGraph = new JGraphTGraph();
        JGraphTVertex x = new JGraphTVertex(0);
        JGraphTVertex z = new JGraphTVertex(1);
        JGraphTVertex v = new JGraphTVertex(2);
        JGraphTVertex y = new JGraphTVertex(3);
        JGraphTVertex w = new JGraphTVertex(4);

        paperGraph.addToV0(x, v, w);
        paperGraph.addToV1(z, y);

        SingleWeightFunctionInt wf = new SingleWeightFunctionInt();

        addEdge(paperGraph, wf, x, z, 6);
        addEdge(paperGraph, wf, z, v, 2);
        addEdge(paperGraph, wf, v, y, 3);
        addEdge(paperGraph, wf, y, w, 1);
        addEdge(paperGraph, wf, w, z, 4);

        MPGIntImplJGraphT game = new MPGIntImplJGraphT(paperGraph, wf);

        Map<JGraphTVertex, Fraction> result
                = PolicyIterationInt.solve(game).getLeft();
        assertEquals(result.get(x), new Fraction(5, 2));
        assertEquals(result.get(z), new Fraction(5, 2));
        assertEquals(result.get(v), new Fraction(5, 2));
        assertEquals(result.get(y), new Fraction(5, 2));
        assertEquals(result.get(w), new Fraction(5, 2));
    }

    @Test
    public void testValueSolvingExample2() {
        // Test example graph 1.
        JGraphTGraph paperGraph = new JGraphTGraph();
        JGraphTVertex a = new JGraphTVertex();
        JGraphTVertex b = new JGraphTVertex();
        JGraphTVertex c = new JGraphTVertex();
        JGraphTVertex d = new JGraphTVertex();
        JGraphTVertex e = new JGraphTVertex();

        paperGraph.addToV0(a, c, e);
        paperGraph.addToV1(d, b);

        SingleWeightFunctionInt wf = new SingleWeightFunctionInt();

        addEdge(paperGraph, wf, a, d, 0);
        addEdge(paperGraph, wf, a, b, 0);
        addEdge(paperGraph, wf, d, e, 2);
        addEdge(paperGraph, wf, e, d, 1);
        addEdge(paperGraph, wf, b, c, 3);
        addEdge(paperGraph, wf, c, b, 4);

        MPGIntImplJGraphT game = new MPGIntImplJGraphT(paperGraph, wf);

        Map<JGraphTVertex, Fraction> result
                = PolicyIterationInt.solve(game).getLeft();
        assertEquals(result.get(a), new Fraction(7, 2));
        assertEquals(result.get(b), new Fraction(7, 2));
        assertEquals(result.get(c), new Fraction(7, 2));
        assertEquals(result.get(d), new Fraction(3, 2));
        assertEquals(result.get(e), new Fraction(3, 2));
    }

    @Test
    public void testValueSolvingExample3() {
        // Test example graph 1.
        JGraphTGraph paperGraph = new JGraphTGraph();
        JGraphTVertex a = new JGraphTVertex();
        JGraphTVertex b = new JGraphTVertex();
        JGraphTVertex c = new JGraphTVertex();
        JGraphTVertex d = new JGraphTVertex();
        JGraphTVertex e = new JGraphTVertex();

        paperGraph.addToV0(a, c, e);
        paperGraph.addToV1(d, b);

        SingleWeightFunctionInt wf = new SingleWeightFunctionInt();

        addEdge(paperGraph, wf, a, d, 0);
        addEdge(paperGraph, wf, a, b, 0);
        addEdge(paperGraph, wf, d, e, 2);
        addEdge(paperGraph, wf, e, d, 1);
        addEdge(paperGraph, wf, b, c, -3);
        addEdge(paperGraph, wf, c, b, -4);

        MPGIntImplJGraphT game
                = new MPGIntImplJGraphT(paperGraph, wf);

        Map<JGraphTVertex, Fraction> result
                = PolicyIterationInt.solve(game).getLeft();
        assertEquals(result.get(a), new Fraction(3, 2));
        assertEquals(result.get(b), new Fraction(-7, 2));
        assertEquals(result.get(c), new Fraction(-7, 2));
        assertEquals(result.get(d), new Fraction(3, 2));
        assertEquals(result.get(e), new Fraction(3, 2));
    }

    private static void addEdge(JGraphTGraph g, SingleWeightFunctionInt wf,
                                JGraphTVertex src, JGraphTVertex target, Integer weight) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight);
    }

    private static void addEdge(JGraphTGraph g, SingleWeightFunctionDouble wf,
                                JGraphTVertex src, JGraphTVertex target, Double weight) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight);
    }
}
