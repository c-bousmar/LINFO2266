package linearprogramming;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RuntimeComparison {
    private static final String DATA_DIRECTORY = "data/Flow";
    private static final String OUTPUT_CSV = "runtime_comparison.csv";

    public static void main(String[] args) {
        File folder = new File(DATA_DIRECTORY);
        File[] files = folder.listFiles((dir, name) -> name.startsWith("flow") && name.endsWith(".txt"));

        if (files == null) {
            System.out.println("No files found in the data directory.");
            return;
        }

        try (FileWriter writer = new FileWriter(OUTPUT_CSV)) {
            writer.append("Instance,FlowMatrices_Runtime,LinearProgramming_Runtime\n");

            for (File file : files) {
                FlowNetwork network = FlowNetwork.fromFile(file.getPath());
                int source = network.source();
                int sink = network.sink();

                // Measure runtime for FlowMatrices + LinearProgramming
                long startFlowMatrices = System.nanoTime();
                FlowMatrices matrices = new FlowMatrices(network); // Convert network to LP form
                LinearProgramming simplex = new LinearProgramming(matrices.A, matrices.b, matrices.c);
                double[] solution = simplex.primal();
                matrices.assignFlow(solution);  // Apply solution to network
                long elapsedFlowMatrices = System.nanoTime() - startFlowMatrices;
                double flowMatricesRuntime = TimeUnit.NANOSECONDS.toMillis(elapsedFlowMatrices) / 1000.0;

                // Measure runtime for FordFulkerson
                long startFordFulkerson = System.nanoTime();
                new FordFulkerson(network, source, sink); // Execute Ford-Fulkerson
                long elapsedFordFulkerson = System.nanoTime() - startFordFulkerson;
                double fordFulkersonRuntime = TimeUnit.NANOSECONDS.toMillis(elapsedFordFulkerson) / 1000.0;

                // Write results to CSV
                writer.append(file.getName())
                        .append(",")
                        .append(String.valueOf(flowMatricesRuntime))
                        .append(",")
                        .append(String.valueOf(fordFulkersonRuntime))
                        .append("\n");
            }

            System.out.println("Runtime data written to " + OUTPUT_CSV);

        } catch (IOException e) {
            System.out.println("Error writing to CSV: " + e.getMessage());
        }
    }
}
