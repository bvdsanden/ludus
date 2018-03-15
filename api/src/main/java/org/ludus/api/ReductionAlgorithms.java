package org.ludus.api;

import org.ludus.backend.algebra.Matrix;
import org.ludus.backend.fsm.FSM;
import org.ludus.backend.fsm.PrintToCIF;
import org.ludus.backend.fsm.impl.Edge;
import org.ludus.backend.fsm.impl.Location;
import org.ludus.backend.por.AmplePOR;
import org.ludus.backend.por.ClusterPOR;
import org.ludus.backend.por.DependencyInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ReductionAlgorithms {
    final static Logger logger = LoggerFactory.getLogger(ReductionAlgorithms.class);

    /**
     * Given a set of FSMs calculate a reduced FSM.
     *
     * @param fsmList list of FSMs
     * @return reduced composition of the given list of FSMs
     * @throws MaxPlusException
     */
    public static FSM<Location, Edge> computeAmpleReduction(List<FSM<Location, Edge>> fsmList, Map<String, Matrix> mapping) throws MaxPlusException {
        // Pre-conditions.
        if (fsmList.isEmpty()) {
            throw new MaxPlusException("The specification contains no FSM.");
        }

        // Compute the dependency graph by looking at resource sharing.
        DependencyInterface dependencies =
                MatrixDependencies.getDependencyGraphResourceSharing(mapping);

        logger.info("Finished calculating the dependencies.");

        AmplePOR por = new AmplePOR();
        FSM<Location, Edge> result = por.compute(fsmList, dependencies);

        logger.info("Computed the reduced FSM with " + result.getVertices().size() + " vertices and " + result.getEdges().size() + " edges.");
        return result;
    }


    /**
     * Given a set of FSMs calculate a reduced FSM.
     *
     * @param fsmList list of FSMs
     * @return reduced composition of the given list of FSMs
     * @throws MaxPlusException
     */
    public static FSM<Location, Edge> computeClusterReduction(List<FSM<Location, Edge>> fsmList, Map<String, Matrix> mapping) throws MaxPlusException {
        // Pre-conditions.
        if (fsmList.isEmpty()) {
            throw new MaxPlusException("The specification contains no FSM.");
        }

        // Compute the dependency graph by looking at resource sharing.
        DependencyInterface dependencies =
                MatrixDependencies.getDependencyGraphResourceSharing(mapping);

        logger.info("Finished calculating the dependencie.s");

        ClusterPOR por = new ClusterPOR();
        FSM<Location, Edge> result = por.compute(fsmList, dependencies);

        logger.info("Computed the reduced FSM with " + result.getVertices().size() + " vertices and " + result.getEdges().size() + " edges.");
        return result;
    }

    public static void writeToFile(FSM<Location, Edge> fsm, String fsmName, String filePath) throws IOException {
        File file = new File(filePath);
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write(PrintToCIF.print(fsm, fsmName));
        writer.close();
    }
}
