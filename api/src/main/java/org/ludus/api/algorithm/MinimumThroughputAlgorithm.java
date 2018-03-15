package org.ludus.api.algorithm;

import org.ludus.api.MaxPlusException;
import org.ludus.api.MinimumThroughputResult;
import org.ludus.backend.algebra.Matrix;
import org.ludus.backend.algorithms.Howard;
import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.fsm.FSM;
import org.ludus.backend.fsm.impl.Edge;
import org.ludus.backend.fsm.impl.Location;
import org.ludus.backend.statespace.ComputeStateSpace;
import org.ludus.backend.statespace.MaxPlusStateSpace;
import org.ludus.backend.statespace.Transition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MinimumThroughputAlgorithm extends MaxPlusAlgorithm {
    private final static Logger logger = LoggerFactory.getLogger(MinimumThroughputAlgorithm.class);

    public static MinimumThroughputResult run(FSM<Location, Edge> fsm, Map<String, Matrix> matrixMap) throws MaxPlusException {

        // Check pre-conditions.
        checkNotCyclic(fsm);
        checkNoDeadlocks(fsm);
        checkAllMatricesSameSize(matrixMap.values());
        checkEventMapping(fsm,matrixMap);

        // Number of resources.
        Integer resourceCount = matrixMap.values().iterator().next().getRows();

        // Compute the max-plus state space.
        MaxPlusStateSpace stateSpaceOriginal = ComputeStateSpace.computeMaxPlusStateSpace(
                fsm, resourceCount, matrixMap);

        // Swap the reward and duration weights.
        MaxPlusStateSpace stateSpace = ComputeStateSpace.swapWeights(stateSpaceOriginal);

        logger.info("Max-plus state space constructed: " + stateSpace.getVertices().size() + " states and "
                + stateSpace.getEdges().size() + " edges.");

        // Perform the minimum cycle mean computation on the strongly connected components.
        List<MaxPlusStateSpace> mpsSCCs = ComputeStateSpace.getSCCs(stateSpace);

        logger.info("Computed the " + mpsSCCs.size() + " strongly connected components.");

        int i = 1;
        Tuple<Double, List<Transition>> result = Tuple.of(Double.MAX_VALUE, new LinkedList<Transition>());
        for (MaxPlusStateSpace mpsSCC : mpsSCCs) {
            Tuple<Double, List<Transition>> sccResult = Howard.runHoward(mpsSCC);
            logger.info("Running Howard on component " + i + "/" + mpsSCCs.size());
            if (sccResult.getLeft() < result.getLeft()) {
                result = sccResult;
            }
            i++;
        }

        Double throughput = 1.0d / result.getLeft();

        // Create the output.
        if (!result.getLeft().equals(Double.NEGATIVE_INFINITY)) {
            List<String> listOfEventNames =
                    result.getRight().stream().map(Transition::getEvent).collect(Collectors.toList());
            return new MinimumThroughputResult(throughput, listOfEventNames);
        } else {
            return new MinimumThroughputResult(throughput, new LinkedList<>());
        }
    }
}
