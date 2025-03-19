package core;

import org.junit.Test;
import tileengine.TETile;
import tileengine.Tileset;

import static core.BSPMapGenerator.*;

public class AutograderBuddy {

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    /***public static TETile[][] getWorldFromInput(String input) {

        // build your own world!
        //TERenderer ter = new TERenderer();
        //ter.initialize(MAP_WIDTH, MAP_HEIGHT);
        TETile[][] world = new TETile[MAP_WIDTH][MAP_HEIGHT];
        BSPMapGenerator generator = new BSPMapGenerator(world);
        //Menu menu = new Menu(generator);
        String str = extractNumber(input);
        System.out.println(str);
        generator.changeSeed(Long.parseLong(str));
        generator.initWorld();
        //menu.startGame();
        //menu.menuOptions();
        generator.createWorld();
        return world;
    }**/

    public static TETile[][] getWorldFromInput(String input) {

        TETile[][] world = new TETile[MAP_WIDTH][MAP_HEIGHT];
        BSPMapGenerator generator = new BSPMapGenerator(world);
        Menu menu = new Menu(generator);
        boolean loading = false;

        if (input.charAt(0) == 'N') {
            String seedString = extractNumber(input);
            generator.changeSeed(Long.parseLong(seedString));
            generator.initWorld();
            generator.createWorld();
        } else if (input.charAt(0) == 'L') {
            generator.loadWorld();
            loading = true;
        } // if they are loading the world

        int startIndex = input.indexOf('S') + 1;
        if (loading) {
            startIndex = 1;
        } // if its loading we dont care

        for (int i = startIndex; i < input.length(); i++) {
            char movement = input.charAt(i);
            if (movement == ':') {
                if (i + 1 < input.length() && (input.charAt(i + 1) == 'Q' || input.charAt(i + 1) == 'q')) {
                    generator.savinWorld();
                }
            } else if (movement == 'W' || movement== 'A' || movement == 'S' || movement == 'D') {
                generator.updateWorld(menu);
            }
        }
        return world;
    }


    public static String extractNumber(String input) {
        StringBuilder number = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                number.append(c);
            }
        }

        return number.toString();
    }
    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character()
                || t.character() == Tileset.AVATAR.character()
                || t.character() == Tileset.FLOWER.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character();
    }

    @Test
    public void GWorldFromInputTest() {
        TETile[][] test= getWorldFromInput("N999SDDDWWWDDD");
        TETile[][] test1= getWorldFromInput("N999SDDD:Q");
        TETile[][] test2= getWorldFromInput("LWWWDDD");
        TETile[][] test3= getWorldFromInput("N999SDDD:Q");
        TETile[][] test4= getWorldFromInput("LWWW:Q");
        TETile[][] test5= getWorldFromInput("LDDD:Q");
        TETile[][] test6= getWorldFromInput("N999SDDD:Q");
        TETile[][] test7= getWorldFromInput("L:Q");
        TETile[][] test8= getWorldFromInput("L:Q");
        TETile[][] test9= getWorldFromInput("LWWWDDD");
    }
}
