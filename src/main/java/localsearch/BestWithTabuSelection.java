package localsearch;

import util.Pair;
import util.tsp.TSPInstance;

import java.util.HashSet;

/**
 * Select the best neighbor that is not tabu
 */
public class BestWithTabuSelection implements NeighborSelection {

    int iteration = 0;
    private int tabuSize; // size of the tabu list
    int[][] tabu; // tabu[i][j] is the next iteration when the edge (i,j) become non-tabu

    public BestWithTabuSelection(int tabuSize, TSPInstance tsp) {
        iteration = 0;
        this.tabuSize = tabuSize;
        tabu = new int[tsp.n][tsp.n];
    }


    public void addTabu(int i, int j) {
        tabu[i][j] = iteration + tabuSize;
    }

    public boolean isTabu(int i, int j) {
        return iteration < tabu[i][j];
    }

    /**
     * Selects the best non-tabu 2Opt neighbor
     * It should never return the same candidate as the one given in argument
     * The first removed edge in the selected move becomes tabu
     * @param candidate
     * @return the best non-tabu 2Opt neighbor, the first removed edge in the selected move becomes tabu
     */
    @Override
    public Candidate getNeighbor(Candidate candidate) {
        iteration++;
        //TODO
        double bestDelta = Double.POSITIVE_INFINITY;
        int bestIdx1 = 0;
        int bestIdx2 = 0;

        for (int i = 0; i < candidate.getTour().size() - 1; i++) {
            for (int j = i + 2; j < candidate.getTour().size(); j++) {

                if (!isTabu(candidate.getTour().get(i), candidate.getTour().get(j))) {

                    double delta = candidate.twoOptDelta(i, j);
                    if (delta < bestDelta) {
                        bestDelta = delta;
                        bestIdx1 = i;
                        bestIdx2 = j;
                    }
                }
            }
        }
        int removedEdgeEndIdx = candidate.getTour().get(bestIdx1 + 1);
        addTabu(candidate.getTour().get(bestIdx1), removedEdgeEndIdx);
        candidate.twoOpt(bestIdx1, bestIdx2);
        return candidate;
    }
}

