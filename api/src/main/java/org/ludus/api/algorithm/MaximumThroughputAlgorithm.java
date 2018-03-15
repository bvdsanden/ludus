package org.ludus.api.algorithm;

import org.ludus.api.MaxPlusException;
import org.ludus.api.MaximumThroughputResult;
import org.ludus.backend.algebra.Matrix;
import org.ludus.backend.algorithms.Howard;
import org.ludus.backend.automaton.ComputeMPA;
import org.ludus.backend.automaton.MPATransition;
import org.ludus.backend.automaton.MaxPlusAutomaton;
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

public class MaximumThroughputAlgorithm extends MaxPlusAlgorithm {
    private final static Logger logger = LoggerFactory.getLogger(MaximumThroughputAlgorithm.class);

    private static void runChecks(FSM<Location, Edge> fsm, Map<String, Matrix> matrixMap) throws MaxPlusException {
        checkNotCyclic(fsm);
        checkNoDeadlocks(fsm);
        checkAllMatricesSameSize(matrixMap.values());
    }

    public static MaximumThroughputResult runMaxPlusStateSpace(FSM<Location, Edge> fsm, Map<String, Matrix> matrixMap) throws MaxPlusException {
        runChecks(fsm,matrixMap);

        // Number of resources.
        Integer resourceCount = matrixMap.values().iterator().next().getRows();

        // Compute the max-plus state space.
        MaxPlusStateSpace mpss = ComputeStateSpace.computeMaxPlusStateSpace(
                fsm, resourceCount, matrixMap);

        logger.info("Max-Plus state space constructed: " + mpss.getVertices().size()
                + " states and " + mpss.getEdges().size() + " edges.");

        // Perform the minimum cycle mean computation on the SCCs.
        List<MaxPlusStateSpace> mpssSCCs = ComputeStateSpace.getSCCs(mpss);

        logger.info("Computed the " + mpssSCCs.size() + " strongly connected components.");

        Tuple<Double, List<Transition>> result = Tuple.of(Double.MAX_VALUE, new LinkedList<Transition>());
        int i = 1;
        for (MaxPlusStateSpace mpssSCC : mpssSCCs) {
            Tuple<Double, List<Transition>> sccResult = Howard.runHoward(mpssSCC);
            logger.info("Running Howard on component " + i + "/" + mpssSCCs.size());
            if (sccResult.getLeft() < result.getLeft()) {
                result = sccResult;
            }
            i++;
        }

        // Create the output.
        List<String> listOfEventNames =
                result.getRight().stream().map(Transition::getEvent).collect(Collectors.toList());
        return new MaximumThroughputResult(result.getLeft(), listOfEventNames);
    }

    public static MaximumThroughputResult runMaxPlusAutomaton(FSM<Location, Edge> fsm, Map<String, Matrix> matrixMap) throws MaxPlusException {
        runChecks(fsm,matrixMap);

        // Number of resources.
        Integer resourceCount = matrixMap.values().iterator().next().getRows();

        // Compute the max-plus automaton.
        MaxPlusAutomaton<Location> mpa = ComputeMPA.computeMaxPlusAutomaton(
                fsm, resourceCount, matrixMap);

        logger.info("Max-Plus automaton constructed: " + mpa.getVertices().size()
                + " states and " + mpa.getEdges().size() + " edges.");

        // Perform the minimum cycle mean computation on the SCCs.
        List<MaxPlusAutomaton<Location>> mpaSCCs = ComputeMPA.getSCCs(mpa);

        logger.info("Computed the " + mpaSCCs.size() + " strongly connected components.");

        Tuple<Double, List<MPATransition>> result = Tuple.of(Double.MAX_VALUE, new LinkedList<MPATransition>());
        int i = 1;
        for (MaxPlusAutomaton<Location> mpaSCC : mpaSCCs) {
            Tuple<Double, List<MPATransition>> sccResult = Howard.runHoward(mpaSCC);
            logger.info("Running Howard on component " + i + "/" + mpaSCCs.size());
            if (sccResult.getLeft() < result.getLeft()) {
                result = sccResult;
            }
            i++;
        }

        // Create the output.
        List<String> listOfEventNames =
                result.getRight().stream().map(MPATransition::getEvent).collect(Collectors.toList());
        return new MaximumThroughputResult(result.getLeft(), listOfEventNames);
    }
}
