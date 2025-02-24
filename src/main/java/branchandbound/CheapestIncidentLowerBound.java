package branchandbound;


import java.util.LinkedList;
import java.util.List;


/**
 * Compute the cheapest incident edges lower bound for the TSP
 * For each node, the cheapest incoming edge is selected.
 * The sum of these edges is a lower bound on the TSP
 */
public class CheapestIncidentLowerBound implements TSPLowerBound {

    public CheapestIncidentLowerBound() {
    }

    @Override
    public TSPLowerBoundResult compute(double [][] distanceMatrix, boolean [][] excludedEdges) {
        // TODO
        List<Edge> usedEdges = new LinkedList<>();
        double lowerBound = 0.0;
        for (int vertex = 0; vertex < distanceMatrix.length; vertex++) {
            double minCostEdge = Double.MAX_VALUE;
            int incommingVertex = -1;
            for (int v = 0; v < distanceMatrix.length; v++) {
                if (v != vertex
                        && !excludedEdges[v][vertex]
                        && distanceMatrix[v][vertex] < minCostEdge) {
                    minCostEdge = distanceMatrix[v][vertex];
                    incommingVertex = v;
                }
            }
            if (incommingVertex != -1) {
                usedEdges.add(new Edge(incommingVertex, vertex, minCostEdge));
                lowerBound += minCostEdge;
            }
        }
        return new TSPLowerBoundResult(lowerBound, usedEdges);
    }
}
