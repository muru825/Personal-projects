package core;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import utils.*;

public class LoadGameState {

        public static GameState loadGame(String filename) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String gameState = reader.readLine();
                if (gameState != null) {
                    String[] parts = gameState.split(",");
                    long seed = Long.parseLong(parts[0]);
                    int avatarX = Integer.parseInt(parts[1]);
                    int avatarY = Integer.parseInt(parts[2]);
                    return new GameState(seed, avatarX, avatarY);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

}
