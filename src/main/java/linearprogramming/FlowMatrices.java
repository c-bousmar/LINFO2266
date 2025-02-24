package linearprogramming;

import util.NotImplementedException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Encodes a maximum fow problem
 * Given a flow network {@link FlowNetwork}, the goal is to find the flow on all edges {@link FlowEdge} maximizing
 * the flow passing from the source {@link FlowNetwork#source()} until the sink {@link FlowNetwork#sink()}
 *
 * This is encoded as a linear program, by initializing the matrices A, b and c suitable for
 * solving the problem through a simplex solver {@link LinearProgramming}
 */
public class FlowMatrices {

    // matrices of the problem: maximize cx s.t. Ax <= b
    final double[][] A;
    final double[] b;
    final double[] c;
    private final FlowNetwork network; // instance that needs to be modelled

    /**
     * Creates the matrices A, b and c from a flow network
     * The network is assumed to have only one source {@link FlowNetwork#source()}
     * and one sink {@link FlowNetwork#sink()}
     *
     * @param network flow network that needs to be modeled as a linear program
     */
    public FlowMatrices(FlowNetwork network) {
        this.network = network;

        List<double[]> constraints = new ArrayList<>();
        List<Double> bounds = new ArrayList<>();

        int idx = 0;
        for (FlowEdge edge : network.edges()) {
            double[] capacityConstraint = new double[network.E()];
            capacityConstraint[idx++] = 1.0;
            constraints.add(capacityConstraint);
            bounds.add(edge.capacity());
        }
        for (int v = 0; v < network.V(); v++) {
            if (v == network.source() || v == network.sink()) continue;

            double[] flowConstraint = new double[network.E()];
            idx = 0;
            for (FlowEdge edge : network.edges()) {
                if (edge.from() == v) {
                    flowConstraint[idx] = 1.0;
                } else if (edge.to() == v) {
                    flowConstraint[idx] = -1.0;
                }
                idx++;
            }
            constraints.add(flowConstraint);
            bounds.add(0.0);
        }

        this.A = new double[constraints.size()][network.E()];
        for (int i = 0; i < constraints.size(); i++) {
            this.A[i] = constraints.get(i);
        }

        this.b = new double[bounds.size()];
        for (int i = 0; i < bounds.size(); i++) {
            this.b[i] = bounds.get(i);
        }

        this.c = new double[network.E()];
        idx = 0;
        for (FlowEdge edge : network.edges()) {
            if (edge.to() == network.sink()) {
                this.c[idx++] = 1.0;
            } else {
                this.c[idx++] = 0.0;
            }
        }
    }

    /**
     * Assign the flow passing through the {@link FlowEdge} in the solution
     * You are supposed to assign the flow from the provided {@code solution} only
     *
     * @param solution optimal primal solution from the linear program
     *                 this is retrieved through a {@link LinearProgramming#primal()} call by using
     *                 the A, B and C matrices from the formulation
     * @return flow network where the flow across all edges is set according to the primal solution
     */
    public FlowNetwork assignFlow(double[] solution) {
        Iterator<FlowEdge> edgeIterator = network.edges().iterator();
        int edgeIndex = 0;
        while (edgeIterator.hasNext()) {
            FlowEdge edge = edgeIterator.next();
            double delta = solution[edgeIndex] - edge.flow();
            if (delta != 0.0) {
                edge.addResidualFlowTo(edge.to(), delta);
            }
            edgeIndex++;
        }
        return network;
    }

    public double[][] getA() {
        return A;
    }

    public double[] getB() {
        return b;
    }

    public double[] getC() {
        return c;
    }

    public static void main(String[] args) {
        FlowNetwork instance = FlowNetwork.fromFile("data/Flow/flow0242_1001.txt");
        FlowMatrices matrices = new FlowMatrices(instance); // transform the problem in LP form
        LinearProgramming simplex = new LinearProgramming(matrices.A, matrices.b, matrices.c);
        System.out.println("max flow = " + simplex.value());
        // assign the flow
        double[] solution = simplex.primal();
        FlowNetwork flowAssigned = matrices.assignFlow(solution);
        System.out.println(flowAssigned);
    }

}