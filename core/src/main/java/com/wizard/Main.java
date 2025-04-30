
package com.wizard;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.wizard.screens.ScreenManager;

public class Main extends Game {
    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();

       
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
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}
