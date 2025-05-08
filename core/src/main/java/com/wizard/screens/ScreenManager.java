package com.wizard.screens;

import com.badlogic.gdx.Gdx;
import com.wizard.Main;
import com.wizard.utils.AudioManager;

public class ScreenManager {
    private static Main game;
    private static GameScreen currentGame; 

    public static void initialize(Main gameInstance) {
        game = gameInstance;
    }

    public static void showMenu() {
        AudioManager.stopBackgroundMusic();
        AudioManager.playMenuMusic();
        AudioManager.stopWalkingSound();
        AudioManager.playButtonClickSound();
        game.setScreen(new MenuScreen(game));
    }

    public static void showGame() {
        if (currentGame == null) {
            currentGame = new GameScreen(game);
        }
        AudioManager.stopMenuMusic();
        AudioManager.playBackgroundMusic();
        AudioManager.playButtonClickSound();
        game.setScreen(new GameScreen(game));
    }

    public static void exit() {
        AudioManager.dispose();
        Gdx.app.exit();
    }

    public static void showStory() {
        game.setScreen(new StoryScreen(game));
    }

    public static void showGameOver() {
        AudioManager.stopBackgroundMusic();
        game.setScreen(new gameoverscreen(game));
    }

    public static void diedscreen() {
        AudioManager.stopBackgroundMusic();
        AudioManager.stopWalkingSound();
        game.setScreen(new diedscreen(game));
    }

    // pause screen
    public static void showPause() {
        game.setScreen(new pausescreen(game));
    }

    public static void resumeGame() {
        if (currentGame != null) {
            AudioManager.playBackgroundMusic();
            game.setScreen(currentGame);
        }
    }
}


