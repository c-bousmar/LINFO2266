package localsearch;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Implements a KOpt neighborhood for the TSP problem
 * also known as the Lin-Kernighan heuristic
 */
public class KOptSelection implements NeighborSelection {

    int maxK;

    public KOptSelection(int maxK) {
        this.maxK = maxK;
    }

    @Override
    public Candidate getNeighbor(Candidate candidate) {
        // TODO implement a KOpt neighborhood by repeatedly applying the best 2Swap k times then return the best solution found
        // hint : use a map to save intermediate solutions
        Map<Integer, Candidate> solutions = new HashMap<>();
        solutions.put(0, candidate);

        Candidate bestCandidate = candidate;
        double bestCost = candidate.getCost();
        int globalK = 0;

        for (int city = 0; city < candidate.getTour().size(); city++) {
            Candidate bestSwapCandidate = doKOpt(candidate, city);

            if (bestSwapCandidate.getCost() < bestCost) {
                bestCandidate = bestSwapCandidate;
                bestCost = bestSwapCandidate.getCost();
                solutions.put(globalK, bestCandidate);
                globalK++;
            }
        }
        return bestCandidate;
    }


    private Candidate doKOpt(Candidate candidate, int fixedCity) {
        Map<Integer, Candidate> solutions = new HashMap<>();
        solutions.put(0, candidate);

        Candidate bestCandidate = candidate;
        double bestCost = candidate.getCost();

        for (int k = 2; k < maxK; k++) {
            Candidate bestSwapCandidate = bestCandidate;
            double bestSwapCost = bestCost;

            for (int city = 0; city < bestCandidate.getTour().size(); city++) {
                if (city == fixedCity) continue;

                Candidate newCandidate = bestCandidate.clone();
                newCandidate.twoOpt(fixedCity, city);

                if (newCandidate.getCost() < bestSwapCost) {
                    bestSwapCandidate = newCandidate;
                    bestSwapCost = newCandidate.getCost();
                }
            }

            if (bestSwapCost < bestCost) {
                bestCandidate = bestSwapCandidate;
                bestCost = bestSwapCost;
                solutions.put(k + 1, bestCandidate);
            } else {
                break;
            }
        }

        return bestCandidate;
    }
}