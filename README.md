# Convex Hull Java Project

This project implements the QuickHull algorithm (both sequential and parallel versions) to compute the convex hull of a set of randomly generated 2D points.

## Features
- QuickHull algorithm for convex hull computation
- Parallel version using Java threads for faster performance on large datasets
- Visualization of points and convex hull (for small n)
- Outputs convex hull points to a text file

## Files
- `ConvexHull.java` – Main class with sequential and parallel QuickHull implementations
- `IntList.java` – Simple dynamic integer list for storing point indices
- `NPunkter17.java` – Generates random, unique 2D points (Created by UiO)
- `Oblig5Precode.java` – Visualization and output utilities (Created by UiO)

## Usage
1. Compile all Java files:
   ```
   javac *.java
   ```
2. Run the program with the desired number of points and a random seed:
   ```
   java ConvexHull <n> <seed>
   ```
   Example:
   ```
   java ConvexHull 1000 42
   ```
   - For n < 10000, a window will display the points and the convex hull.
   - The convex hull points are saved to a file named `CONVEX-HULL-POINTS_<n>.txt`.

## Requirements
- Java 8 or newer

## About
This project was created for an assignment at the University of Oslo.
