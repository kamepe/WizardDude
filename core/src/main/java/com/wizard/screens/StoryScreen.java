/* Code for the story line .. basically a presentation n then we click to continue*/
package com.wizard.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Align;
import com.wizard.Main;

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
        batch = game.getBatch();
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);

        // All the background art
        backgrounds = new Texture[]{
            new Texture(Gdx.files.internal("storyline/story1.png")),
            new Texture(Gdx.files.internal("storyline/story2.png")),
            new Texture(Gdx.files.internal("storyline/story3.png")),
            new Texture(Gdx.files.internal("storyline/story4.png")),
            new Texture(Gdx.files.internal("storyline/story5.png")),
            new Texture(Gdx.files.internal("storyline/story6.png"))
        };

        // diaglogs for the screen
        texts = new String[]{
            "balahahahahahahaha",
            "blahahahahahahaha", "blahahahahahahaha", "blahahahahahaahhahaha", "blahahahahahahahahahahahahahahahahahahahahaha", "spawneennenenene where aim I???S?S?S?S?"
        };

        font = new BitmapFont();
        font.getData().setScale(1.2f);
        instrFont = new BitmapFont();
        instrFont.getData().setScale(1f);

        // moving to next slides
        Gdx.input.setInputProcessor(new InputAdapter(){
            @Override public boolean touchDown(int x, int y, int p, int b) {
                if (chars < texts[page].length()) {
                    // finish current page instantly
                    chars = texts[page].length();
                } else {
                    // next page or start game
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
        

        time += delta;
        if (chars < texts[page].length()) {
            chars = Math.min(texts[page].length(),
                             (int)(time * CHARS_PER_SEC));
        }

        cam.update();
        batch.setProjectionMatrix(cam.combined);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        // draw the current background

        batch.draw(backgrounds[page],
                   0, 0,
                   cam.viewportWidth,
                   cam.viewportHeight);

        // draw the typed‐out story text at the bottom

        float margin = 60f; // for text (tap) thing space between them 
        String toShow = texts[page].substring(0, chars);
        GlyphLayout layout = new GlyphLayout(
            font,
            toShow,
            Color.WHITE,
            cam.viewportWidth - 2*margin,
            Align.left,
            true
        );
       

        font.draw(batch,
                  layout,
                  margin,
                  margin + layout.height);

       

        if (chars >= texts[page].length()) {
            String instr = "(tap anywhere to continue)";
            GlyphLayout il = new GlyphLayout(instrFont, instr);
            float ix = (cam.viewportWidth - il.width)/2f;
            float iy = margin + (-40f);  // positioning
            instrFont.draw(batch, il, ix, iy);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        for (Texture t : backgrounds) t.dispose();
        font.dispose();
        instrFont.dispose();
    }
}
