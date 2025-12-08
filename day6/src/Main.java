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

void main() {

    List<String> lines = readFile("src/input");

    MatrixResult matrix = parseMatrix(lines);

    System.out.println(part1(matrix));

}
