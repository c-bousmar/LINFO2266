package branchandbound;

import java.util.*;


public class HeldKarpLowerBound implements TSPLowerBound {

    private static final double EPSILON = 1e-6;
    private HashMap<Integer, List<Edge>> graph;
//    double[] lowerBoundEvolution;


    public HeldKarpLowerBound() {
        this.graph = new HashMap<>();
//        this.lowerBoundEvolution = new double[101];
    }

//    public static void main(String[] args) {
//        TSPInstance instance = new TSPInstance("data/TSP/instance_30_0.xml");
//        HeldKarpLowerBound algo = new HeldKarpLowerBound();
//        algo.compute(instance.distanceMatrix, new boolean[instance.n][instance.n]);
//        String outputFilePath = "output.txt";
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
//            for (double value : algo.lowerBoundEvolution) {
//                writer.write(String.valueOf(value));
//                writer.newLine();
//            }
//            System.out.println("Output successfully written to " + outputFilePath);
//        } catch (IOException e) {
//            System.err.println("An error occurred while writing to the file: " + e.getMessage());
//        }
//    }

    @Override
    public TSPLowerBoundResult compute(double [][] distanceMatrix, boolean [][] excludedEdges) {
        int n = distanceMatrix.length;
        double[] pi = new double[n];
        double lambda = 0.1;
        TSPLowerBoundResult lagrangian = new OneTreeLowerBound().compute(distanceMatrix, excludedEdges);
        List<Edge> edgesSolution = lagrangian.edges();
        double lb = lagrangian.lb();
        double best = lagrangian.lb();
        double [][] relaxedDistMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            relaxedDistMatrix[i] = distanceMatrix[i].clone();
        }
        boolean isSolutionHamilonian = false;
//        int iteration = 1;
//        this.lowerBoundEvolution = new double[101];
//        this.lowerBoundEvolution[0] = 0.0;

        while (lambda >= EPSILON) {
//        while (lambda >= EPSILON && iteration <= 100) {
//            this.lowerBoundEvolution[iteration] = lb;
            this.graph.clear();
            constructDiGraph(lagrangian.edges());

            double lambdaPrime = lagrangian.lb();

            if (isHamiltonian()) {
                isSolutionHamilonian = true;
                break;
            }

            if (lambdaPrime > lb) {
                lambda *= 0.9;
            }

            double mu = computeMu(lambda, lb, pi);
            updateRelaxation(pi, mu);
            lb = lambdaPrime;
            if (best < lb) {
                best = lb;
                edgesSolution.clear();
                edgesSolution.addAll(lagrangian.edges());
            }

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    relaxedDistMatrix[i][j] -= pi[i];
                    relaxedDistMatrix[i][j] -= pi[j];
                }
            }
            lagrangian = new OneTreeLowerBound().compute(relaxedDistMatrix, excludedEdges);
//            iteration++;
        }
        if (isSolutionHamilonian) {
            best = 0;
            for (Edge e : lagrangian.edges()) {
                best += distanceMatrix[e.v1()][e.v2()];
            }
        }
        return new TSPLowerBoundResult(best, edgesSolution);
    }

    private void constructDiGraph(List<Edge> oneTree) {
        for (Edge e : oneTree) {
            if (!graph.containsKey(e.v1())) {
                graph.put(e.v1(), new ArrayList<>());
            }
            graph.get(e.v1()).add(e);
            if (!graph.containsKey(e.v2())) {
                graph.put(e.v2(), new ArrayList<>());
            }
            graph.get(e.v2()).add(new Edge(e.v2(), e.v1(), e.cost()));
        }
    }

    private boolean isHamiltonian() {
        return isDegreeTwo();
    }
    private boolean isDegreeTwo() {
        for (int i = 0; i < this.graph.size(); i++) {
            if (this.graph.get(i).size() != 2) return false;
        }
        return true;
    }
    private boolean isCycle() {
        BitSet hasVertexAlreadyBeenReached = new BitSet(this.graph.size());
        int currentVertex = 0;
        hasVertexAlreadyBeenReached.set(0);
        while (hasVertexAlreadyBeenReached.cardinality() < hasVertexAlreadyBeenReached.size()) {
            // Using edge #1
            int nextVertex = this.graph.get(currentVertex).get(0).v2();
            if (hasVertexAlreadyBeenReached.get(nextVertex)) {
                // Using edge #2
                nextVertex = this.graph.get(currentVertex).get(1).v2();
                if (hasVertexAlreadyBeenReached.get(nextVertex) && nextVertex != 0) return false;
            } else {
                hasVertexAlreadyBeenReached.flip(nextVertex);
                currentVertex = nextVertex;
            }
        }
        return true;
    }

    private double computeMu(double lambda, double lb, double[] pi) {
        double sum = 0.0;
        for (int i = 0; i < this.graph.size(); i++) {
            int degree = this.graph.get(i).size();
            sum += Math.pow(degree - 2, 2);
        }
        return (lambda * lb) / sum;
    }

    private void updateRelaxation(double[] pi, double mu) {
        for (int i = 0; i < pi.length; i++) {
            int degree = this.graph.get(i).size();
            pi[i] = mu * (2 - degree);
        }
    }
}
