import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

# Read the CSV file generated by the Java program
data = pd.read_csv('tsp_runtimes.csv')

# Separate the data based on instance size
data8 = data[data['Instance Size'] == 8]['Runtime (ms)'].values
data10 = data[data['Instance Size'] == 10]['Runtime (ms)'].values
data18 = data[data['Instance Size'] == 18]['Runtime (ms)'].values

# Replace zero or very small values to avoid log(0)
data8[data8 == 0] = 1
data10[data10 == 0] = 1
data18[data18 == 0] = 1

# Sort the runtimes for cumulative plotting
data8_sorted = np.sort(data8)
data10_sorted = np.sort(data10)
data18_sorted = np.sort(data18)

# Create a cumulative number of instances resolved (1 to N)
cumulative_8 = np.arange(1, len(data8_sorted) + 1)
cumulative_10 = np.arange(1, len(data10_sorted) + 1)
cumulative_18 = np.arange(1, len(data18_sorted) + 1)

# Create the plot
plt.figure(figsize=(10, 6))

# Use the logarithmic scale for the x-axis
plt.xscale('log')

# Plot for each size using actual runtime values
plt.plot(data8_sorted, cumulative_8, label='Size 8', marker='o')
plt.plot(data10_sorted, cumulative_10, label='Size 10', marker='s')
plt.plot(data18_sorted, cumulative_18, label='Size 18', marker='^')

# Graph labels and title
plt.xlabel('Runtime (ms) [Log Scale]')
plt.ylabel('Cumulative Number of Instances Resolved')
plt.title('Cumulative TSP Instances Resolved vs. Runtime (Log Scale)')
plt.legend()

# Display the plot
plt.grid(True, which="both", ls="--")
plt.show()
