package core;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import utils.*;

public class SavingGameState {

        public static void saveGame(long seed, int avatarX, int avatarY, String filename) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                String gameState = seed + "," + avatarX + "," + avatarY;
                writer.write(gameState);
                System.out.println("Game state saved to " + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

}
