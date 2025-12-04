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

// first try with just horizontal line counting, idea was to then parse again and check neighbors
public static Integer[][] parseLines(List<String> lines) {
    int width = lines.getFirst().length();
    Integer[][] result = new Integer[lines.size()][width];

    for (int i = 0; i < lines.size(); i++) {
        if (lines.get(i).toCharArray()[1] == '@') {
            result[i][0] = 1;
        } else  {
            result[i][0] = 0;
        }
        if (lines.get(i).toCharArray()[width - 2] == '@') {
            result[i][width-1] = 1;
        } else   {
            result[i][width-1] = 0;
        }
    }


    for (int i = 0; i < lines.size(); i++) {
        for (int j = 1; j < width-1; j++) {
            if (lines.get(i).toCharArray()[j-1] == '@' && lines.get(i).toCharArray()[j+1] == '@') {
                result[i][j] = 2;
            }
            else if  (lines.get(i).toCharArray()[j-1] == '@') {
                result[i][j] = 1;

            } else if (lines.get(i).toCharArray()[j+1] == '@') {
                result[i][j] = 1;
            } else
                result[i][j] = 0;
        }

    }
    return result;
}

public Integer parseTotalPart1(List<String> originalLines) {
    int height = originalLines.size();
    int width = originalLines.getFirst().length();
    Integer[][] resultTotal = new Integer[height][width];

    // 8 direction offsets: up-left, up, up-right, left, right, down-left, down, down-right
    int[] dRow = {-1, -1, -1, 0, 0, 1, 1, 1};
    int[] dCol = {-1, 0, 1, -1, 1, -1, 0, 1};

    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            int count = 0;

            // Check all 8 neighbors
            for (int d = 0; d < 8; d++) {
                int newRow = i + dRow[d];
                int newCol = j + dCol[d];

                // Check bounds before accessing
                if (newRow >= 0 && newRow < height && newCol >= 0 && newCol < width) {
                    if (originalLines.get(newRow).charAt(newCol) == '@') {
                        count++;
                    }
                }
            }

            resultTotal[i][j] = count;
        }
    }

    // Count accessible rolls
    int accessible = 0;
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            if (originalLines.get(i).charAt(j) == '@' && resultTotal[i][j] < 4) {
                accessible++;
            }
        }
    }

    return accessible;
}

public Integer parseTotalPart2(List<String> originalLines, int totalRemoved) {
    int height = originalLines.size();
    int width = originalLines.getFirst().length();
    Integer[][] resultTotal = new Integer[height][width];

    // 8 direction offsets: up-left, up, up-right, left, right, down-left, down, down-right
    int[] dRow = {-1, -1, -1, 0, 0, 1, 1, 1};
    int[] dCol = {-1, 0, 1, -1, 1, -1, 0, 1};

    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            int count = 0;

            // Check all 8 neighbors
            for (int d = 0; d < 8; d++) {
                int newRow = i + dRow[d];
                int newCol = j + dCol[d];

                // Check bounds before accessing
                if (newRow >= 0 && newRow < height && newCol >= 0 && newCol < width) {
                    if (originalLines.get(newRow).charAt(newCol) == '@') {
                        count++;
                    }
                }
            }

            resultTotal[i][j] = count;
        }
    }

    // Count accessible rolls
    int accessible = 0;
    List<String> newOriginalLines = new  ArrayList<>(originalLines);
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            if (originalLines.get(i).charAt(j) == '@' && resultTotal[i][j] < 4) {
                accessible++;
                StringBuilder sb = new StringBuilder(newOriginalLines.get(i));
                sb.setCharAt(j,'.');
                newOriginalLines.set(i, sb.toString());
            }
        }
    }

    totalRemoved += accessible;
    System.out.println("total removed: " + accessible);

    if (accessible > 0 ) {
        return parseTotalPart2(newOriginalLines, totalRemoved);
    }

    return totalRemoved;
}


void main() {
    List<String> lines = readFile("src/input");

    int answer = parseTotalPart2(lines,0);



    System.out.println("Accessible rolls: " + answer);
}

