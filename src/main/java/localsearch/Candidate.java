package localsearch;

import util.tsp.TSPInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a candidate solution for the TSP problem
 */
public class Candidate {

    // The tour of the candidate solution from the first city
    // to the last, the first and last city is not repeated
    private ArrayList<Integer> tour; // a permutation of the cities from 0 to n-1

    private TSPInstance tsp;
    private double cost;

    public Candidate(TSPInstance tsp, List<Integer> tour) {
        this.tsp = tsp;
        this.tour = new ArrayList<>(tour);
        this.cost = computeCost();
    }

    public TSPInstance getTsp() {
        return tsp;
    }

    public double getCost() {
        return cost;
    }

    /**
     *returns the city that is after the city placed at index index
     * @param index
     * @return an int that is the city
     */
    public int getSucc(int index) {
        return tour.get((index + 1) % tour.size());
    }

    public double computeCost() {
        // computes the initial cost of the solution : only called once
        // at initialisation and updated using twoOptDelta at each move
        double sum = 0;
        for (int i = 0; i < tour.size(); i++) {
            sum += tsp.distance(tour.get(i), getSucc(i));
        }
        return sum;
    }


    /**
     * Compute the delta of the 2-opt move between index1 and index2
     *
     * @param index1 the first index (excluded)
     * @param index2 > index1, the second index (included)
     * @return the delta of the 2-opt move, that is the cost after the move minus the cost before the move
     */
    public double twoOptDelta(int index1, int index2) {
        return tsp.distance(tour.get(index1), tour.get(index2)) +
                tsp.distance(tour.get((index1 + 1) % tour.size()), tour.get((index2 + 1) % tour.size())) -
                tsp.distance(tour.get(index1), tour.get((index1 + 1) % tour.size())) -
                tsp.distance(tour.get(index2), tour.get((index2 + 1) % tour.size()));
    }


    /**
     * Apply the 2-opt move by reverting the order of the cities between
     * index1 (excluded) and index2 (included) if index1 > index2 they need to be swapped
     * and also update the cost of the solution
     * @param index1 the first index (excluded)
     * @param index2 the second index (included)
     */
    public void twoOpt(int index1, int index2) {
        // TODO Swap the successor of index1 with index 2. Note : index1 can be greater than index2
        if (index1 > index2) {
            int temp = index1;
            index1 = index2;
            index2 = temp;
        }
        Collections.reverse(tour.subList(index1 + 1, index2 + 1));

        this.cost = computeCost();
    }


    @Override
    public Candidate clone() {
        Candidate c = new Candidate(tsp, this.tour);
        return c;
    }

    public List<Integer> getTour() {
        return tour;
    }

}
