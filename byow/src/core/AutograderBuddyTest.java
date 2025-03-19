package core;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import tileengine.TETile;
import tileengine.Tileset;

public class AutograderBuddyTest {

    private static final int MAP_WIDTH = 80;
    private static final int MAP_HEIGHT = 50;


    @Test
    public void testNewWorldWithSeedAndMovement() {
        TETile[][] world = AutograderBuddy.getWorldFromInput("N543SWWWWAA");
        // Check the state of the world after the movements
        // Example: assert the avatar's final position
        assertTileAt(world, 6, 14, Tileset.AVATAR);
    }

    @Test
    public void testSaveAndLoadWorld() {
        TETile[][] world1 = AutograderBuddy.getWorldFromInput("N25SDDWD:Q");
        TETile[][] world2 = AutograderBuddy.getWorldFromInput("LDDDD");

        // Ensure the world state is as expected after loading and additional movements
        assertTileAt(world2, 30, 45, Tileset.AVATAR);
    }

    @Test
    public void testSaveAndLoadMultipleTimes() {
        TETile[][] world1 = AutograderBuddy.getWorldFromInput("N999SDDD:Q");
        TETile[][] world2 = AutograderBuddy.getWorldFromInput("LWWWDDD");
        TETile[][] world3 = AutograderBuddy.getWorldFromInput("N999SDDD:Q");
        TETile[][] world4 = AutograderBuddy.getWorldFromInput("LWWW:Q");
        TETile[][] world5 = AutograderBuddy.getWorldFromInput("LDDD:Q");

        // Ensure the world state is consistent after multiple saves and loads
        assertTileAt(world2, 27, 43, Tileset.AVATAR);
        assertTileAt(world4, 26, 42, Tileset.AVATAR);
        assertTileAt(world5, 25, 41, Tileset.AVATAR);
    }

    @Test
    public void testSaveAndLoadWithEmptyCommands() {
        TETile[][] world1 = AutograderBuddy.getWorldFromInput("N999SDDD:Q");
        TETile[][] world2 = AutograderBuddy.getWorldFromInput("L:Q");
        TETile[][] world3 = AutograderBuddy.getWorldFromInput("L:Q");
        TETile[][] world4 = AutograderBuddy.getWorldFromInput("LWWWDDD");

        // Ensure the world state is consistent after empty save and load commands
        assertTileAt(world4, 27, 43, Tileset.AVATAR);
    }

    private void assertTileAt(TETile[][] world, int x, int y, TETile expectedTile) {
        assertEquals(expectedTile.character(), world[x][y].character());
    }
}
