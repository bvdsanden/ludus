package org.ludus.backend.games.ratio.solvers;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionDouble;
import org.ludus.backend.games.algorithms.DoubleFunctions;
import org.ludus.backend.games.ratio.solvers.energy.RatioGameValueIterationDouble;
import org.ludus.backend.games.ratio.solvers.policy.PolicyIterationDouble;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.ratio.RGDoubleImplJGraphT;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Bram van der Sanden
 */
@Tag("slow")
public class FindValuesDoubleRandomGraphs {

    public static RGDoubleImplJGraphT constructGameGraphImpl() {
        JGraphTGraph paperGraph = new JGraphTGraph();
        Set<JGraphTVertex> list = new HashSet<>();

        int n = 10;
        int W = 10;

        for (int i = 0; i < n; i++) {
            JGraphTVertex v = new JGraphTVertex();
            list.add(v);
            if (i % 2 == 0) {
                paperGraph.addToV0(v);
            } else {
                paperGraph.addToV1(v);
            }
        }

        DoubleWeightFunctionDouble wf = new DoubleWeightFunctionDouble();

        Iterator<JGraphTVertex> slowI = list.iterator();
        Iterator<JGraphTVertex> fastI;

        while (slowI.hasNext()) { //While there are more vertices in the set
            JGraphTVertex latestVertex = slowI.next();
            fastI = list.iterator();
            //Jump to the first vertex *past* latestVertex
            while (fastI.next() != latestVertex) {
            }
            //And, add edges to all remaining vertices
            JGraphTVertex temp;
            while (fastI.hasNext()) {
                temp = fastI.next();
                Random r = new Random();
                Double rand1 = 1.0 + (W - 1.0) * r.nextDouble();
                Double rand2 = 1.0 + (W - 1.0) * r.nextDouble();
                addEdge(paperGraph, wf, latestVertex, temp, rand1, rand2);
                addEdge(paperGraph, wf, temp, latestVertex, rand2, rand1);

            }
        }
        return new RGDoubleImplJGraphT(paperGraph, wf);
    }

    @Test
    public void compareResults() {
        RGDoubleImplJGraphT jGraphGraph = constructGameGraphImpl();
        Map<JGraphTVertex, Double> resultEG = RatioGameValueIterationDouble.solve(jGraphGraph);
        Map<JGraphTVertex, Double> resultPI = PolicyIterationDouble.solve(jGraphGraph, 10E-10).getLeft();
        for (JGraphTVertex v : jGraphGraph.getVertices()) {
            assertTrue(DoubleFunctions.equalTo(resultEG.get(v), resultPI.get(v)));
        }
    }

    private static void addEdge(JGraphTGraph g, DoubleWeightFunctionDouble wf, JGraphTVertex src, JGraphTVertex target, Double weight1, Double weight2) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight1, weight2);
    }
}
