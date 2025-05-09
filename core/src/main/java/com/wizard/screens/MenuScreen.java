package com.wizard.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.wizard.Main;
import com.wizard.utils.AudioManager;
import com.wizard.screens.ScreenManager;

public class MenuScreen extends ScreenAdapter {
    private static final float INTRO_SCREEN_DURATION = 2f; //intro
    private static final float BUTTON_W = 200f;
    private static final float BUTTON_H = 100f;
    private static final float PADDING  = 10f;
    private static final float FRAME_DURATION = 0.3f;

    private final SpriteBatch batch;
    private final OrthographicCamera cam;
    private final Texture introScreenTexture;
    private float introScreenTimer = 0f;

    private final Texture[] bgFrames;
    private float stateTime = 0f;
    private final Texture play, exit;
    private final float playX, playY, exitX, exitY;

    public MenuScreen(Main game) {
        batch = game.getBatch();

        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.update();

        // Intro screen texture
        introScreenTexture = new Texture(Gdx.files.internal("intro.png"));

        // Animated menu backgrounds
        bgFrames = new Texture[] {
            new Texture(Gdx.files.internal("backgrounds/sprites0.png")),
            new Texture(Gdx.files.internal("backgrounds/sprites1.png")),
            new Texture(Gdx.files.internal("backgrounds/sprites2.png")),
            new Texture(Gdx.files.internal("backgrounds/sprites3.png")),
            new Texture(Gdx.files.internal("backgrounds/sprites4.png"))
        };

        play = new Texture(Gdx.files.internal("play.png"));
        exit = new Texture(Gdx.files.internal("exit.png"));

        playX = (cam.viewportWidth - BUTTON_W) / 2f;
        playY = (cam.viewportHeight / 2f) + PADDING;
        exitX = playX;
        exitY = (cam.viewportHeight / 2f) - BUTTON_H - PADDING;

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                

                if (introScreenTimer < INTRO_SCREEN_DURATION) return true;

                Vector3 worldTouch = new Vector3(screenX, screenY, 0);
                cam.unproject(worldTouch);
                float x = worldTouch.x;
                float y = worldTouch.y;

                if (x >= playX && x <= playX + BUTTON_W
                 && y >= playY && y <= playY + BUTTON_H) {
                    AudioManager.playButtonClickSound();
                    ScreenManager.showStory();
                } else if (x >= exitX && x <= exitX + BUTTON_W
                        && y >= exitY && y <= exitY + BUTTON_H) {
                    AudioManager.playButtonClickSound();
                    ScreenManager.exit();
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        introScreenTimer += delta;
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        if (introScreenTimer < INTRO_SCREEN_DURATION) {
            // Draw intro screen
            batch.draw(introScreenTexture,
                       0, 0,
                       cam.viewportWidth,
                       cam.viewportHeight);
        } else {
            
            stateTime += delta;
            int idx = (int)(stateTime / FRAME_DURATION) % bgFrames.length;
            Texture bg = bgFrames[idx];

            batch.draw(bg,
                       0, 0,
                       cam.viewportWidth,
                       cam.viewportHeight);
            batch.draw(play, playX, playY, BUTTON_W, BUTTON_H);
            batch.draw(exit, exitX, exitY, BUTTON_W, BUTTON_H);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        introScreenTexture.dispose();
        for (Texture t : bgFrames) t.dispose();
        play.dispose();
        exit.dispose();
    }
}
