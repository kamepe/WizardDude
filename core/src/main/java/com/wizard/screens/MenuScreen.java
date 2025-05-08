package com.wizard.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.wizard.Main;
import com.wizard.utils.AudioManager;

public class MenuScreen extends ScreenAdapter {
    private static final float BUTTON_W = 200f;
    private static final float BUTTON_H = 100f;
    private static final float PADDING  = 10f;
    private static final float FRAME_DURATION = 0.3f;
    private static final float V_WIDTH = 800f;
    private static final float V_HEIGHT = 480f;

    private final SpriteBatch batch;
    private final OrthographicCamera cam;
    private final Viewport viewport;

    private final Texture[] bgFrames;
    private float stateTime = 0f;
    private final Texture play, exit;
    private final float playX, playY, exitX, exitY;

    public MenuScreen(Main game) {
        batch = game.getBatch();
        cam = new OrthographicCamera();
        viewport = new FitViewport(V_WIDTH, V_HEIGHT, cam);
        viewport.apply(true);  // center camera

        bgFrames = new Texture[] {
            new Texture(Gdx.files.internal("backgrounds/sprites0.png")),
            new Texture(Gdx.files.internal("backgrounds/sprites1.png")),
            new Texture(Gdx.files.internal("backgrounds/sprites2.png")),
            new Texture(Gdx.files.internal("backgrounds/sprites3.png")),
            new Texture(Gdx.files.internal("backgrounds/sprites4.png"))
        };

        play = new Texture(Gdx.files.internal("play.png"));
        exit = new Texture(Gdx.files.internal("exit.png"));

        float cx = (V_WIDTH - BUTTON_W) / 2f;
        float cy = V_HEIGHT / 2f;
        playX = cx;
        playY = cy + PADDING;
        exitX = cx;
        exitY = cy - BUTTON_H - PADDING;

        // Use unprojected touch for screen-independent input
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector2 touch = new Vector2(screenX, screenY);
                viewport.unproject(touch);
                float x = touch.x, y = touch.y;

                if (x >= playX && x <= playX + BUTTON_W
                 && y >= playY && y <= playY + BUTTON_H) {
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
        stateTime += delta;
        int frameIdx = (int)(stateTime / FRAME_DURATION) % bgFrames.length;
        Texture bg = bgFrames[frameIdx];

        cam.update();
        batch.setProjectionMatrix(cam.combined);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(bg, 0, 0, V_WIDTH, V_HEIGHT);
        batch.draw(play, playX, playY, BUTTON_W, BUTTON_H);
        batch.draw(exit, exitX, exitY, BUTTON_W, BUTTON_H);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        for (Texture t : bgFrames) t.dispose();
        play.dispose();
        exit.dispose();
    }
}

