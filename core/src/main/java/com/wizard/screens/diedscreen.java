package com.wizard.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.wizard.Main;
import com.wizard.utils.AudioManager;

public class diedscreen extends ScreenAdapter {
    private final SpriteBatch batch;
    private final OrthographicCamera cam;
    private final Texture background;
    private float alpha = 0f;               // current opacity
    private static final float FADE_DURATION = 2f; // seconds to fully fade in

    public diedscreen(Main game) {
        AudioManager.initSoundEffects();
        batch = game.getBatch();
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        background = new Texture(Gdx.files.internal("youdied.png"));

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (alpha >= 1f) { // only accept input after fade in completes
                    if (keycode == Input.Keys.R) {
                        AudioManager.playButtonClickSound();
                        ScreenManager.showGame();
                    } else if (keycode == Input.Keys.E) {
                        AudioManager.playButtonClickSound();
                        ScreenManager.showMenu();
                        
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        // increment alpha until fully opaque
        if (alpha < 1f) {
            alpha = Math.min(1f, alpha + delta / FADE_DURATION);
        }

        cam.update();
        batch.setProjectionMatrix(cam.combined);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(background, 0, 0, cam.viewportWidth, cam.viewportHeight);
        batch.setColor(1f, 1f, 1f, 1f); // reset to full opacity
        batch.end();
    }

    @Override
    public void dispose() {
        background.dispose();
    }
}
