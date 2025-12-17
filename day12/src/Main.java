static List<String> readFile(String fileName) {
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

    //IO.println(readFile("src/input"));
    IO.println("hello");
}
