package org.ludus.backend.games.ratio;

import org.junit.jupiter.api.Test;
import org.ludus.backend.datastructures.weights.DoubleWeightFunctionDouble;
import org.ludus.backend.games.GameGraph;
import org.ludus.backend.graph.jgrapht.JGraphTEdge;
import org.ludus.backend.graph.jgrapht.JGraphTGraph;
import org.ludus.backend.graph.jgrapht.JGraphTVertex;
import org.ludus.backend.graph.jgrapht.ratio.RGDoubleImplJGraphT;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Bram van der Sanden
 */
public class SerializeTest {

    public static RGDoubleImplJGraphT getGameGraph() {
        JGraphTGraph paperGraph = new JGraphTGraph();
        Set<JGraphTVertex> list = new HashSet<>();

        int n = 6;
        int W = 3;

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
    public void canSerialize()
            throws IOException, ClassNotFoundException {
        GameGraph graph = getGameGraph();

        File file = new File("serialize_test");

        // Write to file.
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
        out.writeObject(graph);
        out.close();

        // Read from file.
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        final GameGraph restoredGraph = (GameGraph) in.readObject();
        assertThat(restoredGraph, instanceOf(GameGraph.class));
        in.close();

        // Clean-up.
        file.delete();
    }

    private static void addEdge(JGraphTGraph g, DoubleWeightFunctionDouble<JGraphTEdge> wf, JGraphTVertex src, JGraphTVertex target, Double weight1, Double weight2) {
        JGraphTEdge e = g.addEdge(src, target);
        wf.addWeight(e, weight1, weight2);
    }
}
