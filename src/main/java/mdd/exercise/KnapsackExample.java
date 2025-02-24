package mdd.exercise;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import mdd.framework.core.Decision;
import mdd.framework.core.Frontier;
import mdd.framework.core.Problem;
import mdd.framework.core.Relaxation;
import mdd.framework.core.Solver;
import mdd.framework.heuristics.StateRanking;
import mdd.framework.heuristics.VariableHeuristic;
import mdd.framework.implem.frontier.SimpleFrontier;
import mdd.framework.implem.heuristics.DefaultVariableHeuristic;
import mdd.framework.implem.heuristics.FixedWidth;
import mdd.framework.implem.solver.ParallelSolver;

public final class KnapsackExample {

    public static class KnapsackProblem implements Problem<Integer> {
        final int     capa;
        final int[]   profit;
        final int[]   weight;
        public final Integer optimal;

        public KnapsackProblem(final int capa, final int[] profit, final int[] weight, final Integer optimal) {
            this.capa   = capa;
            this.profit = profit;
            this.weight = weight; 
            this.optimal= optimal;
        }

        @Override
        public int nbVars() {
            return profit.length;
        }

        @Override
        public Integer initialState() {
            return capa;
        }

        @Override
        public int initialValue() {
            return 0;
        }

        @Override
        public Iterator<Integer> domain(Integer state, int var) {
            if (state >= weight[var]) {
                return Arrays.asList(1, 0).iterator();
            } else {
                return Arrays.asList(0).iterator();
            }
        }

        @Override
        public Integer transition(Integer state, Decision decision) {
            if (decision.val() == 1) {
                return state - weight[decision.var()];
            } else {
                return state;
            }
        }

        @Override
        public int transitionCost(Integer state, Decision decision) {
            return profit[decision.var()] * decision.val();
        }
    }

    public static class KnapsackRelax implements Relaxation<Integer> {
        @Override
        public Integer mergeStates(final Iterator<Integer> states) {
            int capa = 0;
            while (states.hasNext()) {
                final Integer state = states.next();
                capa  = Math.max(capa,  state);
            }
            return capa;
        }

        @Override
        public int relaxEdge(Integer from, Integer to, Integer merged, Decision d, int cost) {
            return cost;
        }
    }

    public static class KnapsackRanking implements StateRanking<Integer> {
        @Override
        public int compare(final Integer o1, final Integer o2) {
            return o1 - o2;
        }
    }

    public static KnapsackProblem readInstance(final String fname) throws IOException {
        final File f = new File(fname);
        try(final BufferedReader bf = new BufferedReader(new FileReader(f))) {
            final PinReadContext context = new PinReadContext();

            bf.lines().forEachOrdered((String s) -> {
                if (context.isFirst) {
                    context.isFirst = false;
                    String[] tokens = s.split("\\s");
                    context.n    = Integer.parseInt(tokens[0]);
                    context.capa = Integer.parseInt(tokens[1]);

                    if (tokens.length == 3) {
                        context.optimal = Integer.parseInt(tokens[2]);
                    }

                    context.profit= new int[context.n];
                    context.weight= new int[context.n];
                } else {
                    if (context.count < context.n) {
                        String[] tokens = s.split("\\s");
                        context.profit[context.count] = Integer.parseInt(tokens[0]);
                        context.weight[context.count] = Integer.parseInt(tokens[1]);

                        context.count ++;
                    }
                }
            });

            return new KnapsackProblem(context.capa, context.profit, context.weight, context.optimal);
        }
    }

    private static class PinReadContext {
        boolean isFirst = true;
        int n = 0;
        int count = 0;
        int capa = 0;
        int[] profit = new int[0];
        int[] weight = new int[0];
        Integer optimal = null;
    }

    public static void main(final String[] args) throws IOException {
        final String instance = "data/Knapsack/instance_n100_c500_10_5_10_5_0";
        final KnapsackProblem                problem = readInstance(instance);
        final KnapsackRelax                    relax = new KnapsackRelax();
        final KnapsackRanking                ranking = new KnapsackRanking();
        final FixedWidth<Integer>        width = new FixedWidth<>(250);
        final VariableHeuristic<Integer>  varh = new DefaultVariableHeuristic<>();
        final Frontier<Integer>       frontier = new SimpleFrontier<>(ranking);
        final Solver                          solver = new ParallelSolver<Integer>(
                Runtime.getRuntime().availableProcessors(),
                problem, 
                relax, 
                varh, 
                ranking, 
                width, 
                frontier);


        long start = System.currentTimeMillis();
        solver.maximize();
        double duration = (System.currentTimeMillis() - start) / 1000.0;


        int[] solution = solver.bestSolution()
            .map(decisions -> {
                int[] values = new int[problem.nbVars()];
                for (Decision d : decisions) {
                    values[d.var()] = d.val();
                }
                return values;
            })
            .get();
        
        System.out.println(String.format("Duration : %.3f", duration));
        System.out.println(String.format("Objective: %d", solver.bestValue().get()));
        System.out.println(String.format("Solution : %s", Arrays.toString(solution)));
    }
}
