package branchandbound;

import util.UF;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * A lower bound on the TSP based on the 1-tree relaxation
 * A minimum 1-tree is composed of the edges:
 * - that belong to the minimum spanning-tree that spans all
 *   nodes of the graph (independently of their direction), except the node 0.
 * - the two edges that connect the node 0 to the two
 *   closest nodes (also independently of their direction).
 */
public class OneTreeLowerBound implements TSPLowerBound {

    @Override
    public TSPLowerBoundResult compute(double [][] distMatrix, boolean [][] excluded) {
        double lowerBound = 0.0;
        List<Edge> edges = new LinkedList<>();                              // initialize MST
        UF uf = new UF(distMatrix.length);                                  // initialize UF for connected components
        List<Edge> twoSmallestFrom0 = new ArrayList<>(2);       // init. array to track 2 smallest 0-edges

        PriorityQueue<Edge> pq = new PriorityQueue<>(Edge::compareTo);      // sort all edges based on their cost
        for (int i = 0; i < distMatrix.length; i++) {
            for (int j = 0; j < distMatrix.length; j++) {
                if (i != j && !excluded[i][j]) {
                    pq.add(new Edge(i, j, distMatrix[i][j]));
                }
            }
        }

        while (uf.count() > 1 && !pq.isEmpty()) {                            // starting from the smallest edges, fore:
            Edge edge = pq.poll();
            if (edge.v1() == 0 || edge.v2() == 0) {                          // if it's 0-edge, put aside
                if (twoSmallestFrom0.size() < 2) {
                    if (twoSmallestFrom0.isEmpty()) twoSmallestFrom0.add(edge);
                    else if (twoSmallestFrom0.get(0).v1() != edge.v2() || twoSmallestFrom0.get(0).v2() != edge.v1()) {
                            twoSmallestFrom0.add(edge);
                    }
                }
            } else if (uf.find(edge.v1()) != uf.find(edge.v2())) {           // if vertices in 2 diff. components
                uf.union(edge.v1(), edge.v2());                              // take it into account for UF
                edges.add(edge);                                             // add the edge to the MST
                lowerBound += edge.cost();
            }
        }
        // add the two first 0-edges' costs to the lower bound and MST
        for (Edge e : twoSmallestFrom0) {
            lowerBound += e.cost();
            edges.add(e);
        }
        return new TSPLowerBoundResult(lowerBound, edges);
    }
}
