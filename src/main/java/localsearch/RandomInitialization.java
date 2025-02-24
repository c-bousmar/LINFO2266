package localsearch;

import util.tsp.TSPInstance;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Initializes with a random tour
 */
public class RandomInitialization extends Initialization {


    public RandomInitialization(TSPInstance tsp) {
        super(tsp);
    }

    @Override
    public Candidate getInitialSolution() {
        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i < tsp.nCities(); i++) {
            list.add(i);
        }

        Collections.shuffle(list);
        return new Candidate(tsp, list);
    }

    public static void main(String[] args) {
        test_tabu_for_report();
        test_enhanced_search_for_report();
    }

    private static void test_tabu_for_report() {
        // Paths
        String tspFilePath = "data/TSP/gr48.xml";
        String outputFilePath = "/Users/cyrilbousmar/Dev/LINFO2266/reports_plots/local_search/tabu.csv";

        try {
            TSPInstance tsp = new TSPInstance(tspFilePath);

            RandomInitialization randomInit = new RandomInitialization(tsp);
            Candidate initialSolution = randomInit.getInitialSolution();

            BestSelection bestSelection = new BestSelection();
            BestWithTabuSelection tabuSelection3 = new BestWithTabuSelection(3, tsp);
            BestWithTabuSelection tabuSelection15 = new BestWithTabuSelection(15, tsp);

            try (FileWriter writer = new FileWriter(outputFilePath)) {
                writer.append("Iteration,BestSelection,TabuSelection3,TabuSelection15\n");

                Candidate bestSelectionCandidate = initialSolution.clone();
                Candidate tabu3Candidate = initialSolution.clone();
                Candidate tabu15Candidate = initialSolution.clone();

                for (int i = 0; i < 200; i++) {
                    bestSelectionCandidate = bestSelection.getNeighbor(bestSelectionCandidate);
                    tabu3Candidate = tabuSelection3.getNeighbor(tabu3Candidate);
                    tabu15Candidate = tabuSelection15.getNeighbor(tabu15Candidate);

                    writer.append(i + "," + bestSelectionCandidate.getCost() + ","
                            + tabu3Candidate.getCost() + ","
                            + tabu15Candidate.getCost() + "\n");
                }
            }
            System.out.println("Results saved to: " + outputFilePath);

        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error running the program: " + e.getMessage());
        }
    }


    private static void test_enhanced_search_for_report() {
        // Paths
        String tspFilePath = "data/TSP/gr48.xml";
        String outputFilePath = "/Users/cyrilbousmar/Dev/LINFO2266/reports_plots/local_search/enhanced.csv";

        try {
            TSPInstance tsp = new TSPInstance(tspFilePath);

            RandomInitialization randomInit = new RandomInitialization(tsp);

            BestSelection bestSelection = new BestSelection();
            SimulatedAnnealing enhancedSearch = new SimulatedAnnealing(1000000, 0.05, 100);

            int numRuns = 5;
            int numIterations = 1000;

            double[][] bestSelectionResults = new double[numRuns][numIterations];
            double[][] enhancedResults = new double[numRuns][numIterations];

            for (int run = 0; run < numRuns; run++) {
                Candidate initialSolution = randomInit.getInitialSolution();
                Candidate bestSelectionCandidate = initialSolution.clone();
                Candidate enhancedCandidate = initialSolution.clone();

                for (int i = 0; i < numIterations; i++) {
                    bestSelectionCandidate = bestSelection.getNeighbor(bestSelectionCandidate);
                    enhancedCandidate = enhancedSearch.getNeighbor(enhancedCandidate);
                    bestSelectionResults[run][i] = bestSelectionCandidate.getCost();
                    enhancedResults[run][i] = enhancedCandidate.getCost();
                }
            }
            try (FileWriter writer = new FileWriter(outputFilePath)) {
                StringBuilder header = new StringBuilder("Iteration");
                for (int run = 0; run < numRuns; run++) {
                    header.append(",BS").append(run + 1).append(",SA").append(run + 1);
                }
                writer.append(header).append("\n");
                for (int i = 0; i < numIterations; i++) {
                    StringBuilder row = new StringBuilder(String.valueOf(i));
                    for (int run = 0; run < numRuns; run++) {
                        row.append(",").append(bestSelectionResults[run][i])
                                .append(",").append(enhancedResults[run][i]);
                    }
                    writer.append(row).append("\n");
                }
            }
            System.out.println("Results saved to: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error running the program: " + e.getMessage());
        }
    }

}
