package com.wizard.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
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
    private Constants.Direction currentDirection;


    public Player(World world, float x, float y) {
        this.world = world;
        texture = new Texture("characters/tempPlayer.png");// need to actually add a texture\
        position = new Vector2();
        float sizeAdjustment = 0.1f;
        width = (texture.getWidth() / Constants.PPM) * sizeAdjustment;
        height = (texture.getHeight() / Constants.PPM) * sizeAdjustment;

        createBody(x, y);
    }

    private void createBody(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true; // Prevent the player from rotating

        // Create the body
        body = world.createBody(bodyDef);

        //create fixture, collision shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        // Create fixture definition
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;

        body.createFixture(fixtureDef);

        shape.dispose();
    }

    public void update(float deltaTime){
        handleMovement(deltaTime);
    }

    private void handleMovement(float deltaTime){
        velocity = body.getLinearVelocity();
        Vector2 direction = new Vector2(0, 0);
//        enum state = getDirection();
//
//        switch(state)
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            direction.x = -Constants.MAX_SPEED;
            currentDirection = Constants.Direction.LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            direction.x = Constants.MAX_SPEED;
            currentDirection = Constants.Direction.RIGHT;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            direction.y = Constants.MAX_SPEED;
            currentDirection = Constants.Direction.UP;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            direction.y = -Constants.MAX_SPEED;
            currentDirection = Constants.Direction.DOWN;
        }

        body.setLinearVelocity(direction);
    }

    private void getDirection(){

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

    public float getHeight() {
        return height;
    }
    public float getWidth() {
        return width;
    }
    public Vector2 getPosition() {
        return position;
    }
}
