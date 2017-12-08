package org.ludus.backend.games.meanpayoff.solvers;

import org.apache.commons.math3.fraction.Fraction;
import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.weights.SingleWeightFunctionInt;
import org.ludus.backend.games.meanpayoff.MeanPayoffGame;
import org.ludus.backend.games.meanpayoff.solvers.energy.MeanPayoffGameEnergy;
import org.ludus.backend.games.meanpayoff.solvers.energy.ValueIterationReductionInt;
import org.ludus.backend.games.meanpayoff.solvers.zwick.ZPSolverInt;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.meanpayoff.MPGIntImplJGraphT;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Bram van der Sanden
 */
public class ValueProblemTest {

    @Test
    public void testValueSolvingExample1() {
        // Test example graph 1.
        JGraphTGraph paperGraph = new JGraphTGraph();
        JGraphTVertex x = new JGraphTVertex();
        JGraphTVertex z = new JGraphTVertex();
        JGraphTVertex v = new JGraphTVertex();
        JGraphTVertex y = new JGraphTVertex();
        JGraphTVertex w = new JGraphTVertex();

        paperGraph.addToV0(x, v, w);
        paperGraph.addToV1(z, y);

        SingleWeightFunctionInt<JGraphTEdge> wf = new SingleWeightFunctionInt<>();

        addEdge(paperGraph, wf, x, z, 6);
        addEdge(paperGraph, wf, z, v, 2);
        addEdge(paperGraph, wf, v, y, 3);
        addEdge(paperGraph, wf, y, w, 1);
        addEdge(paperGraph, wf, w, z, 4);

        MeanPayoffGameEnergy<JGraphTVertex,JGraphTEdge,Integer> game = new MPGIntImplJGraphT(paperGraph, wf);

        // Run algorithms.
        Map<JGraphTVertex, Fraction> result = runEnergyGame(game);
        assertEquals(new Fraction(5, 2), result.get(x));
        assertEquals(new Fraction(5, 2), result.get(z));
        assertEquals(new Fraction(5, 2), result.get(v));
        assertEquals(new Fraction(5, 2), result.get(y));
        assertEquals(new Fraction(5, 2), result.get(w));

        Map<JGraphTVertex, Fraction> resultzp = runZwickPaterson(game);
        assertEquals(new Fraction(5, 2), resultzp.get(x));
        assertEquals(new Fraction(5, 2), resultzp.get(z));
        assertEquals(new Fraction(5, 2), resultzp.get(v));
        assertEquals(new Fraction(5, 2), resultzp.get(y));
        assertEquals(new Fraction(5, 2), resultzp.get(w));
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

        SingleWeightFunctionInt<JGraphTEdge> wf = new SingleWeightFunctionInt<>();

        addEdge(paperGraph, wf, a, d, 0);
        addEdge(paperGraph, wf, a, b, 0);
        addEdge(paperGraph, wf, d, e, 2);
        addEdge(paperGraph, wf, e, d, 1);
        addEdge(paperGraph, wf, b, c, 3);
        addEdge(paperGraph, wf, c, b, 4);

        MPGIntImplJGraphT game = new MPGIntImplJGraphT(paperGraph, wf);

        // Run algorithms.
        Map<JGraphTVertex, Fraction> result = runEnergyGame(game);
        assertEquals(new Fraction(7, 2), result.get(a));
        assertEquals(new Fraction(7, 2), result.get(b));
        assertEquals(new Fraction(7, 2), result.get(c));
        assertEquals(new Fraction(3, 2), result.get(d));
        assertEquals(new Fraction(3, 2), result.get(e));

        Map<JGraphTVertex, Fraction> resultzp = runZwickPaterson(game);
        assertEquals(new Fraction(7, 2), resultzp.get(a));
        assertEquals(new Fraction(7, 2), resultzp.get(b));
        assertEquals(new Fraction(7, 2), resultzp.get(c));
        assertEquals(new Fraction(3, 2), resultzp.get(d));
        assertEquals(new Fraction(3, 2), resultzp.get(e));
    }

    //@Test
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

        SingleWeightFunctionInt<JGraphTEdge> wf = new SingleWeightFunctionInt<>();

        addEdge(paperGraph, wf, a, d, 0);
        addEdge(paperGraph, wf, a, b, 0);
        addEdge(paperGraph, wf, d, e, 2);
        addEdge(paperGraph, wf, e, d, 1);
        addEdge(paperGraph, wf, b, c, -3);
        addEdge(paperGraph, wf, c, b, -4);

        MeanPayoffGameEnergy<JGraphTVertex, JGraphTEdge, Integer> game = new MPGIntImplJGraphT(paperGraph, wf);

        Map<JGraphTVertex, Fraction> result = ValueIterationReductionInt.solve(game);
        assertEquals(result.get(a), new Fraction(3, 2));
        assertEquals(result.get(b), new Fraction(-7, 2));
        assertEquals(result.get(c), new Fraction(-7, 2));
        assertEquals(result.get(d), new Fraction(3, 2));
        assertEquals(result.get(e), new Fraction(3, 2));
    }

    private static <V, E> Map<V, Fraction> runZwickPaterson(MeanPayoffGame<V, E, Integer> game) {
        return ZPSolverInt.getValues(game);
    }

    private static <V, E> Map<V, Fraction> runEnergyGame(MeanPayoffGameEnergy<V, E, Integer> game) {
        return ValueIterationReductionInt.solve(game);
    }

    private static void addEdge(JGraphTGraph g, SingleWeightFunctionInt<JGraphTEdge> wf, JGraphTVertex src, JGraphTVertex target, Integer weight) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight);
    }

}
