package mdd.exercise;

import java.util.BitSet;
import java.util.Iterator;
import java.util.PriorityQueue;

import mdd.framework.core.Decision;
import mdd.framework.core.Relaxation;
import util.decarbonation.MaximumDecarbonationInstance;

/**
 * In this class you will implement a relaxation of the maximum decarbonation
 * problem model that you have defined.
 */
public final class MaximumDecarbonationRelaxation implements Relaxation<MaximumDecarbonationState> {
    /** The instance of the problem we want to solve */
    // TODO you can add fields to this class if deemed useful.
    private final MaximumDecarbonationInstance instance;
    private final int maxWidth;
    
    /** 
     * Creates the DP model based on a given instance
     * @param instance the actual problem instance which is going to be solved
     */
    public MaximumDecarbonationRelaxation(final MaximumDecarbonationInstance instance) {
        this.instance = instance;
        this.maxWidth = instance.nbSites();
    }

    @Override
    public MaximumDecarbonationState mergeStates(final Iterator<MaximumDecarbonationState> states) {
        // TODO Fill in the details of your DP relaxation
        MaximumDecarbonationState[] reducedStates = reduceStates(states);
        return mergeReducedStates(reducedStates);
    }

    private MaximumDecarbonationState mergeReducedStates(MaximumDecarbonationState[] states) {
        BitSet unionOfNodes = new BitSet(instance.nbSites());
        int totalValue = 0;
        for (MaximumDecarbonationState state : states) {
            BitSet currentIncludedNodes = state.getIncludedNodes();
            for (int i = currentIncludedNodes.nextSetBit(0); i >= 0; i = currentIncludedNodes.nextSetBit(i + 1)) {
                if (canIncludeNode(unionOfNodes, i)) {
                    unionOfNodes.set(i);
                }
            }
            totalValue += state.getCurrentValue();
        }
        return new MaximumDecarbonationState(unionOfNodes, totalValue);
    }

    public MaximumDecarbonationState[] reduceStates(final Iterator<MaximumDecarbonationState> states) {
        PriorityQueue<MaximumDecarbonationState> stateQueue = new PriorityQueue<>(
                (s1, s2) -> Integer.compare(s2.getCurrentValue(), s1.getCurrentValue()));
        while (states.hasNext()) {
            stateQueue.add(states.next());
        }
        while (stateQueue.size() > maxWidth) {
            MaximumDecarbonationState state1 = stateQueue.poll();
            MaximumDecarbonationState state2 = stateQueue.poll();
            MaximumDecarbonationState mergedState = mergeReducedStates(new MaximumDecarbonationState[] {state1, state2});
            stateQueue.add(mergedState);
        }
        return stateQueue.toArray(new MaximumDecarbonationState[0]);
    }

    private boolean canIncludeNode(BitSet includedNodes, int node) {
        BitSet neighbors = instance.neighbors(node + 1);
        neighbors.and(includedNodes);
        return neighbors.isEmpty();
    }

    @Override
    public int relaxEdge(final MaximumDecarbonationState from, final MaximumDecarbonationState to, final MaximumDecarbonationState merged, final Decision d, final int cost) {
        // TODO Fill in the details of your DP relaxation
        if (d.val() == 1 && canIncludeNode(merged.getIncludedNodes(), d.var())) return cost + 1;
        return cost;
    }
    
}
