import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.*;

void main() throws IOException {
    var testInput = Files.readString(Path.of("src/input-test"));
    var input = Files.readString(Path.of("src/input"));

    IO.println("Part 1 (test): " + partOne(testInput));
    IO.println("Part 1: " + partOne(input));
    IO.println("Part 2 (test): " + partTwo(testInput));
    IO.println("Part 2: " + partTwo(input));
}

int partOne(String input) {
    if (input.isBlank()) return 0;

    var lines = input.trim().split("\n");
    int totalPresses = 0;

    for (String line : lines) {
        totalPresses += solveMachine(line);
    }

    return totalPresses;
}

int solveMachine(String line) {
    // Parse: [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
    Pattern pattern = Pattern.compile("\\[([.#]+)\\](.*)\\{.*\\}");
    Matcher matcher = pattern.matcher(line);

    if (!matcher.find()) {
        return 0;
    }

    String lights = matcher.group(1);
    String buttonsStr = matcher.group(2).trim();

    // Parse target state (# = on, . = off)
    int numLights = lights.length();
    boolean[] target = new boolean[numLights];
    for (int i = 0; i < numLights; i++) {
        target[i] = lights.charAt(i) == '#';
    }

    // Parse button definitions (each button toggles certain lights)
    List<List<Integer>> buttons = new ArrayList<>();
    Pattern buttonPattern = Pattern.compile("\\(([0-9,]+)\\)");
    Matcher buttonMatcher = buttonPattern.matcher(buttonsStr);

    while (buttonMatcher.find()) {
        String[] indices = buttonMatcher.group(1).split(",");
        List<Integer> button = new ArrayList<>();
        for (String idx : indices) {
            button.add(Integer.parseInt(idx.trim()));
        }
        buttons.add(button);
    }

    int numButtons = buttons.size();

    // Key insight: This is linear algebra over GF(2) (binary field)
    // Since toggle operations are XOR, pressing a button twice = doing nothing
    // So we only need to check: press each button 0 or 1 times
    // Find the combination with minimum presses
    int minPresses = Integer.MAX_VALUE;

    // Try all 2^numButtons combinations
    for (int mask = 0; mask < (1 << numButtons); mask++) {
        boolean[] state = new boolean[numLights]; // all start off
        int presses = 0;

        for (int i = 0; i < numButtons; i++) {
            if ((mask & (1 << i)) != 0) {
                presses++;
                // Press button i: toggle its lights
                for (int lightIdx : buttons.get(i)) {
                    state[lightIdx] = !state[lightIdx];
                }
            }
        }

        // Check if we reached the target state
        if (Arrays.equals(state, target)) {
            minPresses = Math.min(minPresses, presses);
        }
    }

    return minPresses == Integer.MAX_VALUE ? 0 : minPresses;
}

int partTwo(String input) {
    if (input.isBlank()) return 0;

    var lines = input.trim().split("\n");
    int totalPresses = 0;

    for (String line : lines) {
        int result = solveMachineJoltage(line);
        if (result == Integer.MAX_VALUE) {
            System.err.println("No solution found for: " + line);
        }
        totalPresses += result;
    }

    return totalPresses;
}

int solveMachineJoltage(String line) {
    // Parse: [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
    Pattern pattern = Pattern.compile("\\[([.#]+)\\](.*)\\{([0-9,]+)\\}");
    Matcher matcher = pattern.matcher(line);

    if (!matcher.find()) {
        return 0;
    }

    String buttonsStr = matcher.group(2).trim();
    String joltageStr = matcher.group(3).trim();

    // Parse target joltage levels
    String[] joltageValues = joltageStr.split(",");
    int numCounters = joltageValues.length;
    int[] target = new int[numCounters];
    for (int i = 0; i < numCounters; i++) {
        target[i] = Integer.parseInt(joltageValues[i].trim());
    }

    // Parse button definitions (each button increments certain counters)
    List<List<Integer>> buttons = new ArrayList<>();
    Pattern buttonPattern = Pattern.compile("\\(([0-9,]+)\\)");
    Matcher buttonMatcher = buttonPattern.matcher(buttonsStr);

    while (buttonMatcher.find()) {
        String[] indices = buttonMatcher.group(1).split(",");
        List<Integer> button = new ArrayList<>();
        for (String idx : indices) {
            button.add(Integer.parseInt(idx.trim()));
        }
        buttons.add(button);
    }

    // Sort buttons by length (descending) - this helps the Gaussian elimination
    buttons.sort((a, b) -> b.size() - a.size());

    // This is a system of linear equations with non-negative integer solutions
    // We want to minimize: sum of all button presses
    // Subject to: for each counter i, sum of (button presses * button affects counter i) = target[i]

    // Use Gaussian elimination to solve the system, then find minimum
    int numButtons = buttons.size();

    // For small systems, we can use a greedy/backtracking approach
    // Build coefficient matrix: matrix[counter][button] = 1 if button affects counter, 0 otherwise
    int[][] matrix = new int[numCounters][numButtons];
    for (int b = 0; b < numButtons; b++) {
        for (int counter : buttons.get(b)) {
            if (counter < numCounters) {
                matrix[counter][b] = 1;
            }
        }
    }

    // Solve using bounded search with pruning
    return findMinimalSolution(matrix, target, numButtons, numCounters);
}

int findMinimalSolution(int[][] matrix, int[] target, int numButtons, int numCounters) {
    // Use Gaussian elimination to find dependent and independent (free) variables
    // Then DFS only over the free variables

    double[][] data = new double[numCounters][numButtons + 1];

    // Build augmented matrix [A | b]
    for (int r = 0; r < numCounters; r++) {
        for (int c = 0; c < numButtons; c++) {
            data[r][c] = matrix[r][c];
        }
        data[r][numButtons] = target[r];
    }

    // Gaussian elimination
    List<Integer> dependents = new ArrayList<>();
    List<Integer> independents = new ArrayList<>();

    int pivot = 0;
    int col = 0;

    while (pivot < numCounters && col < numButtons) {
        // Find best pivot row for this column
        int bestRow = pivot;
        double bestValue = Math.abs(data[pivot][col]);
        for (int r = pivot + 1; r < numCounters; r++) {
            double absVal = Math.abs(data[r][col]);
            if (absVal > bestValue) {
                bestRow = r;
                bestValue = absVal;
            }
        }

        // If best value is ~zero, this is a free variable
        if (bestValue < 1e-9) {
            independents.add(col);
            col++;
            continue;
        }

        // Swap rows and mark this column as dependent
        double[] temp = data[pivot];
        data[pivot] = data[bestRow];
        data[bestRow] = temp;
        dependents.add(col);

        // Normalize pivot row
        double pivotValue = data[pivot][col];
        for (int j = col; j <= numButtons; j++) {
            data[pivot][j] /= pivotValue;
        }

        // Eliminate this column in all other rows
        for (int r = 0; r < numCounters; r++) {
            if (r != pivot) {
                double factor = data[r][col];
                if (Math.abs(factor) > 1e-9) {
                    for (int j = col; j <= numButtons; j++) {
                        data[r][j] -= factor * data[pivot][j];
                    }
                }
            }
        }

        pivot++;
        col++;
    }

    // Any remaining columns are free variables
    for (int c = col; c < numButtons; c++) {
        independents.add(c);
    }

    // DFS over independent variables
    int maxVal = 0;
    for (int t : target) {
        maxVal = Math.max(maxVal, t);
    }
    maxVal++; // +1 like in Rust

    int[] values = new int[independents.size()];
    int[] minResult = {Integer.MAX_VALUE};

    dfsSearch(data, dependents, independents, numButtons, 0, values, minResult, maxVal);

    return minResult[0];
}

void dfsSearch(double[][] data, List<Integer> dependents, List<Integer> independents,
               int numButtons, int idx, int[] values, int[] min, int maxVal) {

    // When we've assigned all independent variables, check if valid
    if (idx == independents.size()) {
        Integer total = checkValid(data, dependents, independents, numButtons, values);
        if (total != null) {
            min[0] = Math.min(min[0], total);
        }
        return;
    }

    // Calculate sum of values assigned so far
    int totalSoFar = 0;
    for (int i = 0; i < idx; i++) {
        totalSoFar += values[i];
    }

    // Try different values for the current independent variable
    for (int val = 0; val < maxVal; val++) {
        // Optimization: if we ever go above min, we can't do better
        if (totalSoFar + val >= min[0]) {
            break;
        }
        values[idx] = val;
        dfsSearch(data, dependents, independents, numButtons, idx + 1, values, min, maxVal);
    }
}

Integer checkValid(double[][] data, List<Integer> dependents, List<Integer> independents,
                   int numButtons, int[] values) {
    // Start with sum of independent variable values (free variable presses)
    int total = 0;
    for (int v : values) {
        total += v;
    }

    // Calculate dependent variable values
    for (int row = 0; row < dependents.size(); row++) {
        // Calculate this dependent by subtracting the sum of free variable contributions
        double val = data[row][numButtons]; // the RHS (target)

        for (int i = 0; i < independents.size(); i++) {
            int col = independents.get(i);
            val -= data[row][col] * values[i];
        }

        // Need non-negative, whole numbers for valid solution
        if (val < -1e-9) {
            return null;
        }

        double rounded = Math.round(val);
        if (Math.abs(val - rounded) > 1e-9) {
            return null;
        }

        total += (int) rounded;
    }

    return total;
}

