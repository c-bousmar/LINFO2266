package constraintprogramming.problems;

import constraintprogramming.solver.TinyCSP;
import constraintprogramming.solver.Variable;
import util.NotImplementedException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Solves a magic square problem
 * A magic square is a square of size n*n, with the following constraints:
 * - each number within a cell is included between 1 (included) and n*n (included)
 * - each number can appear once and only once within the magic square
 * - the sum of each row, columns and of the 2 diagonals are equal
 */
public class MagicSquareSolver {

    public final MagicSquareInstance instance; // the instance to solve
    public final TinyCSP csp; // the solver to use to solve the instance

    public MagicSquareSolver(MagicSquareInstance instance) {
        this.instance = instance;
        this.csp = new TinyCSP();
    }

    /**
     * Solves the instance {@link MagicSquareSolver#instance} by
     * - creating the variables {@link TinyCSP#makeVariable(int)} of the problems
     * - adding the constraints of the problem to the variables
     *   (see the methods within {@link TinyCSP} for a list of the constraints)
     * and returns all {@link constraintprogramming.problems.MagicSquareInstance.Solution} related to it
     *
     * @return list of solutions to the given instance (possibly empty if no solution exists)
     */
    public List<MagicSquareInstance.Solution> solve() {
        List<MagicSquareInstance.Solution> listSol = new ArrayList<>();
        int n = instance.n();
        // TODO Report
        // Track metrics
        AtomicInteger failureCount = new AtomicInteger();
        AtomicInteger recursiveCallCount = new AtomicInteger();
        // Increment recursive calls in dfs
        csp.setRecursiveCallTracker(recursiveCallCount);

        // TODO 1 create the variables for your problem
        //  don't forget to take into account the possibly already (un)set values in the magic square!
        //  you can check if a value is set with the instance.isValue() method
        int magicConstant = getMagicConstant();

        Variable[][] vars = new Variable[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                vars[i][j] = csp.makeVariable(n * n + 1);
                if (instance.isValue(i, j)) {
                    vars[i][j].dom.fix(instance.value(i, j));
                } else {
                    vars[i][j].dom.removeBelow(1);
                }
            }
        }

        // TODO 2 add constraints on your variables
        // Unique numbers
        Variable[] allVars = new Variable[n * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                allVars[i * n + j] = vars[i][j];
            }
        }
        for (int i = 0; i < n * n; i++) {
            for (int j = i + 1; j < n * n; j++) {
                csp.notEqual(allVars[i], allVars[j]);
            }
        }

        // Sum row is ok
        for (int i = 0; i < n; i++) {
            csp.sum(vars[i], magicConstant);
        }

        // Sum col is ok
        for (int j = 0; j < n; j++) {
            Variable[] col = new Variable[n];
            for (int i = 0; i < n; i++) {
                col[i] = vars[i][j];
            }
            csp.sum(col, magicConstant);
        }

        // Sum diag (top-left->bottom-right) is ok
        Variable[] diag = new Variable[n];
        for (int i = 0; i < n; i++) {
            diag[i] = vars[i][i];
        }
        csp.sum(diag, magicConstant);

        // Sum diag (top-right->bottom-left) is ok
        diag = new Variable[n];
        for (int i = 0; i < n; i++) {
            diag[i] = vars[i][n - i - 1];
        }
        csp.sum(diag, magicConstant);

        // TODO report: Symmetry-breaking constraint: Uncomment the following line to enable
//         csp.lessOrEqual(vars[0][0], vars[0][1]);

        // Open the CSV for writing
//        String outputFile = "standard.csv";
//        String outputFile = "improved.csv";
//        try (FileWriter fileWriter = new FileWriter(outputFile);
//             PrintWriter csvWriter = new PrintWriter(fileWriter)) {
//
//            // Write CSV header
//            csvWriter.println("time(ms),solutions");
//
//            // Track elapsed time
//            long startTime = System.currentTimeMillis();
//
//            // Solve and log solutions
//            csp.dfs(() -> {
//                int[][] solution = new int[n][n];
//                for (int i = 0; i < n; i++) {
//                    for (int j = 0; j < n; j++) {
//                        solution[i][j] = vars[i][j].dom.value();
//                    }
//                }
//                listSol.add(new MagicSquareInstance.Solution(solution));
//
//                // Log time and solutions
//                long elapsedTime = System.currentTimeMillis() - startTime;
//                csvWriter.println(elapsedTime + "," + listSol.size());
//            });
//        } catch (IOException e) {
//            System.err.println("Error writing to CSV: " + e.getMessage());
//        }
        // TODO 3 set the values of solution based on your fixed variables
//        csp.dfs(() -> {
//            int[][] solution = new int[n][n];
//            for (int i = 0; i < n; i++) {
//                for (int j = 0; j < n; j++) {
//                    solution[i][j] = vars[i][j].dom.value();
//                }
//            }
//            listSol.add(new MagicSquareInstance.Solution(solution));
//        }, failureCount);
//        System.out.printf("Number of solutions: %d%n", listSol.size());
//        System.out.printf("Number of recursive calls: %d%n", recursiveCallCount.get());
//        System.out.printf("Number of failures: %d%n", failureCount.get());
        csp.dfs(() -> {
            int[][] solution = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    solution[i][j] = vars[i][j].dom.value();
                }
            }
            listSol.add(new MagicSquareInstance.Solution(solution));
        });
        return listSol;
    }

    /**
     * Computes the magic constant of the magic square
     * The magic constant is the value of the sum that needs to be found
     *
     * @return expected sum across all rows, columns and diagonals
     */
    public int getMagicConstant() {
        // TODO 4 (not needed for correctness but useful for efficiency) compute the sum of a row within a n*n magic square
        //  and use it to enhance your model
        int n = instance.n();
        return n * (n * n + 1) / 2;
    }


    public static void main(String[] args) throws IOException {
        int[][] values = new int[][]{
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
        };
        MagicSquareInstance instance = new MagicSquareInstance(values);
        System.out.println(instance);
        MagicSquareSolver solver = new MagicSquareSolver(instance);
        List<MagicSquareInstance.Solution> solutionList = solver.solve();
        System.out.println("# solutions = " + solutionList.size());
        System.out.println("1st solution = \n" + solutionList.get(0));
    }
}
