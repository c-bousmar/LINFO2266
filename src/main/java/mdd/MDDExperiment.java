package mdd;

import mdd.exercise.MaximumDecarbonationProblem;
import mdd.exercise.MaximumDecarbonationRelaxation;
import mdd.exercise.MaximumDecarbonationState;
import mdd.exercise.MaximumDecarbonationStateRanking;
import mdd.framework.core.Frontier;
import mdd.framework.core.Relaxation;
import mdd.framework.core.Solver;
import mdd.framework.heuristics.StateRanking;
import mdd.framework.heuristics.VariableHeuristic;
import mdd.framework.heuristics.WidthHeuristic;
import mdd.framework.implem.frontier.SimpleFrontier;
import mdd.framework.implem.heuristics.DefaultVariableHeuristic;
import mdd.framework.implem.heuristics.FixedWidth;
import mdd.framework.implem.solver.SequentialSolver;
import util.decarbonation.MaximumDecarbonationInstance;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MDDExperiment {

    public static void main(String[] args) throws IOException {
        String instancesPath = "data/decarbonation/instances";
        int[] widths = {1, 50, 100, 1000, 2000, 5000, 10000};
        String outputCsv = "sequential_solver_runtime.csv";

        List<String[]> results = new ArrayList<>();

        // Add header to CSV file
        results.add(new String[]{"Width", "Instance", "Runtime (ms), Solution"});
        System.out.println("Experiment started.");
        for (int width : widths) {
            results.add(new String[]{String.valueOf(width), String.valueOf(0), String.valueOf(0), String.valueOf(0)});
            for (int i = 0; i < 20; i++) {
                String instanceNumber = String.format("%02d", i);
                MaximumDecarbonationInstance decInstance = MaximumDecarbonationInstance.fromFile(instancesPath + "/decarbonation_100_0" + instanceNumber + ".dimacs");
                long startTime = System.currentTimeMillis();
                int solution = solveInstance(decInstance, width);
                long runtime = System.currentTimeMillis() - startTime;
                results.add(new String[]{String.valueOf(width), String.valueOf(i+1), String.valueOf(runtime), String.valueOf(solution)});
                System.out.println("Width: " + width + ", Instance: " + instanceNumber + ", Runtime: " + runtime + "ms" + ", Solution: " + solution);
            }
        }
        saveToCsv(results, outputCsv);
        System.out.println("Results saved to " + outputCsv);
    }

    private static int solveInstance(MaximumDecarbonationInstance instance, int width) {
        MaximumDecarbonationProblem problem = new MaximumDecarbonationProblem(instance);
        Relaxation<MaximumDecarbonationState> relax = new MaximumDecarbonationRelaxation(instance);
        StateRanking<MaximumDecarbonationState> ranking = new MaximumDecarbonationStateRanking();
        VariableHeuristic<MaximumDecarbonationState> varh = new DefaultVariableHeuristic<>();
        WidthHeuristic<MaximumDecarbonationState> widthHeuristic = new FixedWidth<>(width);
        Frontier<MaximumDecarbonationState> frontier = new SimpleFrontier<>(ranking);

        Solver solver = new SequentialSolver<>(problem, relax, varh, ranking, widthHeuristic, frontier);
        solver.maximize();

        return solver.bestValue().orElse(Integer.MIN_VALUE);
    }

    private static void saveToCsv(List<String[]> data, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] row : data) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        }
    }
}
