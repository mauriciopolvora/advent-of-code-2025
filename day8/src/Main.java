// Junction box with 3D coordinates
static class Point {
    int x, y, z;
    int index;

    Point(int x, int y, int z, int index) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.index = index;
    }

    double distanceTo(Point other) {
        long dx = this.x - other.x;
        long dy = this.y - other.y;
        long dz = this.z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}

// Edge between two points with distance
static class Edge implements Comparable<Edge> {
    int from, to;
    double distance;

    Edge(int from, int to, double distance) {
        this.from = from;
        this.to = to;
        this.distance = distance;
    }

    @Override
    public int compareTo(Edge other) {
        return Double.compare(this.distance, other.distance);
    }
}

// Union-Find data structure
static class UnionFind {
    int[] parent;
    int[] size;

    UnionFind(int n) {
        parent = new int[n];
        size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // path compression
        }
        return parent[x];
    }

    boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) {
            return false; // already in same set
        }

        // union by size
        if (size[rootX] < size[rootY]) {
            parent[rootX] = rootY;
            size[rootY] += size[rootX];
        } else {
            parent[rootY] = rootX;
            size[rootX] += size[rootY];
        }
        return true;
    }

    List<Integer> getCircuitSizes() {
        Map<Integer, Integer> circuits = new HashMap<>();
        for (int i = 0; i < parent.length; i++) {
            int root = find(i);
            circuits.put(root, size[root]);
        }
        return new ArrayList<>(circuits.values());
    }
}

// Helper class to hold parsed input
private static class ParsedInput {
    List<Point> points;
    List<Edge> sortedEdges;

    ParsedInput(List<Point> points, List<Edge> sortedEdges) {
        this.points = points;
        this.sortedEdges = sortedEdges;
    }
}

public static List<String> readFile(String fileName) {
    try {
        return Files.readAllLines(
                Paths.get(fileName),
                StandardCharsets.UTF_8);
    } catch (IOException e) {
        e.printStackTrace();
        return Collections.emptyList();
    }
}

private static ParsedInput parseAndCalculateEdges(String fileName) {
    List<String> lines = readFile(fileName);

    // Parse junction boxes
    List<Point> points = new ArrayList<>();
    for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i).trim();
        if (line.isEmpty()) continue;

        String[] parts = line.split(",");
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        int z = Integer.parseInt(parts[2]);
        points.add(new Point(x, y, z, i));
    }

    // Calculate all pairwise distances
    List<Edge> edges = new ArrayList<>();
    for (int i = 0; i < points.size(); i++) {
        for (int j = i + 1; j < points.size(); j++) {
            double dist = points.get(i).distanceTo(points.get(j));
            edges.add(new Edge(i, j, dist));
        }
    }

    // Sort edges by distance
    Collections.sort(edges);

    return new ParsedInput(points, edges);
}

public static long part1(String fileName, int connectionsToMake) {
    ParsedInput input = parseAndCalculateEdges(fileName);

    // Use Union-Find to connect the shortest pairs
    UnionFind uf = new UnionFind(input.points.size());
    int connectionAttempts = 0;

    for (Edge edge : input.sortedEdges) {
        if (connectionAttempts >= connectionsToMake) {
            break;
        }
        connectionAttempts++;
        uf.union(edge.from, edge.to);
    }

    // Get circuit sizes and find the three largest
    List<Integer> circuitSizes = uf.getCircuitSizes();
    circuitSizes.sort(Collections.reverseOrder());

    // Multiply the three largest
    long result = 1;
    for (int i = 0; i < Math.min(3, circuitSizes.size()); i++) {
        result *= circuitSizes.get(i);
    }

    return result;
}

public static long part2(String fileName) {
    ParsedInput input = parseAndCalculateEdges(fileName);

    // Use Union-Find to connect pairs until all are in one circuit
    UnionFind uf = new UnionFind(input.points.size());
    int lastFrom = -1, lastTo = -1;
    int successfulConnections = 0;
    int totalNodes = input.points.size();

    for (Edge edge : input.sortedEdges) {
        if (uf.union(edge.from, edge.to)) {
            lastFrom = edge.from;
            lastTo = edge.to;
            successfulConnections++;

            // All nodes are in one circuit after n-1 successful connections
            if (successfulConnections == totalNodes - 1) {
                break;
            }
        }
    }

    // Return product of X coordinates of the last two connected boxes
    return (long) input.points.get(lastFrom).x * input.points.get(lastTo).x;
}

void main() {
    IO.println("=== Part 1 Test ===");
    long testResult = part1("src/input-test", 10);
    IO.println("Test result (10 connections): " + testResult);

    IO.println("\n=== Part 1 ===");
    long result = part1("src/input", 1000);
    IO.println("Result (1000 connections): " + result);

    IO.println("\n=== Part 2 Test ===");
    long testResult2 = part2("src/input-test");
    IO.println("Test result: " + testResult2);

    IO.println("\n=== Part 2 ===");
    long result2 = part2("src/input");
    IO.println("Result: " + result2);
}

