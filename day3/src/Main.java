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

public static int parseBatteryBankPart1(String bank) {
    int maxJoltage = -1;
    char[] array = bank.toCharArray();
    for (int i = 0; i <= bank.length()-2; i++) {
        for (int j = i+1; j <= bank.length()-1; j++) {
            String totalNumber = array[i] + "" + array[j];
            int numberTesting = Integer.parseInt(totalNumber);
            if (numberTesting > maxJoltage) {
                maxJoltage = numberTesting;
            }
        }
    }
    return maxJoltage;
}

public static long parseBatteryBankPart2(String bank) {
    StringBuilder result = new StringBuilder();
    int currentPos = 0;
    int remaining = 12;

    for (int digitIndex = 0; digitIndex < 12; digitIndex++) {
        int searchEnd = bank.length() - remaining;

        char maxDigit = '0';
        int maxPos = currentPos;

        // Find the largest digit in the valid range
        for (int i = currentPos; i <= searchEnd; i++) {
            if (bank.charAt(i) > maxDigit) {
                maxDigit = bank.charAt(i);
                maxPos = i;
            }
        }

        result.append(maxDigit);
        currentPos = maxPos + 1;
        remaining--;
    }

    return Long.parseLong(result.toString());
}


void main() {

    List<String> batteries = readFile("src/input");
    long sumJ = 0;

    for (String battery : batteries) {
        sumJ = sumJ + parseBatteryBankPart2(battery);
    }

    System.out.println(sumJ);
}
