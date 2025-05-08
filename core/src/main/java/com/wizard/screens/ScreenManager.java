
package com.wizard.screens;

import com.badlogic.gdx.Gdx;
import com.wizard.Main;
import com.wizard.utils.AudioManager;


public class ScreenManager {
    private static Main game;

    public static void initialize(Main gameInstance) {
        game = gameInstance;
    }

    public static void showMenu() {
        // Stop game music and play menu music
        AudioManager.stopBackgroundMusic();
        AudioManager.playMenuMusic();
        // Stop walking sound when returning to menu
        AudioManager.stopWalkingSound();
        // Play button click sound when transitioning to menu
        AudioManager.playButtonClickSound();
        game.setScreen(new MenuScreen(game));
    }

    public static void showGame() {
        // Stop menu music and play game music
        AudioManager.stopMenuMusic();
        AudioManager.playBackgroundMusic();
        // Play button click sound when starting the game
        AudioManager.playButtonClickSound();
        game.setScreen(new GameScreen(game));
    }

    public static void exit() {
        AudioManager.dispose();
        Gdx.app.exit();
    }
    // storyline
    public static void showStory() {
        game.setScreen(new StoryScreen(game));
      }
}
