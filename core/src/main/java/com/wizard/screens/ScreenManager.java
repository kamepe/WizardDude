/* This manages all screens in game can be expanded for pause button and stuff currently handels only play and exit butoon. */
package com.wizard.screens;

import com.badlogic.gdx.Gdx;
import com.wizard.Main;

public class ScreenManager {
    private static Main game;

    
    public static void initialize(Main gameInstance) {
        game = gameInstance;
    }

  
    public static void showMenu() {
        game.setScreen(new MenuScreen(game));
    }

   
    public static void showGame() {
        game.setScreen(new GameScreen(game));
    }

    
    public static void exit() {
        Gdx.app.exit();
    }

    public static void showStory() {
        game.setScreen(new StoryScreen(game));
      }
}
