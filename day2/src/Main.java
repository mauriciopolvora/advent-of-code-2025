
public static String readFile(String fileName) {
    // Created List of String
    List<String> lines = Collections.emptyList();

    try {
        lines = Files.readAllLines(
                Paths.get(fileName),
                StandardCharsets.UTF_8);
    } catch (IOException e) {
        e.printStackTrace();
    }

    return lines.getFirst();
}

public static void parseInputPart1(String input) {
    String[] parts = input.split(",");
    long matchSum = 0;

    for (String part : parts) {
        System.out.println(part);
        int dashIndex = part.indexOf('-');
        if (dashIndex != -1) {
            String firstPart = part.substring(0, dashIndex);
            String secondPart = part.substring(dashIndex + 1);


            //System.out.println("First Part: " + firstPart);
            //System.out.println("Second Part: " + secondPart);

            for (long i = Long.parseLong(firstPart); i <= Long.parseLong(secondPart); i++) {
                String numberStr = Long.toString(i);
                if (numberStr.length()%2 == 0) {
                    String half1 = numberStr.substring(0, numberStr.length() / 2);
                    String half2 = numberStr.substring(numberStr.length() / 2);
                    if (half1.equals(half2)) {
                        System.out.println("Match found: " + numberStr);
                        matchSum = matchSum + Long.parseLong(numberStr);
                    }
                }
            }
        }
    }
    System.out.println("Total Sum of Matches: " + matchSum);
}

public static void parseInputPart2(String input) {
    String[] parts = input.split(",");
    List<String> matchesFound = new ArrayList<>();

    for (String part : parts) {
        System.out.println("Part: " + part);
        int dashIndex = part.indexOf('-');
        if (dashIndex != -1) {
            String firstPart = part.substring(0, dashIndex).trim();
            String secondPart = part.substring(dashIndex + 1).trim();

            System.out.println("  firstPart  = " + firstPart);
            System.out.println("  secondPart = " + secondPart);

            long start = Long.parseLong(firstPart);
            long endInclusive = Long.parseLong(secondPart);

            for (long currentNumber = start; currentNumber <= endInclusive; currentNumber++) {
                String s = Long.toString(currentNumber);
                System.out.println("Parsing: " + currentNumber);
                int maxChunkSize = s.length();
                System.out.println("Max chunkSize = " + maxChunkSize);

                // chunkSize from 1 up to maxChunkSize
                for (int chunkSize = 1; chunkSize <= maxChunkSize; chunkSize++) {
                    // start index i in the string (must be int)
                    List<String> chunkEntries = new ArrayList<>();
                    for (int i = 0; i + chunkSize <= s.length(); i += chunkSize) {
                        String chunk = s.substring(i, i + chunkSize);
                        System.out.println("  chunk (size " + chunkSize + "): " + chunk);
                        chunkEntries.add(chunk);
                    }
                    System.out.println(chunkEntries);
                    if (allEqual(chunkEntries, chunkSize, s)) {
                        System.out.println("Match found: " + s);
                        if (!matchesFound.contains(s)) {
                            matchesFound.add(s);
                        }
                    }
                }
            }
        }
    }
    System.out.println("List of matches found: " + matchesFound);
    long matchSum = 0;
    for (String match : matchesFound) {
        matchSum = matchSum + Long.parseLong(match);
    }
    System.out.println("Total Sum of Matches: " + matchSum);
}

private static boolean allEqual(List<String> chunks, int chunkSize, String s) {
    // Pattern must repeat at least twice AND fill the entire string
    if (chunks.size() < 2 || chunks.size() * chunkSize != s.length()) {
        return false;
    }

    String first = chunks.getFirst();
    for (String chunk : chunks) {
        if (!chunk.equals(first)) {
            return false;
        }
    }
    return true;
}

void main() {
    long startTime = System.currentTimeMillis();

    String input = readFile("src/input");
    parseInputPart2(input);

    long endTime = System.currentTimeMillis();
    long elapsedTime = endTime - startTime;
    System.out.println("\n️  Execution time: " + elapsedTime + "ms (" + (elapsedTime / 1000.0) + "s)");
}

