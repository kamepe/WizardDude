package com.wizard.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.wizard.utils.Animator;
import com.wizard.utils.Constants;
import static com.wizard.utils.Constants.PLAYER_DIMENSIONS;
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
    private OrthographicCamera camera;

    private float width;
    private float height;
    private Constants.Direction currentDirection;


    public Player(World world, float x, float y, EntityManager em, OrthographicCamera cam) {
        this.world = world;
        this.entityManager = em;
        this.camera = cam;
        texture = new Texture(Gdx.files.internal("characters/walkingRight.png"));
        sprite = new Sprite(texture);// need to actually add a texture
        fireballTexture = new Texture(Gdx.files.internal("characters/tempFireBall.png"));
        fireballSprite = new Sprite(fireballTexture);
        position = new Vector2();
        System.out.println(sprite.getWidth());
//        width = (sprite.getWidth() / (PPM * 10));//not sure about these lines but pretty sure i can just get rid of them
//        height = (sprite.getHeight() / (PPM * 10));
        width = PLAYER_DIMENSIONS / PPM;
        height = PLAYER_DIMENSIONS / PPM;
        createBody(x, y);// Here im changing the cords because i have downsized the picture, may not be needed later
        animation = new Animator(this, body, "characters/walkingRight.png");
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
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
        // Get all the variables to initialise a spell
            position = body.getPosition();
            float startX = position.x;
            float startY = position.y;
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Vector3 mouseWorld  = camera.unproject(mousePos);
            float targetX = mouseWorld.x / PPM;
            float targetY = mouseWorld.y / PPM;
            Vector2 aim = new Vector2(targetX, targetY)
            .sub(body.getPosition().x, body.getPosition().y);

            float width = 0.2f, height = 0.2f, speed = 5f;


            entityManager.addToActiveSpells(new Spells(world, startX,
            startY, aim, width, height, speed, fireball ));
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
