package dynamicprogramming;

import util.tsp.TSPInstance;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TSPAnalyzer {

    public static void main(String[] args) {
        String tspDirectoryPath = "data/TSP";

        int[] sizes = {8, 10, 18};

        List<Long> runtimes8 = new ArrayList<>();
        List<Long> runtimes10 = new ArrayList<>();
        List<Long> runtimes18 = new ArrayList<>();

        printMachineSpecifications();

        System.out.printf("%-20s %-10s %-15s%n", "File Name", "Size", "Runtime (ms)");

        for (int size : sizes) {
            File directory = new File(tspDirectoryPath);
            File[] tspFiles = directory.listFiles((dir, name) -> name.matches("instance_" + size + "_\\d+\\.xml"));

            if (tspFiles != null) {
                for (File tspFile : tspFiles) {
                    try {
                        TSPInstance tspInstance = new TSPInstance(tspFile.getPath());
                        TSP model = new TSP(tspInstance);

                        long startTime = System.currentTimeMillis();
                        DynamicProgramming<TSPState> solver = new DynamicProgramming<>(model);
                        solver.getSolution();
                        long endTime = System.currentTimeMillis();

                        long runtime = endTime - startTime;

                        if (size == 8) {
                            runtimes8.add(runtime);
                        } else if (size == 10) {
                            runtimes10.add(runtime);
                        } else if (size == 18) {
                            runtimes18.add(runtime);
                        }

                        System.out.printf("%-20s %-10d %-15d%n", tspFile.getName(), size, runtime);

                    } catch (Exception e) {
                        System.out.println("Error solving TSP instance: " + tspFile.getName() + ". " + e.getMessage());
                    }
                }
            } else {
                System.out.println("No files found for TSP size: " + size);
            }
        }

        writeRuntimesToCSV("tsp_runtimes.csv", runtimes8, runtimes10, runtimes18);

        System.out.println("Runtime results have been written to tsp_runtimes.csv.");
    }

    private static void writeRuntimesToCSV(String fileName, List<Long> runtimes8, List<Long> runtimes10, List<Long> runtimes18) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.append("Instance Size,Runtime (ms)\n");
            for (Long runtime : runtimes8) {
                writer.append("8,").append(runtime.toString()).append("\n");
            }
            for (Long runtime : runtimes10) {
                writer.append("10,").append(runtime.toString()).append("\n");
            }
            for (Long runtime : runtimes18) {
                writer.append("18,").append(runtime.toString()).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    private static void printMachineSpecifications() {
        System.out.println("Machine Specifications:");
        System.out.println("Operating System: " + System.getProperty("os.name"));
        System.out.println("CPU Architecture: " + System.getProperty("os.arch"));
        System.out.println("JVM Version: " + System.getProperty("java.version"));
        System.out.println("Available Processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Total Memory (MB): " + Runtime.getRuntime().totalMemory() / (1024 * 1024));
        System.out.println("--------------------------------------------------------");
    }
}
