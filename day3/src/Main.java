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


void main() {

    List<String> batteries = readFile("src/input-test");

    for (String battery : batteries) {
        System.out.println(battery);
    }
}
