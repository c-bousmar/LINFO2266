import matplotlib.pyplot as plt

file_dfs = "linfo2266-Phytinfo/output_og_dfs.txt"
file_bfs = "linfo2266-Phytinfo/output_og_bfs.txt"


def read_lower_bounds(file_path):
    with open(file_path, 'r') as file:
        return [float(line.strip()) for line in file.readlines()]


og_dfs = read_lower_bounds(file_dfs)
og_bfs = read_lower_bounds(file_bfs)

iterations = list(range(min(1000000, len(og_dfs), len(og_bfs))))

plt.figure(figsize=(10, 6))
plt.plot(iterations, og_dfs[:1000000], label='dfs', color='blue')
plt.plot(iterations, og_bfs[:1000000], label='bfs', color='green')
plt.xlabel('Iterations')
plt.ylabel('Optimality gap')
plt.title('Evolution of the optimality gap')
plt.ylim(-30, max(max(og_dfs[:1000000]), max(og_bfs[:1000000])))
plt.xscale('log')
plt.legend()
plt.grid()

plt.show()
