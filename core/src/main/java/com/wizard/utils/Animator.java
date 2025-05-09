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
    private boolean invert;
    private float width;
    private float height;
    private boolean playOnce; // true if it's something like a death sprite and the last frame has to be looped
    private boolean isProjectile;

    public Animator(Player player, Body body, String path, boolean invert) {
        this.player = player;
        this.body = body;
        this.invert = invert;
        this.isProjectile = (player == null); // If player is null, we assume it's a projectile

        duration = 0.0f;
        current = 0;
        Texture texture = new Texture(Gdx.files.internal(path));

        // Determine if this is a projectile animation
        if (path.contains("firebullet") && invert) {
            TextureRegion fullTexture = new TextureRegion(texture);
            fullTexture.setRegion(0, 49, 80, 16);
            frames = new TextureRegion[4];
            for (int i = 0; i < 4; i++) {
                frames[i] = new TextureRegion(fullTexture, i * 16, 0, 16, 16);
            }
        } else if (path.contains("ball")) {
            TextureRegion fullTexture = new TextureRegion(texture);
            fullTexture.setRegion(16, 16, 192, 64);
            frames = new TextureRegion[1];
            for (int i = 0; i < 1; i++) {
                frames[i] = new TextureRegion(fullTexture, i * 64, 0, 64, 64);
            }
        } else if (path.contains("firebullet") ) {
            TextureRegion fullTexture = new TextureRegion(texture);
            fullTexture.setRegion(0, 17, 80, 16);
            frames = new TextureRegion[5];
            for (int i = 0; i < 5; i++) {
                frames[i] = new TextureRegion(fullTexture, i * 16, 0, 16, 16);
            }
        } else {
            // Default animation handling for player
            frames = TextureRegion.split(texture, 32, 32)[0];
        }
        delay = 0.1f;

        if (invert) {
            for (int i = 0; i < frames.length; i++) {
                frames[i].flip(true, false);  // flip horizontally
            }
        }

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
        TextureRegion frame = frames[current];

        // true pixel size of this frame
        float frameWpx = frame.getRegionWidth();
        float frameHpx = frame.getRegionHeight();

        if (isProjectile) {
            // For projectiles, we use the body position directly
            float centerX = body.getPosition().x * Constants.PPM;
            float centerY = body.getPosition().y * Constants.PPM;

            if (frameWpx == 64 && frameHpx == 64) {
                frameWpx /= 2;
                frameHpx /= 2;
            }

            // Draw bottom-left such that the frame is centered
            batch.draw(frame,
                centerX - frameWpx * 0.5f,
                centerY - frameHpx * 0.5f,
                frameWpx,
                frameHpx
            );
        } else {
            // For player animations, use the player position
            float centerX = player.getX() * Constants.PPM;
            float centerY = player.getY() * Constants.PPM;

            // Draw bottom-left such that the frame is centered
            batch.draw(frame,
                centerX - frameWpx * 0.5f,
                centerY - frameHpx * 0.5f,
                frameWpx,
                frameHpx
            );
        }
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
