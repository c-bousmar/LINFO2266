import matplotlib.pyplot as plt
import pandas as pd

# Data for the TSP instances (provided earlier)
data = {
    "Instance": list(range(10)),  # Instances 0 to 9
    "Size 8": [1, 1, 0, 0, 1, 1, 1, 1, 3, 1],  # Runtimes for Size 8
    "Size 10": [3, 3, 3, 3, 3, 4, 3, 3, 3, 3],  # Runtimes for Size 10
    "Size 18": [1905, 1858, 2267, 1920, 1963, 1903, 1904, 1940, 1819, 1848]  # Runtimes for Size 18
}

# Create DataFrames for each size
df_size8 = pd.DataFrame({"Instance": list(range(10)), "Runtime (ms)": data["Size 8"]})
df_size10 = pd.DataFrame({"Instance": list(range(10)), "Runtime (ms)": data["Size 10"]})
df_size18 = pd.DataFrame({"Instance": list(range(10)), "Runtime (ms)": data["Size 18"]})

# Function to create and save table images
def plot_table(df, size, filename):
    fig, ax = plt.subplots(figsize=(4, 2))  # Adjust size for each table
    ax.axis('tight')
    ax.axis('off')

    # Create the table
    table = ax.table(cellText=df.values, colLabels=df.columns, cellLoc='center', loc='center')

    # Adjust table formatting
    table.scale(1, 1.5)  # Scale table height for better readability
    table.auto_set_font_size(False)
    table.set_fontsize(10)

    # Set the title for the table
    plt.title(f"TSP Instance Size {size}", fontsize=12)

    # Save the table as an image file
    plt.savefig(filename, bbox_inches='tight', dpi=300)
    plt.close()  # Close the plot to avoid overlapping issues

# Plot and save the three separate tables
plot_table(df_size8, 8, 'tsp_runtime_table_size8.png')
plot_table(df_size10, 10, 'tsp_runtime_table_size10.png')
plot_table(df_size18, 18, 'tsp_runtime_table_size18.png')

print("Table images have been created and saved as 'tsp_runtime_table_size8.png', 'tsp_runtime_table_size10.png', and 'tsp_runtime_table_size18.png'.")
