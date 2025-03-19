package core;

public class GameState {
        public long seed;
        public int avatarX;
        public int avatarY;

        public GameState(long seed, int avatarX, int avatarY) {
            this.seed = seed;
            this.avatarX = avatarX;
            this.avatarY = avatarY;
        }
}
