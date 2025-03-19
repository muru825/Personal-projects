package core;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class SoundPlayer {
    private AdvancedPlayer player;
    private Thread playerThread;
    private volatile boolean isPlaying; // Use volatile to ensure visibility across threads

    public void playBackgroundMusic(String filePath) {
        if (isPlaying) {
            stopMusic(); // Stop any currently playing music
        }

        isPlaying = true;
        playerThread = new Thread(() -> {
            try (FileInputStream fis = new FileInputStream(filePath);
                 BufferedInputStream bis = new BufferedInputStream(fis)) {
                player = new AdvancedPlayer(bis);

                // Play until isPlaying is set to false
                while (isPlaying) {
                    player.play(2048); // Play a chunk of frames
                }
            } catch (IOException | JavaLayerException e) {
                e.printStackTrace();
            } finally {
                // Ensure player is closed and resources are cleaned up
                if (player != null) {
                    player.close();
                }
            }
        });
        playerThread.start();
    }

    public void stopMusic() {
        isPlaying = false;
        if (playerThread != null && playerThread.isAlive()) {
            try {
                playerThread.join(); // Wait for the player thread to finish
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void playSoundEffect(String filePath) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath))) {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bis);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start(); // Play the sound effect
            // Optionally, you can wait for the clip to finish if needed
            clip.drain(); // Ensure the clip has completed playback
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
