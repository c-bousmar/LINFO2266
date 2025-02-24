package dynamicprogramming;

import java.util.*;
import java.util.stream.IntStream;

import util.tsp.TSPInstance;

public class TSP extends Model<TSPState> {

    TSPInstance instance;
    TSPState root;

    public TSP(TSPInstance instance) {
        this.instance = instance;
        // TODO initialize root state, assuming the salesman starts at city 0
        this.root = new TSPState(
                0,
                new BitSet(instance.n)
        );
        this.root.citiesToVisit.set(1, instance.n);
    }

    @Override
    boolean isBaseCase(TSPState state) {
        // TODO checks if the state is a base case
        return state.citiesToVisit.isEmpty();
    }

    @Override
    double getBaseCaseValue(TSPState state) {
        // TODO return the value of the base case, don't forget the salesman must go back to city 0
         return instance.distanceMatrix[0][state.cityNumber];
    }

    @Override
    TSPState getRootState() {
        return root;
    }

    @Override
    List<Transition<TSPState>> getTransitions(TSPState state) {
        // TODO specify the transitions applicable to the given state
        List<Transition<TSPState>> transitions = new LinkedList<>();
        for (int nextCityNumber = state.citiesToVisit.nextSetBit(0); nextCityNumber > 0;
             nextCityNumber = state.citiesToVisit.nextSetBit(nextCityNumber + 1)) {

            BitSet nextCitiesToVisit = (BitSet) state.citiesToVisit.clone();
            nextCitiesToVisit.clear(nextCityNumber);

            TSPState nextState = new TSPState(
                    nextCityNumber,
                    nextCitiesToVisit
            );
            Transition<TSPState> transition = new Transition<>(
                    nextState,                                                  // successor
                    nextCityNumber,                                             // decision
                    instance.distanceMatrix[state.cityNumber][nextCityNumber]   // value
            );
            transitions.add(transition);
        }
        return transitions;
    }

    @Override
    boolean isMaximization() {
        return false;
    }
}
