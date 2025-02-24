package localsearch;

/**
 * Select the best neighbor that improves the solution.
 * If no neighbor improves the solution, return the original solution
 */
public class BestSelection implements NeighborSelection {
    @Override
    public Candidate getNeighbor(Candidate candidate) {
        // TODO implement a method that selects the best neighbor in the neighborhood of a solution
        // hint : help you from the implementation of FirstSelection
        double bestDelta = 0.0;
        int bestI = -1;
        int bestJ = -1;

        for (int i = 0; i < candidate.getTour().size() - 1; i++) {
            for (int j = i + 2; j < candidate.getTour().size(); j++) {
                double delta = candidate.twoOptDelta(i, j);
                if (delta < bestDelta) {
                    bestDelta = delta;
                    bestI = i;
                    bestJ = j;
                }
            }
        }

        if (bestI != -1 && bestJ != -1) {
            candidate.twoOpt(bestI, bestJ);
        }

        return candidate;
    }
}
