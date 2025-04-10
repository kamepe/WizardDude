package com.wizard.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.wizard.utils.Constants;

//InderStuff
public class Player {
    private Body body;
    private World world;

    private Texture texture;
    private Vector2 position;
    private Vector2 velocity; // Not sure if needed

    private float width;
    private float height;


    public Player(float x, float y) {
        texture = new Texture("characters/tempPlayer.png");// need to actually add a texture\
        position = new Vector2();
        width = texture.getWidth() / Constants.PPM;
        height = texture.getHeight() / Constants.PPM;
    }

    public void update(float deltaTime){
        handleMovement(deltaTime);
    }

    private void handleMovement(float deltaTime){

    }

    public void render(SpriteBatch batch) {
        Vector2 position = body.getPosition();

        // Draw centered at physics body position
        batch.draw(texture,
            (position.x - width/2) * Constants.PPM,
            (position.y - height/2) * Constants.PPM,
            width * Constants.PPM, height * Constants.PPM);
    }

    public void dispose() {
        texture.dispose();
    }

    // Getters for camera following
    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getCenterX() {
        return position.x + width / 2;
    }

    public float getCenterY() {
        return position.y + height / 2;
    }
}
