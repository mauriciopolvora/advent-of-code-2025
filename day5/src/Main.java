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

public static int part1(List<String> ingredients, List<long[]> fresh) {

    Set<String> freshIngredientsSet = new HashSet<>();

    for (String ingredient : ingredients) {
        if (ingredient.contains("-") || ingredient.isEmpty()) {
            continue;
        }

        for (long[] freshArray : fresh) {
            if (freshArray[0] <= Long.parseLong(ingredient) && Long.parseLong(ingredient) <= freshArray[1]) {
                freshIngredientsSet.add(ingredient);
                break;
            }
        }
    }

    return freshIngredientsSet.size();
}

public static List<long[]> parseFresh(List<String> ingredients) {
    List<long[]> fresh = new ArrayList<>();
    for (String ingredient : ingredients) {
        if (ingredient.isBlank()) {
            return fresh;
        }
        String[] parts = ingredient.split("-");
        long start = Long.parseLong(parts[0]);
        long end = Long.parseLong(parts[1]);

        fresh.add(new long[]{start, end});
    }
    return fresh;
}

public static List<long[]> parseOverlapping(List<long[]> fresh) {
    if (fresh.isEmpty()) return new ArrayList<>();

    fresh.sort(Comparator.comparingLong(a -> a[0]));
    List<long[]> merged = new ArrayList<>();
    long[] current = fresh.getFirst().clone();

    for (int i = 1; i < fresh.size(); i++) {
        long[] next = fresh.get(i);
        if (next[0] <= current[1]) {
            // overlap
            current[1] = Math.max(current[1], next[1]);
        } else {
            // no overlap
            merged.add(current);
            current = next.clone();
        }
    }
    merged.add(current);

    return merged;
}


public static long part2(List<long[]> fresh) {
    long sum = 0;
    for (long[] freshArray : fresh) {
        long count = freshArray[1] - freshArray[0] + 1;
        sum += count;
    }
    return sum;
}

void main() {

    List<String> ingredients = readFile("src/input");
    List<long[]> fresh = parseFresh(ingredients);

    System.out.println(part2(parseOverlapping(fresh)));

}
