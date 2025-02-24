package localsearch;

import java.util.Random;

public class SimulatedAnnealing implements NeighborSelection {

    private double temperature;
    private final double coolingRate;
    private final double initialTemperature;
    private final Random random;

    private Candidate bestSolution;
    private double bestSolutionCost;
    private int iterationsSinceImprovement;
    private final int maxIterationsWithoutImprovement;

    public SimulatedAnnealing(double initialTemperature, double coolingRate, int maxIterationsWithoutImprovement) {
        this.temperature = initialTemperature;
        this.initialTemperature = initialTemperature;
        this.coolingRate = coolingRate;
        this.random = new Random();
        this.maxIterationsWithoutImprovement = maxIterationsWithoutImprovement;
        this.iterationsSinceImprovement = 0;
    }

    @Override
    public Candidate getNeighbor(Candidate candidate) {
        double bestDelta = 0.0;
        int bestI = -1;
        int bestJ = -1;

        for (int i = 0; i < candidate.getTour().size() - 1; i++) {
            for (int j = i + 2; j < candidate.getTour().size(); j++) {
                double delta = candidate.twoOptDelta(i, j);

                if (delta < bestDelta || random.nextDouble() < Math.exp(-delta / temperature)) {
                    bestDelta = delta;
                    bestI = i;
                    bestJ = j;
                }
            }
        }
        if (bestI != -1 && bestJ != -1) {
            candidate.twoOpt(bestI, bestJ);
        }

        if (bestSolution == null || candidate.getCost() < bestSolutionCost) {
            bestSolution = candidate.clone();
            bestSolutionCost = candidate.getCost();
            iterationsSinceImprovement = 0;
        } else {
            iterationsSinceImprovement++;
        }
        if (iterationsSinceImprovement >= maxIterationsWithoutImprovement) {
            candidate = bestSolution.clone();
            iterationsSinceImprovement = 0;
        }
        temperature *= coolingRate;

        return candidate;
    }
}
