package org.ludus.backend.games.benchmarking;

import org.ludus.backend.games.algorithms.DoubleFunctions;
import org.ludus.backend.games.ratio.solvers.energy.RatioGameValueIterationDouble;
import org.ludus.backend.games.ratio.solvers.energy.RatioGameValueIterationInt;
import org.ludus.backend.games.ratio.solvers.policy.PolicyIterationDouble;
import org.ludus.backend.games.ratio.solvers.policy.PolicyIterationInt;
import org.ludus.backend.games.ratio.solvers.zwick.SolverZPDouble;
import org.ludus.backend.games.ratio.solvers.zwick.SolverZPInt;
import org.ludus.backend.graph.jgrapht.ratio.RGDoubleImplJGraphT;
import org.ludus.backend.graph.jgrapht.ratio.RGIntImplJGraphT;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Bram van der Sanden
 */
public abstract class Benchmark {

    protected boolean runPI;
    protected boolean runEG;
    protected boolean runZP;

    public abstract String getName();

    public void setEnabled(boolean runPI, boolean runEG, boolean runZP) {
        this.runPI = runPI;
        this.runEG = runEG;
        this.runZP = runZP;
    }

    public void run(Integer numberOfIterations) {
        run(numberOfIterations, runPI, runEG, runZP);
    }

    public abstract void run(Integer numberOfIterations,
                             boolean runPI, boolean runEG, boolean runZP);

    protected long runPI(RGIntImplJGraphT game) {
        long start = System.nanoTime();
        PolicyIterationInt.solve(game);
        long end = System.nanoTime();
        return (end - start);
    }

    protected long runEG(RGIntImplJGraphT game) {
        long start = System.nanoTime();
        RatioGameValueIterationInt.solve(game);
        long end = System.nanoTime();
        return (end - start);
    }

    protected long runZP(RGIntImplJGraphT game) {
        long start = System.nanoTime();
        SolverZPInt.getValues(game);
        long end = System.nanoTime();
        return (end - start);
    }

    protected long runPI(RGDoubleImplJGraphT game) {
        return runPI(game, DoubleFunctions.EPSILON);
    }

    protected long runPI(RGDoubleImplJGraphT game, Double epsilon) {
        long start = System.nanoTime();
        PolicyIterationDouble.solve(game, epsilon);
        long end = System.nanoTime();
        return (end - start);
    }

    protected long runEG(RGDoubleImplJGraphT game) {
        return runEG(game, DoubleFunctions.EPSILON);
    }

    protected long runEG(RGDoubleImplJGraphT game, Double epsilon) {
        long start = System.nanoTime();
        RatioGameValueIterationDouble.solve(game, epsilon);
        long end = System.nanoTime();
        return (end - start);
    }

    protected long runZP(RGDoubleImplJGraphT game) {
        return runZP(game, DoubleFunctions.EPSILON);
    }

    protected long runZP(RGDoubleImplJGraphT game, Double epsilon) {
        long start = System.nanoTime();
        SolverZPDouble.getValues(game, epsilon);
        long end = System.nanoTime();
        return (end - start);
    }

    protected PrintWriter getFile(String fileName) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").format(new Date());

        try {
            return new PrintWriter("results/" + fileName + "_" + timeStamp + ".csv");
        } catch (IOException ex) {
            Logger.getLogger(Benchmark.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
