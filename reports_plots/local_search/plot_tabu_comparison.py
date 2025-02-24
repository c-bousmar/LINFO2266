import pandas as pd
import matplotlib.pyplot as plt

# Load data
data = pd.read_csv("/Users/cyrilbousmar/Dev/LINFO2266/reports_plots/local_search/tabu.csv")

# Plot
plt.figure(figsize=(10, 6))
plt.plot(data["Iteration"], data["BestSelection"], label="Best Selection")
plt.plot(data["Iteration"], data["TabuSelection3"], label="Tabu Selection (Length 3)")
plt.plot(data["Iteration"], data["TabuSelection15"], label="Tabu Selection (Length 15)")
plt.xlabel("Iteration")
plt.ylabel("Objective Function Value")
plt.title("Comparison of Best and Tabu Selection")
plt.yscale("log")
plt.legend()
plt.grid()
plt.show()
