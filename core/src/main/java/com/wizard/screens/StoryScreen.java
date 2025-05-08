package com.wizard.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.wizard.Main;
import com.wizard.utils.AudioManager;

public class StoryScreen extends ScreenAdapter {
    private final SpriteBatch batch;
    private final OrthographicCamera cam;
    private final Texture[] backgrounds;
    private final String[] texts;
    private final BitmapFont font, instrFont;
    private int page = 0, chars = 0;
    private float time = 0f;
    private static final float CHARS_PER_SEC = 30f;

    public StoryScreen(Main game) {
        // Load all sound effects
        AudioManager.initSoundEffects();

        // Initialize batch and camera
        batch = game.getBatch();
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);

        // Load backgrounds
        backgrounds = new Texture[]{
            new Texture(Gdx.files.internal("storyline/story1.png")),
            new Texture(Gdx.files.internal("storyline/story2.png")),
            new Texture(Gdx.files.internal("storyline/story3.png")),
            new Texture(Gdx.files.internal("storyline/story4.png")),
            new Texture(Gdx.files.internal("storyline/story5.png")),
            new Texture(Gdx.files.internal("storyline/story6.png"))
        };

        // Story texts
        texts = new String[]{
            "balahahahahahahaha",
            "blahahahahahahaha",
            "blahahahahahahaha",
            "blahahahahahaahhahaha",
            "blahahahahahahahahahahahahahahahahahahahahaha",
            "spawneennenenene where aim I???S?S?S?S?"
        };

        // Setup fonts
        font = new BitmapFont();
        font.getData().setScale(1.2f);
        instrFont = new BitmapFont();
        instrFont.getData().setScale(1f);

        // Handle taps: advance text/page and play sound
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                // Play speak sound on every tap
                AudioManager.playSpeakSound();

                // Reveal text or advance page
                if (chars < texts[page].length()) {
                    chars = texts[page].length();
                } else {
                    page++;
                    if (page >= texts.length) {
                        ScreenManager.showGame();
                    } else {
                        time = 0f;
                        chars = 0;
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        // Update character reveal timer
        time += delta;
        if (chars < texts[page].length()) {
            chars = Math.min(texts[page].length(), (int)(time * CHARS_PER_SEC));
        }

        // Clear and set camera
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        // Draw background
        batch.draw(backgrounds[page], 0, 0, cam.viewportWidth, cam.viewportHeight);

        // Prepare story text
        float margin = 60f;
        String toShow = texts[page].substring(0, chars);
        GlyphLayout layout = new GlyphLayout(
            font, toShow, Color.WHITE, cam.viewportWidth - 2 * margin,
            Align.left, true
        );
        font.draw(batch, layout, margin, margin + layout.height);

        // Draw instruction once text fully shown
        if (chars >= texts[page].length()) {
            String instr = "(tap anywhere to continue)";
            GlyphLayout il = new GlyphLayout(instrFont, instr);
            float ix = (cam.viewportWidth - il.width) / 2f;
            float iy = margin - 40f;
            instrFont.draw(batch, il, ix, iy);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        // Dispose textures and fonts
        for (Texture t : backgrounds) {
            t.dispose();
        }
        font.dispose();
        instrFont.dispose();
    }
}

