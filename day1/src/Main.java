public static List<String> readFileInList(String fileName) {
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

public static void parseDial(List<String> lines) {
    int dial = 50;
    int totalPassesZero = 0;
    System.out.println("Current dial position: " + dial);
    for (String line : lines) {
        System.out.print("The dial is rotated " + line);
        int[] result = runDial(dial, line.substring(0, 1), Integer.parseInt(line.substring(1)));
        dial = result[0];
        totalPassesZero += result[1];

        System.out.println("New dial position: " + dial);
    }
    System.out.println("Total passes of 0: " + totalPassesZero);
}

public static int[] runDial(int dial, String direction, int amount) {
    int passesZero = 0;

    if (direction.equals("L")) {
        for (int i = 1; i <= amount; i++) {
            dial = dial - 1;
            if (dial == 0) {
                passesZero++;
            }
            if (dial < 0) {
                dial = 99;
            }
        }
    } else {
        for (int i = 1; i <= amount; i++) {
            dial = dial + 1;
            if (dial > 99) {
                dial = 0;
                passesZero++;
            }
        }
    }
    System.out.println(" to point at " + dial + ", passing 0 " + passesZero + " times.");
    return new int[]{dial, passesZero};
}

void main() {
    List<String> l = readFileInList("src/input");
    parseDial(l);
}


