package org.ludus.backend.games.energy;

import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.weights.SingleWeightFunctionDouble;
import org.ludus.backend.datastructures.weights.SingleWeightFunctionInt;
import org.ludus.backend.games.energy.solvers.SEPM;
import org.ludus.backend.games.energy.solvers.ValueIterationDouble;
import org.ludus.backend.games.energy.solvers.ValueIterationInt;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.energy.EGDoubleImplJGraphT;
import org.ludus.backend.graph.jgrapht.energy.EGIntImplJGraphT;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Bram van der Sanden
 */
public class EnergyGameTest {

    @Test
    public void testDemoGraph() {
        // This graph is taken from "Faster Algorithms for Mean-Payoff Games",
        // L. Brim, J. Chaloupka, L. Doyen, R. Gentilini and J.F. Raskin. (2011).
        JGraphTGraph paperGraph = new JGraphTGraph();
        JGraphTVertex x = new JGraphTVertex();
        JGraphTVertex z = new JGraphTVertex();
        JGraphTVertex v = new JGraphTVertex();
        JGraphTVertex y = new JGraphTVertex();
        JGraphTVertex w = new JGraphTVertex();

        paperGraph.addToV0(y, z, w);
        paperGraph.addToV1(x, v);

        SingleWeightFunctionInt wf = new SingleWeightFunctionInt();

        addEdge(paperGraph, wf, x, z, 3);
        addEdge(paperGraph, wf, y, x, 2);
        addEdge(paperGraph, wf, y, z, 1);
        addEdge(paperGraph, wf, z, y, -3);
        addEdge(paperGraph, wf, z, w, 1);
        addEdge(paperGraph, wf, w, v, -4);
        addEdge(paperGraph, wf, v, z, 1);
        addEdge(paperGraph, wf, v, w, 0);

        EGIntImplJGraphT game = new EGIntImplJGraphT(paperGraph, wf);

        SEPM sepm = ValueIterationInt.getProgressMeasure(game);
        assertEquals(0, sepm.getValue(x));
        assertEquals(3, sepm.getValue(z));
        assertEquals(0, sepm.getValue(y));
        assertEquals(ValueIterationInt.TOP, sepm.getValue(v));
        assertEquals(ValueIterationInt.TOP, sepm.getValue(w));
    }

    @Test
    public void testDemoGraphDouble() {
        // This graph is taken from "Faster Algorithms for Mean-Payoff Games",
        // L. Brim, J. Chaloupka, L. Doyen, R. Gentilini and J.F. Raskin. (2011).
        JGraphTGraph paperGraph = new JGraphTGraph();
        JGraphTVertex x = new JGraphTVertex();
        JGraphTVertex z = new JGraphTVertex();
        JGraphTVertex v = new JGraphTVertex();
        JGraphTVertex y = new JGraphTVertex();
        JGraphTVertex w = new JGraphTVertex();

        paperGraph.addToV0(y, z, w);
        paperGraph.addToV1(x, v);

        SingleWeightFunctionDouble wf = new SingleWeightFunctionDouble();

        addEdge(paperGraph, wf, x, z, 3.0);
        addEdge(paperGraph, wf, y, x, 2.0);
        addEdge(paperGraph, wf, y, z, 1.0);
        addEdge(paperGraph, wf, z, y, -3.0);
        addEdge(paperGraph, wf, z, w, 1.0);
        addEdge(paperGraph, wf, w, v, -4.0);
        addEdge(paperGraph, wf, v, z, 1.0);
        addEdge(paperGraph, wf, v, w, 0.0);

        EGDoubleImplJGraphT game = new EGDoubleImplJGraphT(paperGraph, wf);

        SEPM sepm = ValueIterationDouble.getProgressMeasure(game);
        assertEquals(0.0, sepm.getValue(x));
        assertEquals(3.0, sepm.getValue(z));
        assertEquals(0.0, sepm.getValue(y));
        assertEquals(ValueIterationDouble.TOP, sepm.getValue(v));
        assertEquals(ValueIterationDouble.TOP, sepm.getValue(w));
    }

    @Test
    public void testDemoGraphReweightedDouble() {
        // This graph is taken from "Faster Algorithms for Mean-Payoff Games",
        // L. Brim, J. Chaloupka, L. Doyen, R. Gentilini and J.F. Raskin. (2011).
        // Weights are divided by 2.
        JGraphTGraph paperGraph = new JGraphTGraph();
        JGraphTVertex x = new JGraphTVertex();
        JGraphTVertex z = new JGraphTVertex();
        JGraphTVertex v = new JGraphTVertex();
        JGraphTVertex y = new JGraphTVertex();
        JGraphTVertex w = new JGraphTVertex();

        paperGraph.addToV0(y, z, w);
        paperGraph.addToV1(x, v);

        SingleWeightFunctionDouble wf = new SingleWeightFunctionDouble();

        addEdge(paperGraph, wf, x, z, 1.5);
        addEdge(paperGraph, wf, y, x, 1.0);
        addEdge(paperGraph, wf, y, z, 0.5);
        addEdge(paperGraph, wf, z, y, -1.5);
        addEdge(paperGraph, wf, z, w, 0.5);
        addEdge(paperGraph, wf, w, v, -2.0);
        addEdge(paperGraph, wf, v, z, 0.5);
        addEdge(paperGraph, wf, v, w, 0.0);

        EGDoubleImplJGraphT game = new EGDoubleImplJGraphT(paperGraph, wf);

        SEPM sepm = ValueIterationDouble.getProgressMeasure(game);
        assertEquals(0.0, sepm.getValue(x));
        assertEquals(1.5, sepm.getValue(z));
        assertEquals(0.0, sepm.getValue(y));
        assertEquals(ValueIterationDouble.TOP, sepm.getValue(v));
        assertEquals(ValueIterationDouble.TOP, sepm.getValue(w));
    }

    private static void addEdge(JGraphTGraph g,
                                SingleWeightFunctionInt wf,
                                JGraphTVertex src, JGraphTVertex target, Integer weight) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight);
    }

    private static void addEdge(JGraphTGraph g,
                                SingleWeightFunctionDouble wf,
                                JGraphTVertex src, JGraphTVertex target, Double weight) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight);
    }

}
