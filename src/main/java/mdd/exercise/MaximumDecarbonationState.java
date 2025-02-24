package mdd.exercise;

import java.util.BitSet;

/** 
 * This is the class where you will define the STATE part of
 * your model to solve the maximum decarbonation problem.
 * 
 * PS: 
 * Do not forget to give a correct implementation for the
 * equals() and hashCode() methods.
 */
public final class MaximumDecarbonationState {
    // TODO You can implement the state of your model in whichever way you like.
    private final BitSet includedNodes;
    private final int currentValue;

    public MaximumDecarbonationState(BitSet includedNodes, int newValue) {
        // NOTE TO MYSELF: make sure that the bitset provided is cloned !!!
        this.includedNodes = includedNodes;
        this.currentValue = newValue;
    }

    public BitSet getIncludedNodes() {
        return includedNodes;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public MaximumDecarbonationState includeNode(int nodeIndex, int nodeValue) {
        if (!includedNodes.get(nodeIndex)) {
            BitSet newNodes = (BitSet) includedNodes.clone();
            newNodes.set(nodeIndex);
            return new MaximumDecarbonationState(newNodes, currentValue + nodeValue);
        }
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (o != null && (o instanceof MaximumDecarbonationState)) {
            final MaximumDecarbonationState other = (MaximumDecarbonationState) o;
            // TODO implement an equality test between two states of your model
            return includedNodes.equals(other.includedNodes) && currentValue == other.currentValue;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        // TODO provide a hashcode implementation that would be consistent
        // TODO with the equals method defined above
        int result = includedNodes.hashCode();
        result = 31 * result + currentValue;
        return result;
    }

    @Override
    public String toString() {
        return "State{" +
                "includedNodes=" + includedNodes +
                ", values=" + currentValue +
                '}';
    }
}
