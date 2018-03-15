package org.ludus.api.algorithm;

import org.ludus.api.MaxPlusException;
import org.ludus.api.MinimumMakespanResult;
import org.ludus.backend.algebra.Matrix;
import org.ludus.backend.algorithms.CycleCheck;
import org.ludus.backend.algorithms.Dijkstra;
import org.ludus.backend.datastructures.tuple.Tuple;
import org.ludus.backend.fsm.FSM;
import org.ludus.backend.fsm.impl.Edge;
import org.ludus.backend.fsm.impl.Location;
import org.ludus.backend.statespace.ComputeStateSpace;
import org.ludus.backend.statespace.MaxPlusStateSpace;
import org.ludus.backend.statespace.Transition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MinimumMakespanAlgorithm extends MaxPlusAlgorithm {
    final static Logger logger = LoggerFactory.getLogger(MinimumMakespanAlgorithm.class);

    public static MinimumMakespanResult run(FSM<Location, Edge> fsm, Map<String, Matrix> matrixMap) throws MaxPlusException {
        // Check if the FSM is acyclic.
        if (CycleCheck.check(fsm)) {
            throw new MaxPlusException("Cannot compute the minimum makespan. Input CIF file contains a cycle.");
        }

        // Check that each matrix has the same size.
        Matrix matrixFirst = matrixMap.values().iterator().next();
        for (Matrix m : matrixMap.values()) {
            if (matrixFirst.getRows() != m.getRows()) {
                throw new MaxPlusException("Cannot compute the minimum makespan. Matrices differ in size.");
            }
        }

        // Compute the state space.
        MaxPlusStateSpace stateSpace = ComputeStateSpace.computeMaxPlusStateSpace(
                fsm, matrixFirst.getRows(), matrixMap);

        logger.info("Max-plus state space constructed: " + stateSpace.getVertices().size() + " states and "
                + stateSpace.getEdges().size() + " edges.");

        // Perform the makespan computation.
        Tuple<Double, List<Transition>> result = Dijkstra.runDijkstra(stateSpace, stateSpace.getInitialConfiguration());

        List<String> listOfEventNames =
                result.getRight().stream().map(Transition::getEvent).collect(Collectors.toList());
        return new MinimumMakespanResult(result.getLeft(), listOfEventNames);
    }
}