import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

# Load data
data = pd.read_csv("/Users/cyrilbousmar/Dev/LINFO2266/reports_plots/local_search/enhanced.csv")

# Extract number of runs
num_runs = (data.shape[1] - 1) // 2  # Exclude "Iteration" column

# Separate BestSelection and SimulatedAnnealing results
best_selection = data[[f"BS{i+1}" for i in range(num_runs)]]
simulated_annealing = data[[f"SA{i+1}" for i in range(num_runs)]]

# Compute means and standard deviations
iterations = data["Iteration"]
best_selection_mean = best_selection.mean(axis=1)
best_selection_std = best_selection.std(axis=1)
simulated_annealing_mean = simulated_annealing.mean(axis=1)
simulated_annealing_std = simulated_annealing.std(axis=1)

# Plot with mean and standard deviation
plt.figure(figsize=(10, 6))
plt.plot(iterations, best_selection_mean, label="Best Selection")
plt.fill_between(iterations, 
                 best_selection_mean - best_selection_std, 
                 best_selection_mean + best_selection_std, 
                 alpha=0.2)
plt.plot(iterations, simulated_annealing_mean, label="Simulated Annealing")
plt.fill_between(iterations, 
                 simulated_annealing_mean - simulated_annealing_std, 
                 simulated_annealing_mean + simulated_annealing_std, 
                 alpha=0.2)
plt.xlabel("Iteration")
plt.ylabel("Objective Function Value")
plt.title("Comparison of Best Selection and Enhanced Local Search (5 Runs)")
# plt.yscale("log")
plt.legend()
plt.grid()
plt.show()
