import pandas as pd
import matplotlib.pyplot as plt

# Load data from the CSV files
standard_data = pd.read_csv("standard.csv")
improved_data = pd.read_csv("improved.csv")

# Determine the maximum time between the two datasets
max_time = max(standard_data["time(ms)"].max(), improved_data["time(ms)"].max())

# Extend the improved_data to the max_time with the same last solution value
last_time_improved = improved_data["time(ms)"].iloc[-1]
last_solution_improved = improved_data["solutions"].iloc[-1]

# Variable to track when artificial values start
artificial_start_time = None

if last_time_improved < max_time:
    additional_times = list(range(last_time_improved + 1, max_time + 1))
    additional_solutions = [last_solution_improved] * len(additional_times)
    additional_data = pd.DataFrame({"time(ms)": additional_times, "solutions": additional_solutions})
    improved_data = pd.concat([improved_data, additional_data], ignore_index=True)
    artificial_start_time = last_time_improved + 1

# Plot the data
plt.figure(figsize=(10, 6))

# Standard case
standard_line, = plt.plot(standard_data["time(ms)"], standard_data["solutions"], label="Standard", linestyle="-")

# Improved case
improved_line, = plt.plot(improved_data["time(ms)"], improved_data["solutions"], label="Standard+1 constraint", linestyle="-")

# Add vertical lines to indicate when the algorithms are done or artificial values start
plt.axvline(x=standard_data["time(ms)"].iloc[-1], color=standard_line.get_color(), linestyle='--', label="Standard end runtime")
if artificial_start_time:
    plt.axvline(x=artificial_start_time, color=improved_line.get_color(), linestyle='--', label="Standard+1 constraint end runtime")

# Customize the plot
plt.title("Number of Solutions Found Over Time")
plt.xlabel("Time Elapsed (ms)")
plt.ylabel("Number of Solutions Found")
plt.legend()
plt.grid(True)

# Save and display the plot
plt.savefig("solutions_comparison.png")
plt.show()
