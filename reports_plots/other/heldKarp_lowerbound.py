import matplotlib.pyplot as plt

file_mu1 = "linfo2266-Phytinfo/output_mu1.txt"
file_mu2 = "linfo2266-Phytinfo/output_mu2.txt"


def read_lower_bounds(file_path):
    with open(file_path, 'r') as file:
        return [float(line.strip()) for line in file.readlines()]


lower_bounds_mu1 = read_lower_bounds(file_mu1)
lower_bounds_mu2 = read_lower_bounds(file_mu2)

iterations = list(range(min(100, len(lower_bounds_mu1), len(lower_bounds_mu2))))

plt.figure(figsize=(10, 6))
plt.plot(iterations, lower_bounds_mu1[:100], label='µ = 0.1', color='blue')
plt.plot(iterations, lower_bounds_mu2[:100], label='µ = 0.2', color='green')
plt.xlabel('Iterations')
plt.ylabel('Lower Bound')
plt.title('Evolution of the Lower Bound Over 100 Iterations')
plt.ylim(400, max(max(lower_bounds_mu1[:100]), max(lower_bounds_mu2[:100])))
plt.legend()
plt.grid()

plt.show()
