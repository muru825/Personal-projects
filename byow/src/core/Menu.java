package core;


import edu.princeton.cs.algs4.StdDraw;
import java.awt.Color;
import java.awt.Font;
import java.util.Random;
import utils.*;

public class Menu {
    BSPMapGenerator mapGenerator;
    long seed;
    Random RANDOM;
    int WIDTH;
    int HEIGHT;
    boolean loading=false; // j inputed this
    static boolean quit = false;
    static final int PIXELS = 16;
    static final int BIG_FONT_SIZE = 30;
    static final int SMALL_FONT_SIZE = 20;
    static final int WAIT_TIME = 500;
    static final char HEART = '\u2764';
    boolean load = false;

    /*    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        World newWorld = new World();
        newWorld.ter.renderFrame(newWorld.world);
    }*/

    public Menu(BSPMapGenerator generator) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        mapGenerator = generator;
        WIDTH = BSPMapGenerator.MAP_WIDTH;
        HEIGHT = BSPMapGenerator.MAP_HEIGHT;

        StdDraw.setCanvasSize(WIDTH * PIXELS, HEIGHT * PIXELS);
        Font font = new Font("Monaco", Font.BOLD, BIG_FONT_SIZE);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public void drawBeginningFrame(String s) {
        /* Take the input string S and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, BIG_FONT_SIZE);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, s);

        /* If the game is not over, display encouragement, and let the user know if they
         * should be typing their answer or watching for the next round. */
        Font fontSmall = new Font("Monaco", Font.BOLD, SMALL_FONT_SIZE);
        StdDraw.setFont(fontSmall);
    }

    public void drawFrame(String s) {
        /* Take the input string S and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, BIG_FONT_SIZE);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, s);

        /* If the game is not over, display encouragement, and let the user know if they
         * should be typing their answer or watching for the next round. */
        StdDraw.show();
        //StdDraw.pause(WAIT_TIME);
    }

    public void startGame() {
        drawBeginningFrame("CS61B: THE GAME");
        StdDraw.setPenColor(Color.WHITE);
        Font fontSmall = new Font("Monaco", Font.BOLD, SMALL_FONT_SIZE);
        StdDraw.setFont(fontSmall);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 5, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 7, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 9, "Quit (Q)");
        StdDraw.show();
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

            // Optional: Draw the current input string on the canvas
            //StdDraw.clear(Color.BLACK);
            drawFrame(input.toString());
        }

        return input.toString();
    }

    public void menuOptions() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 'n' || key == 'N') {
                    StdDraw.clear(Color.BLACK);
                    drawFrame("Enter Seed: ");
                    StdDraw.pause(WAIT_TIME);
                    String str = getInputString();
                    mapGenerator.seed = Integer.parseInt(str);
                    seed = mapGenerator.seed;
                    mapGenerator.random = new Random(seed);
                    break;
                }
                if (key == 'Q' || key == 'q') {
                    System.exit(0);
                }
                if (key == 'L' || key == 'l') {
                    mapGenerator.loadWorld();
                    load = true;
                    break; // TBD
                }
            }
        }
    }

    public void HUD(int health, int level) {
        // Set the HUD's transparency level
        Color hudColor = new Color(0, 0, 0, 0.5f); // Black with 50% transparency
        StdDraw.setPenColor(hudColor);
        StdDraw.filledRectangle(WIDTH / 2.0, HEIGHT - 1, WIDTH / 2.0, 1);

        // Draw HUD text
        StdDraw.setPenColor(Color.WHITE);
        Font fontSmall = new Font("Monaco", Font.BOLD, SMALL_FONT_SIZE);
        StdDraw.setFont(fontSmall);

        // Draw hearts for health
        StringBuilder healthDisplay = new StringBuilder();
        for (int i = 0; i < health; i++) {
            healthDisplay.append(HEART).append(" ");
        }
        StdDraw.textLeft(1, HEIGHT - 1, "Health: " + healthDisplay.toString().trim());

        // Draw level information
        StdDraw.text(WIDTH / 2.0, HEIGHT - 1, "Level: " + level);
        mapGenerator.renderHUD();
        StdDraw.show();
    }

    public void LoadingWorld() {
        loading=true;
        mapGenerator.loadWorld();
    }


/*
    public void loadWorld(String filename) {
          // Set loading to true when loading the world


        String content = FileUtils.readFile(filename);
        if (content == null || content.isEmpty()) {
            System.exit(0);  // No previous save, quit the program.
        }
        String[] data = content.split(",");
        long seed = Long.parseLong(data[0]);
        int avatarX = Integer.parseInt(data[1]);
        int avatarY = Integer.parseInt(data[2]);
        // Add parsing for avatar if needed

        // Restore the map generator state // note move this into main instead
        mapGenerator.changeSeed(seed);
        mapGenerator.initWorld();
        mapGenerator.createWorld();
        mapGenerator.setAvatarPosition(avatarX, avatarY);
        // Add restoring avatar details if needed

        // Restore the world and avatar positions
        //while (true){
            //menu.HUD(5, 0);
            //generator.updateWorld();
            //ter.renderFrame(world);
        }
//        mapGenerator.ter.renderFrame(mapGenerator.world);
//        this.loading=false;
    //}*/
}


