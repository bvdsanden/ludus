package org.ludus.api.algorithm;

import org.ludus.api.MaxPlusException;
import org.ludus.api.MaximumMakespanResult;
import org.ludus.api.MinimumMakespanResult;
import org.ludus.backend.algebra.Matrix;
import org.ludus.backend.algorithms.BellmanFord;
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

public class MaximumMakespanAlgorithm extends MaxPlusAlgorithm {
    final static Logger logger = LoggerFactory.getLogger(MaximumMakespanAlgorithm.class);

    public static MaximumMakespanResult run(FSM<Location, Edge> fsm, Map<String, Matrix> matrixMap) throws MaxPlusException {
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


        // Perform the maximum makespan computation. Since the graph is acyclic, we can negate the weights.
        // This will not introduce any negative cycle.
        stateSpace = ComputeStateSpace.negateWeights(stateSpace);

        Tuple<Double, List<Transition>> result = BellmanFord.runBellmanFord(stateSpace, stateSpace.getInitialConfiguration());

        // Since we have negated the weights, the result is also a negative value.
        List<String> listOfEventNames =
                result.getRight().stream().map(Transition::getEvent).collect(Collectors.toList());
        return new MaximumMakespanResult(result.getLeft() * (-1.0d), listOfEventNames);

    }
}