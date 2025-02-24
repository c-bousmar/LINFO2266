import pandas as pd
import matplotlib.pyplot as plt

file_path = 'sequential_solver_runtime.csv'
data = pd.read_csv(file_path)

plt.figure(figsize=(10, 6))

for width in data['Width'].unique():
    subset = data[data['Width'] == width]
    cumulative_runtime = subset['Runtime'].cumsum().tolist()
    
    plt.plot(cumulative_runtime, subset["Instance"], label=f'Width = {width}', marker='o')

plt.xscale('log')
plt.xlabel('Cumulative Runtime to solve instances (ms)')
plt.ylabel('Problem instances')
plt.title('Impact of width on performance to solve instances')
plt.ylim(0)
plt.yticks(range(0, 21, 5))  # Set y-axis ticks every 5
plt.grid(axis='y', which='major', linestyle='--', linewidth=0.5, alpha=0.7)


# Adding a grid selectively for y-axis values divisible by 5
plt.grid(axis='y', which='major', linestyle='--', linewidth=0.5, alpha=0.7)
plt.legend()

plt.savefig('cactus_plot_corrected.png', dpi=300)
plt.show()
