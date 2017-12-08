package org.ludus.backend.games.benchmarking;

import java.util.HashMap;

/**
 * @author Bram van der Sanden
 */
public class RunBenchmarks {

    public static HashMap<String, Benchmark> generateBenchmarks() {
        HashMap<String, Benchmark> benchmarks = new HashMap<>();

        Benchmark b;

        // Testset Sprand_4W10N1_10
        b = new SprandIntBenchmark("Sprand_4W10N1_10", 1, 10, 1, 4, 10, 10);
        b.setEnabled(true, true, true);
        benchmarks.put(b.getName(), b);

        b = new SprandDoubleBenchmark("Sprand_4W10N1_10d", 1, 10, 1, 4, 10.0, 10.0);
        b.setEnabled(true, true, true);
        benchmarks.put(b.getName(), b);

        b = new SprandIntDoubleEncodingBenchmark("Sprand_4W10N1_10dn", 1, 10, 1, 4, 10, 10);
        b.setEnabled(true, true, true);
        benchmarks.put(b.getName(), b);

        // Testset Sprand_4W10N1_101
        b = new SprandIntBenchmark("Sprand_4W10N1_101", 1, 111, 10, 4, 10, 10);
        b.setEnabled(true, true, false);
        benchmarks.put(b.getName(), b);

        b = new SprandDoubleBenchmark("Sprand_4W10N1_101d", 1, 111, 10, 4, 10.0, 10.0);
        b.setEnabled(true, true, false);
        benchmarks.put(b.getName(), b);

        b = new SprandIntDoubleEncodingBenchmark("Sprand_4W10N1_101dn", 1, 111, 10, 4, 10, 10);
        b.setEnabled(true, true, false);
        benchmarks.put(b.getName(), b);

        // Testset Sprand_4W50N1_50000.
        b = new SprandIntBenchmark("Sprand_4W50N1_50000", 500, 50000, 500, 4, 50, 50);
        b.setEnabled(true, false, false);
        benchmarks.put(b.getName(), b);

        b = new SprandDoubleBenchmark("Sprand_4W50N1_50000d", 500, 50000, 500, 4, 50.0, 50.0);
        b.setEnabled(true, false, false);
        benchmarks.put(b.getName(), b);

        b = new SprandIntDoubleEncodingBenchmark("Sprand_4W50N1_50000dn", 500, 50000, 500, 4, 50, 50);
        b.setEnabled(true, false, false);
        benchmarks.put(b.getName(), b);

        b = new EpsilonVaryingBenchmark("SprandEpsilon", 500, 50000, 500, 4, 50, 50);
        b.setEnabled(true, false, false);
        benchmarks.put(b.getName(), b);

        // Testset Tor_W10N1_3.
        b = new TorIntBenchmark("Tor_W10N1_3", 1, 3, 1, 10, 10);
        b.setEnabled(true, true, true);
        benchmarks.put(b.getName(), b);

        b = new TorDoubleBenchmark("Tor_W10N1_3d", 1, 3, 1, 10.0, 10.0);
        b.setEnabled(true, true, true);
        benchmarks.put(b.getName(), b);

        b = new TorIntDoubleEncodingBenchmark("Tor_W10N1_3dn", 1, 3, 1, 10, 10);
        b.setEnabled(true, true, true);
        benchmarks.put(b.getName(), b);

        // Testset Tor_W10N1_10.
        b = new TorIntBenchmark("Tor_W10N1_10", 1, 10, 1, 10, 10);
        b.setEnabled(true, true, false);
        benchmarks.put(b.getName(), b);

        b = new TorDoubleBenchmark("Tor_W10N1_10d", 1, 10, 1, 10.0, 10.0);
        b.setEnabled(true, true, false);
        benchmarks.put(b.getName(), b);

        b = new TorIntDoubleEncodingBenchmark("Tor_W10N1_10dn", 1, 10, 1, 10, 10);
        b.setEnabled(true, true, false);
        benchmarks.put(b.getName(), b);

        // Testset Tor_W50N1_221.       
        b = new TorIntBenchmark("Tor_W50N1_221", 1, 221, 10, 50, 50);
        b.setEnabled(true, false, false);
        benchmarks.put(b.getName(), b);

        b = new TorDoubleBenchmark("Tor_W50N1_221d", 1, 221, 10, 50.0, 50.0);
        b.setEnabled(true, false, false);
        benchmarks.put(b.getName(), b);

        b = new TorIntDoubleEncodingBenchmark("Tor_W50N1_221dn", 1, 221, 10, 50, 50);
        b.setEnabled(true, false, false);
        benchmarks.put(b.getName(), b);

        b = new TorDoubleBenchmark("TorDoubleLarge", 191, 221, 10, 50.0, 50.0);
        b.setEnabled(true, false, false);
        benchmarks.put(b.getName(), b);

        b = new TorEpsDeltaBenchmark("TorEpsDelta", 1, 221, 10, 50, 50);
        b.setEnabled(true, false, false);
        benchmarks.put(b.getName(), b);


        b = new TorEpsBenchmark("TorEps", 1, 221, 10, 50, 50);
        b.setEnabled(true, false, false);
        benchmarks.put(b.getName(), b);

        return benchmarks;
    }

    /**
     * Entry point for running benchmarks. Parse the command line arguments, and
     * execute the corresponding benchmark.
     *
     * @param args arguments
     */
    public static void main(String[] args) {

        HashMap<String, Benchmark> benchmarks = generateBenchmarks();

        if (args.length == 2) {
            // Execute predefined benchmark.
            Benchmark b = benchmarks.get(args[0]);
            Integer numberOfRuns = Integer.valueOf(args[1]);
            b.run(numberOfRuns);
        } else {
            // Used to run some benchmark within NetBeans.
            Benchmark b;
            //b = new EpsilonVaryingBenchmark("SprandEpsilon", 10500, 50000, 500, 4, 50, 50);   
            //b = new SprandDoubleBenchmark("Sprand_4W50N1_50000d", 10000, 50000, 2000, 4, 50.0, 50.0);
            b = new TorEpsDeltaFuzzyBenchmark("TorEpsDeltaFuzzy", 1, 221, 10, 50, 50);
            b.setEnabled(true, false, false);
            b.run(5);
        }
    }
}
