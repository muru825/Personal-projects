package core;

import javax.sound.sampled.*;
import edu.princeton.cs.algs4.Graph;
import tileengine.TETile;
import tileengine.Tileset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.util.*;

public class Enemy extends Avatar {
    TETile enemyTile = Tileset.TREE;
    int x;
    int y;
    Graph g;
    private ScheduledExecutorService scheduler;
    private int playerX, playerY;
    Random random;
    private List<int[]> pathToPlayer = new ArrayList<>(); // Store the path
    boolean toggle = false;

    public Enemy(int health, int attackValue, Graph g, Random rand) {
        super(health, attackValue, null);
        this.g = g;
        this.random = rand;
    }

    public void startMoving(TETile[][] world, int initialPlayerX, int initialPlayerY) {
        playerX = initialPlayerX;
        playerY = initialPlayerY;
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> movement(world), 0, random.nextInt(500)+ 500, TimeUnit.MILLISECONDS);
    }

    public void highlightPath(TETile[][] world) {

        // Highlight new path
        world[x][y] = Tileset.FLOOR;
        unHighlight(world);
        for (int[] pos : pathToPlayer) {
            if (world[pos[0]][pos[1]].equals(Tileset.AVATAR)) {
                continue;
            }
            world[pos[0]][pos[1]] = Tileset.FLOWER;
        }

    }

    public void unHighlight(TETile[][] world) {
        // Clear previous highlights
        for (int i = 0; i < world.length; i ++) {
            for (int j = 0; j < world[i].length; j++) {
                if (world[i][j].equals(Tileset.FLOWER)) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
        }
    }

    public void stopMoving() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    public void updatePlayerPosition(int newPlayerX, int newPlayerY) {
        playerX = newPlayerX;
        playerY = newPlayerY;
    }

    private void movement(TETile[][] world) {
        int enemyPos = convertTo1D(x, y, world.length);
        int playerPos = convertTo1D(playerX, playerY, world.length);
        List<Integer> path = dijkstraPath(enemyPos, playerPos);
        pathToPlayer.clear(); // Clear previous path
        for (int pos : path) {
            pathToPlayer.add(convertTo2D(pos, world.length));
        }
        if (toggle) {
            highlightPath(world);
        } else {
            unHighlight(world);
        }
        if (path.size() > 1) {
            int nextMove = path.get(1);  // First element is current position, second is next move
            int[] nextPos = convertTo2D(nextMove, world.length);

            world[x][y] = Tileset.FLOOR;  // Replace current position with floor
            x = nextPos[0];
            y = nextPos[1];
            world[x][y] = enemyTile;  // Move to next position
        }
    }

    private List<Integer> dijkstraPath(int start, int stop) {
        int[] edgeTo = new int[g.V()];
        int[] distTo = new int[g.V()];
        PriorityQueue<Integer> pq = new PriorityQueue<>(g.V(), new EdgeComparator(distTo));

        for (int i = 0; i < g.V(); i++) {
            distTo[i] = Integer.MAX_VALUE;
        }
        distTo[start] = 0;
        pq.add(start);

        while (!pq.isEmpty()) {
            int v = pq.poll();
            for (int w : g.adj(v)) {
                if (distTo[w] > distTo[v] + 1) {  // Assuming uniform weight of 1 for each edge
                    distTo[w] = distTo[v] + 1;
                    edgeTo[w] = v;
                    pq.add(w);
                }
            }
        }

        List<Integer> path = new ArrayList<>();
        if (distTo[stop] < Integer.MAX_VALUE) {
            for (int x = stop; x != start; x = edgeTo[x]) {
                path.add(x);
            }
            path.add(start);
            Collections.reverse(path);
        }

        return path;
    }

    private int convertTo1D(int x, int y, int width) {
        return x + y * width;
    }

    private int[] convertTo2D(int pos, int width) {
        int y = pos/ width;
        int x = pos % width;
        return new int[]{x, y};
    }

    public static class EdgeComparator implements Comparator<Integer> {
        private final int[] distTo;

        public EdgeComparator(int[] distTo) {
            this.distTo = distTo;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            return Integer.compare(distTo[o1], distTo[o2]);
        }
    }
}
