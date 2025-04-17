package com.wizard.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.wizard.utils.Animator;
import com.wizard.utils.Constants;

import static com.wizard.utils.Constants.PPM;

//InderStuff
public class Player {
    private Body body;
    private World world;
    private Texture texture;
    private Sprite sprite;
    private TextureRegion animation;
    private Vector2 position;
    private Vector2 velocity; // Not sure if needed

    private float width;
    private float height;
    private Constants.Direction currentDirection;

    private Animator animator;

    public Player(World world, float x, float y) {
        this.world = world;
        texture = new Texture(Gdx.files.internal("characters/tempPlayer.png"));
        sprite = new Sprite(texture);// need to actually add a texture
        position = new Vector2();
        width = (sprite.getWidth() / PPM) / 10;
        height = (sprite.getHeight() / PPM) / 10;
        createBody(x, y);

        animator = new Animator(this, body, "characters/idleSoldier.png");
    }

    private void createBody(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true; // not sure if actually needed, since didnt turn when i changed it
        // Create the body
        body = world.createBody(bodyDef);

        //create fixture, collision shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width, height);

        // Create fixture definition
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f; // probably not needed

        body.createFixture(fixtureDef);

        shape.dispose();
    }

    public void update(float deltaTime){
        position = body.getPosition();
        handleMovement(deltaTime);
        animator.update(deltaTime);
    }

    private void handleMovement(float deltaTime){
        velocity = body.getLinearVelocity();
        Vector2 direction = new Vector2(0, 0);

        // make these into switch cases and make diagonal movement divide by sqrt2 so not faster
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            direction.x = -Constants.MAX_SPEED;
            currentDirection = Constants.Direction.LEFT;
//            System.out.println("LEFT");
        } else if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            direction.x = Constants.MAX_SPEED;
            currentDirection = Constants.Direction.RIGHT;
//            System.out.println("RIGHT");
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

    public void render(SpriteBatch batch) {
        position = body.getPosition();

        // Draw centered at physics body position

        if (true) {
            animator.render(batch);
        } else {
            // Fallback to sprite rendering
            batch.draw(sprite,
                (position.x - width/2) * PPM,
                (position.y - height/2) * PPM,
                width * PPM, height * PPM);
        }
    }

    public void dispose() {
        texture.dispose();
    }

    // Getters for future use like collisions
    public float getX() {return position.x;}

    public float getY() {return position.y;}

    public float getHeight() {return height;}

    public float getWidth() {return width;} // make sure updated if for some reason it changes

    public Vector2 getPosition() {return body.getPosition();}
}
