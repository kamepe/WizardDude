
package com.wizard.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.wizard.Main;

public class MenuScreen extends ScreenAdapter {
    // button size and spacing
    private static final float BUTTON_W = 200f;
    private static final float BUTTON_H = 100f;
    private static final float PADDING  = 10f;

    // animation timing
    private static final float FRAME_DURATION = 0.3f; // seconds per bg‐frame

    private final SpriteBatch batch;
    private final OrthographicCamera cam;

    // SUPPORT FOR MULTI-FRAME BG
    private final Texture[] bgFrames;
    private float stateTime = 0f;

    private final Texture play, exit;
    private final float playX, playY, exitX, exitY;

    public MenuScreen(Main game) {
        batch = game.getBatch();
        cam   = new OrthographicCamera();
        cam.setToOrtho(false);

        // load your 5 background images into an array
        bgFrames = new Texture[] {
            new Texture(Gdx.files.internal("backgrounds/sprites0.png")),
            new Texture(Gdx.files.internal("backgrounds/sprites1.png")),
            new Texture(Gdx.files.internal("backgrounds/sprites2.png")),
            new Texture(Gdx.files.internal("backgrounds/sprites3.png")),
            new Texture(Gdx.files.internal("backgrounds/sprites4.png"))
        };

        // load buttons
        play = new Texture(Gdx.files.internal("play.png"));
        exit = new Texture(Gdx.files.internal("exit.png"));

        // compute centered button positions
        float cx = (cam.viewportWidth  - BUTTON_W) / 2f;
        float cy = cam.viewportHeight / 2f;
        playX = cx;
        playY = cy + PADDING;
        exitX = cx;
        exitY = cy - BUTTON_H - PADDING;

        // input handler
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int x, int y, int p, int b) {
                float fy = cam.viewportHeight - y;
                if (x >= playX && x <= playX + BUTTON_W
                 && fy>= playY && fy<= playY + BUTTON_H) {
                    ScreenManager.showGame();
                }
                else if (x >= exitX && x <= exitX + BUTTON_W
                      && fy>= exitY && fy<= exitY + BUTTON_H) {
                    ScreenManager.exit();
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        // advance the animation clock
        stateTime += delta;
        int frameIdx = (int)(stateTime / FRAME_DURATION) % bgFrames.length;
        Texture bg = bgFrames[frameIdx];

        cam.update();
        batch.setProjectionMatrix(cam.combined);

        // draw current bg frame + buttons
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(bg, 0, 0, cam.viewportWidth, cam.viewportHeight);
        batch.draw(play, playX, playY, BUTTON_W, BUTTON_H);
        batch.draw(exit, exitX, exitY, BUTTON_W, BUTTON_H);
        batch.end();
    }

    @Override
    public void dispose() {
        // dispose all bg frames
        for (Texture t : bgFrames) t.dispose();
        play.dispose();
        exit.dispose();
    }
}







