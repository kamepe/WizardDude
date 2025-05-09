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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.wizard.screens.ScreenManager;
import com.wizard.utils.Animator;
import com.wizard.utils.AudioManager;
import com.wizard.utils.Constants;
import static com.wizard.utils.Constants.PPM;
import com.wizard.utils.KeyManager;

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
    private Texture lightningTexture;
    private Sprite lightningSprite;
    private Vector2 velocity; // Not sure if needed
    private EntityManager entityManager;
    private OrthographicCamera camera;
    private static final float SPELL_COOLDOWN = 0.3f;  // seconds between casts
    private int health = 10;
    private float scale = 0.7f;
    private float width;
    private float height;
    private boolean dead = false;
    private Constants.Direction currentDirection;

    private float fireballCooldownTimer = 0f;
    private static final float FIREBALL_COOLDOWN = 2.0f;

    private float lightningCooldownTimer = 0f;
    private static final float LIGHTNING_COOLDOWN = 1.0f;

    private KeyManager keyManager;

    public Player(World world, float x, float y, EntityManager em, OrthographicCamera cam) {
        this(world, x, y, em, cam, null);
    }

    public Player(World world, float x, float y, EntityManager em, OrthographicCamera cam, KeyManager keyManager) {
        this.world = world;
        this.entityManager = em;
        this.camera = cam;
        this.keyManager = keyManager;
        texture = new Texture(Gdx.files.internal("characters/wizard/right.png"));
        sprite = new Sprite(texture);// need to actually add a texture
        fireballTexture = new Texture(Gdx.files.internal("spells/fireball.png"));
        fireballSprite = new Sprite(fireballTexture);
        lightningTexture = new Texture(Gdx.files.internal("spells/ball.png"));
        lightningSprite = new Sprite(lightningTexture);
        position = new Vector2();
        System.out.println(sprite.getWidth());
        float spriteWpx = sprite.getWidth() / 4;
        float spriteHpx = sprite.getHeight() * 1.5f;

        width = (spriteWpx / PPM) * scale;
        height = (spriteHpx  / PPM) * scale;

        sprite.setSize(width * PPM, height * PPM);
        sprite.setOriginCenter();

        createBody(x, y);
        animation = new Animator(this, body, "characters/wizard/down.png", true);
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
        float halfW = width * scale  * 0.5f;
        float halfH = height * scale * 0.5f;
        shape.setAsBox(halfW, halfH);

        // Create fixture definition
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f; // probably not needed

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();
    }

    public void update(float deltaTime){
        position = body.getPosition();
        fireballCooldownTimer = Math.max(0, fireballCooldownTimer - deltaTime);
        lightningCooldownTimer = Math.max(0, lightningCooldownTimer - deltaTime);
        handleMovement(deltaTime);
        animation.update(deltaTime);
        handleSpellCast(deltaTime);
    }

    private void handleMovement(float deltaTime){
        Vector2 direction = new Vector2(0, 0);
        boolean changed = false;

        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            direction.x = -Constants.MAX_SPEED;
            if(currentDirection != Constants.Direction.LEFT){
                changed = true;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            direction.x = Constants.MAX_SPEED;
            if(currentDirection != Constants.Direction.RIGHT){
                changed = true;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            direction.y = Constants.MAX_SPEED;
            if(currentDirection != Constants.Direction.UP){
                changed = true;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            direction.y = -Constants.MAX_SPEED;
            if(currentDirection != Constants.Direction.DOWN){
                changed = true;
            }
        }

        if (direction.x != 0 && direction.y != 0) {
            direction.scl(1f / (float)Math.sqrt(2));
        }

        if(changed){
            setDirection(direction);
        }

        // Play walking sound if player is moving, stop it if player is not moving
        if (direction.x != 0 || direction.y != 0) {
            AudioManager.playWalkingSound();
        } else {
            AudioManager.stopWalkingSound();
        }

        body.setLinearVelocity(direction);
    }

    private void handleSpellCast(float deltaTime){

        Sprite fireball = new Sprite(fireballSprite);
        Sprite lightning = new Sprite(lightningSprite);
        Vector2 direction = new Vector2(0, 0);
         if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            if (fireballCooldownTimer > 0.f) return;
        // Get all the variables to initialise a spell
            position = body.getPosition();
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Vector3 mouseWorld  = camera.unproject(mousePos);
            float startX = position.x;
            float startY = position.y;
            float targetX = mouseWorld.x / PPM;
            float targetY = mouseWorld.y / PPM;
            Vector2 aim = new Vector2(targetX, targetY)
            .sub(body.getPosition().x, body.getPosition().y);

            float w = 0.3f, h = 0.3f, speed = 1.5f;

            entityManager.addToActiveSpells(new Spells(world, startX,
                startY, aim, w, h, speed, "spells/firebullet.png", this));
            fireballCooldownTimer = FIREBALL_COOLDOWN;

            AudioManager.playPlayerFireballSound();
        }
        else if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
        // Get all the variables to initialise a spell
            if (lightningCooldownTimer > 0.f) return;
            position = body.getPosition();
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Vector3 mouseWorld  = camera.unproject(mousePos);
            float startX = position.x;
            float startY = position.y;
            float targetX = mouseWorld.x / PPM;
            float targetY = mouseWorld.y / PPM;
            Vector2 aim = new Vector2(targetX, targetY).sub(body.getPosition().x, body.getPosition().y);

            float width= 0.5f, height = 0.5f, speed = 1f;


            entityManager.addToActiveSpells(new Spells(world, startX,
            startY, aim, width, height, speed, "spells/ball.png", this ));
            lightningCooldownTimer = LIGHTNING_COOLDOWN;

             AudioManager.playPlayerSpellSound();

         }
    }

    private void setDirection(Vector2 direction){

        if((direction.x == 0 && direction.y == 0) || (direction.x == 0 && direction.y < 0)){ // Idle
            currentDirection = Constants.Direction.DOWN;
            animation = new Animator(this, body, "characters/wizard/down.png", false);
        }
        else if(direction.x == 0 && direction.y > 0){
            currentDirection = Constants.Direction.UP;
            animation = new Animator(this, body, "characters/wizard/up.png", false);
        }
        else if(direction.x > 0 && direction.y == 0){
            currentDirection = Constants.Direction.RIGHT;
            animation = new Animator(this, body, "characters/wizard/right.png", false);
        }
        else if(direction.x < 0 && direction.y == 0){
            currentDirection = Constants.Direction.LEFT;
            animation = new Animator(this, body, "characters/wizard/right.png", true);
        }
        else if(direction.x > 0 && direction.y > 0){
            currentDirection = Constants.Direction.UPRIGHT;
            animation = new Animator(this, body, "characters/wizard/upRight.png", false);
        }
        else if(direction.x > 0 && direction.y < 0){
            currentDirection = Constants.Direction.DOWNRIGHT;
        }
        else if(direction.x < 0 && direction.y > 0){
            currentDirection = Constants.Direction.UPLEFT;
            animation = new Animator(this, body, "characters/wizard/upRight.png", true);
        }
        else if(direction.x < 0 && direction.y < 0){
            currentDirection = Constants.Direction.DOWNLEFT;
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
    public void takeDamage(){
        health --;
        System.out.println("player damaged / health: " + health);
        AudioManager.playPlayerDamageSound();
        if(health <= 0){
            die();
        }
    }
    private void die() {
        dead = true;
        System.err.println("Player died");
        AudioManager.stopWalkingSound();
        ScreenManager.diedscreen();
    }
    public int getHealth(){ return health;}

    public void heal(int amount) {
        health = Math.min(10, health + amount);
        System.out.println("Player healed / health: " + health);
    }

    // Getters for future use like collisions
    public float getX() {return position.x;}

    public float getY() {return position.y;}

    public float getHeight() {return height;}

    public float getWidth() {return width;} // make sure updated if fore some reason it changes

    public Vector2 getPosition() {return position;}

    public float getCooldownFireTimer(){return fireballCooldownTimer;}

    public float getCooldownFire(){return FIREBALL_COOLDOWN;}

    public float getCooldownLightningTimer(){return lightningCooldownTimer;}

    public float getCooldownLightning(){return LIGHTNING_COOLDOWN;}

    public boolean hasKey() {
        return keyManager != null && keyManager.hasKey();
    }

    public int getKeyCount() {
        return keyManager != null ? keyManager.getAvailableKeys() : 0;
    }

    public void addKey() {
        if (keyManager != null) {
            keyManager.addKey();
        }
    }

    public void setKeyManager(KeyManager keyManager) {
        this.keyManager = keyManager;
    }

    public KeyManager getKeyManager() {
        return keyManager;
    }
}
