package branchandbound;

import util.tsp.TSPInstance;

import java.util.*;

public class BranchAndBoundTSP {

    public static void main(String[] args) {
        TSPInstance instance = new TSPInstance("data/TSP/instance_30_0.xml");
        optimize(instance, new HeldKarpLowerBound());
    }

    /**
     * Entry point for your Branch and Bound TSP Solver
     *
     * You must use the BrandAndBound class without modifying it (except possibly to compute the gap).
     * Have a look at @see {@link branchandbound.BranchAndBoundKnapsack} for an example.
     * As you will see, you have to implement your own State/Node class.
     *
     * You are free to choose your state representation.
     * This is part of the difficulty of the exercise.
     * Choose one representation that makes it easy to generate the successors of a node.
     *
     * @param instance an instance for the TSP
     * @param lbAlgo a lower-bound algorithm for TSP
     * @return the list of edges in the optimal solution
     */
    public static List<Edge> optimize(TSPInstance instance, TSPLowerBound lbAlgo) {

//        TSPSuperLowerBound takeBest = new TSPSuperLowerBound();
//        TSPLowerBoundResult res = takeBest.compute(instance.distanceMatrix, new boolean[instance.n][instance.n]);
        TSPLowerBoundResult res = lbAlgo.compute(instance.distanceMatrix, new boolean[instance.n][instance.n]);
        TSPNode root = new TSPNode(
                null,
                0,
                new BitSet(instance.n),
                0.0,
                res.lb(),
                instance.distanceMatrix,
                0);
        root.citiesToExplore.set(1, instance.n);

        OpenNodes<TSPNode> openNodes = new DepthFirstOpenNodes<>();
//        OpenNodes<TSPNode> openNodes = new BestFirstOpenNodes<>();
        openNodes.add(root);

        List<Edge> edges = new ArrayList<>();
        List<Node<TSPNode>> list = new ArrayList<>();
        BranchAndBound.minimize(openNodes, list::add);

        TSPNode last = (TSPNode) list.remove(list.size() - 1);
        edges.add(new Edge(last.cityNumber, 0, instance.distanceMatrix[last.cityNumber][0]));
        TSPNode previous = last.parent;
        while (previous != null) {
            edges.add(new Edge(
                    previous.cityNumber,
                    last.cityNumber,
                    instance.distanceMatrix[previous.cityNumber][last.cityNumber]
            ));
            last = previous;
            previous = previous.parent;
        }
        return edges;
    }
}

class TSPNode implements Node<TSPNode> {

    TSPNode parent;
    int cityNumber;
    double pathWeight;
    double lowerBound;
    double[][] distMatrix;
    BitSet citiesToExplore;
    int depth;

    TSPNode(TSPNode parent, int cityNumber, BitSet citiesToExplore, double pathWeight, double lowerBound,
            double[][] distanceMatrix, int depth) {
        this.parent = parent;
        this.cityNumber = cityNumber;
        this.citiesToExplore = citiesToExplore;
        this.pathWeight = pathWeight;
        this.lowerBound = lowerBound;
        this.distMatrix = distanceMatrix;
        this.depth = depth;
        if (this.isSolutionCandidate()) {
            this.pathWeight += distanceMatrix[this.cityNumber][0];
        }
    }
    @Override
    public boolean isSolutionCandidate() {
        return citiesToExplore.cardinality() == 0;
    }

    @Override
    public double objectiveFunction() {
        return pathWeight;
    }

    @Override
    public double lowerBound() {
        return lowerBound;
    }

    @Override
    public int depth() {
        return depth;
    }

    @Override
    public List<Node<TSPNode>> children() {
        List<Node<TSPNode>> cities = new ArrayList<>();
        for (int nextCityNumber = this.citiesToExplore.nextSetBit(1); nextCityNumber > 0;
             nextCityNumber = this.citiesToExplore.nextSetBit(nextCityNumber + 1)) {

            BitSet nextVisitedCities = (BitSet) this.citiesToExplore.clone();
            nextVisitedCities.clear(nextCityNumber);
            cities.add(new TSPNode(
                    this,
                    nextCityNumber,
                    nextVisitedCities,
                    pathWeight + distMatrix[this.cityNumber][nextCityNumber],
                    lowerBound,
                    distMatrix,
                    depth + 1));
        }
        return cities;
    }

    @Override
    public TSPNode getState() {
        return this;
    }
}
