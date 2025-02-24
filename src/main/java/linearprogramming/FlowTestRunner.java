package linearprogramming;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class FlowTestRunner {
    private static final String DATA_DIRECTORY = "data/Flow";
    private static final List<Long> TIME_LIMITS = Arrays.asList(
            TimeUnit.SECONDS.toNanos(1),
            TimeUnit.SECONDS.toNanos(5),
            TimeUnit.SECONDS.toNanos(10),
            TimeUnit.SECONDS.toNanos(15),
            TimeUnit.SECONDS.toNanos(20),
            TimeUnit.SECONDS.toNanos(25),
            TimeUnit.SECONDS.toNanos(30)
    );
    private static final String OUTPUT_CSV = "cumulative_results.csv";

    public static void main(String[] args) {
        List<Map<String, Integer>> cumulativeResults = new ArrayList<>();

        for (long maxTime : TIME_LIMITS) {
            System.out.println("Run");
            // Initialize counters for each algorithm
            int fordFulkersonSolved = 0;
            int linearProgrammingSolved = 0;

            // Run both algorithms and count cumulative solves within each time limit
            fordFulkersonSolved = countSolvedWithinTimeLimit(maxTime, true);
            linearProgrammingSolved = countSolvedWithinTimeLimit(maxTime, false);

            // Record cumulative results
            Map<String, Integer> resultMap = new LinkedHashMap<>();
            resultMap.put("FF-" + TimeUnit.NANOSECONDS.toSeconds(maxTime), fordFulkersonSolved);
            resultMap.put("LP-" + TimeUnit.NANOSECONDS.toSeconds(maxTime), linearProgrammingSolved);
            cumulativeResults.add(resultMap);
        }

        // Write cumulative results to CSV
        writeCumulativeResultsToCSV(cumulativeResults);
    }

    private static int countSolvedWithinTimeLimit(long maxTime, boolean isFordFulkerson) {
        int solvedCount = 0;

        File folder = new File(DATA_DIRECTORY);
        File[] files = folder.listFiles((dir, name) -> name.startsWith("flow") && name.endsWith(".txt"));

        if (files == null) {
            System.out.println("No files found in the data directory.");
            return 0;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();

        for (File file : files) {
            try {
                long startTime = System.nanoTime();
                FlowNetwork network = FlowNetwork.fromFile(file.getPath());
                int source = network.source();
                int sink = network.sink();

                // Create a Callable to run the appropriate algorithm
                Callable<Boolean> task = () -> {
                    return isFordFulkerson
                            ? runFordFulkerson(network, source, sink)
                            : runLinearProgramming(network);
                };

                // Execute the task with a timeout
                Future<Boolean> future = executor.submit(task);
                boolean solved;
                try {
                    solved = future.get(maxTime, TimeUnit.NANOSECONDS);
                } catch (TimeoutException e) {
                    solved = false;  // Timed out before completion
                    future.cancel(true);  // Cancel the task if it exceeds the time limit
                }

                long elapsedTime = System.nanoTime() - startTime;
                if (solved && elapsedTime <= maxTime) {
                    solvedCount++;
                }

            } catch (Exception e) {
                System.out.println("Error processing file " + file.getName() + ": " + e.getMessage());
            }
        }

        executor.shutdown();
        return solvedCount;
    }

    private static boolean runFordFulkerson(FlowNetwork network, int source, int sink) {
        try {
            new FordFulkerson(network, source, sink);  // Execute Ford-Fulkerson
            return true;
        } catch (Exception e) {
            System.out.println("Ford-Fulkerson failed: " + e.getMessage());
            return false;
        }
    }

    private static boolean runLinearProgramming(FlowNetwork network) {
        try {
            FlowMatrices matrices = new FlowMatrices(network); // Transform to LP form
            LinearProgramming simplex = new LinearProgramming(matrices.A, matrices.b, matrices.c);
            double[] solution = simplex.primal();
            matrices.assignFlow(solution);  // Assign the solution to the network
            return true;
        } catch (Exception e) {
            System.out.println("Linear Programming failed: " + e.getMessage());
            return false;
        }
    }

    private static void writeCumulativeResultsToCSV(List<Map<String, Integer>> cumulativeResults) {
        try (FileWriter writer = new FileWriter(OUTPUT_CSV)) {
            // Write header
            for (Map<String, Integer> results : cumulativeResults) {
                for (String key : results.keySet()) {
                    writer.append(key).append(",");
                }
            }
            writer.append("\n");

            // Write data
            for (Map<String, Integer> results : cumulativeResults) {
                for (String key : results.keySet()) {
                    writer.append(String.valueOf(results.get(key))).append(",");
                }
                writer.append("\n");
            }

            System.out.println("Cumulative results written to " + OUTPUT_CSV);

        } catch (IOException e) {
            System.out.println("Error writing to CSV: " + e.getMessage());
        }
    }
}
