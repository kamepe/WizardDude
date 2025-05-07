
package com.wizard;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.wizard.screens.ScreenManager;
import com.wizard.utils.AudioManager;

public class Main extends Game {
    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Initialize all audio resources
        AudioManager.initBackgroundMusic("audio/bgmusic.mp3");
        AudioManager.initMenuMusic("audio/menumusic.mp3");
        AudioManager.initSoundEffects();

        ScreenManager.initialize(this);
        ScreenManager.showMenu();
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (getScreen() != null) getScreen().dispose();
        AudioManager.dispose();
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}
