package core;

import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.In;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import utils.*;

// Add these imports at the top of the file
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;
import tileengine.TERenderer;

public class BSPMapGenerator {
    static final int MAP_WIDTH = 80;
    static final int MAP_HEIGHT = 50;
    static final int MIN_SIZE = 10;
    static long seed = System.currentTimeMillis();
    Random random;
    Graph g;
    TERenderer ter;
    static TETile[][] world;
    Avatar.Person avatar;
    int avatarX;
    int avatarY;
    //File CWD =
    private static final int MIN_DISTANCE_FROM_PLAYER = 5;
    private List<int[]> floorTiles = new ArrayList<>();
    List<Enemy> enemies = new ArrayList<>();
    char[] input = new char[2];
    String[] songs = new String[]{"music\\boop.WAV" ,"music\\Megelovania.mp3", "music\\WiiShopMusic.mp3",
            "music\\MiiChanel.mp3", "music\\Rick.mp3", "music\\Nyan.mp3"};
    SoundPlayer soundPlayer = new SoundPlayer();
    boolean togle = false;
    //Menu menu;


     // Add an avatar field

    public BSPMapGenerator(TETile[][] world) {
        this.world = world;
        random = new Random(seed);
        //initWorld();
    }

    public void setAvatarPosition(int x, int y) {
        this.avatarX = x;
        this.avatarY = y;
        this.avatar = new Avatar.Person(10, 0, Tileset.AVATAR); // Reinitialize avatar if needed
        world[avatarX][avatarY] = avatar.getAvatarTile();
    }

    public  long getSeed(){
        return seed;
    }

    public int getAvatarX() {
        return avatarX;
    }
    public  int getAvatarY() {
        return avatarY;
    }
    public Avatar.Person getAvatar() {
        return avatar;
    }

    class BSPNode {
        int x, y, width, height;
        BSPNode left, right;

        BSPNode(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.left = null;
            this.right = null;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }
    }

    class Room {
        int x, y, width, height;

        Room(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        boolean intersects(Room other) {
            return !(other.x + other.width <= x || other.x >= x + width
                    || other.y + other.height <= y || other.y >= y + height);
        }
    }

    class Hallway {
        List<int[]> path;

        Hallway() {
            path = new ArrayList<>();
        }

        void addTile(int x, int y) {
            path.add(new int[]{x, y});
        }
    }

    private List<Room> rooms = new ArrayList<>();
    private List<Hallway> hallways = new ArrayList<>();

    public void initWorld() {
        ter = new TERenderer();
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        ter.renderFrame(world);  // comment out for autograder
    }

    public void createWorld() {
        boolean validWorld = false;
        while (!validWorld) {
            BSPNode root = new BSPNode(0, 0, MAP_WIDTH, MAP_HEIGHT);
            split(root, MIN_SIZE);
            generateRoomsAndHallways(root);
            connectRooms();
            renderWorld();
            cleanBorder();
            if (!validGen()) {
                //System.out.println("Invalid World Generation");
                seed += 1;
                random = new Random(seed);
            }
            validWorld = validGen();
        }

        // Initialize the avatar at a specific position (e.g., first room's center)
        Room firstRoom = rooms.get(0);
        avatarX = firstRoom.x + firstRoom.width / 2;
        avatarY = firstRoom.y + firstRoom.height / 2;
        avatar = new Avatar.Person(10, 0, Tileset.AVATAR);
        world[avatarX][avatarY] = avatar.getAvatarTile();
        placeEnemies(1/*floorTiles.size() / 100*/);
        for (Enemy enemy: enemies) {
            enemy.startMoving(world, avatarX, avatarY);
        }
        String str = songs[random.nextInt(songs.length-1)];
        // str = songs[songs.length-1];
        soundPlayer.playBackgroundMusic(str);
        ter.renderFrame(world); // comment out for autograder

        // Update the world with the avatar movement
        //updateWorld(avatarX, avatarY);
    }

    public void updateWorld(Menu menu) {

        inputStr();


        for (Enemy enemy : enemies) {
            //menu.HUD(avatar.getHealth(), 0);
//            //menu.HUD(avatar.getHealth(), 0);
//            enemy.movement(world, avatarX, avatarY)
            enemy.updatePlayerPosition(avatarX, avatarY);
        }
        renderHUD();
        //printEnemies();
        quitAndSave();
        //renderHUD();
    }

    public void changeSeed(long i) {
        seed = i;
        random = new Random(i);
    }

    private void split(BSPNode node, int minSize) {
        if (node.width < minSize || node.height < minSize) {
            return;
        }
        boolean splitHorizontally = random.nextBoolean();
        int max = (splitHorizontally ? node.width : node.height) - minSize;
        if (max <= minSize) {
            return;
        }
        int splitPoint = minSize + random.nextInt(max - minSize);
        if (splitHorizontally) {
            node.left = new BSPNode(node.x, node.y, splitPoint, node.height);
            node.right = new BSPNode(node.x + splitPoint, node.y, node.width - splitPoint, node.height);
        } else {
            node.left = new BSPNode(node.x, node.y, node.width, splitPoint);
            node.right = new BSPNode(node.x, node.y + splitPoint, node.width, node.height - splitPoint);
        }

        split(node.left, minSize);
        split(node.right, minSize);
    }

    private void generateRoomsAndHallways(BSPNode node) {
        if (node == null) {
            return;
        }
        if (node.isLeaf()) {
            int roomWidth = Math.max(2, random.nextInt(node.width - 1));
            int roomHeight = Math.max(2, random.nextInt(node.height - 1));
            int roomX = node.x + random.nextInt(node.width - roomWidth);
            int roomY = node.y + random.nextInt(node.height - roomHeight);

            Room room = new Room(roomX, roomY, roomWidth, roomHeight);
            rooms.add(room);
        } else {
            generateRoomsAndHallways(node.left);
            generateRoomsAndHallways(node.right);
        }
    }

    private void connectRooms() {
        for (int i = 0; i < rooms.size() - 1; i++) {
            Room roomA = rooms.get(i);
            Room roomB = rooms.get(i + 1);
            int startX = roomA.x + random.nextInt(roomA.width);
            int startY = roomA.y + random.nextInt(roomA.height);
            int endX = roomB.x + random.nextInt(roomB.width);
            int endY = roomB.y + random.nextInt(roomB.height);
            createHallway(startX, startY, endX, endY);
        }
    }

    private void createHallway(int startX, int startY, int endX, int endY) {
        Hallway hallway = new Hallway();
        int x = startX, y = startY;

        while (x != endX || y != endY) {
            if (x != endX) {
                x += (endX > x) ? 1 : -1;
            } else if (y != endY) {
                y += (endY > y) ? 1 : -1;
            }
            hallway.addTile(x, y);
        }
        hallways.add(hallway);
    }

    private void renderWorld() {
        for (Room room : rooms) {
            for (int x = room.x; x < room.x + room.width; x++) {
                for (int y = room.y; y < room.y + room.height; y++) {
                    if (isInBounds(x, y, MAP_WIDTH, MAP_HEIGHT)) {
                        world[x][y] = Tileset.FLOOR;
                    }
                }
            }

            for (int x = Math.max(0, room.x - 1); x <= Math.min(MAP_WIDTH - 1, room.x + room.width); x++) {
                if (isInBounds(x, room.y - 1, MAP_WIDTH, MAP_HEIGHT)) {
                    world[x][room.y - 1] = Tileset.WALL;
                }
                if (isInBounds(x, room.y + room.height, MAP_WIDTH, MAP_HEIGHT)) {
                    world[x][room.y + room.height] = Tileset.WALL;
                }
            }
            for (int y = Math.max(0, room.y - 1); y <= Math.min(MAP_HEIGHT - 1, room.y + room.height); y++) {
                if (isInBounds(room.x - 1, y, MAP_WIDTH, MAP_HEIGHT)) {
                    world[room.x - 1][y] = Tileset.WALL;
                }
                if (isInBounds(room.x + room.width, y, MAP_WIDTH, MAP_HEIGHT)) {
                    world[room.x + room.width][y] = Tileset.WALL;
                }
            }
        }

        for (Hallway hallway : hallways) {
            for (int[] tile : hallway.path) {
                int x = tile[0];
                int y = tile[1];
                if (isInBounds(x, y, MAP_WIDTH, MAP_HEIGHT)) {
                    world[x][y] = Tileset.FLOOR;
                    if (isInBounds(x - 1, y, MAP_WIDTH, MAP_HEIGHT) && world[x - 1][y] == Tileset.NOTHING) {
                        world[x - 1][y] = Tileset.WALL;
                    }
                    if (isInBounds(x + 1, y, MAP_WIDTH, MAP_HEIGHT) && world[x + 1][y] == Tileset.NOTHING) {
                        world[x + 1][y] = Tileset.WALL;
                    }
                    if (isInBounds(x, y - 1, MAP_WIDTH, MAP_HEIGHT) && world[x][y - 1] == Tileset.NOTHING) {
                        world[x][y - 1] = Tileset.WALL;
                    }
                    if (isInBounds(x, y + 1, MAP_WIDTH, MAP_HEIGHT) && world[x][y + 1] == Tileset.NOTHING) {
                        world[x][y + 1] = Tileset.WALL;
                    }
                }
            }
        }
        floorTiles.clear();
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                if (world[x][y] == Tileset.FLOOR) {
                    floorTiles.add(new int[]{x, y});
                }
            }
        }
    }

    private boolean isInBounds(int x, int y, int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private void cleanBorder() {
        for (int i = 0; i < MAP_WIDTH; i++) {
            for (int j = 0; j < MAP_HEIGHT; j++) {
                if ((i == 0 || i == MAP_WIDTH - 1 || j == 0 || j == MAP_HEIGHT - 1)
                        && world[i][j].equals(Tileset.FLOOR)) {
                    world[i][j] = Tileset.WALL;
                }
            }
        }
    }

    public boolean validGen() {
        int mapWidth = world.length;
        int mapHeight = world[0].length;
        g = new Graph(mapWidth * mapHeight);
        genGraph();
        return isConnected();
    }

    // Generate graph from the world map
    private void genGraph() {
        int mapWidth = world.length;
        int mapHeight = world[0].length;
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                if (world[x][y].equals(Tileset.FLOOR)) {
                    int vertex = y * mapWidth + x;
                    // Check right neighbor
                    if (x + 1 < mapWidth && world[x + 1][y].equals(Tileset.FLOOR)) {
                        g.addEdge(vertex, y * mapWidth + (x + 1));
                    }
                    // Check top neighbor
                    if (y + 1 < mapHeight && world[x][y + 1].equals(Tileset.FLOOR)) {
                        g.addEdge(vertex, (y + 1) * mapWidth + x);
                    }
                }
            }
        }
    }

    // Check if all floor tiles are connected
    private boolean isConnected() {
        int totalVertices = 0;
        int startVertex = -1;
        int mapWidth = world.length;
        int mapHeight = world[0].length;

        // Count total floor tiles and find a starting floor tile for DFS
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                if (world[x][y].equals(Tileset.FLOOR)) {
                    totalVertices++;
                    if (startVertex == -1) {
                        startVertex = y * mapWidth + x;
                    }
                }
            }
        }

        // If no floor tiles, the world is trivially connected
        if (totalVertices == 0) {
            return true;
        }

        boolean[] visited = new boolean[g.V()];
        int count = dfs(startVertex, visited);
        return count == totalVertices; // The map is fully connected if all floor tiles are reachable
    }

    // Depth-First Search to count reachable vertices
    private int dfs(int v, boolean[] visited) {
        visited[v] = true;
        int count = 1; // Count the current vertex
        for (int w : g.adj(v)) {
            if (!visited[w]) {
                count += dfs(w, visited); // Count all reachable vertices from w
            }
        }
        return count;
    }

    public void placeEnemies(int numberOfEnemies) {
        List<int[]> potentialPositions = new ArrayList<>();
        for (int i = 0; i < numberOfEnemies; i++) {
            int enemyX, enemyY;
            do {
                enemyX = random.nextInt(MAP_WIDTH);
                enemyY = random.nextInt(MAP_HEIGHT);
            } while (!isInBounds(enemyX, enemyY, MAP_WIDTH, MAP_HEIGHT) ||
                    world[enemyX][enemyY] != Tileset.FLOOR ||
                    distance(avatarX, avatarY, enemyX, enemyY) < MIN_DISTANCE_FROM_PLAYER);

            Enemy enemy = new Enemy(5, 1,  g, random);
            enemies.add(enemy);
            world[enemyX][enemyY] = Tileset.TREE;
            enemy.x = enemyX;
            enemy.y = enemyY;
        }
    }

    private int distance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2); // Manhattan distance
    }

/*    public void saveWorld(String filename) {
        File file = new File(filename);

        StringBuilder data = new StringBuilder();

        // Save the dimensions of the world
        data.append(MAP_WIDTH).append(" ").append(MAP_HEIGHT).append("\n");

        // Save the world tiles
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                data.append(world[x][y].character());
            }
            data.append("\n");
        }

        // Save the avatar's position and details
        data.append(avatarX).append(" ").append(avatarY).append("\n");
        data.append(avatar.getHealth()).append(" ").append(avatar.attackValue).append("\n");

        // Write the data to the file
        utils.writeContents(file, data.toString());
    }

    public void loadWorld(String filename) {
        File file = new File(filename);
        String content = UtilsFiles.readContentsAsString(file);
        String[] lines = content.split("\n");

        // Read the dimensions of the world
        String[] dimensions = lines[0].split(" ");
        int width = Integer.parseInt(dimensions[0]);
        int height = Integer.parseInt(dimensions[1]);

        // Initialize the world array with the read dimensions
        world = new TETile[width][height];

        // Read the world tiles
        for (int y = 0; y < height; y++) {
            String line = lines[y + 1];
            for (int x = 0; x < width; x++) {
                world[x][y] = charToTile(line.charAt(x));
            }
        }

        // Read the avatar's position
        String[] avatarPosition = lines[height + 1].split(" ");
        avatarX = Integer.parseInt(avatarPosition[0]);
        avatarY = Integer.parseInt(avatarPosition[1]);

        // Read the avatar's health and attack value
        String[] avatarDetails = lines[height + 2].split(" ");
        int health = Integer.parseInt(avatarDetails[0]);
        int attackValue = Integer.parseInt(avatarDetails[1]);

        // Initialize the avatar with the loaded details
        avatar = new Avatar.Person(health, attackValue, Tileset.AVATAR);
        world[avatarX][avatarY] = avatar.getAvatarTile();
    }*/

    private TETile charToTile(char c) {
        // Map characters to TETile objects
        switch (c) {
            case '.':
                return Tileset.FLOOR;
            case '#':
                return Tileset.WALL;
            case ' ':
                return Tileset.NOTHING;
            case '@':
                return Tileset.AVATAR;
            // Add more cases as needed for other tiles
            default:
                throw new IllegalArgumentException("Unknown tile character: " + c);
        }
    }
    public String getInputString() {
        StringBuilder input = new StringBuilder();
        boolean enterPressed = false;
        while (!enterPressed) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == '\n' || key == 's') { //Check if the Enter (newline) is pressed; Remove key == S
                    enterPressed = true;
                } else if (key == '\b') { // Handle backspace
                    if (input.length() > 0) {
                        input.deleteCharAt(input.length() - 1);
                    }
                } else {
                    input.append(key);
                }
            }
        }
        return input.toString();
    }

    public void inputStr() {
        if (StdDraw.hasNextKeyTyped()) {

            char key = StdDraw.nextKeyTyped();
            input[0] = input[1];
            //System.out.println(key);
            input[1] = key;
            int[] arr = avatar.movingOptions(world, ter, avatarX, avatarY, input[1], soundPlayer);

            avatarX = arr[0];
            avatarY = arr[1];
            if ((input[1] == 'l' || input[1] == 'L')) {
                for (Enemy enemy: enemies) {
                    enemy.toggle = !enemy.toggle;
                }
            }
                /*if (togle) {
                    for (Enemy enemy: enemies) {
                        enemy.highlightPath(world);
                    }
                } else {
                    for (Enemy enemy: enemies) {
                        enemy.unHighlight(world);
                    }
                }*/
        }

    }

    public void quitAndSave() {
/*        StringBuilder quitAndSave; // made menu get input string status
        while ((StdDraw.hasNextKeyTyped() && StdDraw.nextKeyTyped() == ':')) {
            System.out.println(":");
            if(StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                System.out.println(key);
                if (StdDraw.hasNextKeyTyped() && (key == 'q' || key == 'Q')) {
                    savinWorld();
                    Menu.quit = true;
                    System.out.println(';');
                }
            }
        }*/
        if (input[0] ==':' && (input[1] == 'q' || input[1] == 'Q')) {
            //System.out.println("save");
            savinWorld();
            Menu.quit = true;
        }
    }


    // method to split the string on the position based on the spaces;
    public void savinWorld(){ // { seed, avatarX, avatarY, avatar }
        String contents= new String (  seed + "," + Integer.toString(avatarX) + "," +
                Integer.toString(avatarY) /*+ "," + Integer.toString(avatar.getHealth()*/
                +   '\n' );
        for (Enemy enemy: enemies) {
            contents += Integer.toString(enemy.x) + ',' + Integer.toString(enemy.y)
                    + /*enemy.getHealth() +*/ '\n';
        }
        FileUtils.writeFile("world.txt", contents);
    }

    public void printEnemies() {
        for (Enemy enemy: enemies) {
            System.out.println("X: "+ enemy.x + "; Y:" + enemy.y + '\n');
        }
    }

    public void loadWorld() {
        try (
        BufferedReader reader = new BufferedReader(new FileReader("world.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                String[] parts = line.split(",");
                seed = Long.parseLong(parts[0]);
                random = new Random(seed);
                boolean validWorld = false;
                while (!validWorld) {
                    BSPNode root = new BSPNode(0, 0, MAP_WIDTH, MAP_HEIGHT);
                    split(root, MIN_SIZE);
                    generateRoomsAndHallways(root);
                    connectRooms();
                    renderWorld();
                    cleanBorder();
                    if (!validGen()) {
                        //System.out.println("Invalid World Generation");
                        seed += 1;
                        random = new Random(seed);
                    }
                    validWorld = validGen();
                }
                avatarX = Integer.parseInt(parts[1]);
                avatarY = Integer.parseInt(parts[2]);
                //int avatarHealth = Integer.parseInt(parts[3]);
                avatar = new Avatar.Person(1, 0, Tileset.AVATAR);

                // Initialize Random with the seed
                //random = new Random(seed);

                // Load enemies
                enemies = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    parts = line.split(",");
                    int enemyX = Integer.parseInt(parts[0]);
                    int enemyY = Integer.parseInt(parts[1]);
                    //int enemyHealth = Integer.parseInt(parts[2]);
                    Enemy enemy = new Enemy(5, 1, g, random);

                    world[enemyX][enemyY] = Tileset.TREE;
                    enemy.x = enemyX;
                    enemy.y = enemyY;
                    enemies.add(enemy);
                }

                // Set the avatar and enemies in the world
                world[avatarX][avatarY] = avatar.getAvatarTile();
                for (Enemy enemy : enemies) {
                    world[enemy.x][enemy.y] = Tileset.TREE;
                    enemy.startMoving(world, avatarX, avatarY);
                }
                // Render the loaded world
                String str = songs[random.nextInt(songs.length-1)];
                // str = songs[songs.length-1];
                soundPlayer.playBackgroundMusic(str);
                ter.renderFrame(world);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void renderHUD() {
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();

        if (isInBounds(mouseX, mouseY, MAP_WIDTH, MAP_HEIGHT)) {
            TETile tile = world[mouseX][mouseY];
            String description = getTileDescription(tile);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.textLeft(MAP_WIDTH-10, MAP_HEIGHT - 1, "Tile: " + description);
            StdDraw.show();
        }
    }

    // Get description of the tile
    private String getTileDescription(TETile tile) {
        if (tile == Tileset.WALL) {
            return "wall";
        } else if (tile == Tileset.FLOOR) {
            return "floor";
        } else if (tile == Tileset.AVATAR) {
            return "avatar";
        } else {
            return "nothing";
        }
    }
}
