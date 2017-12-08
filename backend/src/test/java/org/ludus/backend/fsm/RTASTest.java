package org.ludus.backend.fsm;

import org.junit.jupiter.api.Test;
import org.ludus.backend.algebra.DenseMatrix;
import org.ludus.backend.algebra.Matrix;
import org.ludus.backend.algorithms.Howard;
import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.fsm.impl.Edge;
import org.ludus.backend.fsm.impl.FSMImpl;
import org.ludus.backend.fsm.impl.Location;
import org.ludus.backend.por.AmplePOR;
import org.ludus.backend.por.ClusterPOR;
import org.ludus.backend.por.DependencyGraph;
import org.ludus.backend.statespace.ComputeStateSpace;
import org.ludus.backend.statespace.DOTGenerator;
import org.ludus.backend.statespace.MaxPlusStateSpace;
import org.ludus.backend.statespace.Transition;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RTASTest {

    private Map<String, Matrix> mapping;

    public RTASTest() {
        Double MININF = Double.NEGATIVE_INFINITY;
        Matrix matrixA = new DenseMatrix(3, 3,
                4.0, 5.0, MININF,
                MININF, 3.0, MININF,
                MININF, MININF, 0.0);

        Matrix matrixB = new DenseMatrix(3, 3,
                1.0, 3.0, MININF,
                1.0, 3.0, MININF,
                MININF, MININF, 0.0);

        Matrix matrixC = new DenseMatrix(3, 3,
                0.0, MININF, MININF,
                MININF, 0.0, MININF,
                MININF, MININF, 4.0);

        Matrix matrixD = new DenseMatrix(3, 3,
                2.0, MININF, 3.0,
                MININF, 0.0, MININF,
                2.0, MININF, 3.0);

        mapping = new HashMap<>();
        mapping.put("A", matrixA);
        mapping.put("B", matrixB);
        mapping.put("C", matrixC);
        mapping.put("D", matrixD);
    }

    @Test
    public void testReduction() throws Exception {
        FSMImpl fsm1 = new FSMImpl();
        Location l10 = new Location("l0");
        Location l11 = new Location("l1");
        fsm1.setInitial(l10);
        fsm1.addLocation(l10, l11);
        fsm1.addControllable("A", "D");
        fsm1.addEdge(l10, l11, "A");
        fsm1.addEdge(l11, l10, "D");

        FSMImpl fsm2 = new FSMImpl();
        Location l20 = new Location("l0");
        Location l21 = new Location("l1");
        fsm2.setInitial(l20);
        fsm2.addLocation(l20, l21);
        fsm2.addControllable("B", "D");
        fsm2.addEdge(l20, l21, "B");
        fsm2.addEdge(l21, l20, "D");

        FSMImpl fsm3 = new FSMImpl();
        Location l30 = new Location("l0");
        Location l31 = new Location("l1");
        fsm3.setInitial(l30);
        fsm3.addLocation(l30, l31);
        fsm3.addControllable("C", "D");
        fsm3.addEdge(l30, l31, "C");
        fsm3.addEdge(l31, l30, "D");

        DependencyGraph depGraph = new DependencyGraph();
        depGraph.addDependency("A", "B");
        depGraph.addDependency("A", "D");
        depGraph.addDependency("B", "D");
        depGraph.addDependency("C", "D");

        // FSM.
        FSMComposition composer = new FSMComposition();
        FSM<Location, Edge> composition = composer.compute(Arrays.asList(fsm1, fsm2, fsm3));

        ClusterPOR cpor = new ClusterPOR();
        FSM<Location, Edge> fsmCluster = cpor.compute(Arrays.asList(fsm1, fsm2, fsm3), depGraph);

        AmplePOR apor = new AmplePOR();
        FSM<Location, Edge> fsmAmple = apor.compute(Arrays.asList(fsm1, fsm2, fsm3), depGraph);


        // State space.
        MaxPlusStateSpace statespace = ComputeStateSpace.computeMaxPlusStateSpace(composition, 3, mapping);
        MaxPlusStateSpace statespaceAmple = ComputeStateSpace.computeMaxPlusStateSpace(fsmAmple, 3, mapping);
        MaxPlusStateSpace statespaceCluster = ComputeStateSpace.computeMaxPlusStateSpace(fsmCluster, 3, mapping);

        printStateSpace(statespace);
        printStateSpace(statespaceAmple);
        printStateSpace(statespaceCluster);

        // Print state space.
        DOTGenerator gen = new DOTGenerator();
        File f = new File("/home/bram/Desktop/rtas.dot");
        gen.generate(statespace, f);

        // Get SCC.
        MaxPlusStateSpace statespaceSCC = ComputeStateSpace.getSCCs(statespace).get(0);
        MaxPlusStateSpace statespaceAmpleSCC = ComputeStateSpace.getSCCs(statespaceAmple).get(0);
        MaxPlusStateSpace statespaceClusterSCC = ComputeStateSpace.getSCCs(statespaceCluster).get(0);

        // Analyze throughput.
        Tuple<Double, List<Transition>> howardFull = Howard.runHoward(statespaceSCC, 0.0);
        Tuple<Double, List<Transition>> howardAmple = Howard.runHoward(statespaceAmpleSCC, 0.0);
        Tuple<Double, List<Transition>> howardCluster = Howard.runHoward(statespaceClusterSCC, 0.0);

        assertEquals(howardFull.getLeft(), howardAmple.getLeft());
        assertEquals(howardFull.getLeft(), howardCluster.getLeft());

        // Minimum throughput.
        Tuple<Double, List<Transition>> howardFull_min = Howard.runHoward(ComputeStateSpace.swapWeights(statespaceSCC));
        Tuple<Double, List<Transition>> howardAmple_min = Howard.runHoward(ComputeStateSpace.swapWeights(statespaceAmpleSCC));
        Tuple<Double, List<Transition>> howardCluster_min = Howard.runHoward(ComputeStateSpace.swapWeights(statespaceClusterSCC));

        assertEquals(howardFull_min.getLeft(), howardAmple_min.getLeft());
        assertEquals(howardFull_min.getLeft(), howardCluster_min.getLeft());
        System.out.println("Minimum throughput: " + howardFull_min.getRight().toString());
    }

    private void printStateSpace(MaxPlusStateSpace statespace) {
        System.out.println("Statespace:  " + statespace.getVertices().size() + " vertices and " + statespace.getEdges().size() + " edges.");
        for (Transition e : statespace.getEdges()) {
            System.out.println(e.toString());
        }
        System.out.println();
    }
}
