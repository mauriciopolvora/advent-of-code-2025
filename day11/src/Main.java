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

static class GraphBuilder {
    static class Graph {
            public final Map<String, Set<String>> outEdges = new HashMap<>();
            public final Map<String, Integer> inDegree = new HashMap<>();

            public Set<String> nodes() {
                Set<String> all = new HashSet<>(outEdges.keySet());
                for (Set<String> tos : outEdges.values()) all.addAll(tos);
                return all;
            }

            public Set<String> roots() {
                Set<String> roots = new HashSet<>();
                for (String n : nodes()) {
                    if (inDegree.getOrDefault(n, 0) == 0) roots.add(n);
                }
                return roots;
            }
        }
    }

static long part1(List<String> lines, String startingString) {
        GraphBuilder.Graph g = new GraphBuilder.Graph();

        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(":", 2);
            String from = parts[0].trim();

            g.outEdges.computeIfAbsent(from, k -> new LinkedHashSet<>());
            g.inDegree.putIfAbsent(from, g.inDegree.getOrDefault(from, 0));

            if (parts.length == 1 || parts[1].trim().isEmpty()) continue;

            String[] tos = parts[1].trim().split("\\s+");
            for (String toRaw : tos) {
                String to = toRaw.trim();
                if (to.isEmpty()) continue;

                g.outEdges.get(from).add(to);
                g.outEdges.computeIfAbsent(to, k -> new LinkedHashSet<>());

                g.inDegree.put(to, g.inDegree.getOrDefault(to, 0) + 1);
                g.inDegree.putIfAbsent(from, g.inDegree.getOrDefault(from, 0));
            }
        }

        return countSteps(g, startingString, new HashSet<>());
    }

static long countSteps(GraphBuilder.Graph g, String node, Set<String> visited) {
        if (node.equals("out")) {
            return 1;
        }

        if (visited.contains(node)) return 0;

        visited.add(node);

        if (!g.outEdges.containsKey(node) || g.outEdges.get(node).isEmpty()) {
            visited.remove(node);
            return 0;
        }

        long total = 0;
        for (String next : g.outEdges.get(node)) {
            total += countSteps(g, next, visited);
        }

        visited.remove(node);
        return total;
    }

static long part2(List<String> lines, String start, String end, Set<String> mustVisit) {
    GraphBuilder.Graph g = new GraphBuilder.Graph();

    for (String raw : lines) {
        String line = raw.trim();
        if (line.isEmpty()) continue;

        String[] parts = line.split(":", 2);
        String from = parts[0].trim();

        g.outEdges.computeIfAbsent(from, k -> new LinkedHashSet<>());
        g.inDegree.putIfAbsent(from, g.inDegree.getOrDefault(from, 0));

        if (parts.length == 1 || parts[1].trim().isEmpty()) continue;

        String[] tos = parts[1].trim().split("\\s+");
        for (String toRaw : tos) {
            String to = toRaw.trim();
            if (to.isEmpty()) continue;

            g.outEdges.get(from).add(to);
            g.outEdges.computeIfAbsent(to, k -> new LinkedHashSet<>());

            g.inDegree.put(to, g.inDegree.getOrDefault(to, 0) + 1);
            g.inDegree.putIfAbsent(from, g.inDegree.getOrDefault(from, 0));
        }
    }

    List<String> requiredList = new ArrayList<>(mustVisit);
    Map<String, Map<Integer, Long>> memo = new HashMap<>();

    return countPathsWithRequired(g, start, end, new HashSet<>(), requiredList, (1 << requiredList.size()) - 1, memo);
}

static long countPathsWithRequired(GraphBuilder.Graph g, String node, String target,
                                   Set<String> visited, List<String> requiredList, int remainingMask,
                                   Map<String, Map<Integer, Long>> memo) {
    int newMask = remainingMask;
    for (int i = 0; i < requiredList.size(); i++) {
        if (requiredList.get(i).equals(node) && (remainingMask & (1 << i)) != 0) {
            newMask &= ~(1 << i);
        }
    }

    if (node.equals(target)) {
        return newMask == 0 ? 1 : 0;
    }

    if (visited.contains(node)) {
        return 0;
    }

    Map<Integer, Long> nodeCache = memo.get(node);
    if (nodeCache != null && nodeCache.containsKey(newMask)) {
        return nodeCache.get(newMask);
    }

    visited.add(node);

    if (!g.outEdges.containsKey(node) || g.outEdges.get(node).isEmpty()) {
        visited.remove(node);
        return 0;
    }

    long total = 0;
    for (String next : g.outEdges.get(node)) {
        total += countPathsWithRequired(g, next, target, visited, requiredList, newMask, memo);
    }

    visited.remove(node);

    memo.computeIfAbsent(node, k -> new HashMap<>()).put(newMask, total);

    return total;
}


void main() {
    System.out.println("Test result: " + part1(readFile("src/input-test"), "you"));
    System.out.println("Part 1: " + part1(readFile("src/input"), "you"));


    System.out.println("Part 2 Test (expected 2): " + part2(readFile("src/input-test2"), "svr", "out", Set.of("dac", "fft")));
    System.out.println("Part 2: " + part2(readFile("src/input"), "svr", "out", Set.of("dac", "fft")));

}
