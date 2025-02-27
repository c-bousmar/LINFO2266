package constraintprogramming.solver;


import java.util.BitSet;
import java.util.Iterator;

/**
 * A constraint between two
 * consecutive variables of a knight's tour
 * ensuring that the knight can move from one
 * position to the other.
 */
public class KnightMove extends Constraint {
    private final Variable x;
    private final Variable y;
    private final int n;

    /**
     * y is a position of the chess board accessible from x
     * by a knight move.
     * @param x
     * @param y
     * @param n the size of the board (n x n)
     */
    public KnightMove(Variable x, Variable y, int n) {
        this.x = x;
        this.y = y;
        this.n = n;

    }

    @Override
    boolean propagate() {
        // TODO implement the constraint,
        //  it should enforce that y can only take values corresponding to positions reachable from
        //  any possible destination of x.
        //  For example : consider a 5 x 5 grid, the positions are represented by the duo (line, column).
        //  The domain of x contains only the values 0 and 2 in its domain,
        //  respectively the positions (0,0) and (0,2).
        //  The domain of y should only contain the positions accessible from those two
        //  positions : 11 (2, 1), 7 (1, 2), 5 (1, 0), 13 (2, 3) and 9 (1, 4)

        boolean changed = false;
        BitSet newSetOfPositions = getValidMovesBitSet();

        Iterator<Integer> it = y.dom.iterator();
        while (it.hasNext()) {
            int value = it.next();
            if (!newSetOfPositions.get(value)) {
                if (y.dom.remove(value)) {
                    changed = true;
                }
            }
        }
        return changed;
    }

    private BitSet getValidMovesBitSet() {
        BitSet nextPositions = new BitSet(n * n);

        for (int currentX : x.dom) {
            int rowX = currentX / n;
            int colX = currentX % n;
            int[][] knightMoves = {
                    {rowX - 2, colX - 1}, {rowX - 2, colX + 1},
                    {rowX + 2, colX - 1}, {rowX + 2, colX + 1},
                    {rowX - 1, colX - 2}, {rowX - 1, colX + 2},
                    {rowX + 1, colX - 2}, {rowX + 1, colX + 2},
            };

            for (int[] move : knightMoves) {
                int newRow = move[0];
                int newCol = move[1];

                if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < n) {
                    int newPosition = newRow * n + newCol;
                    nextPositions.set(newPosition);
                }
            }
        }
        return nextPositions;
    }
}
