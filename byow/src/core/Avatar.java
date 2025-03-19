package core;


import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;




import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;
import tileengine.TERenderer;


import java.util.*;


// citation: used chat gpt for user movement
public class Avatar {
    int health;
    int attackValue;
    TETile spawningPoint;


    public Avatar(int health, int attackValue, TETile spawningPoint) {
        this.health = health;
        this.attackValue = attackValue;
        this.spawningPoint = spawningPoint;
    }


    public int getHealth() {
        return health;
    }


    public TETile getAvatarTile() {
        return spawningPoint;
    }


    private boolean isInBounds(int x, int y, TETile[][] world) {
        return x >= 0 && x < world.length && y >= 0 && y < world[0].length;
    }


    public void attacking() {
        // Implement attacking logic here
    }


    public void spawningPoint() {
        // Implement spawning logic here
    }


    public static class Person extends Avatar {
        static int attackValue = 0;
        static int health = 10;
        static TETile avatar = Tileset.AVATAR;
        String song = "music\\boop.WAV ";


        public Person(int health, int attackValue, TETile spawningPoint) {
            super(health, attackValue, spawningPoint);
        }


        public int[] movingOptions(TETile[][] world, TERenderer ter, int avatarX, int avatarY, char key, SoundPlayer soundPlayer) {
           // while (true) {
                System.out.println(avatarX + " " + avatarY);
                int newX = avatarX;
                int newY = avatarY;
                //if (StdDraw.hasNextKeyTyped()) {
                    //char key = StdDraw.nextKeyTyped();
                    if (key == 'w' || key == 'W') {
                        newY += 1;
                    }
                    if (key == 's' || key == 'S') {
                        newY -= 1;
                    }
                    if (key == 'a' || key == 'A') {
                        newX -= 1;
                    }
                    if (key == 'd' || key == 'D') {
                        newX += 1;
                    }
                //}
                if (isInBounds(newX, newY, world) && world[newX][newY] != Tileset.WALL) {
                    world[avatarX][avatarY] = Tileset.FLOOR;
                    world[newX][newY] = avatar;
                    avatarX = newX;
                    avatarY = newY;
                    //ter.renderFrame(world);
                    System.out.println(avatarX + " " + avatarY);
                    soundPlayer.playSoundEffect(song);
                    return new int[]{avatarX, avatarY};
                }
            //}
            return new int[]{avatarX, avatarY};
        }


        private boolean isInBounds(int x, int y, TETile[][] world) {
            return x >= 0 && x < world.length && y >= 0 && y < world[0].length;
        }
    }
}