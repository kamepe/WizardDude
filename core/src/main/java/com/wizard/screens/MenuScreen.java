/*
 * This is code for MainMenu screen.
 * How it works:
 * Background animations is set of 5 sprites which play in loop (To make it smooth ill create new sprites) -- TODO
 * Play button calls Screenmanager to switch to game screen
 * Exit calls exit in screenmanager
 */
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
    private static final float FRAME_DURATION = 0.3f;

    private final SpriteBatch batch;
    private final OrthographicCamera cam;

    private final Texture[] bgFrames;
    private float stateTime = 0f;

    private final Texture play, exit;
    private final float playX, playY, exitX, exitY;

    // field we actually mutate
    private boolean CAN_EXIT = true;

    public MenuScreen(Main game) {
        this.batch = game.getBatch();
        this.cam   = new OrthographicCamera();
        cam.setToOrtho(false);

        // load background frames
        bgFrames = new Texture[] {
            new Texture(Gdx.files.internal("backgrounds/sprites0.png")),
            new Texture(Gdx.files.internal("backgrounds/sprites1.png")),
            new Texture(Gdx.files.internal("backgrounds/sprites2.png")),
            new Texture(Gdx.files.internal("backgrounds/sprites3.png")),
            new Texture(Gdx.files.internal("backgrounds/sprites4.png"))
        };

        // load button textures
        play = new Texture(Gdx.files.internal("play.png"));
        exit = new Texture(Gdx.files.internal("exit.png"));

        // calculate button positions
        float cx = (cam.viewportWidth  - BUTTON_W) / 2f;
        float cy = cam.viewportHeight / 2f;
        playX = cx;
        playY = cy + PADDING + BUTTON_H/2f;
        exitX = cx;
        exitY = cy - PADDING - BUTTON_H/2f;

        // now the input processor—no shadowing here!
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int x, int y, int p, int b) {
                float fy = cam.viewportHeight - y; // flip Y

                // PLAY button?
                if (x >= playX && x <= playX + BUTTON_W
                 && fy>= playY && fy<= playY + BUTTON_H) {
                    ScreenManager.showStory();
                    CAN_EXIT = false;  
                    System.err.println(CAN_EXIT);
                }
                // EXIT button (only if CAN_EXIT is still true)
                else if (CAN_EXIT
                     && x >= exitX && x <= exitX + BUTTON_W
                     && fy>= exitY && fy<= exitY + BUTTON_H) {
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
        batch.draw(bg, 0, 0, cam.viewportWidth, cam.viewportHeight);
        batch.draw(play, playX, playY, BUTTON_W, BUTTON_H);
        batch.draw(exit, exitX, exitY, BUTTON_W, BUTTON_H);
        batch.end();
    }

    @Override
    public void dispose() {
        for (Texture t : bgFrames) t.dispose();
        play.dispose();
        exit.dispose();
    }
}
