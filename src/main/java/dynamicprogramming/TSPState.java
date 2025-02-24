package dynamicprogramming;

import java.util.*;

public class TSPState extends State {

    // TODO specify state fields and constructor
    int cityNumber;
//    List<Integer> citiesToVisit;
    BitSet citiesToVisit;

    TSPState(int cityNumber, BitSet citiesToVisit) {
        this.cityNumber = cityNumber;
        this.citiesToVisit = citiesToVisit;
    }

    @Override
    int hash() {
        // TODO implement hash of state, see interface
        return Objects.hash(cityNumber, citiesToVisit);
    }

    @Override
    boolean isEqual(State s) {
        // TODO implement equality of states, see interface
        if (s instanceof TSPState) {
            return ((TSPState) s).cityNumber == this.cityNumber &&
                    this.citiesToVisit.equals(((TSPState) s).citiesToVisit);
        }
        return false;
    }
    
}
