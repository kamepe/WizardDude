package com.wizard.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.wizard.utils.Animator;
import com.wizard.utils.Constants;
import static com.wizard.utils.Constants.PPM;

//InderStuff
public class Player {
    private Body body;
    private Spells spells;
    private World world;
    private Texture texture;
    private Sprite sprite;
    private Animator animation;
    private Vector2 position;
    private Texture fireballTexture;
    private Sprite fireballSprite;
    private Vector2 velocity; // Not sure if needed
    private EntityManager entityManager;

    private float width;
    private float height;
    private Constants.Direction currentDirection;


    public Player(World world, float x, float y, EntityManager em) {
        this.world = world;
        this.entityManager = em;
        texture = new Texture(Gdx.files.internal("characters/tempPlayer2.png"));
        sprite = new Sprite(texture);// need to actually add a texture
        fireballTexture = new Texture(Gdx.files.internal("characters/tempPlayer.png"));
        fireballSprite = new Sprite(fireballTexture);
        position = new Vector2();
        position = new Vector2();
        width = (sprite.getWidth() / (PPM * 10));
        height = (sprite.getHeight() / (PPM * 10));
        createBody(x, y);// Here im changing the cords because i have downsized the picture, may not be needed later

        animation = new Animator(this, body, "characters/walkingRight.png");
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
        animation.update(deltaTime);
        handleSpellCast(deltaTime);
    }

    private void handleMovement(float deltaTime){
        velocity = body.getLinearVelocity();
        Vector2 direction = new Vector2(0, 0);

        // make these into switch cases and make diagonal movement divide by sqrt2 so not faster
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            direction.x = -Constants.MAX_SPEED;
            currentDirection = Constants.Direction.LEFT;
            System.out.println("LEFT");
        } else if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            direction.x = Constants.MAX_SPEED;
            currentDirection = Constants.Direction.RIGHT;
            System.out.println("RIGHT");
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
    private void handleSpellCast(float deltaTime){

        Sprite fireball = new Sprite(fireballSprite);
        Vector2 direction = new Vector2(0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.O) ) {
        // Get all the variables to initialise a spell
            position = body.getPosition();
            float startX = position.x;
            float startY = position.y;
            Vector2 rawDir = new Vector2(body.getLinearVelocity());
            if (rawDir.len() == 0) rawDir.set(1,0);  // default to “right” if you weren’t moving
            rawDir.nor();

            float width = 0.2f, height = 0.2f, speed = 5f;


            entityManager.addToActiveSpells(new Spells(world, startX,
            startY, rawDir.x, rawDir.y, width, height, speed, fireball ));
        }
    }
    public void render(SpriteBatch batch) {
        position = body.getPosition();

        // Draw centered at physics body position
        if(true){
            animation.render(batch);
        }
        else{
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

    public float getWidth() {return width;} // make sure updated if fore some reason it changes

    public Vector2 getPosition() {return position;}
}
