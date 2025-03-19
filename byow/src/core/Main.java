package core;


import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;


import java.awt.*;

import static core.BSPMapGenerator.*;


public class Main {
    public static void main(String[] args) {

        // build your own world!
        TERenderer ter = new TERenderer();
        ter.initialize(MAP_WIDTH, MAP_HEIGHT);
        TETile[][] world = new TETile[MAP_WIDTH][MAP_HEIGHT];
        BSPMapGenerator generator = new BSPMapGenerator(world);
        Menu menu = new Menu(generator);
        generator.initWorld();
        menu.startGame();
        menu.menuOptions();
        if (!menu.load) {
            generator.createWorld();
        }
        while (!menu.quit) {
            //System.out.println("AAHH");
            StdDraw.clear(Color.BLACK);
            ter.drawTiles(world);
            menu.HUD(generator.avatar.getHealth(), 0);
            generator.updateWorld(menu);
        }
        System.exit(0);
    }
}

/** public static void main(String[] args) {

            // build your own world!
            TERenderer ter = new TERenderer();
            ter.initialize(MAP_WIDTH, MAP_HEIGHT);
            TETile[][] world = new TETile[MAP_WIDTH][MAP_HEIGHT];
            BSPMapGenerator generator = new BSPMapGenerator(world);
            Menu menu = new Menu(generator);
            generator.initWorld();
            menu.startGame();
            menu.menuOptions();
            System.out.println("Starting gameez...");

            String loadIt = menu.getInputString();
            System.out.println("Input received: " + loadIt);

            if (loadIt.equalsIgnoreCase("L")) {  // Corrected string comparison
                System.out.println("Loading game state...");
                GameState gameState = LoadGameState.loadGame("world.txt");
                if (gameState != null) {
                    System.out.println("Game state loaded: " + gameState.seed + ", " + gameState.avatarX + ", " + gameState.avatarY);
                    generator.changeSeed(gameState.seed);
                    generator.initWorld();
                    generator.createWorld();
                    generator.setAvatarPosition(gameState.avatarX, gameState.avatarY);
                    // Render the loaded world
                } else {
                    System.out.println("Failed to load game state.");
                    return;
                }
            } else {
                    System.out.println("Starting a new game...");
                    generator.createWorld();
                      // Render the new world
                }

                System.out.println("Starting a new game...");

                  // Render the new world

                while(true) {
                    String quitAndSave = "";
                    if (StdDraw.hasNextKeyTyped()) {
                        quitAndSave = menu.getInputString();
                        if (quitAndSave.equals(":Q") || quitAndSave.equals(":q")) {
                            System.out.println("Saving game state...");
                            SavingGameState.saveGame(generator.getSeed(), generator.getAvatarX(), generator.getAvatarY(), "world.txt");
                            System.exit(0);
                        }
                    }
                    menu.HUD(5, 0);
                    generator.updateWorld();
                    ter.renderFrame(world);
                }
            }
        } **/

        /**System.out.println("Starting game...");

        menu.startGame();  // Show initial menu
        String loadIt = menu.getInputString();

        System.out.println("Input received: " + loadIt);

        if (loadIt.equalsIgnoreCase("L")) {  // Corrected string comparison
            System.out.println("Loading game state...");
            GameState gameState = LoadGameState.loadGame("world.txt");
            if (gameState != null) {
                System.out.println("Game state loaded: " + gameState.seed + ", " + gameState.avatarX + ", " + gameState.avatarY);
                generator.changeSeed(gameState.seed);
                generator.initWorld();
                generator.createWorld();
                generator.setAvatarPosition(gameState.avatarX, gameState.avatarY);
                ter.renderFrame(world);  // Render the loaded world
            } else {
                System.out.println("Failed to load game state.");
            }
//            }
//        } else {
//            System.out.println("Starting a new game...");
//            menu.startGame();
//            menu.menuOptions();
//            generator.createWorld();  // Render the new world
//        }

            String quitAndSave = menu.getInputString();
            System.out.println("Game command: " + quitAndSave);
            while (true) {
                if (StdDraw.hasNextKeyTyped()) {
                    if (quitAndSave.equals(":Q") || quitAndSave.equals(":q")) {
                        System.out.println("Saving game state...");
                        SavingGameState.saveGame(generator.getSeed(), generator.getAvatarX(), generator.getAvatarY(), "world.txt");
                        System.exit(0);
                    }
                }

                    /**generator.initWorld();
                     menu.startGame();
                     menu.menuOptions();
                     generator.createWorld();

//        String quitAndSave = menu.getInputString();
//        if (StdDraw.hasNextKeyTyped()) {
//            if (quitAndSave.equals(":Q") || quitAndSave.equals(":q")) {
//                SavingGameState.saveGame(generator.getSeed(), generator.getAvatarX(), generator.getAvatarY(), "world.txt");
//                System.exit(0);
//            }

                    while (true) {
                        menu.HUD(5, 0);
                        generator.updateWorld();
                    }
                }
            }
        }
    }**/

