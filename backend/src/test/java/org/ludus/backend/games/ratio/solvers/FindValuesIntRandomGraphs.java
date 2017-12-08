package org.ludus.backend.games.ratio.solvers;

import org.apache.commons.math3.fraction.Fraction;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionInt;
import org.ludus.backend.games.ratio.solvers.energy.RatioGameValueIterationInt;
import org.ludus.backend.games.ratio.solvers.policy.PolicyIterationInt;
import org.ludus.backend.games.ratio.solvers.zwick.SolverZPInt;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.ratio.RGIntImplJGraphT;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Bram van der Sanden
 */
public class FindValuesIntRandomGraphs {

    public static RGIntImplJGraphT constructGameGraphImpl() {
        JGraphTGraph paperGraph = new JGraphTGraph();
        Set<JGraphTVertex> list = new HashSet<>();

        int n = 7;
        int W = 4;

        for (int i = 0; i < n; i++) {
            JGraphTVertex v = new JGraphTVertex();
            list.add(v);
            if (i % 2 == 0) {
                paperGraph.addToV0(v);
            } else {
                paperGraph.addToV1(v);
            }
        }

        DoubleWeightFunctionInt<JGraphTEdge> wf = new DoubleWeightFunctionInt<>();

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
                Integer rand1 = r.nextInt(W - 1) + 1;
                Integer rand2 = r.nextInt(W - 1) + 1;
                addEdge(paperGraph, wf, latestVertex, temp, rand1, rand2);
                addEdge(paperGraph, wf, temp, latestVertex, rand2, rand1);

            }
        }
        return new RGIntImplJGraphT(paperGraph, wf);
    }

    public static void main(String[] args) {
        RGIntImplJGraphT jGraphGraph = constructGameGraphImpl();
        // Zwick Paterson on Integers.
        Map<JGraphTVertex, Fraction> resultZP = SolverZPInt.getValues(jGraphGraph);
        // Energy Game conversion on Integers.
        Map<JGraphTVertex, Fraction> resultEG = RatioGameValueIterationInt.solve(jGraphGraph);
        // Policy Iteration on Integers.
        Map<JGraphTVertex, Fraction> resultPI = PolicyIterationInt.solve(jGraphGraph).getLeft();

        for (JGraphTVertex v : jGraphGraph.getVertices()) {
            assertEquals(resultZP.get(v), resultEG.get(v));
            assertEquals(resultZP.get(v), resultPI.get(v));
        }
    }

    private static void addEdge(JGraphTGraph g, DoubleWeightFunctionInt<JGraphTEdge> wf, JGraphTVertex src, JGraphTVertex target, Integer weight1, Integer weight2) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight1, weight2);
    }
}
