package localsearch;

import util.tsp.TSPInstance;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Beam a TSP using a beam search algorithm
 */
public class BeamSearchInitialization extends Initialization {

    private final int beamWidth;

    public BeamSearchInitialization(TSPInstance tsp, int beamWidth) {
        super(tsp);
        this.beamWidth = beamWidth;
    }

    @Override
    public Candidate getInitialSolution() {
        PriorityQueue<Candidate> beam = new PriorityQueue<>(Comparator.comparingDouble(Candidate::getCost));
        ArrayList<Integer> initialTour = new ArrayList<>();
        initialTour.add(0);
        Candidate bestCandidate = new Candidate(tsp, initialTour);
        beam.add(bestCandidate);

        while (!beam.isEmpty()) {
            PriorityQueue<Candidate> childrenCandidates = new PriorityQueue<>(Comparator.comparingDouble(Candidate::getCost));

            while (!beam.isEmpty()) {
                Candidate parentCandidate = beam.poll();
                ArrayList<Integer> parentTour = new ArrayList<>(parentCandidate.getTour());
                BitSet visitedCities = new BitSet(tsp.nCities());
                parentTour.forEach(visitedCities::set);

                if (parentTour.size() == tsp.n) {
                    double finalCost = parentCandidate.getCost();
                    if (bestCandidate == null || finalCost < bestCandidate.getCost()) {
                        bestCandidate = parentCandidate;
                    }
                    continue;
                }

                for (int city = 1; city < tsp.n; city++) {
                    if (!visitedCities.get(city)) {
                        ArrayList<Integer> newTour = new ArrayList<>(parentTour);
                        newTour.add(city);
                        Candidate childCandidate = new Candidate(tsp, newTour);
                        childrenCandidates.add(childCandidate);
                    }
                }
            }
            beam.clear();
            for (int i = 0; i < beamWidth && !childrenCandidates.isEmpty(); i++) {
                beam.add(childrenCandidates.poll());
            }
        }
        if (bestCandidate.getTour().size() != tsp.n) {
            ArrayList<Integer> finalTour = new ArrayList<>(bestCandidate.getTour());
            for (int i = 1; i < tsp.n; i++) {
                if (finalTour.contains(i)) continue;
                finalTour.add(i);
            }
            bestCandidate = new Candidate(tsp, finalTour);
        }
        return bestCandidate;
    }
}
