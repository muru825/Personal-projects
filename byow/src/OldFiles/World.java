package OldFiles;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.ArrayList;
import java.util.Random;

public class World {

    public static final int HEIGHT = 30;
    static final Random RANDOM = new Random(2);
    static long seed = System.currentTimeMillis();
    public final int WIDTH = 60;
    TERenderer ter;
    TETile[][] world;

    // build your own world!
    public World() {
        ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        world = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                //world[i][j] = TileRandomizer(); // Vivian: I think we should make it all black; Tileset.NOTHING;
                world[i][j] = Tileset.NOTHING;// Vivian: I think we should make it all black; Tileset.NOTHING;
            }
        }
        generateRooms();
        singleWalls();
    }

    public TETile TileRandomizer() {
        int tileSelect = RANDOM.nextInt(12);
        switch (tileSelect) {
            case 0:
                return Tileset.AVATAR;
            case 1:
                return Tileset.CELL;
            case 2:
                return Tileset.FLOOR;
            case 3:
                return Tileset.FLOWER;
            case 4:
                return Tileset.GRASS;
            case 5:
                return Tileset.LOCKED_DOOR;
            case 6:
                return Tileset.MOUNTAIN;
            case 7:
                return Tileset.NOTHING;
            case 8:
                return Tileset.SAND;
            case 9:
                return Tileset.TREE;
            case 10:
                return Tileset.UNLOCKED_DOOR;
            case 11:
                return Tileset.WALL;
            case 12:
                return Tileset.WATER;
        }
        return Tileset.NOTHING;
    }

    private void generateRooms() {
        int roomQuantity = RANDOM.nextInt(10) + 5; // Generate between 5 and 10 rooms
        int hallwayQuantity = RANDOM.nextInt(8) + 7;
        Rectangle[] rooms = new Rectangle[roomQuantity];

        for (int i = 0; i < roomQuantity; i++) {
            Room room = new Room();
            rooms[i] = room;
            room.createRoom();
            for (int j : room.roomCenterPoint) {
                System.out.println(j);
            }
            singleWalls();
        }
//        Hallway h = new Hallway();
//        h.placementGenerator();
//        singleWalls();
//        Hallway[] hallways = new Hallway[hallwayQuantity];
//        for (int i = 0; i < hallwayQuantity; i++) {
//            Hallway hallway = new Hallway();
//            hallways[i] = hallway;
//            hallway.placementGenerator();
//            singleWalls();
        // }
    }

    public void singleWalls() {
        for (int x = 1; x < WIDTH - 1; x++) {
            for (int y = 1; y < HEIGHT - 1; y++) {
                if (world[x][y] == Tileset.WALL) {
                    // Check adjacent tiles to determine if it should be a floor;
                    TETile curr = world[x][y];
                    TETile left = world[x - 1][y];
                    TETile right = world[x + 1][y];
                    TETile up = world[x][y - 1];
                    TETile down = world[x][y + 1];
                    // checks if the right side is a wall
                    if ((left == Tileset.WALL && right == Tileset.FLOOR && up == Tileset.WALL && down == Tileset.WALL)) {
                        world[x][y] = Tileset.FLOOR;
                        break;
                    }
                    // checks if the left side is a wall
                    if ((left == Tileset.FLOOR && right == Tileset.WALL && up == Tileset.WALL && down == Tileset.WALL)) {
                        world[x][y] = Tileset.FLOOR;
                        break;
                    }
                    if ((left == Tileset.FLOOR && right == Tileset.FLOOR && up == Tileset.WALL && down == Tileset.WALL)) {
                        world[x][y] = Tileset.FLOOR;
                        break;
                    }
                    if ((left == Tileset.WALL && right == Tileset.WALL && up == Tileset.FLOOR && down == Tileset.FLOOR)) {
                        world[x][y] = Tileset.FLOOR;
                        break;
                    }
                    if ((left == Tileset.FLOOR && right == Tileset.FLOOR && up == Tileset.FLOOR && down == Tileset.FLOOR)) {
                        world[x][y] = Tileset.FLOOR;
                        break;
                    }
                    if ((left == Tileset.FLOOR && right == Tileset.WALL && up == Tileset.FLOOR && down == Tileset.FLOOR)) {
                        world[x][y] = Tileset.FLOOR;
                        break;
                    }
                    if ((left == Tileset.FLOOR && right == Tileset.FLOOR && up == Tileset.WALL && down == Tileset.FLOOR)) {
                        world[x][y] = Tileset.FLOOR;
                        break;
                    }
                    if ((left == Tileset.WALL && right == Tileset.WALL && up == Tileset.WALL && down == Tileset.FLOOR)) {
                        world[x][y] = Tileset.FLOOR;
                        break;
                    }
                    if ((left == Tileset.FLOOR && right == Tileset.WALL && up == Tileset.WALL && down == Tileset.FLOOR)) {
                        world[x][y] = Tileset.FLOOR;
                        break;
                    }
                    if ((world[x + 1][y + 1] == Tileset.WALL) && world[x + 1][y + 2] == Tileset.WALL && right == Tileset.FLOOR && up == Tileset.FLOOR && left == Tileset.FLOOR && down == Tileset.FLOOR && world[x - 1][y - 1] == Tileset.WALL) {
                        world[x + 1][y + 1] = Tileset.FLOOR;
                        break;
                    }
                    if (right == Tileset.FLOOR && left == Tileset.FLOOR && up == Tileset.FLOOR && down == Tileset.FLOOR) {
                        curr = Tileset.FLOOR;
                        break;
                    }
                    if (right == Tileset.FLOOR && left == Tileset.FLOOR && up == Tileset.WALL) {
                        curr = Tileset.FLOOR;
                        break;
                    }
                }
            }
        }
    } // needs debugging; currently cannot identify a strip; maybe in rectangle class we design it strip by strip for the sides


    private void collsionDetect(int width, int height, int xBound, int yBound, int x, int y) {
        if (x == xBound || x == xBound + width - 1 || y == yBound || y == yBound + height - 1) { // creates the straight lines
            if (world[x][y] == Tileset.NOTHING) {
                world[x][y] = Tileset.WALL;
            }
        } else {
            world[x][y] = Tileset.FLOOR;
        }
    }

    public void generateHallway(int x, int y) {

    }

//    public void createHallway(ArrayList<int[]> roomCenter){
//        WeightedQuickUnionUF <int[]> room= new
//        for (int [] i: Room.roomCenter){
//
//        }
//    }

    public class Rectangle {
        int width;
        int height;
        int xBound; // makes sure that the WIDTH of the rectangle will not go out of bounds in the world
        int yBound;// makes sure that the HEIGHT rectangle will not go out of bounds in the world

        public Rectangle() {
            width = RANDOM.nextInt(5) + 6; // Min width---> 5, Max height --> 5
            height = RANDOM.nextInt(5) + 6;
            xBound = RANDOM.nextInt(WIDTH - width - 1) + 1;
            yBound = RANDOM.nextInt(HEIGHT - height - 1) + 1;
        }

        public void createRoom() {
            for (int x = xBound; x < xBound + width; x++) {
                for (int y = yBound; y < yBound + height; y++) {
                    if (x == xBound || x == xBound + width - 1 || y == yBound || y == yBound + height - 1) { // creates the straight lines
                        if (world[x][y] == Tileset.NOTHING) {
                            world[x][y] = Tileset.WALL;
                        }
                    } else {
                        world[x][y] = Tileset.FLOOR;
                    }
                }
            }
        }
    }

    public class Room extends Rectangle {
        static ArrayList<int[]> roomCenter = new ArrayList<>();
        int width;
        int height;
        int xBound; // STARTING FOR WIDTH TOP RIGHT --> world[xbound+width][yBound]
        int yBound; // STARTING FOR HEIGHT --> // world[xbound][yBound+height]
        int[] roomCenterPoint;

        public Room() {
            width = RANDOM.nextInt(3) + 5; // Min width---> 5, Max height --> 5
            height = RANDOM.nextInt(3) + 5;
            xBound = RANDOM.nextInt(WIDTH - width - 1) + 1;
            yBound = RANDOM.nextInt(HEIGHT - height - 1) + 1;
            roomCenterPoint = new int[2];
            roomCenterPoint[0] = xBound + (width / 2);
            roomCenterPoint[1] = yBound + (height / 2);
            roomCenter.add(roomCenterPoint);
        }

        public void createRoom() {
            for (int x = xBound; x < xBound + width; x++) {
                for (int y = yBound; y < yBound + height; y++) {
                    if (x == xBound || x == xBound + width - 1 || y == yBound || y == yBound + height - 1) { // creates the straight lines
//                            if ((x==xBound && y==yBound) || x==xBound+width-1 && y==yBound+height-1 || x==xBound+width-1 && y==yBound+height-1 || (x==xBound && y==yBound+height-1)) {
//                                world[x][y]= Tileset.NOTHING;
//                            }
                        if (world[x][y] == Tileset.NOTHING) {
                            world[x][y] = Tileset.WALL;
                        }
                    } else {
                        world[x][y] = Tileset.FLOOR;
                    }
                }
            }
            world[xBound + (width / 2)][yBound + (height / 2)] = Tileset.SAND;
        }
    }

//

    public class Hallway extends Rectangle {

        int length;

        public Hallway() {
            if (RANDOM.nextInt(2) == 1) {
                width = RANDOM.nextInt(15) + 5;
                height = RANDOM.nextInt(2) + 3;
            } else {
                height = RANDOM.nextInt(15) + 5;
                width = RANDOM.nextInt(2) + 3;
            }
            length = height;
            xBound = RANDOM.nextInt(WIDTH - width);
            yBound = RANDOM.nextInt(HEIGHT - length);
        }

        //@Override
        public void createRoom(int x, int y) { // i had to null this out for some reason because it didnt let us override
            for (int i = x; i < x + width; i++) { // error: java: method does not override or implement a method from a supertype
                for (int j = y; j < y + length; j++) {
                    if ((x == i || i == x + width - 1)) {
                        world[i][j] = Tileset.WALL;
                    } else if (j == y || j == y + length - 1) {
                        world[i][j] = Tileset.WALL;
                    } else {
                        world[i][j] = Tileset.FLOOR;
                    }
                }
            }
        }


        // to place the hallway, we should specifically generate a vertical hallway and a horizontal hallway!!  -->  put the center points as
        // starting points and then we want to make it mandatory to connect each point, we can set it randomly for CONNECTING, but keep the condition
        // such that when centerpoint(x,y) --> goalcenterpoint (z,t); if x and z are the same then insert a vertical hallway at that point!!!
        public void placeHalway(int x, int y) {
            for (int i = x; i < x + width && i < WIDTH; i++) { // Check that i is within bounds
                for (int j = y; j < y + height && j < HEIGHT; j++) { // Check that j is within bounds
                    if (i >= 0 && j >= 0 && i < WIDTH && j < HEIGHT) { // Ensure indices are within valid range
                        if (x == i || i == x + width - 1) {
                            world[i][j] = Tileset.WALL;
                        } else if (j == y || j == y + length - 1) {
                            world[i][j] = Tileset.WALL;
                        } else {
                            world[i][j] = Tileset.FLOOR;
                        }
                    }
                }
            }
        }


        public void placementGenerator() {
            ArrayList<int[][]> lst = new ArrayList<>();
            int counter = 0;
            for (int i = 0; i < WIDTH; i++) {
                for (int j = 0; j < HEIGHT; j++) {
                    if (i + 4 < WIDTH &&
                            world[i][j].equals(Tileset.WALL) && world[i + 1][j].equals(Tileset.WALL) && world[i + 2][j].equals(Tileset.WALL) &&
                            world[i + 3][j].equals(Tileset.WALL) && world[i + 4][j].equals(Tileset.WALL)) { // checks if there is a valid wall running
                        if (findRoomAbove(i, j)) {
                            placeHalway(i, j);
                            ter.renderFrame(world);
                        }
                    } else if (world[i][j].equals(Tileset.WALL) && j + 4 < HEIGHT
                            && world[i][j + 1].equals(Tileset.WALL) && world[i][j + 2].equals(Tileset.WALL) &&
                            world[i][j + 3].equals(Tileset.WALL) && world[i][j + 4].equals(Tileset.WALL)) { // checks if there is a valid wall runnig across the y axis
                        if (findRoomAcross(i, j)) {
                            placeHalway(i, j);
                            ter.renderFrame(world);
                        }
                    }
                }
            }
        }

        public boolean findRoomAcross(int x, int y) {
            try {
                int z = y;
                while (world[x][z].equals(Tileset.NOTHING)) {
                    z++;
                }
                if (world[x][z].equals(Tileset.WALL) && world[x][z + 1].equals(Tileset.WALL) && world[x][z + 2].equals(Tileset.WALL) &&
                        world[x][z + 3].equals(Tileset.WALL) && world[x][z + 4].equals(Tileset.WALL)) {
                    width = z - y - 1;
                    return true;
                }
            } catch (NullPointerException e) {
                return false;
            }
            return false;
        }

        public boolean findRoomAbove(int x, int y) {
            try {
                int z = x;
                while (world[z][y].equals(Tileset.NOTHING)) {
                    z++;
                }
                if (world[z][y].equals(Tileset.WALL) && world[z + 1][y].equals(Tileset.WALL) && world[z + 2][y].equals(Tileset.WALL) &&
                        world[z + 3][y].equals(Tileset.WALL) && world[z + 4][y].equals(Tileset.WALL)) {
                    width = z - y - 1;
                    return true;
                }
            } catch (NullPointerException e) {
                return false;
            }
            return false;
        }
    }
}


