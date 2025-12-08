import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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

public static int part1(List<String> lines) {
    int startCol = -1;
    for (int i = 0; i < lines.getFirst().length(); i++) {
        if (lines.getFirst().charAt(i) == 'S') {
            startCol = i;
            break;
        }
    }

    // beam positions in index
    Set<Integer> currentBeams = new HashSet<>();
    currentBeams.add(startCol);

    int totalSplits = 0;

    for (int lineIdx = 1; lineIdx < lines.size(); lineIdx++) {
        String currentLine = lines.get(lineIdx);
        Set<Integer> nextBeams = new HashSet<>();

        for (int beamPos : currentBeams) {
            if (beamPos >= 0 && beamPos < currentLine.length() && currentLine.charAt(beamPos) == '^') {
                totalSplits++;

                // new beams at index - 1 and index + 1 for next line
                if (beamPos - 1 >= 0) {
                    nextBeams.add(beamPos - 1);
                }
                if (beamPos + 1 < currentLine.length()) {
                    nextBeams.add(beamPos + 1);
                }
            } else {
                nextBeams.add(beamPos);
            }
        }

        currentBeams = nextBeams;
    }

    return totalSplits;
}

public static long part2(List<String> lines) {
    int startCol = -1;
    for (int i = 0; i < lines.getFirst().length(); i++) {
        if (lines.getFirst().charAt(i) == 'S') {
            startCol = i;
            break;
        }
    }

    // memoization
    Map<String, Long> memo = new HashMap<>();
    return buildAndCountTimelines(lines, 0, startCol, memo);
}

private static long buildAndCountTimelines(List<String> lines, int row, int col, Map<String, Long> memo) {
    String key = row + "," + col;

    // check computed
    if (memo.containsKey(key)) {
        return memo.get(key);
    }

    int nextRow = row + 1;

    // If we've exited the manifold, this is one timeline
    if (nextRow >= lines.size()) {
        memo.put(key, 1L);
        return 1L;
    }

    String currentLine = lines.get(nextRow);

    // out of bounds horizontally
    if (col < 0 || col >= currentLine.length()) {
        memo.put(key, 1L);
        return 1L;
    }

    long count;

    if (currentLine.charAt(col) == '^') {
        // two timelines
        long leftCount = buildAndCountTimelines(lines, nextRow, col - 1, memo);
        long rightCount = buildAndCountTimelines(lines, nextRow, col + 1, memo);
        count = leftCount + rightCount;
    } else {
        count = buildAndCountTimelines(lines, nextRow, col, memo);
    }

    memo.put(key, count);
    return count;
}

void main() {
    List<String> input = readFile("input");
    int result1 = part1(input);
    IO.println("Part 1 - Total splits: " + result1);

    long result2 = part2(input);
    IO.println("Part 2 - Total timelines: " + result2);
}
