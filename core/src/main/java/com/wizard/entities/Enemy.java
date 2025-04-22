package com.wizard.entities;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.wizard.utils.Constants;
import static com.wizard.utils.Constants.PPM;


public class Enemy {
    // core
    private Body body;
    private World world;
    private Vector2 position;
    private EntityManager entityManager;
    private Player player;

    // animation
    private float width;
    private float height;
    private Constants.Direction currentDirection = Constants.Direction.DOWN;
    private Animation<TextureRegion> down, up, left, right;
    private float stateTime = 0f;
    
    // stats
    private int health;
    private float attackCooldown;
    private float detectionRange;
    private float moveSpeed;
    private Sprite attackSprite;
    private boolean isRanged;
    
    // state
    private boolean dead = false;
    private float timeSinceLastAttack = 0f;
    private Vector2 lastPlayerPos = new Vector2();
    private Vector2 playerVelocity = new Vector2();
    private float predictionFactor = 0.82f; // I adjusted it until it became kind of accurate,  while testing
                                            // keep in mind that the players hitbox is completely broken
    public Enemy(World world, float x, float y, EntityManager em, Player player, EnemyType type) {
        down = new Animation<>(0.1f, loadStrip(type.getDownSprite()));
        up = new Animation<>(0.1f, loadStrip(type.getUpSprite()));
        left = new Animation<>(0.1f, loadStrip(type.getLeftSprite()));
        right = new Animation<>(0.1f, loadStrip(type.getRightSprite()));

        this.world = world;
        this.entityManager = em;
        this.player = player;
        this.position = new Vector2();
        this.lastPlayerPos.set(player.getPosition());
        this.health = type.getHealth();
        this.attackCooldown = type.getAttackCooldown();
        this.detectionRange = type.getDetectionRange();
        this.moveSpeed = type.getMoveSpeed();
        this.attackSprite = new Sprite(type.getAttackSprite());
        this.isRanged = type.isRanged();

        TextureRegion sample = down.getKeyFrame(0f);
        width = sample.getRegionWidth() / PPM;
        height = sample.getRegionHeight() / PPM;

        createBody(x, y);
    }

    private TextureRegion[] loadStrip(String filename) {
        Texture sprite_part = new Texture(Gdx.files.internal("characters/enemy/" + filename));
        TextureRegion[][] tmp = TextureRegion.split(sprite_part, 32, 32);
        TextureRegion[] frames = new TextureRegion[tmp[0].length];
        System.arraycopy(tmp[0], 0, frames, 0, frames.length);
        return frames;
    }

    private void createBody(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true;
        body = world.createBody(bodyDef);
        
        body.setUserData(this);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/4, height/4); // smaller hitbox, not final
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f; // can be adjusted later
        
        Filter filter = new Filter();
        filter.categoryBits = Constants.CATEGORY_ENEMY;
        filter.maskBits = Constants.CATEGORY_PLAYER | Constants.CATEGORY_PLAYER_SPELL;
        fixtureDef.filter.categoryBits = filter.categoryBits;
        fixtureDef.filter.maskBits = filter.maskBits;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void update(float deltaTime) {
        if (dead) return;

        Vector2 playerPosition = player.getPosition();
        Vector2 direction;
        position = body.getPosition();
        stateTime += deltaTime;
        
        if (isRanged) {
            // very basic prediction where to aim
            playerVelocity.set(
                (playerPosition.x - lastPlayerPos.x) / Math.max(deltaTime, 0.01f),
                (playerPosition.y - lastPlayerPos.y) / Math.max(deltaTime, 0.01f)
            );
            
            lastPlayerPos.set(playerPosition);
            
            // try to predict where the player will be
            Vector2 predictedPos = new Vector2(
                playerPosition.x + playerVelocity.x * predictionFactor,
                playerPosition.y + playerVelocity.y * predictionFactor
            );
            
            // direction to the predicted position
            direction = new Vector2(predictedPos.x - position.x, predictedPos.y - position.y);
        } else {
            // melee enemies use direct path
            direction = new Vector2(playerPosition.x - position.x, playerPosition.y - position.y);
        }
        
        float distance = direction.len();
        direction.nor();
        
        updateDirection(direction);
        // reset velocity, (no knockback)
        body.setLinearVelocity(0, 0);
        
        timeSinceLastAttack += deltaTime;
        if ((distance < detectionRange) && timeSinceLastAttack >= attackCooldown) {
            attackPlayer(direction);
            timeSinceLastAttack = 0;
        }
        
        // go to the player if not in range
        if (!(distance < detectionRange)) {
            body.setLinearVelocity(direction.x * moveSpeed, direction.y * moveSpeed);
        }
    }
    
    private void attackPlayer(Vector2 direction) {
        if (isRanged) {
            // copy of the direction vector
            entityManager.addToActiveSpells(new Spells(
                world,
                position.x, position.y,
                new Vector2(direction),
                0.5f, 0.5f,
                1.5f,
                new Sprite(attackSprite)
            ));
        } else {
            // melee attacks are spells but with a short range and lifetime
            Spells meleeAttack = new Spells(
                world,
                position.x, position.y,
                new Vector2(direction),
                0.4f, 0.4f,
                0.4f,
                new Sprite(attackSprite)
            );
            
            entityManager.addToActiveSpells(meleeAttack);
        }
    }
    
    public void takeDamage() {
        health--;
        System.out.println("enemy damaged / health: " + health);
        if (health <= 0) {
            die();
        }
    }
    
    private void updateDirection(Vector2 direction) {
        if (Math.abs(direction.x) > Math.abs(direction.y)) {
            if (direction.x > 0) {
                currentDirection = Constants.Direction.RIGHT;
            } else {
                currentDirection = Constants.Direction.LEFT;
            }
        } else {
            if (direction.y > 0) {
                currentDirection = Constants.Direction.UP;
            } else {
                currentDirection = Constants.Direction.DOWN;
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (dead) return;
    
        TextureRegion frame = null;
    
        switch (currentDirection) {
            case UP:    frame = up.getKeyFrame(stateTime, true); break;
            case DOWN:  frame = down.getKeyFrame(stateTime, true); break;
            case LEFT:  frame = left.getKeyFrame(stateTime, true); break;
            case RIGHT: frame = right.getKeyFrame(stateTime, true); break;
            default:
                System.out.println("direction doesnt exist, defaulting to down.");
                frame = down.getKeyFrame(stateTime, true);
        }
    
        batch.draw(
            frame,
            (position.x - width/2) * PPM,
            (position.y - height/2) * PPM,
            width * PPM,
            height * PPM
        );
    }

    private void die() {
        dead = true;
    }
    public boolean shouldRemove() {
        return dead;
    }
    public Body getBody() {
        return body;
    }
    public void dispose() {
        attackSprite.getTexture().dispose();
    }
}

