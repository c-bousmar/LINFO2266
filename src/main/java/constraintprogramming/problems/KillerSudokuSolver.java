package constraintprogramming.problems;

import constraintprogramming.solver.TinyCSP;
import constraintprogramming.solver.Variable;

import java.util.ArrayList;
import java.util.List;

/**
 * Solves a Killer Sudoku problem
 * A killer sudoku of size n*n is a variation of the sudoku, with the following constraints:
 * - each cell {@link Coordinate} contains a number between 1 (included) and n (included)
 * - each row must contain different values
 * - each column must contain different values
 * - each sub-square must contain different values
 * - all cells within the problem belong to a given group {@link KillerSudokuInstance#groups()}.
 *     The values within each group sum to a given number {@link KillerSudokuGroup#sum()}
 */
public class KillerSudokuSolver {

    private final KillerSudokuInstance instance;

    public KillerSudokuSolver(KillerSudokuInstance instance) {
        this.instance = instance;
    }

    /**
     * Solves the instance {@link KillerSudokuSolver#instance} by
     * - creating the variables {@link TinyCSP#makeVariable(int)} of the problems
     * - adding the constraints of the problem to the variables
     *   (see the methods within {@link TinyCSP} for a list of the constraints)
     * and returns all {@link constraintprogramming.problems.KillerSudokuInstance.Solution} related to it
     *
     * @return list of solutions to the given instance (possibly empty if no solution exists)
     */
    public List<KillerSudokuInstance.Solution> solve() {
        TinyCSP csp = new TinyCSP();
        List<KillerSudokuInstance.Solution> listSol = new ArrayList<>();
        int n = instance.n();
        // TODO 1 create the variables for your problem
        //  don't forget to take into account the possibly already (un)set values in the killer sudoku!
        //  you can check if a value is set with the instance.isValue() method
        Variable[][] vars = new Variable[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (instance.isValue(i, j)) {
                    vars[i][j] = csp.makeVariable(n + 1);
                    vars[i][j].dom.fix(instance.value(i, j));
                } else {
                    vars[i][j] = csp.makeVariable(n + 1);
                }
            }
        }
        // TODO 2 add constraints on your variables
        // Row is ok
        for (int i = 0; i < n; i++) {
            for (int j1 = 0; j1 < n; j1++) {
                for (int j2 = j1 + 1; j2 < n; j2++) {
                    csp.notEqual(vars[i][j1], vars[i][j2]);
                }
            }
        }

        // Column is ok
        for (int j = 0; j < n; j++) {
            for (int i1 = 0; i1 < n; i1++) {
                for (int i2 = i1 + 1; i2 < n; i2++) {
                    csp.notEqual(vars[i1][j], vars[i2][j]);
                }
            }
        }

        // Square is ok
        int sqrtN = (int) Math.sqrt(n);
        for (int blockRow = 0; blockRow < sqrtN; blockRow++) {
            for (int blockCol = 0; blockCol < sqrtN; blockCol++) {
                List<Variable> blockVars = new ArrayList<>();
                for (int i = 0; i < sqrtN; i++) {
                    for (int j = 0; j < sqrtN; j++) {
                        blockVars.add(vars[blockRow * sqrtN + i][blockCol * sqrtN + j]);
                    }
                }
                for (int b1 = 0; b1 < blockVars.size(); b1++) {
                    for (int b2 = b1 + 1; b2 < blockVars.size(); b2++) {
                        csp.notEqual(blockVars.get(b1), blockVars.get(b2));
                    }
                }
            }
        }

        // Group is ok
        for (KillerSudokuGroup group : instance.groups()) {
            Variable[] groupVars = new Variable[group.size()];
            for (int i = 0; i < group.size(); i++) {
                groupVars[i] = vars[group.values()[i].i()][group.values()[i].j()];
            }
            csp.sum(groupVars, group.sum());
        }

        csp.dfs(() -> {
            int[][] solution = new int[n][n];
            // TODO 3 set the values of solution based on your fixed variables
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    solution[i][j] = vars[i][j].dom.value();
                }
            }
            listSol.add(new KillerSudokuInstance.Solution(solution));
//            KillerSudokuInstance.Solution sol = new KillerSudokuInstance.Solution(solution);
//            //System.out.println(sol+"\n");
//            listSol.add(sol);
        });
        return listSol;
    }

    public static void main(String[] args) {
        Coordinate[][] coords = Coordinate.fromMatrix(new int[][]{
                {1, 0,   3, 0},
                {0, 0,   0, 4},

                {0, 0,   0, 0},
                {2, 0,   0, 0},
        });
        KillerSudokuGroup[] groups = new KillerSudokuGroup[] {
                new KillerSudokuGroup(8,  coords[0][0], coords[0][1], coords[0][2]),
                new KillerSudokuGroup(12, coords[0][3], coords[1][2], coords[1][3], coords[2][2], coords[2][3]),
                new KillerSudokuGroup(9,  coords[2][1], coords[3][1], coords[3][2], coords[3][3]),
                new KillerSudokuGroup(11, coords[1][0], coords[1][1], coords[2][0], coords[3][0]),
        };
        KillerSudokuInstance instance = new KillerSudokuInstance(4, groups);
        System.out.println(instance+"\n");
        KillerSudokuSolver solver = new KillerSudokuSolver(instance);
        List<KillerSudokuInstance.Solution> solutions = solver.solve();
        System.out.println("# solutions = " + solutions.size());
        System.out.println("1st solution = \n" + solutions.get(0));
    }

}
