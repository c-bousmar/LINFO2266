package mdd.exercise;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;

import mdd.framework.core.Decision;
import mdd.framework.core.Problem;
import util.decarbonation.MaximumDecarbonationInstance;

/**
 * In this class you will model the maximum decarbonation problem that is
 * described in your assignment.
 * You can use it more easily with the methods defined in {@link Example}
 */
public final class MaximumDecarbonationProblem implements Problem<MaximumDecarbonationState> {
    /** The instance of the problem we want to solve */
    private final MaximumDecarbonationInstance instance;

    // TODO you can add fields to this class if deemed useful.

    /** 
     * Creates the DP model based on a given instance
     * @param instance the actual problem instance which is going to be solved
     */
    public MaximumDecarbonationProblem(final MaximumDecarbonationInstance instance) {
        this.instance = instance;
    }

    @Override
    public int nbVars() {
        // TODO Fill in the details of your DP model
         return instance.nbSites();
    }

    @Override
    public MaximumDecarbonationState initialState() {
        // TODO Fill in the details of your DP model
        return new MaximumDecarbonationState(new BitSet(instance.nbSites()), 0);
    }

    @Override
    public int initialValue() {
        // TODO Fill in the details of your DP model
         return 0;
    }

    @Override
    public Iterator<Integer> domain(final MaximumDecarbonationState state, final int var) {
        // TODO Fill in the details of your DP model
        BitSet neighbors = instance.neighbors(var + 1);
        if (state.getIncludedNodes().intersects(neighbors)) return Arrays.asList(0).iterator();
        return Arrays.asList(1, 0).iterator();
    }

    @Override
    public MaximumDecarbonationState transition(final MaximumDecarbonationState state, final Decision decision) {
        // TODO Fill in the details of your DP model
        if (decision.val() == 1) return state.includeNode(decision.var(), 1);
        return state;
    }

    @Override
    public int transitionCost(final MaximumDecarbonationState state, final Decision decision) {
        // TODO Fill in the details of your DP model
        return decision.val();
    }
    
}
