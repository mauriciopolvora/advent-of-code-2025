import java.sql.SQLOutput;

public static List<String> readFile(String fileName) {
    // Created List of String
    List<String> lines = Collections.emptyList();

    try {
        lines = Files.readAllLines(
                Paths.get(fileName),
                StandardCharsets.UTF_8);
    } catch (IOException e) {
        e.printStackTrace();
    }

    return lines;
}

public record MatrixResult(long[][] matrix, String[] symbols) {}

public static MatrixResult parseMatrix(List<String> lines) {
    String[][] parts = new String[lines.size()][];
    for (int i = 0; i < lines.size(); i++) {
        parts[i] = lines.get(i).split(" ");
    }

    // remove random spaces
    String[][] cleaned = Arrays.stream(parts)
            .map(row -> Arrays.stream(row)
                    .filter(str -> !str.isEmpty())
                    .toArray(String[]::new))
            .toArray(String[][]::new);

    System.out.println(Arrays.deepToString(cleaned));

    // Extract symbols from last row
    String[] symbols = new String[cleaned[cleaned.length - 1].length];
    for (int j = 0; j < cleaned[cleaned.length - 1].length; j++) {
        symbols[j] = cleaned[cleaned.length - 1][j];
    }

    long[][] matrix = new long[cleaned.length - 1][];
    for (int i = 0; i < cleaned.length - 1; i++) {
        matrix[i] = new long[cleaned[i].length];
        for (int j = 0; j < cleaned[i].length; j++) {
            matrix[i][j] = Long.parseLong(cleaned[i][j]);
        }
    }

    System.out.println("symbols: " + Arrays.toString(symbols));
    System.out.println("matrix: " + Arrays.deepToString(matrix));
    return new MatrixResult(matrix, symbols);
}

public static long part1(MatrixResult result) {
    long sum = 0;
    long[][] matrix = result.matrix();
    String[] symbols = result.symbols();

    for (int j = 0; j < symbols.length; j++) {
        long temp = 0;
        for (long[] longs : matrix) {
            //System.out.println(longs[j] + " " + symbols[j]);
            if (symbols[j].equals("*")) {
                if (temp == 0) temp = 1;
                temp = temp * longs[j];
            } else {
                temp = temp + longs[j];
            }
        }
        //System.out.println("temp for " + j + " is " + temp);
        sum += temp;
    }
    return sum;
}

public static long part2(List<String> lines) {
    // Get the operator line (last line)
    String operatorLine = lines.get(lines.size() - 1);

    // Find max width across all lines
    int maxWidth = 0;
    for (String line : lines) {
        maxWidth = Math.max(maxWidth, line.length());
    }

    // Pad all lines to same width
    String[] paddedLines = new String[lines.size()];
    for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        if (line.length() < maxWidth) {
            paddedLines[i] = line + " ".repeat(maxWidth - line.length());
        } else {
            paddedLines[i] = line;
        }
    }

    long grandTotal = 0;

    // Process columns from right to left
    int col = maxWidth - 1;
    while (col >= 0) {
        // Skip space-only columns (problem separators)
        while (col >= 0 && isSpaceColumn(paddedLines, col)) {
            col--;
        }
        if (col < 0) break;

        // Find the start of this problem (go left until we hit a space-only column)
        int endCol = col;
        while (col >= 0 && !isSpaceColumn(paddedLines, col)) {
            col--;
        }
        int startCol = col + 1;

        // Get the operator for this problem (from the last row)
        char operator = ' ';
        for (int c = startCol; c <= endCol; c++) {
            char ch = paddedLines[paddedLines.length - 1].charAt(c);
            if (ch == '*' || ch == '+') {
                operator = ch;
                break;
            }
        }

        // Read columns from right to left within this problem
        // Each column forms a number (top to bottom = most significant to least significant)
        List<Long> numbers = new ArrayList<>();
        for (int c = endCol; c >= startCol; c--) {
            StringBuilder numStr = new StringBuilder();
            // Read digits from top to bottom (excluding operator row)
            for (int row = 0; row < paddedLines.length - 1; row++) {
                char ch = paddedLines[row].charAt(c);
                if (Character.isDigit(ch)) {
                    numStr.append(ch);
                }
            }
            if (numStr.length() > 0) {
                numbers.add(Long.parseLong(numStr.toString()));
            }
        }

        // Apply operation
        long result = 0;
        if (operator == '*') {
            result = 1;
            for (Long num : numbers) {
                result *= num;
            }
        } else if (operator == '+') {
            for (Long num : numbers) {
                result += num;
            }
        }

        grandTotal += result;
    }

    return grandTotal;
}

// Helper method to check if a column contains only spaces
private static boolean isSpaceColumn(String[] lines, int col) {
    for (String line : lines) {
        if (col < line.length() && line.charAt(col) != ' ') {
            return false;
        }
    }
    return true;
}

void main() {

    List<String> lines = readFile("src/input");

    MatrixResult matrix = parseMatrix(lines);

    System.out.println(part1(matrix));
    System.out.println(part2(lines));

}
