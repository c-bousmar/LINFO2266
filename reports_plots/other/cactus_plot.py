import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

# Load the CSV data
df = pd.read_csv("cumulative_results.csv")

# Separate data for Ford-Fulkerson and Linear Programming
time_limits = [1, 5, 10, 15, 20, 25, 30]
ff_solved = df.filter(regex="^FF").iloc[0].tolist()
lp_solved = df.filter(regex="^LP").iloc[0].tolist()

# Plot the cactus plot
plt.figure(figsize=(10, 6))
plt.plot(time_limits, ff_solved, label="Ford-Fulkerson", marker="o", linestyle="--")
plt.plot(time_limits, lp_solved, label="Linear Programming", marker="s", linestyle="--")

# Set x-axis to log scale
plt.xscale("log")

# Labels and legend
plt.xlabel("Per-instance time limit (s) [Log Scale]")
plt.ylabel("Number of benchmarks solved")
plt.title("Cactus Plot of Running Time to Solve Instances")
plt.legend()

# Show the plot
plt.grid(True, which="both", linestyle="--", linewidth=0.5)
plt.tight_layout()
plt.show()
