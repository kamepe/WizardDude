package com.wizard.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.wizard.entities.Player;

public class Animator {

    private TextureRegion[] frames;
    private Player player;
    private Body body;
    private float duration;
    private float delay;
    private int current;
    private float width;
    private float height;
    private boolean playOnce; // true if it's something like a death sprite and the last frame has to be looped

    public Animator(Player player, Body body, String path){
        this.player = player;
        this.body = body;
        duration = 0.0f;
        current = 0;
        Texture texture = new Texture(Gdx.files.internal(path));
        frames = TextureRegion.split(texture, 32, 32)[0];
        delay = 0.1f;

        width = frames[0].getRegionWidth();
        height = frames[0].getRegionHeight();
    }

    public void update(float delta) {
        duration += delta;
        while(duration >= delay){
            step();
        }
    }

    // Rendering the animation
    public void render(SpriteBatch batch) {
        batch.draw(
            frames[current],
            player.getX() * Constants.PPM - 50, // the 50 is because this sprite is in 100x100 sections if 32x32 then i think its centered
            player.getY() * Constants.PPM - 50,
            width * 1,
            height * 1);
    }

    public void step() {
        duration -= delay;
        current++;

        if (current >= frames.length) {
            if (playOnce) {
                current = frames.length - 1;
                // maybe finished flag needed
            } else {
                current = 0;
            }
        }
    }
}
