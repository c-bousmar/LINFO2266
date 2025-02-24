package branchandbound;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OneTreeTest {

    @Test
    public void oneTreeFromReport() {
        double[][] distMatrix = new double[][]{
                {0, 4, 5, 0, 0, 5, 0},
                {4, 0, 0, 0, 0, 1, 7},
                {5, 0, 0, 3, 1, 4, 0},
                {0, 0, 3, 0, 5, 0, 0},
                {0, 0, 1, 5, 0, 2, 8},
                {5, 1, 4, 0, 2, 0, 6},
                {0, 7, 0, 0, 8, 6, 0}
        };

        boolean [][] excluded  = new boolean[][]{
                {true, false, false, true, true, false, true},
                {false, true, true, true, true, false, false},
                {false, true, true, false, false, false, true},
                {true, true, false, true, false, true, true},
                {true, true, false, false, true, false, false},
                {false, false, false, true, false, true, false},
                {true, false, true, true, false, false, true}
        };

        TSPLowerBoundResult res = new OneTreeLowerBound().compute(distMatrix,excluded);
        assertTrue(TSPUtil.isOneTree(7,res.edges()));
        System.out.println(res.lb());
        System.out.println(res.edges());
    }
}
