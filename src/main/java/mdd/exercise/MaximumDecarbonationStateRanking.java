package mdd.exercise;

import mdd.framework.heuristics.StateRanking;

/** 
 * This is the class where you will define a heuristic to compare two nodes and
 * pick the most promising of the two.
 */
public final class MaximumDecarbonationStateRanking implements StateRanking<MaximumDecarbonationState> {
    
    // TODO You can implement the state ranking heuristic in whichever way you like.
    
    @Override
    public int compare(MaximumDecarbonationState o1, MaximumDecarbonationState o2) {
        // TODO You can implement the state ranking heuristic in whichever way you like.
        int comp = Integer.compare(o2.getCurrentValue(), o1.getCurrentValue());
        if (comp == 0) return Integer.compare(o2.getIncludedNodes().cardinality(), o1.getIncludedNodes().cardinality());
        return Integer.compare(o2.getCurrentValue(), o1.getCurrentValue());
    }
}
