static class Rectangle {
    int x1, y1, x2, y2;
    long area;

    Rectangle(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.area = (long)(Math.abs(x2 - x1) + 1) * (Math.abs(y2 - y1) + 1);
    }
}

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

public static long partOne(String fileName) {
    List<String> lines = readFile(fileName);

    // Parse tiles
    List<int[]> tiles = new ArrayList<>();
    for (String line : lines) {
        if (line.trim().isEmpty()) continue;
        String[] parts = line.split(",");
        int x = Integer.parseInt(parts[0].trim());
        int y = Integer.parseInt(parts[1].trim());
        tiles.add(new int[]{x, y});
    }

    // Find maximum rectangle area
    long maxArea = 0;
    for (int i = 0; i < tiles.size(); i++) {
        for (int j = i + 1; j < tiles.size(); j++) {
            int[] tile1 = tiles.get(i);
            int[] tile2 = tiles.get(j);

            Rectangle rect = new Rectangle(tile1[0], tile1[1], tile2[0], tile2[1]);
            //IO.println("Rectangle coords: " + tile1[0] + ", " + tile1[1] + " || " + tile2[0] + ", " + tile2[1] + " with area " + rect.area);
            maxArea = Math.max(maxArea, rect.area);
        }
    }

    return maxArea;
}

public static long partTwo(String fileName) {
    List<String> lines = readFile(fileName);

    // Parse red tiles (in order)
    List<int[]> redTiles = new ArrayList<>();
    for (String line : lines) {
        if (line.trim().isEmpty()) continue;
        String[] parts = line.split(",");
        int x = Integer.parseInt(parts[0].trim());
        int y = Integer.parseInt(parts[1].trim());
        redTiles.add(new int[]{x, y});
    }

    // Build set of boundary tiles (red + green connecting tiles)
    Set<String> boundaryTiles = new HashSet<>();

    // Add all red tiles
    for (int[] tile : redTiles) {
        boundaryTiles.add(tile[0] + "," + tile[1]);
    }

    // Add green tiles connecting consecutive red tiles
    for (int i = 0; i < redTiles.size(); i++) {
        int[] current = redTiles.get(i);
        int[] next = redTiles.get((i + 1) % redTiles.size());

        if (current[0] == next[0]) { // same column
            int minY = Math.min(current[1], next[1]);
            int maxY = Math.max(current[1], next[1]);
            for (int y = minY; y <= maxY; y++) {
                boundaryTiles.add(current[0] + "," + y);
            }
        } else if (current[1] == next[1]) { // same row
            int minX = Math.min(current[0], next[0]);
            int maxX = Math.max(current[0], next[0]);
            for (int x = minX; x <= maxX; x++) {
                boundaryTiles.add(x + "," + current[1]);
            }
        }
    }

    // Cache for polygon checks
    Map<String, Boolean> polygonCache = new HashMap<>();

    // Find maximum rectangle
    long maxArea = 0;
    for (int i = 0; i < redTiles.size(); i++) {
        for (int j = i + 1; j < redTiles.size(); j++) {
            int[] tile1 = redTiles.get(i);
            int[] tile2 = redTiles.get(j);

            int x1 = Math.min(tile1[0], tile2[0]);
            int x2 = Math.max(tile1[0], tile2[0]);
            int y1 = Math.min(tile1[1], tile2[1]);
            int y2 = Math.max(tile1[1], tile2[1]);

            // Check if all 4 corners are inside polygon or on boundary
            boolean cornersValid = true;
            for (int[] corner : new int[][]{{x1,y1},{x2,y1},{x1,y2},{x2,y2}}) {
                if (!isValidPoint(corner[0], corner[1], boundaryTiles, redTiles, polygonCache)) {
                    cornersValid = false;
                    break;
                }
            }
            if (!cornersValid) continue;

            // Check edges
            boolean edgesValid = true;
            for (int x = x1; x <= x2 && edgesValid; x++) {
                for (int y : new int[]{y1, y2}) {
                    if (!isValidPoint(x, y, boundaryTiles, redTiles, polygonCache)) {
                        edgesValid = false;
                        break;
                    }
                }
            }
            if (!edgesValid) continue;

            for (int y = y1; y <= y2 && edgesValid; y++) {
                for (int x : new int[]{x1, x2}) {
                    if (!isValidPoint(x, y, boundaryTiles, redTiles, polygonCache)) {
                        edgesValid = false;
                        break;
                    }
                }
            }
            if (!edgesValid) continue;

            // For convex polygons, if all corners and edges are valid, interior is valid
            // Sample a few interior points to be safe
            boolean interiorValid = true;
            if (x2 > x1 + 1 && y2 > y1 + 1) {
                int midX = (x1 + x2) / 2;
                int midY = (y1 + y2) / 2;
                if (!isValidPoint(midX, midY, boundaryTiles, redTiles, polygonCache)) {
                    interiorValid = false;
                }
            }

            if (interiorValid) {
                long area = (long)(x2 - x1 + 1) * (y2 - y1 + 1);
                maxArea = Math.max(maxArea, area);
            }
        }
    }

    return maxArea;
}

private static boolean isValidPoint(int x, int y, Set<String> boundaryTiles, List<int[]> redTiles, Map<String, Boolean> cache) {
    if (boundaryTiles.contains(x + "," + y)) {
        return true;
    }
    String key = x + "," + y;
    if (!cache.containsKey(key)) {
        cache.put(key, isInsidePolygon(x, y, redTiles));
    }
    return cache.get(key);
}

private static boolean isInsidePolygon(int x, int y, List<int[]> polygon) {
    int intersections = 0;
    int n = polygon.size();

    for (int i = 0; i < n; i++) {
        int[] p1 = polygon.get(i);
        int[] p2 = polygon.get((i + 1) % n);

        // Check if ray from (x,y) to right intersects edge p1-p2
        if (p1[1] == p2[1]) continue; // horizontal edge

        int minY = Math.min(p1[1], p2[1]);
        int maxY = Math.max(p1[1], p2[1]);

        if (y < minY || y >= maxY) continue;

        // Calculate x coordinate of intersection
        double xIntersect = p1[0] + (double)(y - p1[1]) * (p2[0] - p1[0]) / (p2[1] - p1[1]);

        if (xIntersect > x) {
            intersections++;
        }
    }

    return intersections % 2 == 1;
}


void main() {
    System.out.println("Part One (test): " + partOne("src/input-test"));
    System.out.println("Part One: " + partOne("src/input"));
    System.out.println("Part Two (test): " + partTwo("src/input-test"));
    System.out.println("Part Two: " + partTwo("src/input"));
}
